package com.cafeteria.free.findcafeteria.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.util.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.setNestedScrollingEnabled(false);

        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Logger.d(query);

                Observable<List<CafeteriaData>> observable = CafeteriaDataProvider.getInstance().getCafeteriaDataFilteredAddress(query);
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> updateRecycleView(list));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void updateRecycleView(List<CafeteriaData> list) {
        recyclerViewAdapter.reset();
        recyclerViewAdapter.setCardViewDtos(list);
    }
}
