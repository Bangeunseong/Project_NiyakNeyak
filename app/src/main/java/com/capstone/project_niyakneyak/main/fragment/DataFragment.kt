package com.capstone.project_niyakneyak.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.data.patient_model.MedsData
import com.capstone.project_niyakneyak.databinding.FragmentDataListBinding
import com.capstone.project_niyakneyak.main.activity.DataSettingActivity
import com.capstone.project_niyakneyak.main.adapter.MedicationAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.viewmodel.DataViewModel
import com.capstone.project_niyakneyak.main.listener.OnMedicationChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * This Fragment is used for showing Medication info. by using [DataFragment.adapter].
 * [DataFragment.adapter] will be set by using [MedicationAdapter]
 */
class DataFragment : Fragment(), OnMedicationChangedListener {
    private lateinit var firestore: FirebaseFirestore
    private var query: Query? = null

    private lateinit var binding: FragmentDataListBinding
    private lateinit var viewModel: DataViewModel
    private var adapter: MedicationAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentDataListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

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
            val intent = Intent(context, DataSettingActivity::class.java)
            startActivity(intent)
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
        return !viewModel.isSignedIn && Firebase.auth.currentUser == null
    }

    //<-- Do actions, when adapter component interacted with user -->
    override fun onModifyBtnClicked(target: DocumentSnapshot) {
        val intent = Intent(context, DataSettingActivity::class.java)
        intent.putExtra("snapshot_id", target.id)
        startActivity(intent)
    }
    override fun onDeleteBtnClicked(target: DocumentSnapshot) {
        //TODO: Need to erase timer also!!
        val medicationsRef = firestore.collection("medications")
        medicationsRef.document(target.id).delete()
    }

    companion object {
        private const val TAG = "DATA_FRAGMENT"
    }
}