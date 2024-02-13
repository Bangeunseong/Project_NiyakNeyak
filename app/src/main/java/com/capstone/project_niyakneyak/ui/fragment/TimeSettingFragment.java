package com.capstone.project_niyakneyak.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.model.MedsData;
import com.capstone.project_niyakneyak.data.model.PatientData;
import com.capstone.project_niyakneyak.data.model.TimeData;
import com.capstone.project_niyakneyak.databinding.FragmentTimeSettingBinding;
import com.capstone.project_niyakneyak.ui.listener.OnChangedTimeListener;
import com.capstone.project_niyakneyak.ui.main.ActionResult;
import com.capstone.project_niyakneyak.ui.main.PatientViewModel;
import com.capstone.project_niyakneyak.ui.main.adapter.TimeDataAdapter;
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator;

import java.util.Arrays;

public class TimeSettingFragment extends Fragment {
    private FragmentTimeSettingBinding binding;
    private TimeDataAdapter adapter;
    private PatientViewModel patientViewModel;

    private final OnChangedTimeListener onChangedTimeListener = new OnChangedTimeListener() {
        @Override
        public void onChangedTime(TimeData origin, TimeData changed, int position) {
            patientViewModel.modify_TimeData(origin, changed);
            adapter.changeItem(position);

            patientViewModel.getTimeResult().observe(requireActivity(), new Observer<ActionResult>() {
                @Override
                public void onChanged(ActionResult actionResult) {
                    if(actionResult == null) return;
                    if(actionResult.getSuccess() != null)
                        Toast.makeText(getActivity(), actionResult.getSuccess().getDisplayData(), Toast.LENGTH_SHORT).show();
                    if(actionResult.getError() != null)
                        Toast.makeText(getActivity(), actionResult.getError(), Toast.LENGTH_SHORT).show();
                }
            });
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

        assert data != null;
        patientViewModel = (PatientViewModel) data.getSerializable("PatientViewModel");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Result<PatientData> result = patientViewModel.getPatientData();
        if(result instanceof Result.Success)
            adapter = new TimeDataAdapter(getActivity(), requireActivity().getSupportFragmentManager(), ((Result.Success<PatientData>)result).getData().getTimeData(), onChangedTimeListener);
        else {
            Log.d("TimeSettingFragment","Something is wrong when getting patient data, Presented times are not realtime data");
            adapter = new TimeDataAdapter(getActivity(), requireActivity().getSupportFragmentManager(),
                    Arrays.asList(new TimeData("06:00", MedsData.ConsumeTime.MORNING),
                            new TimeData("12:00", MedsData.ConsumeTime.AFTERNOON),
                            new TimeData("18:00", MedsData.ConsumeTime.EVENING),
                            new TimeData("24:00", MedsData.ConsumeTime.MIDNIGHT))
                , onChangedTimeListener);
        }

        binding.contentTimeTable.setHasFixedSize(false);
        binding.contentTimeTable.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.contentTimeTable.setAdapter(adapter);
        binding.contentTimeTable.addItemDecoration(new HorizontalItemDecorator(10));
        binding.contentTimeTable.addItemDecoration(new VerticalItemDecorator(20));
    }
}