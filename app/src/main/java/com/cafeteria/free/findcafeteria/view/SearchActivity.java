package com.cafeteria.free.findcafeteria.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.MySuggestionProvider;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.cafeteria.free.findcafeteria.view.fragment.FavoriteFragment;
import com.cafeteria.free.findcafeteria.view.fragment.SearchFragment;
import com.cafeteria.free.findcafeteria.view.fragment.SettingFragment;

public class SearchActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private SearchFragment searchFragment;
    private SettingFragment settingFragment;
    private FavoriteFragment favoriteFragment;

    private ViewPager viewPager;

    private MenuItem prevMenuItem;
    private SearchView searchView;

    private String currentQuery;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logger.d("");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_actions, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Logger.d(query);
                searchFragment.updateView(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
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
            return false;
        });

        viewPager.setOffscreenPageLimit(ViewPager.AUTOFILL_TYPE_TOGGLE);

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

        setupViewPager(viewPager);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        handleIntent(getIntent());
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

    private void startMapActivity(String query) {
        Logger.d("clicked" + query);

        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            if (query.equals(currentQuery)) {
                return;
            }
            currentQuery = query;
            Logger.d("query=" + query);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(currentQuery, null);
            searchView.setQuery(currentQuery, true);
        }
    }
}
