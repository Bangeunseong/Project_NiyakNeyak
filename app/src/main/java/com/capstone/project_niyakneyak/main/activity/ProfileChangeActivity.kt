package com.capstone.project_niyakneyak.main.activity
import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.databinding.ActivityProfileChangeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.UUID

class ProfileChangeActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private var user: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private val uiScope = CoroutineScope(Dispatchers.Main)

    lateinit var photoLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectedImageView: ImageView


    private var _binding: ActivityProfileChangeBinding? = null
    private val binding get() = _binding!!

    private var defaultDrawable: Drawable? = null
    var userId: String? = null
    var fileUri: Uri? = null

    val TAG = "ProfileChangeActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth // Firebase Auth 초기화
        firestore = Firebase.firestore // Firestore 초기화
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
                            // "프로필이미지" 필드가 이미 존재하는 경우의 처리
                            Log.d(TAG, "profilePid field already exists!")
                        } else {
                            // "프로필이미지" 필드가 존재하지 않는 경우, 필드를 추가
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


        }
        binding.imageViewGirl.setOnClickListener {
            Toast.makeText(this, "girl.", Toast.LENGTH_SHORT).show()
            binding.imageViewYou.setImageResource(R.drawable.girl)

        }
        binding.imageViewMan.setOnClickListener {
            Toast.makeText(this, "man.", Toast.LENGTH_SHORT).show()
            binding.imageViewYou.setImageResource(R.drawable.man)
        }
        binding.imageViewWoman.setOnClickListener {
            Toast.makeText(this, "woman.", Toast.LENGTH_SHORT).show()
            binding.imageViewYou.setImageResource(R.drawable.woman)
        }

        binding.setProfileButton.setOnClickListener {

        }
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.selectFromGalleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            photoLauncher.launch(intent)
            if (fileUri != null) {
                uploadImageToFirebaseStorage()
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }


        }
    }
    private fun uploadImageToFirebaseStorage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().reference
        val filePath = storageReference.child("profile_images").child("${UUID.randomUUID()}.jpg")

        fileUri?.let { uri ->
            filePath.putFile(uri)
                .addOnSuccessListener {
                    filePath.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfileImageUrl(downloadUri.toString())
                        progressDialog.dismiss()
                    }.addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Log.w(TAG, "Error getting download URL", e)
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Log.w(TAG, "Error uploading image", e)
                }
        }
    }
    private fun updateProfileImageUrl(downloadUrl: String) {
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .update("profilePic", downloadUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Profile picture URL updated in Firestore")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating profile picture URL", e)
                }
        }
    }



}
