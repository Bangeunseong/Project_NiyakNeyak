package com.capstone.project_niyakneyak.ui.main.fragment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CheckListViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    private Application application;
    public CheckListViewModelFactory(Application application){this.application = application;}
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CheckListViewModel.class)) {
            return (T) new CheckListViewModel(application);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
