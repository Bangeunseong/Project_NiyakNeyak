package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityInspectBinding
import com.capstone.project_niyakneyak.main.adapter.CurrentMedicineAdapter
import com.capstone.project_niyakneyak.main.adapter.OptionAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.etc.OpenApiFunctions
import com.capstone.project_niyakneyak.main.listener.OnClickedOptionListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class InspectActivity: AppCompatActivity(), OnClickedOptionListener {
    // Params for View binding and adapters
    private var _binding: ActivityInspectBinding? = null
    private val binding get() = _binding!!
    private var medicineAdapter: CurrentMedicineAdapter? = null
    private var optionAdapter: OptionAdapter? = null

    // Params for firebase
    private var firestore: FirebaseFirestore? = null
    private var firebaseAuth: FirebaseAuth? = null

    // Query
    private var query: Query? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInspectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = Firebase.firestore
        firebaseAuth = Firebase.auth

        if(firebaseAuth!!.currentUser != null){
            query = firestore!!.collection(UserAccount.COLLECTION_ID).document(firebaseAuth!!.currentUser!!.uid)
                .collection(MedicineData.COLLECTION_ID)
        }

        query?.let { query ->
            medicineAdapter = object: CurrentMedicineAdapter(query){
                override fun onDataChanged() {
                    if(itemCount == 0) {
                        binding.contentRecyclerMedicine.visibility = View.INVISIBLE
                        binding.contentRecyclerStateText.visibility = View.VISIBLE
                    }
                    else {
                        binding.contentRecyclerMedicine.visibility = View.VISIBLE
                        binding.contentRecyclerStateText.visibility = View.INVISIBLE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Snackbar.make(binding.root, "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
                }
            }
            binding.contentRecyclerMedicine.adapter = medicineAdapter
        }

        binding.contentRecyclerMedicine.layoutManager = LinearLayoutManager(this)
        binding.contentRecyclerMedicine.addItemDecoration(VerticalItemDecorator(10))
        binding.contentRecyclerMedicine.addItemDecoration(HorizontalItemDecorator(10))

        listOf(OpenApiFunctions.GET_USAGE_JOINT_TABOO_LIST, OpenApiFunctions.GET_ELDERLY_ATTENTION_PRODUCT_LIST, OpenApiFunctions.GET_MEDICINE_CONSUME_DATE_ATTENTION_TABOO_LIST,
            OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST, OpenApiFunctions.GET_PREGNANT_WOMAN_TABOO_LIST).let { options ->
            query?.let { queryForData ->
                queryForData.get().addOnSuccessListener {
                    val data = mutableListOf<MedicineData>()
                    for(document in it.documents){
                        val medsData = document.toObject<MedicineData>() ?: continue
                        data.add(medsData)
                    }
                    optionAdapter = object: OptionAdapter(options, data,this){}
                    binding.contentRecyclerInspectOption.adapter = optionAdapter
                }.addOnFailureListener {

                }
            }
        }
        binding.contentRecyclerInspectOption.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()

        medicineAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        medicineAdapter?.stopListening()
    }

    override fun onOptionClicked(option: String) {
        TODO("Not yet Implemented")
    }
}