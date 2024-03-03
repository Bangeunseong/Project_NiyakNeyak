package com.capstone.project_niyakneyak.ui.main.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.data.patient_model.PatientData;
import com.capstone.project_niyakneyak.databinding.FragmentDataListBinding;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataListViewModelFactory;
import com.capstone.project_niyakneyak.ui.main.listener.OnAddedDataListener;
import com.capstone.project_niyakneyak.ui.main.listener.OnChangedDataListener;
import com.capstone.project_niyakneyak.ui.main.listener.OnDeleteDataListener;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.DataListViewModel;
import com.capstone.project_niyakneyak.ui.main.adapter.MainDataAdapter;
import com.capstone.project_niyakneyak.ui.main.decorator.HorizontalItemDecorator;
import com.capstone.project_niyakneyak.ui.main.decorator.VerticalItemDecorator;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataListFragment extends Fragment {
    private FragmentDataListBinding binding;
    private MainDataAdapter adapter;
    private DataListViewModel dataListViewModel;

    private final OnAddedDataListener onAddedDataListener = new OnAddedDataListener() {
        @Override
        public void onAddedData(MedsData target) {
            Log.d("MainActivity","Data Addition called");

            Result<PatientData> result = dataListViewModel.getPatientData();
            if(result instanceof Result.Success){
                dataListViewModel.add_MedsData(target);
                int position = ((Result.Success<PatientData>)result).getData().getMedsData().size() - 1;
                adapter.addItem(position);
                binding.contentMainGuide.setVisibility(View.GONE);
            }
        }
    };

    private final OnChangedDataListener onChangedDataListener = new OnChangedDataListener() {
        @Override
        public void onChangedData(MedsData origin, MedsData changed) {
            Log.d("MainActivity","Data Modification called");

            Result<PatientData> result = dataListViewModel.getPatientData();
            if(result instanceof Result.Success){
                dataListViewModel.modify_MedsData(origin, changed);
                int position = ((Result.Success<PatientData>)result).getData().getMedsData().indexOf(changed);
                adapter.modifyItem(position);
                binding.contentMainGuide.setVisibility(View.GONE);
            }
        }
    };

    private final OnDeleteDataListener onDeleteDataListener = new OnDeleteDataListener() {
        @Override
        public void onDeletedData(MedsData target) {
            Log.d("MainActivity","Data Deletion called");

            Result<PatientData> result = dataListViewModel.getPatientData();
            if(result instanceof Result.Success){
                int position = ((Result.Success<PatientData>) result).getData().getMedsData().indexOf(target);
                dataListViewModel.delete_MedsData(target); adapter.removeItem(position);
                if(adapter.getItemCount() < 1) binding.contentMainGuide.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MainPage.
     */
    public static DataListFragment newInstance() {
        return new DataListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataListViewModel = new ViewModelProvider(this, new DataListViewModelFactory()).get(DataListViewModel.class);
        dataListViewModel.getActionResult().observe(this, actionResult -> {
            if(actionResult == null) return;
            if(actionResult.getSuccess() != null){
                Toast.makeText(getContext(), actionResult.getSuccess().getDisplayData(), Toast.LENGTH_SHORT).show();
            }
            if(actionResult.getError() != null){
                Toast.makeText(getContext(), actionResult.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDataListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerAdapter for Medication Info.
        Result<PatientData> result = dataListViewModel.getPatientData();
        if(result instanceof Result.Success)
            adapter = new MainDataAdapter(requireActivity().getSupportFragmentManager(), ((Result.Success<PatientData>) result).getData().getMedsData(), onChangedDataListener, onDeleteDataListener);
        else adapter = new MainDataAdapter(requireActivity().getSupportFragmentManager(), new ArrayList<>(), onChangedDataListener, onDeleteDataListener);

        binding.contentMainMeds.setHasFixedSize(false);
        binding.contentMainMeds.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.contentMainMeds.setAdapter(adapter);
        binding.contentMainMeds.addItemDecoration(new VerticalItemDecorator(20));
        binding.contentMainMeds.addItemDecoration(new HorizontalItemDecorator(10));

        binding.contentMainAdd.setOnClickListener(v -> {
            DialogFragment addDataDialog = new DataSettingDialog(onAddedDataListener, null);
            Bundle bundle = new Bundle();
            bundle.putParcelable("BeforeModify", null);
            addDataDialog.setArguments(bundle);
            addDataDialog.show(requireActivity().getSupportFragmentManager(), "DATA_DIALOG_FRAGMENT");
        });

        if(adapter.getItemCount() > 0)
            binding.contentMainGuide.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
        dataListViewModel = null;
    }
}