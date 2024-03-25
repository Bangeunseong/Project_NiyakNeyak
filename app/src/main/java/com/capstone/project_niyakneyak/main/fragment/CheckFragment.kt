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
import com.capstone.project_niyakneyak.data.medication_model.MedsData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.FragmentCheckListBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.capstone.project_niyakneyak.main.adapter.CheckAlarmAdapter
import com.capstone.project_niyakneyak.main.viewmodel.CheckViewModel
import com.capstone.project_niyakneyak.main.listener.OnCheckedMedicationListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import java.util.Calendar

/**
 * This Fragment is used for showing daily Medication list by using [CheckFragment.adapter].
 * [CheckFragment.adapter] will be set by using [CheckAlarmAdapter]
 */

class CheckFragment : Fragment(), OnCheckedMedicationListener {
    private lateinit var binding: FragmentCheckListBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var viewModel: CheckViewModel

    private var adapter: CheckAlarmAdapter? = null
    private var query: Query? = null

    private val loginProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK){
            viewModel.isSignedIn = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCheckListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CheckViewModel::class.java]

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        firestore = Firebase.firestore
        firebaseAuth = Firebase.auth

        if(firebaseAuth.currentUser != null){
            query = getCurrentDateQuery(calendar)
        }

        query?.let {
            adapter = object: CheckAlarmAdapter(it, this@CheckFragment){
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
    }

    override fun onStart() {
        super.onStart()
        if(shouldStartSignIn()){
            val intent = Intent(activity, LoginActivity::class.java)
            loginProcessLauncher.launch(intent)
        }

        // Start Listening Data changes
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        // Stop Listening Data changes
        adapter?.stopListening()
    }

    private fun getCurrentDateQuery(calendar: Calendar): Query? {
        var query: Query? = null
        when(calendar.get(Calendar.DAY_OF_WEEK)){
            Calendar.SUNDAY -> {
                query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID).where(Filter.and(
                        Filter.equalTo(Alarm.FIELD_IS_STARTED, true),
                        Filter.equalTo(Alarm.FIELD_IS_SUNDAY, true))
                    ).orderBy(Alarm.FIELD_HOUR).orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.MONDAY -> {
                query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID).where(Filter.and(
                        Filter.equalTo(Alarm.FIELD_IS_STARTED, true),
                        Filter.equalTo(Alarm.FIELD_IS_MONDAY, true))
                    ).orderBy(Alarm.FIELD_HOUR).orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.TUESDAY -> {
                query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID).where(Filter.and(
                        Filter.equalTo(Alarm.FIELD_IS_STARTED, true),
                        Filter.equalTo(Alarm.FIELD_IS_TUESDAY, true))
                    ).orderBy(Alarm.FIELD_HOUR).orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.WEDNESDAY -> {
                query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID).where(Filter.and(
                        Filter.equalTo(Alarm.FIELD_IS_STARTED, true),
                        Filter.equalTo(Alarm.FIELD_IS_WEDNESDAY, true))
                    ).orderBy(Alarm.FIELD_HOUR).orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.THURSDAY -> {
                query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID).where(Filter.and(
                        Filter.equalTo(Alarm.FIELD_IS_STARTED, true),
                        Filter.equalTo(Alarm.FIELD_IS_THURSDAY, true))
                    ).orderBy(Alarm.FIELD_HOUR).orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.FRIDAY -> {
                query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID).where(Filter.and(
                        Filter.equalTo(Alarm.FIELD_IS_STARTED, true),
                        Filter.equalTo(Alarm.FIELD_IS_FRIDAY, true))
                    ).orderBy(Alarm.FIELD_HOUR).orderBy(Alarm.FIELD_MINUTE)
            }
            Calendar.SATURDAY -> {
                query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(Alarm.COLLECTION_ID).where(Filter.and(
                        Filter.equalTo(Alarm.FIELD_IS_STARTED, true),
                        Filter.equalTo(Alarm.FIELD_IS_SATURDAY, true))
                    ).orderBy(Alarm.FIELD_HOUR).orderBy(Alarm.FIELD_MINUTE)
            }
        }
        return query
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSignedIn && firebaseAuth.currentUser == null
    }

    override fun onItemClicked(data: MedsData) {
        TODO("Not yet Implemented")
    }

    companion object {
        private const val TAG = "CHECK_FRAGMENT"
    }
}