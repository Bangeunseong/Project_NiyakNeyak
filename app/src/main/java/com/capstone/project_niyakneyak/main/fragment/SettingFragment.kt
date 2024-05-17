package com.capstone.project_niyakneyak.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.FragmentSettingBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.capstone.project_niyakneyak.main.activity.OpenProfileActivity
import com.capstone.project_niyakneyak.main.activity.AppSettingActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingFragment: Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private var userId: String? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private val bluetoothManager: BluetoothManager by lazy {
        requireContext().getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    @SuppressLint("MissingPermission")
    private val bluetoothProcessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            if(requireActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PERMISSION_GRANTED){
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                pairedDevices?.forEach { device ->
                    // 연결된 기기 목록
                    Log.d(TAG, "Paired device: ${device.name}, ${device.address}, ${device.uuids}")
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = Firebase.auth
        firestore = Firebase.firestore

        // Initialize the ActivityResultLauncher with a callback function
        val appSettingsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Redirect to LoginActivity
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }

        binding.appSettings.setOnClickListener {
            val intentSetting = Intent(activity, AppSettingActivity::class.java)
            appSettingsLauncher.launch(intentSetting) // Launch using the new launcher
        }

        binding.profileButton.setOnClickListener {
            // 사용자 정보를 Firestore에서 조회
            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val userAccount = documentSnapshot.toObject<UserAccount>()
                    userAccount?.let {
                        val intent = Intent(activity, OpenProfileActivity::class.java).apply {
                            // 인텐트에 사용자 정보를 첨부
                            putExtra(UserAccount.REPRESENT_KEY, userAccount.idToken)
                        }
                        startActivity(intent)
                    }
                }.addOnFailureListener {
                    Snackbar.make(view, it.toString(), Snackbar.LENGTH_SHORT).show()
                }
        }

        binding.bluetoothSettings.setOnClickListener {
            if(bluetoothAdapter == null){
                Toast.makeText(context, "이 기기는 블루투스 기능을 지원하지 않습니다!", Toast.LENGTH_LONG).show()
            } else{
                if(requireActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PERMISSION_DENIED){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requireActivity().requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 101)
                    }
                } else{
                    if(bluetoothAdapter?.isEnabled == false) {
                        val btSettingIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        bluetoothProcessLauncher.launch(btSettingIntent)
                    }
                }
            }
        }

        binding.appSettings.setOnClickListener {
            val intentSetting = Intent(activity, AppSettingActivity::class.java)
            startActivity(intentSetting)
        }


        firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val userAccount = it.toObject<UserAccount>()
                    binding.yourCurrentNameTextview.text = userAccount!!.name
                    //binding.yourCurrentGenderTextview.text = userAccount?.gender ?: "성별 미설정"
                    Log.d(TAG, "YourCurrentNameTextView: ${userAccount.name}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Terrible Error Occurred: $it")
            }

        // Initialize AuthStateListener
        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            if (auth.currentUser == null) {
                // Redirect to LoginActivity if user is not authenticated
                val intent = Intent(activity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }
    override fun onStart() {
        super.onStart()

        firebaseAuth.addAuthStateListener(authStateListener)
        updateProfile()
        firestore.collection(UserAccount.COLLECTION_ID)
            .document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
                val userAccount = it.toObject<UserAccount>()
                if (userAccount != null) {
                    binding.yourCurrentNameTextview.text = userAccount.name
                }
            }
    }
    private fun updateProfile() {
        uiScope.launch {
            userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                val userDocument = firestore.collection("users").document(userId!!)
                userDocument.get()
                firestore.collection("users").document(userId!!).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            if (document.contains("profilePic") && document.getString("profilePic") != null){

                                Toast.makeText(activity, "yes", Toast.LENGTH_SHORT).show()
                                val profilePicUrl = document.getString("profilePic")
                                Glide.with(this@SettingFragment)
                                    .load(profilePicUrl)
                                    .into(binding.profileImageView)
                            } else {
                                Toast.makeText(activity, "no", Toast.LENGTH_SHORT).show()

                            }
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }
            } else {
                Log.d(TAG, "No such document")
            }

        }

    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    companion object { // 이 메소드로 외부에서 이 프래그먼트에 접근할 수 있음
        const val TAG = "SettingFragment"
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }
}
