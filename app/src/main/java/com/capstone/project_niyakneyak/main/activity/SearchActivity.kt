package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.capstone.project_niyakneyak.databinding.ActivitySearchBinding
import com.capstone.project_niyakneyak.main.adapter.MedicineListAdapter
import com.capstone.project_niyakneyak.main.etc.OpenApiFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SearchActivity: AppCompatActivity() {
    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!
    private var _apiFunctions: OpenApiFunctions? = null
    private val apiFunctions get() = _apiFunctions!!
    private var adapter: MedicineListAdapter? = null
    private var jsonObject: JSONObject? = null

    // Using Coroutine for data fetch process in background thread
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting View Binding and Api Function class
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        _apiFunctions = OpenApiFunctions()
        setContentView(binding.root)

        binding.searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                binding.searchBar.isSubmitButtonEnabled = newText != null && newText.length >= 2
                return false
            }

            //TODO: Think about coroutine behavior
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!TextUtils.isEmpty(query)){
                    if(!ioScope.isActive){
                        ioScope.launch {
                            binding.loadingLayout.visibility = View.VISIBLE
                            jsonObject = performDataFetchTaskAsync(query!!)
                            binding.loadingLayout.visibility = View.GONE
                        }
                    }
                    else{
                        ioScope.cancel()
                        ioScope.launch(Dispatchers.IO) {
                            binding.loadingLayout.visibility = View.VISIBLE
                            jsonObject = performDataFetchTaskAsync(query!!)
                            binding.loadingLayout.visibility = View.GONE
                        }
                    }
                }
                return false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (ioScope.isActive) ioScope.cancel()
    }

    private suspend fun performDataFetchTaskAsync(query: String): JSONObject? =
        withContext(Dispatchers.IO) {
            return@withContext apiFunctions.getPrdtMtrDetails(query, null, null)
        }
}