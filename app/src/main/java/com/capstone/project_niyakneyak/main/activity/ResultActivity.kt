package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.data.inspect_model.InspectData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityResultBinding
import com.capstone.project_niyakneyak.main.adapter.InspectResultAdapter
import com.capstone.project_niyakneyak.main.adapter.UsageJointAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class ResultActivity: AppCompatActivity() {
    // Params of view binding
    private var _binding: ActivityResultBinding? = null
    private val binding get() = _binding!!

    // Params about Firebase
    private var _firestore: FirebaseFirestore? = null
    private var _firebaseAuth: FirebaseAuth? = null
    private val firestore get() = _firestore!!
    private val firebaseAuth get() = _firebaseAuth!!

    // Params for adapters
    private var _usageJointAdapter: UsageJointAdapter? = null
    private val usageJointAdapter get() = _usageJointAdapter!!
    private var _elderlyAttnAdapter: InspectResultAdapter? = null
    private val elderlyAttnAdapter get() = _elderlyAttnAdapter!!
    private var _spcAgeGradeAdapter: InspectResultAdapter? = null
    private val spcAgeGradeAdapter get() = _spcAgeGradeAdapter!!
    private var _durationAdapter: InspectResultAdapter? = null
    private val durationAdapter get() = _durationAdapter!!
    private var _prgntWmAdapter: InspectResultAdapter? = null
    private val prgntWmAdapter get() = _prgntWmAdapter!!

    // Query
    private var queryForUsgJnt: Query? = null
    private var queryForEldAttn: Query? = null
    private var queryForSpcAgeGrd: Query? = null
    private var queryForDuration: Query? = null
    private var queryForPrgntWm: Query? = null

    // BackPressed Callback
    private var callback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth
        _binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.contentResultToolbar.title = "Inspection Results"
        setSupportActionBar(binding.contentResultToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(firebaseAuth.currentUser == null){
            setResult(RESULT_CANCELED)
            finish()
        }

        queryForUsgJnt = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(InspectData.COLLECTION_ID).document(InspectData.DOCUMENT_ID).collection(InspectData.USAGE_JOINT_COLLECTION_ID)
        queryForEldAttn = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(InspectData.COLLECTION_ID).document(InspectData.DOCUMENT_ID).collection(InspectData.ELDERLY_ATTENTION_COLLECTION_ID)
        queryForSpcAgeGrd = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(InspectData.COLLECTION_ID).document(InspectData.DOCUMENT_ID).collection(InspectData.SPCF_AGE_GRADE_ATTEN_COLLECTION_ID)
        queryForDuration = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(InspectData.COLLECTION_ID).document(InspectData.DOCUMENT_ID).collection(InspectData.CONSUME_DURATION_ATTEN_COLLECTION_ID)
        queryForPrgntWm = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(InspectData.COLLECTION_ID).document(InspectData.DOCUMENT_ID).collection(InspectData.PRGNT_WOMEN_ATTEN_COLLECTION_ID)

        queryForUsgJnt?.let {
            _usageJointAdapter = object: UsageJointAdapter(it){
                override fun onDataChanged() {
                    if(itemCount == 0) {
                        binding.contentUsageJointView.visibility = View.GONE
                        binding.contentUsageJointNotFoundTxt.visibility = View.VISIBLE
                    } else {
                        binding.contentUsageJointView.visibility = View.VISIBLE
                        binding.contentUsageJointNotFoundTxt.visibility = View.GONE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.w(TAG, "Error Occurred!: $e")
                    Toast.makeText(this@ResultActivity, "Error: check logs for info.", Toast.LENGTH_LONG).show()
                }
            }
            binding.contentUsageJointView.adapter = usageJointAdapter
        }
        binding.contentUsageJointView.setHasFixedSize(false)
        binding.contentUsageJointView.layoutManager = LinearLayoutManager(this)
        binding.contentUsageJointView.addItemDecoration(VerticalItemDecorator(20))
        binding.contentUsageJointView.addItemDecoration(HorizontalItemDecorator(10))

        queryForEldAttn?.let {
            _elderlyAttnAdapter = object: InspectResultAdapter(it){
                override fun onDataChanged() {
                    if(itemCount == 0){
                        binding.contentElderlyAttnView.visibility = View.GONE
                        binding.contentElderlyAttnNotFoundTxt.visibility = View.VISIBLE
                    } else{
                        binding.contentElderlyAttnView.visibility = View.VISIBLE
                        binding.contentElderlyAttnNotFoundTxt.visibility = View.GONE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.w(TAG, "Error Occurred!: $e")
                    Toast.makeText(this@ResultActivity, "Error: check logs for info.", Toast.LENGTH_LONG).show()
                }
            }
            binding.contentElderlyAttnView.adapter = elderlyAttnAdapter
        }
        binding.contentElderlyAttnView.setHasFixedSize(false)
        binding.contentElderlyAttnView.layoutManager = LinearLayoutManager(this)
        binding.contentElderlyAttnView.addItemDecoration(VerticalItemDecorator(10))
        binding.contentElderlyAttnView.addItemDecoration(HorizontalItemDecorator(10))

        queryForSpcAgeGrd?.let {
            _spcAgeGradeAdapter = object: InspectResultAdapter(it){
                override fun onDataChanged() {
                    if(itemCount == 0){
                        binding.contentSpcfcAgeGradeAttnView.visibility = View.GONE
                        binding.contentSpcfcAgeGradeAttnNotFoundTxt.visibility = View.VISIBLE
                    } else{
                        binding.contentSpcfcAgeGradeAttnView.visibility = View.VISIBLE
                        binding.contentSpcfcAgeGradeAttnNotFoundTxt.visibility = View.GONE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.w(TAG, "Error Occurred!: $e")
                    Toast.makeText(this@ResultActivity, "Error: check logs for info.", Toast.LENGTH_LONG).show()
                }
            }
            binding.contentSpcfcAgeGradeAttnView.adapter = spcAgeGradeAdapter
        }
        binding.contentSpcfcAgeGradeAttnView.setHasFixedSize(false)
        binding.contentSpcfcAgeGradeAttnView.layoutManager = LinearLayoutManager(this)
        binding.contentSpcfcAgeGradeAttnView.addItemDecoration(VerticalItemDecorator(10))
        binding.contentSpcfcAgeGradeAttnView.addItemDecoration(HorizontalItemDecorator(10))

        queryForDuration?.let {
            _durationAdapter = object: InspectResultAdapter(it){
                override fun onDataChanged() {
                    if(itemCount == 0){
                        binding.contentConsumeDurationAttnView.visibility = View.GONE
                        binding.contentConsumeDurationAttnNotFoundTxt.visibility = View.VISIBLE
                    } else{
                        binding.contentConsumeDurationAttnView.visibility = View.VISIBLE
                        binding.contentConsumeDurationAttnNotFoundTxt.visibility = View.GONE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.w(TAG, "Error Occurred!: $e")
                    Toast.makeText(this@ResultActivity, "Error: check logs for info.", Toast.LENGTH_LONG).show()
                }
            }
            binding.contentConsumeDurationAttnView.adapter = durationAdapter
        }
        binding.contentConsumeDurationAttnView.setHasFixedSize(false)
        binding.contentConsumeDurationAttnView.layoutManager = LinearLayoutManager(this)
        binding.contentConsumeDurationAttnView.addItemDecoration(VerticalItemDecorator(10))
        binding.contentConsumeDurationAttnView.addItemDecoration(HorizontalItemDecorator(10))

        queryForPrgntWm?.let {
            _prgntWmAdapter = object: InspectResultAdapter(it){
                override fun onDataChanged() {
                    if(itemCount == 0){
                        binding.contentPrgntWomenAttnView.visibility = View.GONE
                        binding.contentPrgntWomenAttnNotFoundTxt.visibility = View.VISIBLE
                    } else{
                        binding.contentPrgntWomenAttnView.visibility = View.VISIBLE
                        binding.contentPrgntWomenAttnNotFoundTxt.visibility = View.GONE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.w(TAG, "Error Occurred!: $e")
                    Toast.makeText(this@ResultActivity, "Error: check logs for info.", Toast.LENGTH_LONG).show()
                }
            }
            binding.contentPrgntWomenAttnView.adapter = prgntWmAdapter
        }
        binding.contentPrgntWomenAttnView.setHasFixedSize(false)
        binding.contentPrgntWomenAttnView.layoutManager = LinearLayoutManager(this)
        binding.contentPrgntWomenAttnView.addItemDecoration(VerticalItemDecorator(10))
        binding.contentPrgntWomenAttnView.addItemDecoration(HorizontalItemDecorator(10))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        callback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback as OnBackPressedCallback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()

        usageJointAdapter.startListening()
        elderlyAttnAdapter.startListening()
        spcAgeGradeAdapter.startListening()
        durationAdapter.startListening()
        prgntWmAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()

        usageJointAdapter.stopListening()
        elderlyAttnAdapter.stopListening()
        spcAgeGradeAdapter.stopListening()
        durationAdapter.stopListening()
        prgntWmAdapter.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()

        _firestore = null
        _firebaseAuth = null
    }

    companion object{
        private const val TAG = "RESULT_ACTIVITY"
    }
}