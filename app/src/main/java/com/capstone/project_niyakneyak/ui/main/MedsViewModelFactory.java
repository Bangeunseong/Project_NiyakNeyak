package com.capstone.project_niyakneyak.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.capstone.project_niyakneyak.data.MedsDataSource;
import com.capstone.project_niyakneyak.data.MedsRepository;

public class MedsViewModelFactory implements ViewModelProvider.Factory {
    private String username;
    public MedsViewModelFactory(String username){this.username = username;}
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MedsViewModel.class)) {
            return (T) new MedsViewModel(MedsRepository.getInstance(new MedsDataSource(username)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
