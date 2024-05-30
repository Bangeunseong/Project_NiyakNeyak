package com.capstone.project_niyakneyak.main.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.FragmentAlarmListBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.capstone.project_niyakneyak.main.activity.AlarmSettingActivity
import com.capstone.project_niyakneyak.main.adapter.AlarmAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.viewmodel.AlarmViewModel
import com.capstone.project_niyakneyak.main.listener.OnAlarmChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.toObject

/**
 * This Fragment is used for showing currently registered alarmList.
 * This Fragment implements [OnAlarmChangedListener] to update alarm data
 * by using [OnAlarmChangedListener.onItemClick], [OnAlarmChangedListener.onDelete]
 */
class AlarmFragment : Fragment(), OnAlarmChangedListener {
    // Field
    private var _binding: FragmentAlarmListBinding? = null
    private val binding get() = _binding!!
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var _viewModel: AlarmViewModel? = null
    private val viewModel get() = _viewModel!!

    private var adapter: AlarmAdapter? = null
    private var query: Query? = null

    private val loginProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK){
            viewModel.isSignedIn = true

            query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(Alarm.COLLECTION_ID)
                .orderBy(Alarm.FIELD_HOUR, Query.Direction.ASCENDING)
                .orderBy(Alarm.FIELD_MINUTE, Query.Direction.ASCENDING)

            query?.let {
                adapter = object: AlarmAdapter(it, this@AlarmFragment){
                    override fun onDataChanged() {
                        if(itemCount == 0) {
                            binding.contentTimeLeftBeforeAlarm.visibility = View.VISIBLE
                            binding.contentTimeTable.visibility = View.GONE
                        }
                        else {
                            binding.contentTimeLeftBeforeAlarm.visibility = View.GONE
                            binding.contentTimeTable.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(e: FirebaseFirestoreException) {
                        Snackbar.make(binding.root, "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
                    }
                }
                binding.contentTimeTable.adapter = adapter
            }
        }
    }
    private val alarmSettingProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            Toast.makeText(context, "Modified Alarm!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Modification Canceled", Toast.LENGTH_SHORT).show()
        }
    }
    private val alarmAddingProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            Toast.makeText(context, "Added Alarm!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Addition Canceled!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentAlarmListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewModel = ViewModelProvider(this)[AlarmViewModel::class.java]

        FirebaseFirestore.setLoggingEnabled(true)
        _firestore = Firebase.firestore
        _firebaseAuth = Firebase.auth

        if(firebaseAuth.currentUser != null){
            query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(Alarm.COLLECTION_ID)
                .orderBy(Alarm.FIELD_HOUR, Query.Direction.ASCENDING)
                .orderBy(Alarm.FIELD_MINUTE, Query.Direction.ASCENDING)
        }

        query?.let {
            adapter = object: AlarmAdapter(it, this@AlarmFragment){
                override fun onDataChanged() {
                    if(itemCount == 0) {
                        binding.contentTimeLeftBeforeAlarm.visibility = View.VISIBLE
                        binding.contentTimeTable.visibility = View.GONE
                    }
                    else {
                        binding.contentTimeLeftBeforeAlarm.visibility = View.GONE
                        binding.contentTimeTable.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Snackbar.make(binding.root, "Error: check logs for info.", Snackbar.LENGTH_LONG).show()
                }
            }
            binding.contentTimeTable.adapter = adapter
        }

        binding.contentTimeTable.setHasFixedSize(false)
        binding.contentTimeTable.layoutManager = LinearLayoutManager(activity)
        binding.contentTimeTable.addItemDecoration(HorizontalItemDecorator(10))
        binding.contentTimeTable.addItemDecoration(VerticalItemDecorator(20))

        binding.contentAlarmAdd.setOnClickListener {
            val intent = Intent(context, AlarmSettingActivity::class.java)
            alarmAddingProcessLauncher.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if(shouldStartSignIn()){
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("request_token", 0)
            loginProcessLauncher.launch(intent)
            return
        }

        // Start Listening Data changes from firebase when activity starts
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()

        // Stop Listening Data changes from firebase when activity stops
        adapter?.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
        _firestore = null
        _firebaseAuth = null
        adapter = null
    }

    override fun onDelete(snapshot: DocumentSnapshot) {
        val alarmRef = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(Alarm.COLLECTION_ID).document(snapshot.id)
        val medicationRef = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(MedicineData.COLLECTION_ID)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Warning!")
        builder.setMessage("Do you want to delete this timer?")
        builder.setPositiveButton("OK") { _: DialogInterface?, _: Int ->
            var alarm: Alarm?
            medicationRef.get().addOnSuccessListener {
                alarmRef.get().addOnSuccessListener { document ->
                    alarm = document.toObject<Alarm>()
                    if(alarm!!.isStarted) alarm!!.cancelAlarm(requireContext())
                    for(data in it.documents){
                        val medicineData = data.toObject<MedicineData>()
                        if(medicineData!!.alarmList.contains(alarm!!.alarmCode)){
                            firestore.runTransaction { transaction ->
                                transaction.update(medicationRef.document(medicineData.medsID.toString()), MedicineData.FIELD_ALARM_LIST_FB, FieldValue.arrayRemove(alarm!!.alarmCode))
                            }
                        }
                    }
                    alarmRef.delete()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to delete Alarm Code in Medicine Data", Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "Error Occurred!: $it")
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to fetch Medicine Data", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error Occurred!: $it")
            }
        }
        builder.setNegativeButton("CANCEL") { _: DialogInterface?, _: Int -> }
        builder.create().show()
    }

    override fun onItemClick(snapshot: DocumentSnapshot) {
        showAlarmSettingDialog(snapshot)
    }

    private fun showAlarmSettingDialog(snapshot: DocumentSnapshot) {
        val intent = Intent(context, AlarmSettingActivity::class.java)
        intent.putExtra("snapshot_id",snapshot.id)
        alarmSettingProcessLauncher.launch(intent)
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSignedIn && firebaseAuth.currentUser == null
    }

    companion object{
        private const val TAG = "ALARM_FRAGMENT"
        private const val DIALOG_TAG = "ALARM_DIALOG"
    }
}