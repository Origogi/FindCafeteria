package com.cafeteria.free.findcafeteria.view;


import android.app.FragmentManager;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.MySuggestionProvider;
import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.SearchHistory;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.cafeteria.free.findcafeteria.view.fragment.FavoriteFragment;
import com.cafeteria.free.findcafeteria.view.fragment.HistoryFragment;
import com.cafeteria.free.findcafeteria.view.fragment.SearchFragment;
import com.cafeteria.free.findcafeteria.view.fragment.SettingFragment;
import com.cafeteria.free.findcafeteria.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private SearchFragment searchFragment;
    private SettingFragment settingFragment;
    private FavoriteFragment favoriteFragment;

    private HistoryFragment historyFragment;

    private ViewPager viewPager;

    private MenuItem prevMenuItem;
    private SearchView searchView;

    private MainViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logger.d("");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_actions, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        searchView.setBackgroundResource(R.drawable.corner);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.history, historyFragment);
                    ft.commit();
                    bottomNavigationView.setVisibility(View.GONE);

                }
                else {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.remove(historyFragment).commit();
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Logger.d(query);


                viewModel.submit(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.change(newText);
                return false;
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(15, 15, 15, 15);
        searchView.setLayoutParams(lp);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            boolean result = false;
            switch (menuItem.getItemId()) {
                case R.id.action_search_menu:
                    viewPager.setCurrentItem(0);
                    result = true;
                    break;
                case R.id.action_favorite_menu:
                    viewPager.setCurrentItem(1);
                    result = true;
                    break;
                case R.id.action_setting_menu:
                    viewPager.setCurrentItem(2);
                    result = true;
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        historyFragment = new HistoryFragment();

        setupViewPager(viewPager);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewModel = ViewModelProviders.of(MainActivity.this).get(MainViewModel.class);

        viewModel.getSubmitKeywordLiveData().observe(this, keyword-> {
            viewPager.setCurrentItem(0);

            if (!TextUtils.isEmpty(keyword)) {

                searchFragment.updateView(keyword);

                searchView.setQuery(keyword, false);
                searchView.clearFocus();
            }
        });
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
}
