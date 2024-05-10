package com.capstone.project_niyakneyak.main.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.data.inspect_model.InspectData
import com.capstone.project_niyakneyak.data.inspect_model.UsageJointData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityResultBinding
import com.capstone.project_niyakneyak.main.adapter.UsageJointAdapter
import com.google.android.material.snackbar.Snackbar
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

    // Query
    private var queryForUsgJnt: Query? = null
    private var queryForEldAttn: Query? = null
    private var queryForSpcAgeGrd: Query? = null
    private var queryForDuration: Query? = null
    private var queryForPrgntWm: Query? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth
        _binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    }
                    else {
                        binding.contentUsageJointView.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Toast.makeText(this@ResultActivity, "Error: check logs for info.", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        _firestore = null
        _firebaseAuth = null
    }
}