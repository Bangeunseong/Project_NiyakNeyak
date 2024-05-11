package com.capstone.project_niyakneyak.main.activity
import android.Manifest
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ProfileChangeActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private var user: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private val uiScope = CoroutineScope(Dispatchers.Main)

    lateinit var photoLauncher: ActivityResultLauncher<Intent>



    private var _binding: ActivityProfileChangeBinding? = null
    private val binding get() = _binding!!
    private var selectedImageView: ImageView? = null
    private var defaultDrawable: Drawable? = null
    var userId: String? = null

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

        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //
                selectedImageView = binding.imageViewYou
                uploadToFirestore(result.data?.data!!)

            }
        }


        binding.setProfileButton.setOnClickListener {

        }
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.selectFromGalleryButton.setOnClickListener {
            //Toast.makeText(this, "a.", Toast.LENGTH_SHORT).show()
            checkPermissionAndPickPhoto()


        }
    }

    override fun onStart() {
        super.onStart()

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }
    fun checkPermissionAndPickPhoto() {
        //Toast.makeText(this, "b.", Toast.LENGTH_SHORT).show()
        var readExternalPhoto: String = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(this, "11111111111111111111.", Toast.LENGTH_SHORT).show()
            readExternalPhoto = android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Toast.makeText(this, "222222222222222222222222222.", Toast.LENGTH_SHORT).show()
            readExternalPhoto = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        Toast.makeText(this, "33333333333333333333333333.", Toast.LENGTH_SHORT).show()
        if(ContextCompat.checkSelfPermission(this, readExternalPhoto) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "c.", Toast.LENGTH_SHORT).show()
            openPhotoPicker()
        } else {
            Toast.makeText(this, "444444444444444444444444444444.", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(readExternalPhoto), 100)
        }
    }

    private fun openPhotoPicker() {
        Toast.makeText(this, "d.", Toast.LENGTH_SHORT).show()
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type="image/*"
        photoLauncher.launch(intent)
        Toast.makeText(this, "e.", Toast.LENGTH_SHORT).show()
    }

    fun uploadToFirestore(photoUri: Uri){
        val firestore = Firebase.firestore
        val data= hashMapOf("profilePic" to photoUri.toString())
        firestore.collection("users").document(userId!!)
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile photo uploaded to Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Profile photo upload failed", Toast.LENGTH_SHORT).show()
            }

    }
    fun postToFirestore(photoUrl: String){
        Firebase.firestore.collection("users")
            .document(userId!!)
            .update("profilePic", photoUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Profile photo update failed", Toast.LENGTH_SHORT).show()
            }
    }



}