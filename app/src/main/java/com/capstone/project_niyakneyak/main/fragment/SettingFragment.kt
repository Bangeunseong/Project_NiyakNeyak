package com.capstone.project_niyakneyak.main.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.FragmentSettingBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.capstone.project_niyakneyak.main.activity.AppSettingActivity
import com.capstone.project_niyakneyak.main.activity.HowToUseActivity
import com.capstone.project_niyakneyak.main.activity.OpenProfileActivity
import com.capstone.project_niyakneyak.main.activity.PolicyActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize the ActivityResultLauncher with a callback function
        val appSettingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
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

        binding.profileImageView.setOnClickListener {
            showImagePopup()
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

        binding.reportSettings.setOnClickListener {
            sendFeedback()
        }

        binding.howtoSettings.setOnClickListener {
            val intent = Intent(activity, HowToUseActivity::class.java)
            startActivity(intent)
        }

        binding.policySettings.setOnClickListener {
            val intent = Intent(activity, PolicyActivity::class.java)
            startActivity(intent)
        }

        firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val userAccount = it.toObject<UserAccount>()
                    binding.yourCurrentNameTextview.text = userAccount!!.name
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

    private fun sendFeedback() {
        val recipient = "kochinko021024@gmail.com"
        val subject = "사용자 의견"
        val body = """
            이곳에 사용자 의견을 작성해 주세요.
            
            
            
            
            * 오류의 경우, 오류화면의 스크린 샷을 첨부해 주세요.
            저희 서비스를 개선하는데 큰 도움이 됩니다.
            
            니약내약을 사용해주셔서 감사합니다.
            
            Device Info:
            Brand: ${Build.BRAND}
            Model: ${Build.MODEL}
            Manufacturer: ${Build.MANUFACTURER}
            SDK: ${Build.VERSION.SDK_INT}
            """.trimIndent()

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(this.context, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImagePopup() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_image_popup, null)
        val popupImageView = dialogView.findViewById<ImageView>(R.id.popupImageView)

        // Firestore에서 원본 이미지 URL을 가져와 Glide로 로드
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("profilePic")) {
                        val profilePicUrl = document.getString("profilePic")
                        if (!profilePicUrl.isNullOrEmpty() && profilePicUrl != "default_profile_image_url") {
                            // Glide를 사용하여 원본 이미지를 로드합니다.
                            val uri = Uri.parse(profilePicUrl)
                            Glide.with(this)
                                .load(uri)
                                .error(binding.profileImageView.drawable)
                                .into(popupImageView)
                        } else {
                            // 기본 이미지 사용
                            popupImageView.setImageDrawable(binding.profileImageView.drawable)
                        }
                    } else {
                        // 기본 이미지 사용
                        popupImageView.setImageDrawable(binding.profileImageView.drawable)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Failed to load original image: ", exception)
                    // 기본 이미지 사용
                    popupImageView.setImageDrawable(binding.profileImageView.drawable)
                }
        }

        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setPositiveButton("닫기") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
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
                        if (document != null && document.contains("profilePic") && document.getString("profilePic") != "default_profile_image_url"){
                            val profilePicUrl = document.getString("profilePic")
                            Glide.with(this@SettingFragment)
                                .load(profilePicUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(binding.profileImageView)
                        } else {
                            // 프로필 이미지가 없을 경우 기본 이미지로 설정
                            binding.profileImageView.setImageResource(R.drawable.baseline_account_circle_24)
                            Log.d(SettingFragment.TAG, "Profile picture URL is null or empty, reverting to default image")
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