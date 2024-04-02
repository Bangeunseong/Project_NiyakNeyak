package com.capstone.project_niyakneyak.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.capstone.project_niyakneyak.R
import com.capstone.project_niyakneyak.data.user_model.UserAccount
import com.capstone.project_niyakneyak.databinding.ActivityRegisterBinding
import com.capstone.project_niyakneyak.databinding.FragmentSettingBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // firestore 초기화
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.profileButton.setOnClickListener {
            firestore.collection(UserAccount.COLLECTION_ID).document(mFirebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    val userAccount = it.toObject<UserAccount>()
                    val setProfile: DialogFragment = SetProfileFragment()
                    val bundle = Bundle()
                    bundle.putString(UserAccount.REPRESENT_KEY,mFirebaseAuth.currentUser!!.uid)
                    setProfile.arguments = bundle
                    setProfile.show(requireActivity().supportFragmentManager, "PROFILE_SETTING")
                }.addOnFailureListener{
                    Snackbar.make(view, it.toString(), Snackbar.LENGTH_SHORT).show()
                }
        }
        binding!!.advancedButton.setOnClickListener {
            val advancedSetting: DialogFragment = AdvancedSettingFragment()
            advancedSetting.show(requireActivity().supportFragmentManager, "ADVANCED_SETTING")
        }

        val currentUser = auth.currentUser

        val docRef = firestore.collection("users").document(currentUser!!.uid)
        docRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val name = document.getString("name")

                    val yourCurrentNameTextView = view.findViewById<TextView>(R.id.your_current_name_textview)
                    yourCurrentNameTextView.text = name
                    Log.d(TAG, "YourCurrentNameTextView: $yourCurrentNameTextView")
                    Log.d(TAG, "Name from document: $name")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }

    companion object {
        private const val TAG = "SettingFragment"
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }
}
