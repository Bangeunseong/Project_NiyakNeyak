package com.capstone.project_niyakneyak.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.FragmentSettingBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class SettingFragment: Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = Firebase.auth
        firestore = Firebase.firestore

        binding.profileButton.setOnClickListener {
            firestore.collection(UserAccount.COLLECTION_ID).document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val userAccount = it.toObject<UserAccount>()
                    val bundle = Bundle()
                    bundle.putString(UserAccount.REPRESENT_KEY, userAccount!!.idToken)
                }.addOnFailureListener{
                    Snackbar.make(view, it.toString(), Snackbar.LENGTH_SHORT).show()
                }
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
    }

    companion object {
        private const val TAG = "SettingFragment"
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }
}