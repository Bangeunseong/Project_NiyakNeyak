package com.capstone.project_niyakneyak.main.activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.project_niyakneyak.R
import androidx.recyclerview.widget.RecyclerView
import com.capstone.project_niyakneyak.databinding.ActivityProfileChangeBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
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

    private var _binding: ActivityProfileChangeBinding? = null
    private val binding get() = _binding!!
    private var selectedImageView: ImageView? = null
    private var defaultDrawable: Drawable? = null

    val TAG = "ProfileChangeActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth // Firebase Auth 초기화
        firestore = Firebase.firestore // Firestore 초기화
        user = auth.currentUser // 현재 사용자 가져오기

        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userDocument = firestore.collection("users").document(userId)
            userDocument.get()
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        if (document.contains("프로필이미지")) {
                            // "프로필이미지" 필드가 이미 존재하는 경우의 처리
                            Log.d(TAG, "프로필이미지 field already exists!")
                        } else {
                            // "프로필이미지" 필드가 존재하지 않는 경우, 필드를 추가
                            userDocument.update("프로필이미지", "default_profile_image_url")
                                .addOnSuccessListener {
                                    Log.d(TAG, "프로필이미지 field successfully added!")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding 프로필이미지 field", e)
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
            selectedImageView= binding.imageViewYou
            // Set selectedImage as profile image
            if (selectedImageView == null) {
                Toast.makeText(this, "프로필 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{

            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.selectFromGalleryButton.setOnClickListener {
            // Set selectedImage as profile image

        }
    }
}