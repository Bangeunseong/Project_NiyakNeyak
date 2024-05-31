package com.capstone.project_niyakneyak.main.activity

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.medication_model.Container
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.databinding.ActivitySearchBinding
import com.capstone.project_niyakneyak.main.adapter.MedicineListAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.etc.OpenApiFunctions
import com.capstone.project_niyakneyak.main.listener.OnCheckedSearchItemListener
import com.capstone.project_niyakneyak.main.viewmodel.SearchActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private var _viewModel: SearchActivityViewModel? = null
    private val viewModel get() = _viewModel!!
    private var adapter: MedicineListAdapter? = null
    private var jsonObject: JSONObject? = null

    // Using Coroutine for data fetch process in background thread
    private var _ioScope: CoroutineScope? = null
    private val ioScope get() = _ioScope!!
    private var _mainScope: CoroutineScope? = null
    private val mainScope get() = _mainScope!!
    private val channel = Channel<String>()
    private val job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting View Binding, Api Function class, and Empty Data adapter
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        _apiFunctions = OpenApiFunctions()
        _viewModel = ViewModelProvider(this)[SearchActivityViewModel::class.java]
        _ioScope = CoroutineScope(Dispatchers.IO)
        _mainScope = CoroutineScope(Dispatchers.Main)
        adapter = MedicineListAdapter(JSONArray(), this)
        setContentView(binding.root)

        binding.toolbar4.setTitle(R.string.toolbar_search_activity)
        binding.toolbar4.setTitleTextAppearance(this, R.style.ToolbarTextAppearance)
        setSupportActionBar(binding.toolbar4)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar4.navigationIcon?.mutate().let { icon ->
            icon?.setTint(Color.WHITE)
            binding.toolbar4.navigationIcon = icon
        }

        // Observe Data changes of radio button selected position
        viewModel.searchQueryObserver.observe(this){
            // Refresh Position LiveData and Page position
            viewModel.currentPage = 1
            viewModel.selectedPositionObserver.value = -1

            // Show Loading Layout and Hide Result Page
            binding.loadingLayout.visibility = View.VISIBLE
            binding.searchResultNotFound.visibility = View.GONE

            CoroutineScope(Dispatchers.Default + job).launch{
                // Start Fetching Data by using API function
                ioScope.launch{
                    jsonObject = performDataFetchTaskAsync(it!!, viewModel.currentPage, viewModel.currentNumOfRows)
                    Log.w(TAG, jsonObject.toString())
                    if(jsonObject != null) channel.send("Success")
                    else channel.send("Failed")
                }

                // Start Setting Adapter View and Refresh page count
                mainScope.launch {
                    val msg = channel.receive()
                    if(msg == "Success"){
                        try{
                            val jsonArray = jsonObject!!.getJSONObject("body").getJSONArray("items")
                            viewModel.totalItemCount = jsonObject!!.getJSONObject("body").getInt("totalCount")
                            binding.searchCurrentPage.text = String.format("${viewModel.currentPage} / ${viewModel.remainPage}")
                            adapter!!.setJSONArray(jsonArray)
                        }catch (exception: JSONException){
                            Log.w(TAG, "Error Occurred: $it")
                            adapter!!.setJSONArray(JSONArray())
                        }
                    } else Log.w(TAG, "Items not Found!")

                    // Change Layout Statement
                    binding.loadingLayout.visibility = View.GONE
                    if(adapter!!.itemCount < 1) {
                        binding.searchResultAdapterLayout.visibility = View.GONE
                        binding.searchPageConvertBtnLayout.visibility = View.GONE
                        binding.searchResultNotFound.visibility = View.VISIBLE
                    }
                    else {
                        binding.searchResultAdapterLayout.visibility = View.VISIBLE
                        binding.searchPageConvertBtnLayout.visibility = View.VISIBLE
                        binding.searchResultNotFound.visibility = View.GONE
                    }
                }
            }
        }

        viewModel.selectedPositionObserver.observe(this) {
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
                    viewModel.searchQueryObserver.value = query
                return true
            }
        })
        binding.searchPrevPageBtn.setOnClickListener {
            if(viewModel.currentPage <= 1) return@setOnClickListener

            // Refresh Position LiveData and move page position backwards
            viewModel.currentPage--
            viewModel.selectedPositionObserver.value = -1

            // Show Loading Layout and Hide Result Page
            binding.loadingLayout.visibility = View.VISIBLE
            binding.searchResultNotFound.visibility = View.GONE

            CoroutineScope(Dispatchers.Default + job).launch {
                // Start Fetching Data by using API function
                ioScope.launch {
                    jsonObject = performDataFetchTaskAsync(
                        viewModel.searchQueryObserver.value!!,
                        viewModel.currentPage,
                        viewModel.currentNumOfRows
                    )
                    Log.w(TAG, jsonObject.toString())
                    if (jsonObject != null) channel.send("Success")
                    else channel.send("Failed")
                }

                // Start Setting Adapter View and Refresh page count
                mainScope.launch {
                    val msg = channel.receive()
                    if (msg == "Success") {
                        try {
                            val jsonArray = jsonObject!!.getJSONObject("body").getJSONArray("items")
                            binding.searchCurrentPage.text =
                                String.format("${viewModel.currentPage} / ${viewModel.remainPage}")
                            adapter!!.setJSONArray(jsonArray)
                        } catch (exception: JSONException) {
                            Log.w(TAG, "Error Occurred: $it")
                            adapter!!.setJSONArray(JSONArray())
                        }
                    } else Log.w(TAG, "Items not Found!")

                    // Change Layout Statement
                    binding.loadingLayout.visibility = View.GONE
                }
            }
        }

        binding.searchNextPageBtn.setOnClickListener {
            if(viewModel.currentPage >= viewModel.remainPage) return@setOnClickListener

            // Refresh Position LiveData and move page position backwards
            viewModel.currentPage++
            viewModel.selectedPositionObserver.value = -1

            // Show Loading Layout and Hide Result Page
            binding.loadingLayout.visibility = View.VISIBLE
            binding.searchResultNotFound.visibility = View.GONE

            CoroutineScope(Dispatchers.Default + job).launch {
                // Start Fetching Data by using API function
                ioScope.launch {
                    jsonObject = performDataFetchTaskAsync(
                        viewModel.searchQueryObserver.value!!,
                        viewModel.currentPage,
                        viewModel.currentNumOfRows
                    )
                    Log.w(TAG, jsonObject.toString())
                    if (jsonObject != null) channel.send("Success")
                    else channel.send("Failed")
                }.join()

                // Start Setting Adapter View and Refresh page count
                mainScope.launch {
                    val msg = channel.receive()
                    if (msg == "Success") {
                        try {
                            val jsonArray = jsonObject!!.getJSONObject("body").getJSONArray("items")
                            binding.searchCurrentPage.text =
                                String.format("${viewModel.currentPage} / ${viewModel.remainPage}")
                            adapter!!.setJSONArray(jsonArray)
                        } catch (exception: JSONException) {
                            Log.w(TAG, "Error Occurred: $it")
                            adapter!!.setJSONArray(JSONArray())
                        }
                    } else Log.w(TAG, "Items not Found!")

                    // Change Layout Statement
                    binding.loadingLayout.visibility = View.GONE
                }
            }
        }

        binding.submit.setOnClickListener {
            // Exception Handling
            if(jsonObject == null){
                setResult(RESULT_CANCELED)
                finish()
            }

            val bundle = Bundle()
            val data = jsonObject!!.getJSONObject("body").getJSONArray("items").getJSONObject(viewModel.selectedPositionObserver.value!!)
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
        if(ioScope.isActive) ioScope.cancel()
        if(mainScope.isActive) mainScope.cancel()
        _binding = null
        _apiFunctions = null
        adapter = null
        _viewModel = null
        _ioScope = null
        _mainScope = null
    }

    // Controlling Data flows
    private suspend fun performDataFetchTaskAsync(query: String, pageNo: Int?, numOfRows: Int?): JSONObject? =
        withContext(Dispatchers.IO) {
            return@withContext apiFunctions.getPrdtMtrDetails(query, pageNo, numOfRows)
        }

    override fun onItemClicked(prevPos: Int, nextPos: Int) {
        if(prevPos != -1)
            adapter!!.notifyItemChanged(prevPos)
        adapter!!.notifyItemChanged(nextPos)
        viewModel.selectedPositionObserver.value = nextPos
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }
            // 다른 메뉴 아이템 처리
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object{
        private const val TAG = "SEARCH_ACTIVITY"
    }
}