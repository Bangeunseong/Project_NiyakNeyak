package com.capstone.project_niyakneyak.main.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityOpenProfileBinding
import com.google.firebase.firestore.toObject
import com.capstone.project_niyakneyak.login.activity.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import androidx.appcompat.app.AlertDialog

class OpenProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpenProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private var user: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private val uiScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val TAG = "OPEN_PROFILE_ACTIVITY"
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarOpenProfile)
        supportActionBar?.setDisplayShowTitleEnabled(true) // 툴바에 제목을 보이게 설정
        supportActionBar?.title = getString(R.string.toolbar_modification)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d("Toolbar", "Toolbar title set to: ${getString(R.string.toolbar_modification)}")






        auth = Firebase.auth // Firebase Auth 초기화
        firestore = Firebase.firestore // Firestore 초기화
        user = auth?.currentUser // 현재 사용자 가져오기

        binding.progressBarModify.visibility = View.VISIBLE
        binding.modifyButton.isEnabled = false
        //binding.modifyButton.setBackgroundColor(getColor(R.color.gray))

        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get().addOnSuccessListener { documentSnapshot ->
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
                    binding.passwordText.text = user.password
                    binding.phonenumberText.text = user.phoneNum
                    binding.genderText.text = user.gender

                    binding.nameTextEdit.setText(user.name)
                    //binding.birthdayTextEdit.setText(user.birth)
                    binding.passwordTextEdit.setText(user.password)
                    binding.phonenumberTextEdit.setText(user.phoneNum)
                    when(user.gender){
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

                // 사용자에게 오류 메시지를 표시합니다.
                Toast.makeText(this@OpenProfileActivity, "프로필 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.modifyButton.setOnClickListener {
            binding.nameTextEdit.visibility = View.VISIBLE
            binding.birthdayTextEdit.visibility = View.VISIBLE
            binding.passwordTextEdit.visibility = View.VISIBLE
            binding.phonenumberTextEdit.visibility = View.VISIBLE
            binding.genderRadioGroup.visibility = View.VISIBLE
            binding.modifyBackButton.visibility = View.VISIBLE
            binding.modifyFinishButton.visibility = View.VISIBLE

            binding.nameText.visibility = View.INVISIBLE
            binding.birthdayText.visibility = View.INVISIBLE
            binding.passwordText.visibility = View.INVISIBLE
            binding.phonenumberText.visibility = View.INVISIBLE
            binding.genderText.visibility = View.INVISIBLE
            binding.backButton.visibility = View.INVISIBLE
            binding.modifyButton.visibility = View.INVISIBLE


            binding.birthdayTextEdit.text = binding.birthdayText.text

        }
        binding.modifyBackButton.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                firestore.collection("users").document(userId).get().addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject<UserAccount>()
                    val age = user?.age.toString()
                    if (user != null) {
                        binding.yourCurrentNameTextview.text = user.name
                        binding.nameText.text = user.name
                        binding.ageText.text = "만 $age 세"
                        binding.birthdayText.text = user.birth
                        binding.idText.text = user.emailId
                        binding.passwordText.text = user.password
                        binding.phonenumberText.text = user.phoneNum
                        binding.genderText.text = user.gender

                        binding.nameTextEdit.setText(user.name)
                        binding.passwordTextEdit.setText(user.password)
                        binding.phonenumberTextEdit.setText(user.phoneNum)
                        when(user.gender){
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

            binding.nameTextEdit.visibility = View.INVISIBLE
            binding.birthdayTextEdit.visibility = View.INVISIBLE
            binding.passwordTextEdit.visibility = View.INVISIBLE
            binding.phonenumberTextEdit.visibility = View.INVISIBLE
            binding.genderRadioGroup.visibility = View.INVISIBLE
            binding.modifyBackButton.visibility = View.INVISIBLE
            binding.modifyFinishButton.visibility = View.INVISIBLE

            binding.nameText.visibility = View.VISIBLE
            binding.birthdayText.visibility = View.VISIBLE
            binding.passwordText.visibility = View.VISIBLE
            binding.phonenumberText.visibility = View.VISIBLE
            binding.genderText.visibility = View.VISIBLE
            binding.backButton.visibility = View.VISIBLE
            binding.modifyButton.visibility = View.VISIBLE
        }
        binding.modifyFinishButton.setOnClickListener {

            val strPassword = binding.passwordTextEdit.text.toString().trim()


            if (strPassword.isNotEmpty()) {
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
                    age = calculateAge(birthDate)
                )

                firestore.collection(UserAccount.COLLECTION_ID).document(account.idToken!!).set(account)
                    .addOnSuccessListener {
                        Toast.makeText(this@OpenProfileActivity, "프로필 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        binding.yourCurrentNameTextview.text = account.name
                        binding.nameText.text = account.name
                        binding.ageText.text = "만 ${account.age} 세"
                        binding.birthdayText.text = account.birth
                        binding.passwordText.text = account.password
                        binding.phonenumberText.text = account.phoneNum
                        binding.genderText.text = account.gender

                        binding.nameTextEdit.visibility = View.INVISIBLE
                        binding.birthdayTextEdit.visibility = View.INVISIBLE
                        binding.passwordTextEdit.visibility = View.INVISIBLE
                        binding.phonenumberTextEdit.visibility = View.INVISIBLE
                        binding.genderRadioGroup.visibility = View.INVISIBLE
                        binding.modifyBackButton.visibility = View.INVISIBLE
                        binding.modifyFinishButton.visibility = View.INVISIBLE

                        binding.nameText.visibility = View.VISIBLE
                        binding.birthdayText.visibility = View.VISIBLE
                        binding.passwordText.visibility = View.VISIBLE
                        binding.phonenumberText.visibility = View.VISIBLE
                        binding.genderText.visibility = View.VISIBLE
                        binding.backButton.visibility = View.VISIBLE
                        binding.modifyButton.visibility = View.VISIBLE
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@OpenProfileActivity, "프로필 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        Log.w(TAG, "Error updating profile: $it")
                    }
            } else {
                Toast.makeText(this@OpenProfileActivity, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }



        }
        binding.profileImageView.setOnClickListener {
            showImageChoiceDialog()

        }



        binding.logoutButton.setOnClickListener {
            AlertDialog.Builder(this@OpenProfileActivity).apply {
                setTitle("로그아웃")
                setMessage("로그아웃 하시겠습니까?")
                setPositiveButton("예") { _, _ ->
                    auth?.signOut()
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
            // Format and display the date in the TextView
            val dateStr = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.birthdayTextEdit.text = dateStr

            // Update age when a date is selected
            updateAge(dateStr)  // Call this function here to update the age
        }, year, month, day)

        dpd.show()
    }
    private fun showImageChoiceDialog() {
        val items = arrayOf("사진 찍기", "갤러리에서 선택")
        AlertDialog.Builder(this).apply {
            setTitle("프로필 사진 변경")
            setItems(items) { _, which ->
                when (which) {
                    0 -> {
                        // 여기에 사진 찍기 기능을 구현하세요.
                    }
                    1 -> {
                        // 갤러리를 열어 이미지를 선택하도록 합니다.
                        openGalleryForImage()
                    }
                }
            }
            show()
        }
    }
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
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
                calculateAge(birthDate)  // 백그라운드 스레드에서 나이 계산
            }
            binding.ageText.text = "만 $age 세"  // UI 스레드에서 TextView 업데이트
        }
    }
    private fun calculateAge(birthDateString: String): Int {
        val parts = birthDateString.split("-")
        if (parts.size < 3) return 0  // 날짜 형식이 잘못되었을 경우 안전하게 처리

        val birthYear = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        val birthDay = parts[2].toInt()

        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val currentMonth = today.get(Calendar.MONTH) + 1  // Calendar.MONTH는 0부터 시작하므로 1을 더해줌
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        var age = currentYear - birthYear

        // 만약 생일이 아직 지나지 않았다면 나이에서 1을 빼준다
        if (birthMonth > currentMonth || (birthMonth == currentMonth && birthDay > currentDay)) {
            age -= 1
        }

        return age
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }

        }

    }
}