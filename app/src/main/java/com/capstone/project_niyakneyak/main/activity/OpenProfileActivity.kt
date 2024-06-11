package com.capstone.project_niyakneyak.main.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityOpenProfileBinding
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Calendar

class OpenProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private var user: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var url: String? = null

    companion object {
        const val TAG = "OPEN_PROFILE_ACTIVITY"
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarOpenProfile)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = getString(R.string.toolbar_modification)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d("Toolbar", "Toolbar title set to: ${getString(R.string.toolbar_modification)}")

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        user = auth.currentUser
        auth = Firebase.auth // Firebase Auth 초기화
        firestore = Firebase.firestore // Firestore 초기화
        user = auth.currentUser // 현재 사용자 가져오기

        binding.progressBarModify.visibility = View.VISIBLE
        binding.modifyButton.isEnabled = false

        userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId!!).get().addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<UserAccount>()
                val age = user?.age.toString()
                if (user != null) {
                    binding.progressBarModify.visibility = View.GONE
                    binding.modifyButton.isEnabled = true

                    binding.yourCurrentNameTextview.text = user.name
                    binding.nameText.text = user.name
                    binding.ageText.text = "만 $age 세"
                    binding.birthdayText.text = user.birth
                    binding.idText.text = user.emailId
                    binding.phonenumberText.text = user.phoneNum
                    binding.genderText.text = user.gender

                    binding.nameTextEdit.setText(user.name)
                    binding.phonenumberTextEdit.setText(user.phoneNum)
                    when (user.gender) {
                        "남성" -> binding.maleRadioButton.isChecked = true
                        "여성" -> binding.femaleRadioButton.isChecked = true
                    }
                    binding.birthdayTextEdit.setOnClickListener {
                        showDatePickerDialog()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting user data: ", exception)
                binding.progressBarModify.visibility = View.GONE
                binding.modifyButton.isEnabled = false
                Toast.makeText(this@OpenProfileActivity, "프로필 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.modifyButton.setOnClickListener {
            toggleModifyMode(true)
        }

        binding.modifyBackButton.setOnClickListener {
            reloadUserProfile()
            toggleModifyMode(false)
        }

        binding.modifyFinishButton.setOnClickListener {
            saveUserProfile()
        }

        binding.profileImageView.setOnClickListener {
            val profileImageUri = (binding.profileImageView.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "ProfileImage", null)
                Uri.parse(path)
            }

            val intent = Intent(this, ProfileChangeActivity::class.java).apply {
                putExtra("profileImageUri", profileImageUri.toString())
            }
            startActivity(intent)


        }


        binding.logoutButton.setOnClickListener {
            AlertDialog.Builder(this@OpenProfileActivity).apply {
                setTitle("로그아웃")
                setMessage("로그아웃 하시겠습니까?")
                setPositiveButton("예") { _, _ ->
                    auth.signOut()
                    startActivity(Intent(this@OpenProfileActivity, LoginActivity::class.java))
                    finish()
                }
                setNegativeButton("아니오", null)
                show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            val dateStr = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.birthdayTextEdit.text = dateStr
            updateAge(dateStr)
        }, year, month, day)

        dpd.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            binding.profileImageView.setImageURI(data?.data)
        }
    }

    private fun updateAge(birthDate: String) {
        uiScope.launch {
            val age = withContext(Dispatchers.Default) {
                calculateAge(birthDate)
            }
            binding.ageText.text = "만 $age 세"
        }
    }

    private fun calculateAge(birthDateString: String): Int {
        val parts = birthDateString.split("-")
        if (parts.size < 3) return 0

        val birthYear = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        val birthDay = parts[2].toInt()

        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val currentMonth = today.get(Calendar.MONTH) + 1
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        var age = currentYear - birthYear
        if (birthMonth > currentMonth || (birthMonth == currentMonth && birthDay > currentDay)) {
            age -= 1
        }
        return age
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateProfile() {
        uiScope.launch {
            userId = auth.currentUser?.uid
            if (userId != null) {
                firestore.collection("users").document(userId!!).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.contains("profilePic") && document.getString("profilePic") != "default_profile_image_url"){
                            url = document.getString("profilePic")
                            val profilePicUrl = document.getString("profilePic")
                            Glide.with(this@OpenProfileActivity)
                                .load(profilePicUrl)
                                .apply(RequestOptions.circleCropTransform())
                                .into(binding.profileImageView)
                        } else {
                            // 프로필 이미지가 없을 경우 기본 이미지로 설정
                            binding.profileImageView.setImageResource(R.drawable.baseline_account_circle_24)
                            Log.d(TAG, "Profile picture URL is null or empty, reverting to default image")
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

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    private fun toggleModifyMode(enable: Boolean) {
        if (enable) {
            binding.nameTextEdit.visibility = View.VISIBLE
            binding.birthdayTextEdit.visibility = View.VISIBLE
            binding.phonenumberTextEdit.visibility = View.VISIBLE
            binding.genderRadioGroup.visibility = View.VISIBLE
            binding.modifyBackButton.visibility = View.VISIBLE
            binding.modifyFinishButton.visibility = View.VISIBLE

            binding.nameText.visibility = View.INVISIBLE
            binding.birthdayText.visibility = View.INVISIBLE
            binding.phonenumberText.visibility = View.INVISIBLE
            binding.genderText.visibility = View.INVISIBLE
            binding.backButton.visibility = View.INVISIBLE
            binding.modifyButton.visibility = View.INVISIBLE

            binding.birthdayTextEdit.text = binding.birthdayText.text
        } else {
            binding.nameTextEdit.visibility = View.INVISIBLE
            binding.birthdayTextEdit.visibility = View.INVISIBLE
            binding.phonenumberTextEdit.visibility = View.INVISIBLE
            binding.genderRadioGroup.visibility = View.INVISIBLE
            binding.modifyBackButton.visibility = View.INVISIBLE
            binding.modifyFinishButton.visibility = View.INVISIBLE

            binding.nameText.visibility = View.VISIBLE
            binding.birthdayText.visibility = View.VISIBLE
            binding.phonenumberText.visibility = View.VISIBLE
            binding.genderText.visibility = View.VISIBLE
            binding.backButton.visibility = View.VISIBLE
            binding.modifyButton.visibility = View.VISIBLE
        }
    }

    private fun reloadUserProfile() {
        userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId!!).get().addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<UserAccount>()
                val age = user?.age.toString()
                if (user != null) {
                    binding.yourCurrentNameTextview.text = user.name
                    binding.nameText.text = user.name
                    binding.ageText.text = "만 $age 세"
                    binding.birthdayText.text = user.birth
                    binding.idText.text = user.emailId
                    binding.phonenumberText.text = user.phoneNum
                    binding.genderText.text = user.gender

                    binding.nameTextEdit.setText(user.name)
                    binding.phonenumberTextEdit.setText(user.phoneNum)
                    when (user.gender) {
                        "남성" -> binding.maleRadioButton.isChecked = true
                        "여성" -> binding.femaleRadioButton.isChecked = true
                    }
                    binding.birthdayTextEdit.setOnClickListener {
                        showDatePickerDialog()
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting user data: ", exception)
            }
        }
    }

    private fun saveUserProfile() {
        userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId!!).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<UserAccount>()
                    val strPassword = user?.password
                    val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId
                    val selectedGender = if (selectedGenderId != -1) {
                        findViewById<RadioButton>(selectedGenderId).text.toString()
                    } else {
                        "성별 선택 안됨"
                    }
                    val birthDate = binding.birthdayTextEdit.text.toString()
                    val firebaseUser = auth.currentUser
                    val account = UserAccount(
                        firebaseUser?.uid,
                        firebaseUser?.email,
                        strPassword,
                        binding.nameTextEdit.text.toString(),
                        birthDate,
                        selectedGender,
                        binding.phonenumberTextEdit.text.toString(),
                        age = calculateAge(birthDate),
                        profilePic = url
                    )

                    firestore.collection(UserAccount.COLLECTION_ID).document(account.idToken!!)
                        .set(account)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@OpenProfileActivity,
                                "프로필 수정이 완료되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.yourCurrentNameTextview.text = account.name
                            binding.nameText.text = account.name
                            binding.ageText.text = "만 ${account.age} 세"
                            binding.birthdayText.text = account.birth
                            binding.phonenumberText.text = account.phoneNum
                            binding.genderText.text = account.gender

                            toggleModifyMode(false)
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this@OpenProfileActivity,
                                "프로필 수정에 실패하였습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.w(TAG, "Error updating profile: $it")
                        }
                }
        }
    }
}