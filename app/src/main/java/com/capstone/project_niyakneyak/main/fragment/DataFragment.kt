package com.capstone.project_niyakneyak.main.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import com.capstone.project_niyakneyak.data.inspect_model.InspectData
import com.capstone.project_niyakneyak.data.medication_model.MedicineData
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.FragmentDataListBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.capstone.project_niyakneyak.main.activity.DataSettingActivity
import com.capstone.project_niyakneyak.main.activity.InspectActivity
import com.capstone.project_niyakneyak.main.adapter.MedicationAdapter
import com.capstone.project_niyakneyak.main.decorator.HorizontalItemDecorator
import com.capstone.project_niyakneyak.main.decorator.VerticalItemDecorator
import com.capstone.project_niyakneyak.main.etc.Filters
import com.capstone.project_niyakneyak.main.viewmodel.DataViewModel
import com.capstone.project_niyakneyak.main.listener.OnMedicationChangedListener
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeDrawable.BadgeGravity
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

@ExperimentalBadgeUtils /**
 * This Fragment is used for showing Medication info. by using [DataFragment.adapter].
 * [DataFragment.adapter] will be set by using [MedicationAdapter]
 */
class DataFragment : Fragment(), OnMedicationChangedListener, FilterDialogFragment.FilterListener {
    private var _firebaseAuth: FirebaseAuth? = null
    private val firebaseAuth get() = _firebaseAuth!!
    private var _firestore: FirebaseFirestore? = null
    private val firestore get() = _firestore!!
    private var query: Query? = null

    private var _binding: FragmentDataListBinding? = null
    private val binding get() = _binding!!
    private var _viewModel: DataViewModel? = null
    private val viewModel get() = _viewModel!!
    private var adapter: MedicationAdapter? = null

