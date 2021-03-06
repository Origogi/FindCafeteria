package com.cafeteria.free.findcafeteria.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.DataLoadState;
import com.cafeteria.free.findcafeteria.viewmodel.LoadingViewModel;

public class LoadingActivity extends AppCompatActivity {

    private LoadingViewModel viewModel;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        viewModel = ViewModelProviders.of(this).get(LoadingViewModel.class);

        ImageView progressImageView = findViewById(R.id.loading);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(progressImageView);
        Glide.with(this).load(R.raw.spinner).into(imageViewTarget);

        View WarningMessageView = findViewById(R.id.warningLayout);

        viewModel.isLoadedComplete().observe(this, result -> {
            if (result == DataLoadState.SUCCESS) {

                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);

                View icon = findViewById(R.id.icon);

                Pair[] pairs = new Pair[1];

                pairs[0] = new Pair<>(icon, getString(R.string.iconTransition));

                ActivityOptionsCompat options = (ActivityOptionsCompat) ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, pairs);

                startActivity(intent, options.toBundle());

                finish();
            }
            else if (result == DataLoadState.FAIL) {
                //Error popup
            }
        });

        viewModel.getNetworkConnectedLiveData().observe(this, connected -> {
            if (connected) {
                progressImageView.setVisibility(View.VISIBLE);
                WarningMessageView.setVisibility(View.GONE);
            }
            else {
                progressImageView.setVisibility(View.GONE);
                WarningMessageView.setVisibility(View.VISIBLE);

            }
        });
    }


}
