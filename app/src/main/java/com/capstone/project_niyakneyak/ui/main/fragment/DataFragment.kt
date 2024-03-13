package com.capstone.project_niyakneyak.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.databinding.FragmentDataListBinding
import com.capstone.project_niyakneyak.ui.main.adapter.MedicationAdapter
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataListViewModel
import com.capstone.project_niyakneyak.ui.main.listener.OnDialogActionListener
import com.capstone.project_niyakneyak.ui.main.listener.OnMedicationChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * This Fragment is used for showing Medication info. by using [DataFragment.adapter].
 * [DataFragment.adapter] will be set by using [MedicationAdapter]
 */
class DataFragment : Fragment(), OnMedicationChangedListener, OnDialogActionListener {
    private lateinit var firestore: FirebaseFirestore
    private var query: Query? = null

    private lateinit var binding: FragmentDataListBinding
    private lateinit var viewModel: DataListViewModel
    private var adapter: MedicationAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentDataListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        viewModel = ViewModelProvider(this)[DataListViewModel::class.java]

        // Firebase Stuffs
        FirebaseFirestore.setLoggingEnabled(true)
        firestore = Firebase.firestore
        query = firestore.collection("medications")
            .orderBy(MedsData.FIELD_NAME, Query.Direction.ASCENDING)

        // RecyclerAdapter for Medication Info.
        query?.let {
            adapter = object: MedicationAdapter(it, this@DataFragment){
                override fun onDataChanged() {
                    if(itemCount == 0) {
                        binding.contentMainGuide.visibility = View.VISIBLE
                        binding.contentMainMeds.visibility = View.GONE
                    }
                    else {
                        binding.contentMainGuide.visibility = View.GONE
                        binding.contentMainMeds.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Snackbar.make(binding.root, "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
                }
            }
            binding.contentMainMeds.adapter = adapter
        }
        binding.contentMainMeds.setHasFixedSize(false)
        binding.contentMainMeds.layoutManager = LinearLayoutManager(context)
        binding.contentMainMeds.addItemDecoration(VerticalItemDecorator(20))
        binding.contentMainMeds.addItemDecoration(HorizontalItemDecorator(10))

        // Add button about Medication Info.
        binding.contentMainAdd.setOnClickListener {
            TODO("Not yet Implemented")
        }
    }

    override fun onStart() {
        super.onStart()
        //TODO: Add Authentication Step
        //if(shouldStartSignIn()){ return }

        // Start Listening Data changes from firebase when activity starts
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        // Stop Listening Data changes from firebase when activity stops
        adapter?.stopListening()
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSigningIn && Firebase.auth.currentUser == null
    }

    // Do actions, when dialog called and data has changed
    override fun onAddedMedicationData(data: MedsData) {
        val medicationsRef = firestore.collection("medications")
        medicationsRef.add(data)
            .addOnSuccessListener {
                Log.w(TAG, "Medication Data added!")
            }.addOnFailureListener {
                Log.w(TAG, "Add Medication Data failed")
            }
    }
    override fun onModifiedMedicationData(snapshotID: String, changed: MedsData) {
        val medicationsRef = firestore.collection("medications").document(snapshotID)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(medicationsRef)
            val alarms = snapshot.data?.get(MedsData.FIELD_ALARMS) as ArrayList<*>

            transaction.update(medicationsRef,
                mapOf(MedsData.FIELD_NAME to changed.medsName,
                    MedsData.FIELD_DETAIL to changed.medsDetail,
                    MedsData.FIELD_START_DATE to changed.medsStartDate,
                    MedsData.FIELD_END_DATE to changed.medsEndDate))
            alarms.stream().forEach {
                if(!changed.alarms.contains(it))
                    transaction.update(medicationsRef, MedsData.FIELD_ALARMS, FieldValue.arrayRemove(it))
            }
            changed.alarms.forEach {
                if(alarms.contains(it))
                    transaction.update(medicationsRef, MedsData.FIELD_ALARMS, FieldValue.arrayUnion(it))
            }
            null
        }.addOnSuccessListener {
            Log.w(TAG, "Data Modification Success")
        }.addOnFailureListener {
            Log.w(TAG, "Data Modification Failed")
        }
    }

    //<-- Do actions, when adapter component interacted with user -->
    override fun onModifyBtnClicked(target: DocumentSnapshot) {

    }
    override fun onDeleteBtnClicked(target: DocumentSnapshot) {
        val medicationsRef = firestore.collection("medications")
        medicationsRef.document(target.id).delete()
    }

    companion object {
        private const val TAG = "DATA_FRAGMENT"
        private const val DIALOG_TAG = "DATA_DIALOG"
    }
}