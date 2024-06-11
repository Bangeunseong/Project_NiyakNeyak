package com.capstone.project_niyakneyak.main.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
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


    companion object {
        const val TAG = "PROFILE_CHANGE_ACTIVITY"
        private const val IMAGE_PICK_CODE = 1000
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        user = auth.currentUser


        userId = auth.currentUser?.uid



        if (userId != null) {
            val userDocument = firestore.collection("users").document(userId!!)
            userDocument.get()
            firestore.collection("users").document(userId!!).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("profilePic") && document.getString("profilePic") != "default_profile_image_url") {
                        Log.d(TAG, "profilePic field already exists!")
                        val profilePic = document.getString("profilePic")

                        Glide.with(this)
                            .load(profilePic)
                            .error(R.drawable.baseline_account_circle_24)
                            .into(binding.imageViewYou)

                    } else {
                        Log.d(TAG, "Profile picture URL is null or empty, reverting to default image")
                        userDocument.update("profilePic", "default_profile_image_url")
                            .addOnSuccessListener {
                                Log.d(TAG, "profilePic field successfully added!")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding profilePic field", e)
                            }
                        binding.imageViewYou.setImageResource(R.drawable.baseline_account_circle_24)
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

        binding.buttonReset.setOnClickListener {
            clearSelection()
            binding.imageViewYou.setImageResource(R.drawable.baseline_account_circle_24)
            fileUri = "default_profile_image_url".toUri()
        }

        binding.setProfileButton.setOnClickListener {
            if (userId != null) {
                val userDocument = firestore.collection("users").document(userId!!)
                userDocument.get()
                firestore.collection("users").document(userId!!).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            if (fileUri == null) {
                                userDocument.update("profilePic", null)
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
                    val userDocument = firestore.collection("users").document(userId!!)
                    userDocument.update("profilePic", it.toString())
                        .addOnSuccessListener {
                            Log.d(TAG, "profilePic successfully updated with URI: $it")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating profilePic", e)
                        }
                } ?: run {
                    Log.d(TAG, "fileUri is null")
                }
            }
        }

        binding.selectFromGalleryButton.setOnClickListener {


            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            Toast.makeText(this, intent.toString(), Toast.LENGTH_SHORT).show()
            photoLauncher.launch(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            binding.imageViewYou.setImageURI(data?.data)
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