    // Intent Launchers for Login Process
    private val loginProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            viewModel.isSignedIn = true
        }
    }
    private val dataProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            viewModel.isChanged = true
            binding.contentMainInspect.setImageResource(R.drawable.ic_search_alert_icon)
            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(InspectData.COLLECTION_ID).document(InspectData.PARAM_CHANGE_DOCUMENT_ID).set(hashMapOf("changed" to false))
            Toast.makeText(context, "Data Saved!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Data Save Canceled!", Toast.LENGTH_SHORT).show()
        }
    }
    private val inspectProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            viewModel.isChanged = false
            binding.contentMainInspect.setImageResource(R.drawable.ic_search_icon)
            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(InspectData.COLLECTION_ID).document(InspectData.PARAM_CHANGE_DOCUMENT_ID).set(hashMapOf("changed" to false))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentDataListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        _viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        // Firebase Stuffs
        FirebaseFirestore.setLoggingEnabled(true)
        _firebaseAuth = Firebase.auth
        _firestore = Firebase.firestore

        if(firebaseAuth.currentUser != null){
            query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(MedicineData.COLLECTION_ID)
                .orderBy(MedicineData.FIELD_ITEM_NAME_FB, Query.Direction.ASCENDING)

            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .collection(InspectData.COLLECTION_ID).document(InspectData.PARAM_CHANGE_DOCUMENT_ID).get()
                .addOnSuccessListener {
                    viewModel.isChanged = it.data!!["changed"] as Boolean
                    if(viewModel.isChanged) binding.contentMainInspect.setImageResource(R.drawable.ic_search_alert_icon)
                    else binding.contentMainInspect.setImageResource(R.drawable.ic_search_icon)
                }.addOnFailureListener {
                    viewModel.isChanged = false
                    binding.contentMainInspect.setImageResource(R.drawable.ic_search_icon)
                }
        }

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
            dataProcessLauncher.launch(intent)
        }

        binding.contentMainInspect.setOnClickListener {
            val intent = Intent(context, InspectActivity::class.java)
            intent.putExtra("isChanged", viewModel.isChanged)
            inspectProcessLauncher.launch(intent)
        }

        binding.contentFilterOption.setOnClickListener {
            val dialogFragment = FilterDialogFragment()
            dialogFragment.setStyle(STYLE_NORMAL, R.style.DialogFragmentStyle)
            dialogFragment.show(parentFragmentManager, "FILTER_DIALOG_FRAGMENT")
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
        _firestore = null
        _firebaseAuth = null
        _viewModel = null
        _binding = null
        adapter = null
    }

    private fun shouldStartSignIn(): Boolean {
        return !viewModel.isSignedIn && firebaseAuth.currentUser == null
    }

    //<-- Do actions, when adapter component interacted with user -->
    override fun onModifyBtnClicked(target: DocumentSnapshot) {
        val intent = Intent(context, DataSettingActivity::class.java)
        intent.putExtra("snapshot_id", target.id)
        dataProcessLauncher.launch(intent)
    }
    override fun onDeleteBtnClicked(target: DocumentSnapshot) {
        val alarmList = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(Alarm.COLLECTION_ID)
            .whereArrayContains(Alarm.FIELD_MEDICATION_LIST, target.id.toInt())
        val medicationRef = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(MedicineData.COLLECTION_ID).document(target.id)
        val alarmRef = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(Alarm.COLLECTION_ID)

        alarmList.get().addOnSuccessListener { querySnapshot ->
            val alarms = querySnapshot.documents
            firestore.runTransaction { transaction ->
                for(snapshotId in alarms){
                    val alarm = transaction.get(alarmRef.document(snapshotId.id)).toObject<Alarm>()
                    if(alarm!!.medsList.size <= 1){
                        transaction.update(alarmRef.document(snapshotId.id), Alarm.FIELD_IS_STARTED, false)
                        alarm.cancelAlarm(requireContext())
                    }
                    transaction.update(alarmRef.document(snapshotId.id), Alarm.FIELD_MEDICATION_LIST, FieldValue.arrayRemove(target.id.toInt()))
                }
                transaction.delete(medicationRef)
            }.addOnSuccessListener {
                Log.w(TAG, "Successfully Updated Alarm Data")
            }.addOnFailureListener {
                Log.w(TAG, "Failed to Update Alarm Data")
                FirebaseFirestoreException.Code.ABORTED
            }
        }.addOnFailureListener {
            Log.w(TAG, "Failed to Delete Medication ID in Alarm Object")
            FirebaseFirestoreException.Code.ABORTED
        }
    }

    override fun onFilter(filters: Filters) {
        val query = firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
            .collection(MedicineData.COLLECTION_ID)
        if(filters.hasSortBy()){
            when(filters.sortBy){
                MedicineData.FIELD_ITEM_NAME_FB->query.orderBy(MedicineData.FIELD_ITEM_NAME_FB, filters.sortDirection)
                MedicineData.FIELD_ENPT_NAME_FB->query.orderBy(MedicineData.FIELD_ENPT_NAME_FB, filters.sortDirection)
                MedicineData.FIELD_TIME_STAMP_FB->query.orderBy(MedicineData.FIELD_TIME_STAMP_FB)
            }
        }
        if(filters.hasStartDate() && filters.hasEndDate()){
            query.where(Filter.and(Filter.greaterThanOrEqualTo(MedicineData.FIELD_START_DATE_FB, SimpleDateFormat("yyyyMMdd", Locale.KOREAN).parse(filters.startDate!!)),
                Filter.lessThanOrEqualTo(MedicineData.FIELD_END_DATE_FB, SimpleDateFormat("yyyyMMdd", Locale.KOREAN).parse(filters.endDate!!))))
        }else if(filters.hasStartDate()){
            query.where(Filter.greaterThanOrEqualTo(MedicineData.FIELD_START_DATE_FB, SimpleDateFormat("yyyyMMdd", Locale.KOREAN).parse(filters.startDate!!)))
        }else if(filters.hasEndDate()){
            Filter.lessThanOrEqualTo(MedicineData.FIELD_END_DATE_FB, SimpleDateFormat("yyyyMMdd", Locale.KOREAN).parse(filters.endDate!!))
        }

        viewModel.filters = filters
        adapter?.setQuery(query)
    }

    companion object {
        private const val TAG = "DATA_FRAGMENT"
    }


}