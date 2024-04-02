package com.capstone.project_niyakneyak.ui.main.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.databinding.FragmentAdvancedSettingsBinding;

public class AdvancedSettingFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentAdvancedSettingsBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdvancedSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        binding= FragmentAdvancedSettingsBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        builder.setView(view);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return builder.create();
    }
}

