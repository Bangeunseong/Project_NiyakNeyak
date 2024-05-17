package com.capstone.project_niyakneyak.main.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ActivityProfileChangeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ProfileChangeActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private var user: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private val uiScope = CoroutineScope(Dispatchers.Main)

    private lateinit var photoLauncher: ActivityResultLauncher<Intent>

    private var _binding: ActivityProfileChangeBinding? = null
    private val binding get() = _binding!!

    var userId: String? = null
    var fileUri: Uri? = null

    val TAG = "ProfileChangeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance() // Firebase Auth 초기화
        firestore = FirebaseFirestore.getInstance() // Firestore 초기화
        user = auth.currentUser // 현재 사용자 가져오기

        userId = auth.currentUser?.uid
        if (userId != null) {
            val userDocument = firestore.collection("users").document(userId!!)
            userDocument.get()
            firestore.collection("users").document(userId!!).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        if (document.contains("profilePic")) {
                            Log.d(TAG, "profilePic field already exists!")
                        } else {
                            userDocument.update("profilePic", "default_profile_image_url")
                                .addOnSuccessListener {
                                    Log.d(TAG, "profilePic field successfully added!")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding profilePic field", e)
                                }
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

        val imageViews = listOf(binding.imageViewBoy, binding.imageViewGirl, binding.imageViewMan, binding.imageViewWoman)

        imageViews.forEach { imageView ->
            imageView.setOnClickListener {
                clearSelection()
                imageView.isSelected = true
                fileUri = Uri.parse("android.resource://${packageName}/${getResourceIdForImageView(imageView.id)}")
                binding.imageViewYou.setImageURI(fileUri)
            }
        }

        binding.setProfileButton.setOnClickListener {
            if (userId != null) {
                val userDocument = firestore.collection("users").document(userId!!)
                userDocument.get()
                firestore.collection("users").document(userId!!).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            userDocument.update("profilePic", fileUri.toString())
                                .addOnSuccessListener {
                                    Toast.makeText(this, "프로필이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, "profilePic field successfully added!")
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                    Log.w(TAG, "Error adding profilePic field", e)
                                }

                        } else {
                            Toast.makeText(this, "선택된 이미지가 없습니다.", Toast.LENGTH_SHORT).show()
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

        binding.backButton.setOnClickListener {
            finish()
        }

        // ActivityResultLauncher 초기화
        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                fileUri = data?.data
                fileUri?.let {
                    binding.imageViewYou.setImageURI(it)
                    clearSelection()
                    Log.d(TAG, "Image selected: $fileUri")
                } ?: run {
                    Log.d(TAG, "fileUri is null")
                }
            }
        }

        binding.selectFromGalleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            photoLauncher.launch(intent)
        }
    }

    private fun clearSelection() {
        binding.imageViewBoy.isSelected = false
        binding.imageViewGirl.isSelected = false
        binding.imageViewMan.isSelected = false
        binding.imageViewWoman.isSelected = false
    }

    private fun getResourceIdForImageView(imageViewId: Int): Int {
        return when (imageViewId) {
            R.id.imageViewBoy -> R.drawable.boy
            R.id.imageViewGirl -> R.drawable.girl
            R.id.imageViewMan -> R.drawable.man
            R.id.imageViewWoman -> R.drawable.woman
            else -> 0
        }
    }
}
