package com.capstone.project_niyakneyak.ui.main.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.project_niyakneyak.data.Result;
import com.capstone.project_niyakneyak.data.patient_model.MedsData;
import com.capstone.project_niyakneyak.data.patient_model.PatientData;
import com.capstone.project_niyakneyak.databinding.FragmentMainPageBinding;
import com.capstone.project_niyakneyak.ui.main.listener.OnDeleteDataListener;
import com.capstone.project_niyakneyak.ui.main.fragment.viewmodel.MainViewModel;
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
    private MainViewModel mainViewModel;

    private final OnDeleteDataListener onDeleteDataListener = new OnDeleteDataListener() {
        @Override
        public void onDeletedData(MedsData target) {
            Log.d("MainActivity","Data Deletion called");

            Result<PatientData> result = mainViewModel.getPatientData();
            if(result instanceof Result.Success){
                int position = ((Result.Success<PatientData>) result).getData().getMedsData().indexOf(target);
                mainViewModel.delete_MedsData(target); adapter.removeItem(position);
                if(adapter.getItemCount() < 1) binding.contentMainGuide.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MainPage.
     */
    public static MainPageFragment newInstance(MainViewModel mainViewModel) {
        MainPageFragment fragment = new MainPageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("MainViewModel", mainViewModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        assert bundle != null;
        mainViewModel = (MainViewModel) bundle.getSerializable("MainViewModel");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentMainPageBinding.inflate(inflater, container, false);

        // RecyclerAdapter for Medication Info.
        Result<PatientData> result = mainViewModel.getPatientData();
        if(result instanceof Result.Success)
            adapter = new MedsDataAdapter(getContext(), ((Result.Success<PatientData>) result).getData().getMedsData(), onDeleteDataListener);
        else adapter = new MedsDataAdapter(getContext(), new ArrayList<>(), onDeleteDataListener);

        binding.contentMainMeds.setHasFixedSize(false);
        binding.contentMainMeds.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.contentMainMeds.setAdapter(adapter);
        binding.contentMainMeds.addItemDecoration(new VerticalItemDecorator(20));
        binding.contentMainMeds.addItemDecoration(new HorizontalItemDecorator(10));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(adapter.getItemCount() > 0)
            binding.contentMainGuide.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; adapter = null;
        mainViewModel = null;
    }

    public void notifyDataChanged(){
        adapter.notifyDataSetChanged();
        if(adapter.getItemCount() < 1)
            binding.contentMainGuide.setVisibility(View.VISIBLE);
        else binding.contentMainGuide.setVisibility(View.GONE);
    }
}