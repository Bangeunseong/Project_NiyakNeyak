package com.capstone.project_niyakneyak.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.capstone.project_niyakneyak.data.PatientDataSource;
import com.capstone.project_niyakneyak.data.PatientRepository;

public class PatientViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PatientViewModel.class)) {
            return (T) new PatientViewModel(PatientRepository.getInstance(new PatientDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
