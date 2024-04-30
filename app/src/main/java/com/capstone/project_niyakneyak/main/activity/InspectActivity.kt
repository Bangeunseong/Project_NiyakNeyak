package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.inspect_model.InspectData
import com.capstone.project_niyakneyak.data.inspect_model.UsageJointData
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
import com.google.api.Usage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.json.JSONObject

class InspectActivity: AppCompatActivity(), OnClickedOptionListener {
    // Params for View binding and adapters
    private var _binding: ActivityInspectBinding? = null
    private val binding get() = _binding!!
    private var medicineAdapter: CurrentMedicineAdapter? = null
    private var optionAdapter: OptionAdapter? = null

    // Params for firebase
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!

    // Query
    private var query: Query? = null

    // Inspection Results
    private val resultMap = mutableMapOf<String, JSONObject>()
    private var isInspected = false

    // Coroutine Scope
    private var _defaultScope: CoroutineScope? = null
    private val defaultScope get() = _defaultScope!!

    // BackPressed Callback
    private var callback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInspectBinding.inflate(layoutInflater)
        _defaultScope = CoroutineScope(Dispatchers.Default)
        setContentView(binding.root)

        binding.toolbar3.setTitle(R.string.action_main_inspect_medicine)
        setSupportActionBar(binding.toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        FirebaseFirestore.setLoggingEnabled(true)
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth


        if(firebaseAuth.currentUser != null){
            query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(MedicineData.COLLECTION_ID)
        } else {
            setResult(RESULT_CANCELED)
            finish()
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

        binding.contentRecyclerMedicine.setHasFixedSize(false)
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
                    Toast.makeText(this, "Data Fetching Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.contentRecyclerInspectOption.setHasFixedSize(true)
        binding.contentRecyclerInspectOption.layoutManager = LinearLayoutManager(this)

        binding.contentCreateResultDocumentBtn.setOnClickListener {
            defaultScope.launch {
                val documentSnapshot = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                    .collection(InspectData.COLLECTION_ID).document(InspectData.DOCUMENT_ID)
                val result1 = createDocumentAsyncForUsageJoint()
                val result2 = createDocumentAsync(OpenApiFunctions.GET_ELDERLY_ATTENTION_PRODUCT_LIST)
                val result3 = createDocumentAsync(OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST)
                val result4 = createDocumentAsync(OpenApiFunctions.GET_MEDICINE_CONSUME_DATE_ATTENTION_TABOO_LIST)
                val result5 = createDocumentAsync(OpenApiFunctions.GET_PREGNANT_WOMAN_TABOO_LIST)
                result1.await()?.forEach { data ->
                    firestore.runTransaction { transaction -> transaction.set(documentSnapshot.collection(data.typeName!!).document(), data) }
                }
                awaitAll(result2, result3, result4, result5).stream().forEach {
                    if(it != null){
                        for(data in it){
                            firestore.runTransaction { transaction -> transaction.set(documentSnapshot.collection(data.typeName!!).document(), data) }
                        }
                    }
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        callback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                optionAdapter!!.cancelAllActiveCoroutines()
                if(isInspected) setResult(RESULT_OK)
                else setResult(RESULT_CANCELED)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback as OnBackPressedCallback)
    }

    override fun onStart() {
        super.onStart()

        medicineAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        medicineAdapter?.stopListening()
    }

    override fun onOptionClicked(option: String, jsonObject: JSONObject?) {
        if(jsonObject != null){
            resultMap[option] = jsonObject
            isInspected = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_inspect, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                optionAdapter!!.cancelAllActiveCoroutines()
                if(isInspected) setResult(RESULT_OK)
                else setResult(RESULT_CANCELED)
                finish()
                true
            }
            R.id.menu_documents -> {
                TODO("Not yet Implemented")
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createDocumentAsyncForUsageJoint(): Deferred<List<UsageJointData>?> =
        defaultScope.async {
            var result: MutableList<UsageJointData>? = null
            if (resultMap.containsKey(OpenApiFunctions.GET_USAGE_JOINT_TABOO_LIST)) {
                val jsonObject = resultMap[OpenApiFunctions.GET_USAGE_JOINT_TABOO_LIST]
                if (jsonObject != null && !jsonObject.isNull("items")) {
                    val jsonArray = jsonObject.getJSONArray("items")
                    for (pos in 0 until jsonArray.length()) {
                        //TODO: Implement data
                        if (result == null) result = mutableListOf()
                        result.add(UsageJointData(
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_INGR_CODE),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_INGR_KOR_NAME),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_INGR_ENG_NAME),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_TYPE_NAME),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MIX_INGR),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_ITEM_SEQ),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_ITEM_NAME),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_ENTP_NAME),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MAIN_INGR),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MIXTURE_INGR_CODE),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MIXTURE_INGR_KOR_NAME),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MIXTURE_INGR_ENG_NAME),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MIXTURE_ITEM_SEQ),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MIXTURE_ITEM_NAME),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MIXTURE_ENTP_NAME),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MIXTURE_MAIN_INGR),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_NOTIFICATION_DATE),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_PROHBT_CONTENT),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_REMARK),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_ITEM_PERMIT_DATE),
                            jsonArray.getJSONObject(pos).getString(UsageJointData.FIELD_MIXTURE_ITEM_PERMIT_DATE)))
                    }
                }
            }
            return@async result
        }

    private fun createDocumentAsync(option: String): Deferred<List<InspectData>?> =
        defaultScope.async {
            var result: MutableList<InspectData>? = null
            when (option){
                OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST ->{
                    if(resultMap.containsKey(OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST)){
                        val jsonObject = resultMap[OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST]
                        if(jsonObject != null && !jsonObject.isNull("items")){
                            val jsonArray = jsonObject.getJSONArray("items")
                            for(pos in 0 until jsonArray.length()){
                                if(result == null) result = mutableListOf()
                                result.add(InspectData(
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CLASS_CODE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_TYPE_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_MIX_TYPE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_CODE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_ENG_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_ENG_NAME_FULL),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_MIX_INGR),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_FORM_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_SEQ),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_PERMIT_DATE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ENTP_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CHART),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CLASS_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ETC_OTC_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_MAIN_INGR),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_NOTIFICATION_DATE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_PROHIBIT_CONTENT),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_REMARK),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CHANGE_DATE)))
                            }
                        }
                    }
                }
                OpenApiFunctions.GET_PREGNANT_WOMAN_TABOO_LIST -> {
                    if(resultMap.containsKey(OpenApiFunctions.GET_PREGNANT_WOMAN_TABOO_LIST)){
                        val jsonObject = resultMap[OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST]
                        if(jsonObject != null && !jsonObject.isNull("items")){
                            val jsonArray = jsonObject.getJSONArray("items")
                            for(pos in 0 until jsonArray.length()){
                                if(result == null) result = mutableListOf()
                                result.add(InspectData(
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CLASS_CODE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_TYPE_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_MIX_TYPE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_CODE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_ENG_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_ENG_NAME_FULL),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_MIX_INGR),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_FORM_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_SEQ),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_PERMIT_DATE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ENTP_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CHART),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CLASS_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ETC_OTC_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_MAIN_INGR),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_NOTIFICATION_DATE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_PROHIBIT_CONTENT),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_REMARK),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CHANGE_DATE)))
                            }
                        }
                    }
                }
                OpenApiFunctions.GET_MEDICINE_CONSUME_DATE_ATTENTION_TABOO_LIST -> {
                    if(resultMap.containsKey(OpenApiFunctions.GET_MEDICINE_CONSUME_DATE_ATTENTION_TABOO_LIST)){
                        val jsonObject = resultMap[OpenApiFunctions.GET_SPECIFIC_AGE_GRADE_TABOO_LIST]
                        if(jsonObject != null && !jsonObject.isNull("items")){
                            val jsonArray = jsonObject.getJSONArray("items")
                            for(pos in 0 until jsonArray.length()){
                                if(result == null) result = mutableListOf()
                                result.add(InspectData(
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CLASS_CODE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_TYPE_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_MIX_TYPE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_CODE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_ENG_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_ENG_NAME_FULL),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_MIX_INGR),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_FORM_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_SEQ),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_PERMIT_DATE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ENTP_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CHART),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CLASS_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ETC_OTC_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_MAIN_INGR),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_NOTIFICATION_DATE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_PROHIBIT_CONTENT),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_REMARK),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CHANGE_DATE)))
                            }
                        }
                    }
                }
                OpenApiFunctions.GET_ELDERLY_ATTENTION_PRODUCT_LIST ->{
                    if(resultMap.containsKey(OpenApiFunctions.GET_ELDERLY_ATTENTION_PRODUCT_LIST)){
                        val jsonObject = resultMap[OpenApiFunctions.GET_ELDERLY_ATTENTION_PRODUCT_LIST]
                        if(jsonObject != null && !jsonObject.isNull("items")){
                            val jsonArray = jsonObject.getJSONArray("items")
                            for(pos in 0 until jsonArray.length()){
                                if(result == null) result = mutableListOf()
                                result.add(InspectData(
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CLASS_CODE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_TYPE_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_MIX_TYPE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_CODE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_ENG_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_INGR_ENG_NAME_FULL),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_MIX_INGR),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_FORM_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_SEQ),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ITEM_PERMIT_DATE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ENTP_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CHART),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CLASS_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_ETC_OTC_NAME),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_MAIN_INGR),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_NOTIFICATION_DATE),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_PROHIBIT_CONTENT),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FILED_REMARK),
                                    jsonArray.getJSONObject(pos).getString(InspectData.FIELD_CHANGE_DATE)))
                            }
                        }
                    }
                }
            }
            return@async result
        }


    companion object{
        private const val TAG = "Inspect_Activity"
    }
}