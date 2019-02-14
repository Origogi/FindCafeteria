package com.cafeteria.free.findcafeteria.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.DataLoadState;
import com.cafeteria.free.findcafeteria.viewmodel.LoadingViewModel;

public class LoadingActivity extends AppCompatActivity {

    private LoadingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        viewModel = ViewModelProviders.of(this).get(LoadingViewModel.class);
        viewModel.startToLoad();

        viewModel.isLoadedComplete().observe(this, result -> {
            if (result == DataLoadState.SUCCESS) {
                startActivity(new Intent(LoadingActivity.this, SearchActivity.class));
            }
            else if (result == DataLoadState.FAIL) {
                //Error popup
            }
        });

    }
}
