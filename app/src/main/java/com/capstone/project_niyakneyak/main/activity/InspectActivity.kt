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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.coroutineContext

class InspectActivity: AppCompatActivity() {
    // Params for View binding and adapters
    private var _binding: ActivityInspectBinding? = null
    private val binding get() = _binding!!
    private var _medicineAdapter: CurrentMedicineAdapter? = null
    private val medicineAdapter get() = _medicineAdapter!!
    private var _optionAdapter: OptionAdapter? = null
    private val optionAdapter get() = _optionAdapter!!

    // Params for firebase
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!

    // Query
    private var query: Query? = null

    // Coroutines
    private var _ioScope: CoroutineScope? = null
    private val ioScope get() = _ioScope!!
    private var _mainScope: CoroutineScope? = null
    private val mainScope get() = _mainScope!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInspectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth
        _ioScope = CoroutineScope(Dispatchers.IO)
        _mainScope = CoroutineScope(Dispatchers.Main)

        if(firebaseAuth.currentUser != null){
            query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(MedicineData.COLLECTION_ID)
        }

        query?.let {
            _medicineAdapter = object: CurrentMedicineAdapter(it){
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
            OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST, OpenApiFunctions.GET_PREGNANT_WOMAN_TABOO_LIST).let {
                _optionAdapter = object: OptionAdapter(it){}
        }
        binding.contentRecyclerInspectOption.layoutManager = LinearLayoutManager(this)
    }
}