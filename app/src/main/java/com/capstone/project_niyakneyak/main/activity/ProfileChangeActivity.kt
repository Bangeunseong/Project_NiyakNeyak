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

        binding.imageViewBoy.setOnClickListener {
            Toast.makeText(this, "boy.", Toast.LENGTH_SHORT).show()
            binding.imageViewYou.setImageResource(R.drawable.boy)
            fileUri = Uri.parse("android.resource://${packageName}/${R.drawable.boy}")
        }
        binding.imageViewGirl.setOnClickListener {
            Toast.makeText(this, "girl.", Toast.LENGTH_SHORT).show()
            binding.imageViewYou.setImageResource(R.drawable.girl)
            fileUri = Uri.parse("android.resource://${packageName}/${R.drawable.girl}")
        }
        binding.imageViewMan.setOnClickListener {
            Toast.makeText(this, "man.", Toast.LENGTH_SHORT).show()
            binding.imageViewYou.setImageResource(R.drawable.man)
            fileUri = Uri.parse("android.resource://${packageName}/${R.drawable.man}")
        }
        binding.imageViewWoman.setOnClickListener {
            Toast.makeText(this, "woman.", Toast.LENGTH_SHORT).show()
            binding.imageViewYou.setImageResource(R.drawable.woman)
            fileUri = Uri.parse("android.resource://${packageName}/${R.drawable.woman}")
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
                                    Toast.makeText(this, "changed", Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, "profilePic field successfully added!")
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                    Log.w(TAG, "Error adding profilePic field", e)
                                }

                        } else {
                            Toast.makeText(this, "Nothing is selected", Toast.LENGTH_SHORT).show()
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
}
