package com.cafeteria.free.findcafeteria.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.cafeteria.free.findcafeteria.view.fragment.FavoriteFragment;
import com.cafeteria.free.findcafeteria.view.fragment.SearchFragment;
import com.cafeteria.free.findcafeteria.view.fragment.SettingFragment;
import com.cafeteria.free.findcafeteria.viewmodel.MainViewModel;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private SearchFragment searchFragment;
    private SettingFragment settingFragment;
    private FavoriteFragment favoriteFragment;
    private TextView titleTextView;

    private ViewPager viewPager;

    private MenuItem prevMenuItem;

    private MainViewModel viewModel;

    private String keyword;

    private BackPressCloseHandler closeHandler;

    private View titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        titleTextView = findViewById(R.id.titleTextView);
        titleBar = findViewById(R.id.titlebar);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        ImageButton searchButton = findViewById(R.id.searchButton);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            boolean result = false;
            switch (menuItem.getItemId()) {
                case R.id.action_search_menu:
                    viewPager.setCurrentItem(0);
                    titleTextView.setText(keyword);
                    result = true;
                    break;
                case R.id.action_favorite_menu:
                    viewPager.setCurrentItem(1);
                    result = true;
                    titleTextView.setText(getString(R.string.app_name));
                    break;
                case R.id.action_setting_menu:
                    viewPager.setCurrentItem(2);
                    result = true;
                    titleTextView.setText(getString(R.string.app_name));
                    break;
            }
            return result;
        });

        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Logger.d("onPageSelected: " + position);

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

                if (position == 0) {
                    titleTextView.setText(keyword);
                } else {
                    titleTextView.setText(getString(R.string.app_name));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setupViewPager(viewPager);

        viewModel = ViewModelProviders.of(MainActivity.this).get(MainViewModel.class);

        viewModel.getSubmitKeywordLiveData().observe(this, keyword -> {
            viewPager.setCurrentItem(0);

            if (!TextUtils.isEmpty(keyword)) {
                titleTextView.setText(keyword);
//                searchFragment.updateView(keyword);
                this.keyword = keyword;
            }
        });

        searchButton.setOnClickListener(v -> {
            startSearchBarActivity();
        });

        closeHandler = new BackPressCloseHandler(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            String keyword = data.getStringExtra("keyword");
            viewModel.submit(keyword);
        }
    }

    @Override
    public void onBackPressed() {
        closeHandler.onBackPressed();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        searchFragment = new SearchFragment();
        favoriteFragment = new FavoriteFragment();
        settingFragment = new SettingFragment();

        adapter.addFragment(searchFragment);
        adapter.addFragment(favoriteFragment);
        adapter.addFragment(settingFragment);
        viewPager.setAdapter(adapter);
    }

    private class BackPressCloseHandler {

        long backKeyPressedTime = 0;
        Activity activity;


        BackPressCloseHandler(Activity activity) {
            this.activity = activity;
        }

        void onBackPressed() {
            long diff = System.currentTimeMillis() - backKeyPressedTime;

            if (diff > 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(activity, "종료하려면 뒤로 버튼을 한번 더!", Toast.LENGTH_SHORT).show();
            } else {
                activity.finish();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void startSearchBarActivity() {
        Intent intent = new Intent(MainActivity.this, SearchBarActivity.class);

        Pair[] pairs = new Pair[1];

        pairs[0] = new Pair<>(titleBar, getString(R.string.cardTransition));


        ActivityOptionsCompat options = (ActivityOptionsCompat) ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, pairs);

        startActivityForResult(intent, 100, options.toBundle());
    }

}
