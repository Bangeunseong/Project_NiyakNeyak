package com.capstone.project_niyakneyak.main.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.DialogFragment
import com.capstone.project_niyakneyak.databinding.DialogFragmentSearchBinding
import com.capstone.project_niyakneyak.main.etc.OpenApiFunctions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SearchDialogFragment: DialogFragment() {
    private var _binding: DialogFragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var _apiFunctions: OpenApiFunctions? = null
    private val apiFunctions get() = _apiFunctions!!
    private var jsonObject: JSONObject? = null

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFragmentSearchBinding.inflate(inflater, container, false)
        _apiFunctions = OpenApiFunctions()

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
                            jsonObject = performDataFetchTaskAsync(query!!)
                        }
                    }
                    else{
                        ioScope.cancel()
                        ioScope.launch(Dispatchers.IO) {
                            jsonObject = performDataFetchTaskAsync(query!!)
                        }
                    }
                }
                return false
            }
        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (ioScope.isActive) ioScope.cancel()
    }


    private suspend fun performDataFetchTaskAsync(query: String): JSONObject? =
        withContext(Dispatchers.IO) {
            return@withContext apiFunctions.getPrdtMtrDetails(query, null, null)
        }
}