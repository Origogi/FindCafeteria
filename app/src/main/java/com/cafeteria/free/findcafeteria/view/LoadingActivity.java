package com.cafeteria.free.findcafeteria.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cafeteria.free.findcafeteria.model.DataLoadState;
import com.cafeteria.free.findcafeteria.viewmodel.LoadingViewModel;

public class LoadingActivity extends AppCompatActivity {

    private LoadingViewModel viewModel;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(LoadingViewModel.class);
        showProgressDialog();

        viewModel.isLoadedComplete().observe(this, result -> {
            if (result == DataLoadState.SUCCESS) {
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                dialog.dismiss();
                finish();
            }
            else if (result == DataLoadState.FAIL) {
                //Error popup
            }
        });
    }

    private void showProgressDialog() {
        dialog = ProgressDialog.show(LoadingActivity.this,"데이터 로드 중",
                "잠시만 기다려 주세요.",true);
    }

}
