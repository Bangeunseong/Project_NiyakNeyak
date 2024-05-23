package com.capstone.project_niyakneyak.main.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.FragmentCheckListBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.capstone.project_niyakneyak.main.adapter.CheckMedicineAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.viewmodel.CheckViewModel
import com.capstone.project_niyakneyak.main.listener.OnCheckedChecklistListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import java.util.Date

/**
 * This Fragment is used for showing daily Medication list by using [CheckFragment.adapter].
 * [CheckFragment.adapter] will be set by using [CheckMedicineAdapter]
 */

class CheckFragment : Fragment(), OnCheckedChecklistListener {
    private var _binding: FragmentCheckListBinding? = null
    private val binding get() = _binding!!
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!
    private var _viewModel: CheckViewModel? = null
    private val viewModel get() = _viewModel!!

    private var adapter: CheckMedicineAdapter? = null
    private var query: Query? = null

    private val loginProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            viewModel.isSignedIn = true

            query = getCurrentMedicineQuery()

            query?.let {
                adapter = object: CheckMedicineAdapter(it, this@CheckFragment){
                    override fun onDataChanged() {
                        if(itemCount == 0) {
                            binding.contentChecklistDescriptionText.visibility = View.VISIBLE
                            binding.contentChecklist.visibility = View.GONE
                        }
                        else {
                            binding.contentChecklistDescriptionText.visibility = View.GONE
                            binding.contentChecklist.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(e: FirebaseFirestoreException) {
                        Snackbar.make(binding.root, "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
                    }
                }
                binding.contentChecklist.adapter = adapter
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setting View Model
        _viewModel = ViewModelProvider(this)[CheckViewModel::class.java]

        // Setting Firestore and FirebaseAuth
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth

        if(firebaseAuth.currentUser != null){
            query = getCurrentMedicineQuery()
        }

        query?.let {
            adapter = object: CheckMedicineAdapter(it, this@CheckFragment){
                override fun onDataChanged() {
                    if(itemCount == 0) {
                        binding.contentChecklistDescriptionText.visibility = View.VISIBLE
                        binding.contentChecklist.visibility = View.GONE
                    }
                    else {
                        binding.contentChecklistDescriptionText.visibility = View.GONE
                        binding.contentChecklist.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Snackbar.make(binding.root, "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
                }
            }
            binding.contentChecklist.adapter = adapter
        }

        binding.contentChecklist.setHasFixedSize(false)
        binding.contentChecklist.layoutManager = LinearLayoutManager(context)
        binding.contentChecklist.addItemDecoration(HorizontalItemDecorator(10))
        binding.contentChecklist.addItemDecoration(VerticalItemDecorator(20))
    }

    override fun onStart() {
        super.onStart()
        if(shouldStartSignIn()){
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("request_token", 0)
            loginProcessLauncher.launch(intent)
            return
        }

        // Start Listening Data changes
        adapter?.setQuery(getCurrentMedicineQuery())
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        // Stop Listening Data changes
        adapter?.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _firebaseAuth = null
        _firestore = null
        _viewModel = null
        adapter = null
    }

    private fun getCurrentMedicineQuery(): Query {
        return firestore.collection(UserAccount.COLLECTION_ID)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(MedicineData.COLLECTION_ID)
            .where(
                Filter.or(
                    Filter.equalTo(MedicineData.FIELD_START_DATE_FB, null),
                    Filter.and(
                        Filter.lessThanOrEqualTo(MedicineData.FIELD_START_DATE_FB, Date(System.currentTimeMillis())),
                        Filter.greaterThanOrEqualTo(MedicineData.FIELD_END_DATE_FB, Date(System.currentTimeMillis())))
                )
            )
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSignedIn && firebaseAuth.currentUser == null
    }

    override fun onItemClicked(data: MedicineData, alarm: Alarm) {
        TODO("Not yet Implemented")
    }

    companion object {
        private const val TAG = "CHECK_FRAGMENT"
    }
}