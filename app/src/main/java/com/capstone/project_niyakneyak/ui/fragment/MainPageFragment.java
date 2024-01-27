package com.capstone.project_niyakneyak.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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
import com.capstone.project_niyakneyak.databinding.FragmentMainPageBinding;
import com.capstone.project_niyakneyak.ui.listener.OnAddedDataListener;
import com.capstone.project_niyakneyak.ui.listener.OnChangedDataListener;
import com.capstone.project_niyakneyak.ui.listener.OnDeleteDataListener;
import com.capstone.project_niyakneyak.ui.main.ActionResult;
import com.capstone.project_niyakneyak.ui.main.PatientViewModel;
import com.capstone.project_niyakneyak.ui.main.adapter.MedsDataAdapter;
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPageFragment extends Fragment {
    private FragmentMainPageBinding binding;
    private MedsDataAdapter adapter;
    private PatientViewModel patientViewModel;

    private final OnAddedDataListener onAddedDataListener = new OnAddedDataListener() {
        @Override
        public void onAddedData(MedsData target) {
            Log.d("MainActivity","Data Addition called");
            if(!target.getMeds_name().isEmpty()){
                Result<PatientData> result = patientViewModel.getPatientData();
                if(result instanceof Result.Success){
                    patientViewModel.add_MedsData(target);
                    adapter.addItem(((Result.Success<PatientData>) result).getData().getMedsData().size() - 1);
                    binding.contentMainGuide.setVisibility(View.GONE);
                }

                patientViewModel.getActionResult().observe(getActivity(), new Observer<ActionResult>() {
                    @Override
                    public void onChanged(ActionResult actionResult) {
                        if(actionResult == null) return;
                        if(actionResult.getSuccess() != null)
                            Toast.makeText(getContext(), actionResult.getSuccess().getDisplayData(), Toast.LENGTH_SHORT).show();
                        if(actionResult.getError() != null)
                            Toast.makeText(getContext(), actionResult.getError(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast toast = Toast.makeText(getContext(), "Addition Failed!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    private final OnChangedDataListener onChangedDataListener = new OnChangedDataListener() {
        @Override
        public void onChangedData(MedsData origin, MedsData changed) {
            Log.d("MainActivity","Data Modification called");
            if(!changed.getMeds_name().isEmpty()){
                Result<PatientData> result = patientViewModel.getPatientData();
                if(result instanceof Result.Success){
                    patientViewModel.modify_MedsData(origin, changed);
                    adapter.changeItem(((Result.Success<PatientData>) result).getData().getMedsData().indexOf(changed));
                }

                patientViewModel.getActionResult().observe(getActivity(), new Observer<ActionResult>() {
                    @Override
                    public void onChanged(ActionResult actionResult) {
                        if(actionResult == null) return;
                        if(actionResult.getSuccess() != null)
                            Toast.makeText(getContext(), actionResult.getSuccess().getDisplayData(), Toast.LENGTH_SHORT).show();
                        if(actionResult.getError() != null)
                            Toast.makeText(getContext(), actionResult.getError(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast toast = Toast.makeText(getContext(), "Modification Canceled!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    private final OnDeleteDataListener onDeleteDataListener = new OnDeleteDataListener() {
        @Override
        public void onDeletedData(MedsData target) {
            Log.d("MainActivity","Data Deletion called");

            Result<PatientData> result = patientViewModel.getPatientData();
            if(result instanceof Result.Success){
                int position = ((Result.Success<PatientData>) result).getData().getMedsData().indexOf(target);
                patientViewModel.delete_MedsData(target); adapter.removeItem(position);
                if(adapter.getItemCount() < 1) binding.contentMainGuide.setVisibility(View.VISIBLE);
            }
            patientViewModel.getActionResult().observe(getActivity(), new Observer<ActionResult>() {
                @Override
                public void onChanged(ActionResult actionResult) {
                    if(actionResult == null) return;
                    if(actionResult.getSuccess() != null)
                        Toast.makeText(getContext(), actionResult.getSuccess().getDisplayData(), Toast.LENGTH_SHORT).show();
                    if(actionResult.getError() != null)
                        Toast.makeText(getContext(), actionResult.getError(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MainPage.
     */
    public static MainPageFragment newInstance(PatientViewModel patientViewModel) {
        MainPageFragment fragment = new MainPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("PatientViewModel", patientViewModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainPageBinding.inflate(inflater, container, false);
        Bundle data = getArguments();
        patientViewModel = (PatientViewModel) data.getSerializable("PatientViewModel");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //RecyclerAdapter for Medication Info.
        Result<PatientData> result = patientViewModel.getPatientData();
        if(result instanceof Result.Success)
            adapter = new MedsDataAdapter(getActivity().getSupportFragmentManager(), ((Result.Success<PatientData>) result).getData().getMedsData(), onChangedDataListener, onDeleteDataListener);
        else adapter = new MedsDataAdapter(getActivity().getSupportFragmentManager(), new ArrayList<>(), onChangedDataListener, onDeleteDataListener);

        binding.contentMainMeds.setHasFixedSize(false);
        binding.contentMainMeds.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.contentMainMeds.setAdapter(adapter);
        binding.contentMainMeds.addItemDecoration(new VerticalItemDecorator(20));
        binding.contentMainMeds.addItemDecoration(new HorizontalItemDecorator(10));

        if(adapter.getItemCount() > 0)
            binding.contentMainGuide.setVisibility(View.GONE);
        binding.contentMainAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newAddForm = new AddDialogFragment(onAddedDataListener);
                newAddForm.show(getActivity().getSupportFragmentManager(), "dialog");
            }
        });
    }
}