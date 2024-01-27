package com.capstone.project_niyakneyak.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.project_niyakneyak.data.model.TimeData;
import com.capstone.project_niyakneyak.databinding.FragmentTimeSettingBinding;
import com.capstone.project_niyakneyak.ui.listener.OnAddedTimeListenter;
import com.capstone.project_niyakneyak.ui.listener.OnChangedTimeListener;
import com.capstone.project_niyakneyak.ui.listener.OnDeleteTimeListener;
import com.capstone.project_niyakneyak.ui.main.PatientViewModel;
import com.capstone.project_niyakneyak.ui.main.adapter.TimeDataAdapter;

public class TimeSettingFragment extends Fragment {
    private FragmentTimeSettingBinding binding;
    private TimeDataAdapter timeDataAdapter;
    private PatientViewModel patientViewModel;

    private OnAddedTimeListenter onAddedTimeListenter = new OnAddedTimeListenter() {
        @Override
        public void onAddedTime(TimeData target) {

        }
    };
    private OnChangedTimeListener onChangedTimeListener = new OnChangedTimeListener() {
        @Override
        public void onChangedTime(String time, int position) {

        }
    };
    private OnDeleteTimeListener onDeleteTimeListener = new OnDeleteTimeListener() {
        @Override
        public void onDeleteTime(int position) {

        }
    };

    public static TimeSettingFragment newInstance(PatientViewModel patientViewModel) {
        TimeSettingFragment fragment = new TimeSettingFragment();
        Bundle args = new Bundle();
        args.putSerializable("PatientViewModel", patientViewModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTimeSettingBinding.inflate(inflater, container, false);
        Bundle data = getArguments();
        patientViewModel = (PatientViewModel) data.getSerializable("PatientViewModel");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}