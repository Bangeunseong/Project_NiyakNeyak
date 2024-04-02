package com.capstone.project_niyakneyak.ui.main.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.FragmentSetProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class SetProfileFragment : DialogFragment() {
    private var binding: FragmentSetProfileBinding? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = Firebase.firestore // firestore 초기화

        val bundle = arguments // 번들을 받아옵니다.
        if (bundle != null) {
            // 번들에서 데이터를 가져와서 사용합니다.
            userId = bundle.getString(UserAccount.REPRESENT_KEY).toString()

            // 여기에서 사용자 ID를 이용한 추가적인 작업을 수행할 수 있습니다.
        }else{
            requireActivity().finish()
        }
    }





    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        binding = FragmentSetProfileBinding.inflate(getLayoutInflater())
        val view: View = binding!!.getRoot()
        builder.setView(view)
        binding!!.backButton.setOnClickListener { dismiss() }

        val docRef = firestore.collection("users").document(userId)
        docRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name")

                    val textView = view.findViewById<TextView>(R.id.editTextText)
                    textView.text = name
                    Log.d(SettingFragment.TAG, "YourCurrentNameTextView: $textView")
                    Log.d(SettingFragment.TAG, "Name from document: $name")
                } else {
                    Log.d(SettingFragment.TAG, "No such document")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.d(SettingFragment.TAG, "get failed with ", exception)
            }




        return builder.create()
    }
}
