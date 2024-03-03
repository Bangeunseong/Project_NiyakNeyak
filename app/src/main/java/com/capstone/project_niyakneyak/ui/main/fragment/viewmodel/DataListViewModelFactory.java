package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.capstone.project_niyakneyak.data.patient_resource.PatientDataSource;
import com.capstone.project_niyakneyak.data.patient_resource.PatientRepository;

public class DataListViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DataListViewModel.class)) {
            return (T) new DataListViewModel();
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
