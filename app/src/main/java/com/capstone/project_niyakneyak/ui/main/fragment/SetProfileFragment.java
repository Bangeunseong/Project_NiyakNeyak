package com.capstone.project_niyakneyak.ui.main.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.databinding.FragmentSetProfileBinding;

public class SetProfileFragment extends DialogFragment {

    private FragmentSetProfileBinding binding;
    public SetProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        binding=FragmentSetProfileBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        builder.setView(view);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }
}
