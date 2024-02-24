package com.capstone.project_niyakneyak.ui.main.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.databinding.FragmentCheckListBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckListFragment extends Fragment {
    private FragmentCheckListBinding binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment CheckListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckListFragment newInstance() {
        return new CheckListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCheckListBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}