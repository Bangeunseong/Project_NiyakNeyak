package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.data.medication_model.Container
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ActivitySearchBinding
import com.capstone.project_niyakneyak.main.adapter.MedicineListAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.etc.OpenApiFunctions
import com.capstone.project_niyakneyak.main.listener.OnCheckedSearchItemListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SearchActivity: AppCompatActivity(), OnCheckedSearchItemListener {
    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!
    private var _apiFunctions: OpenApiFunctions? = null
    private val apiFunctions get() = _apiFunctions!!
    private var adapter: MedicineListAdapter? = null
    private var jsonObject: JSONObject? = null

    // Using Coroutine for data fetch process in background thread
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private val selectedPositionObserver = MutableLiveData<Int>()
    private val searchQueryObserver = MutableLiveData<String?>()
    private val channel = Channel<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting View Binding, Api Function class, and Empty Data adapter
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        _apiFunctions = OpenApiFunctions()
        adapter = MedicineListAdapter(JSONArray(), this)
        selectedPositionObserver.value = -1
        setContentView(binding.root)

        // Observe Data changes of radio button selected position
        searchQueryObserver.observe(this){
            binding.loadingLayout.visibility = View.VISIBLE
            ioScope.launch{
                jsonObject = performDataFetchTaskAsync(it!!)
                Log.w(TAG, jsonObject.toString())
                if(jsonObject != null) channel.send("Success")
                else channel.send("Failed")
            }
            mainScope.launch {
                val msg = channel.receive()
                if(msg == "Success"){
                    try{
                        val jsonArray = jsonObject!!.getJSONObject("body").getJSONArray("items")
                        adapter!!.setJSONArray(jsonArray)
                    }catch (exception: JSONException){ Log.w(TAG, "Error Occurred: $it")}
                } else Log.w(TAG, "Item not Found!")
                binding.loadingLayout.visibility = View.GONE
            }
        }
        selectedPositionObserver.observe(this) {
            binding.submit.isEnabled = it != -1
        }

        // Set Components in view
        binding.searchResult.adapter = adapter
        binding.searchResult.layoutManager = LinearLayoutManager(this)
        binding.searchResult.addItemDecoration(VerticalItemDecorator(10))
        binding.searchResult.addItemDecoration(HorizontalItemDecorator(10))
        binding.searchBar.isIconified = true
        binding.searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                binding.searchBar.isSubmitButtonEnabled = newText != null && newText.length >= 2
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!TextUtils.isEmpty(query))
                    searchQueryObserver.value = query
                return true
            }
        })
        binding.submit.setOnClickListener {
            // Exception Handling
            if(jsonObject == null){
                setResult(RESULT_CANCELED)
                finish()
            }

            val bundle = Bundle()
            val data = jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(selectedPositionObserver.value!!)
            val container = Container(
                data.getString(MedicineData.FIELD_ITEM_SEQ),
                data.getString(MedicineData.FIELD_ITEM_NAME),
                data.getString(MedicineData.FIELD_ITEM_ENG_NAME),
                data.getString(MedicineData.FIELD_ENPT_NAME),
                data.getString(MedicineData.FIELD_ENPT_ENG_NAME),
                data.getString(MedicineData.FIELD_ENPT_SEQ),
                data.getString(MedicineData.FIELD_ENPT_NO),
                data.getString(MedicineData.FIELD_ITEM_PERMIT_DATE).toString(),
                data.getString(MedicineData.FIELD_INDUTY),
                data.getString(MedicineData.FIELD_PRDLST_STDR_CODE),
                data.getString(MedicineData.FIELD_SPCLTY_PBLC),
                data.getString(MedicineData.FIELD_PRDUCT_TYPE),
                data.getString(MedicineData.FIELD_PRDUCT_PRMISN_NO),
                data.getString(MedicineData.FIELD_ITEM_INGR_NAME),
                data.getString(MedicineData.FIELD_ITEM_INGR_CNT),
                data.getString(MedicineData.FIELD_BIG_PRDT_IMG_URL),
                data.getString(MedicineData.FIELD_PERMIT_KIND_CODE),
                data.getString(MedicineData.FIELD_CANCEL_DATE).toString(),
                data.getString(MedicineData.FIELD_CANCEL_NAME),
                data.getString(MedicineData.FIELD_EDI_CODE),
                data.getString(MedicineData.FIELD_BIZRNO))
            bundle.putParcelable(Container.CONTAINER_KEY, container)
            intent.putExtra(Container.CONTAINER_BUNDLE_KEY, bundle)
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.cancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _apiFunctions = null
        adapter = null
        if (ioScope.isActive) ioScope.cancel()
        if(mainScope.isActive) mainScope.cancel()
    }

    // Controlling Data flows
    private suspend fun performDataFetchTaskAsync(query: String): JSONObject? =
        withContext(Dispatchers.IO) {
            return@withContext apiFunctions.getPrdtMtrDetails(query, null, null)
        }

    override fun onItemClicked(prevPos: Int, nextPos: Int) {
        if(prevPos != -1)
            adapter!!.notifyItemChanged(prevPos)
        adapter!!.notifyItemChanged(nextPos)
        selectedPositionObserver.value = nextPos
    }

    companion object{
        private const val TAG = "SEARCH_ACTIVITY"
    }
}