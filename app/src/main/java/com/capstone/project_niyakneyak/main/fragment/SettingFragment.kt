package com.capstone.project_niyakneyak.main.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private var userId: String? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)


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
                if (result.resultCode == Activity.RESULT_OK) {
                    // Redirect to LoginActivity
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    activity?.finish()
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
                activity?.finish()
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

                                Toast.makeText(getActivity(), "yes", Toast.LENGTH_SHORT).show()
                                val profilePicUrl = document.getString("profilePic")
                                Glide.with(this@SettingFragment)
                                    .load(profilePicUrl)
                                    .into(binding.profileImageView)
                            } else {
                                Toast.makeText(getActivity(), "no", Toast.LENGTH_SHORT).show()

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
