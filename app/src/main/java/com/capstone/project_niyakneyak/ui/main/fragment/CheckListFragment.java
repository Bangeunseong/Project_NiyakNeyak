package com.capstone.project_niyakneyak.ui.main.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.project_niyakneyak.R;
import com.capstone.project_niyakneyak.data.alarm_model.Alarm;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.data.patient_model.PatientData;
import com.capstone.project_niyakneyak.databinding.FragmentCheckListBinding;
import com.capstone.project_niyakneyak.ui.main.adapter.CheckDataAdapter;
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.etc.ActionResult;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.CheckListViewModel;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.CheckListViewModelFactory;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataListViewModel;
import com.capstone.project_niyakneyak.ui.main.listener.OnCheckedAlarmListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckListFragment extends Fragment implements OnCheckedAlarmListener {
    private FragmentCheckListBinding binding;
    private CheckListViewModel checkListViewModel;
    private CheckDataAdapter adapter;
    private List<Alarm> alarms = new ArrayList<>();
    private List<MedsData> medsList = new ArrayList<>();

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
        checkListViewModel = new ViewModelProvider(this, new CheckListViewModelFactory(requireActivity().getApplication()))
                .get(CheckListViewModel.class);
        adapter = new CheckDataAdapter(this);

        checkListViewModel.getAlarmsLiveData().observe(this, alarms -> {
            this.alarms = alarms;
            List<MedsData> temp = getCertainMedsData(medsList, alarms);
            if(temp.size() < 1) binding.contentChecklistDescriptionText.setVisibility(View.VISIBLE);
            else binding.contentChecklistDescriptionText.setVisibility(View.GONE);
            adapter.setDataSet(temp, alarms);
        });

        checkListViewModel.getLiveActionResult().observe(this, actionResult -> {
            if(actionResult == null) return;
            if(actionResult.getSuccess() != null){
                medsList = checkListViewModel.getMedsDataList();
                List<MedsData> temp = getCertainMedsData(medsList, alarms);
                if(temp.size() < 1) binding.contentChecklistDescriptionText.setVisibility(View.VISIBLE);
                else binding.contentChecklistDescriptionText.setVisibility(View.GONE);
                adapter.setDataSet(temp, alarms);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCheckListBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.contentChecklist.setHasFixedSize(false);
        binding.contentChecklist.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.contentChecklist.setAdapter(adapter);
        binding.contentChecklist.addItemDecoration(new HorizontalItemDecorator(10));
        binding.contentChecklist.addItemDecoration(new VerticalItemDecorator(20));
    }

    //TODO: Need Modification in filtering MedsData
    private List<MedsData> getCertainMedsData(List<MedsData> medsList, List<Alarm> alarms){
        List<MedsData> certainMeds = new ArrayList<>();
        for (MedsData data : medsList){
            if(isConsumeDate(alarms, data.getAlarms()))
                certainMeds.add(data);
        }
        return certainMeds;
    }

    private boolean isConsumeDate(List<Alarm> alarms, List<Integer> includedAlarms){
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        for(Alarm alarm : alarms){
            if(includedAlarms.contains(alarm.getAlarmCode())){
                switch(today.get(Calendar.DAY_OF_WEEK)){
                    case Calendar.SUNDAY -> {if (alarm.isSun()) return true;}
                    case Calendar.MONDAY -> {if (alarm.isMon()) return true;}
                    case Calendar.TUESDAY -> {if (alarm.isTue()) return true;}
                    case Calendar.WEDNESDAY -> {if (alarm.isWed()) return true;}
                    case Calendar.THURSDAY -> {if (alarm.isThu()) return true;}
                    case Calendar.FRIDAY -> {if (alarm.isFri()) return true;}
                    case Calendar.SATURDAY -> {if (alarm.isSat()) return true;}
                }
            }
        }
        return false;
    }

    @Override
    @Deprecated
    public void OnItemClicked(Alarm alarm) {}

    @Override
    public void OnItemClicked(Alarm alarm, boolean isChecked) {

    }
}