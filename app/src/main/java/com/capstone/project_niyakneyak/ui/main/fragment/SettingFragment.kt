package com.capstone.project_niyakneyak.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.capstone.project_niyakneyak.databinding.ActivityRegisterBinding
import com.capstone.project_niyakneyak.databinding.FragmentSettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        //bundle data = getArguments();

        //assert data != null;
        return binding!!.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.profileButton.setOnClickListener {
            val setProfile: DialogFragment = SetProfileFragment()
            setProfile.show(requireActivity().supportFragmentManager, "PROFILE_SETTING")
        }
        binding!!.advancedButton.setOnClickListener {
            val advancedSetting: DialogFragment = AdvancedSettingFragment()
            advancedSetting.show(requireActivity().supportFragmentManager, "ADVANCED_SETTING")
        }
        //여기

        val currentUser = auth.currentUser
        //val docRef = currentUser?.let {
        //    firestore.collection("users").document(it.uid)
        //}
        val docRef = firestore.collection("users").document("BdFfnPeR8Faks3SZDijotQoSmqE2")
        //println("hello my name is $currentUser")//com.google.firebase.auth.internal.zzaf@dda4090
        println("watasiwa $docRef")//com.google.firebase.firestore.DocumentReference@3eefc560
        //watasiwa com.google.firebase.firestore.DocumentReference@6a470fc3


        docRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val name = document.getString("name")
                    Log.d(TAG, "my name is~~~~~~~~~: $name")
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
