package com.cafeteria.free.findcafeteria.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.model.room.dao.CafeteriaDataDao;
import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.cafeteria.free.findcafeteria.view.DetailActivity;
import com.cafeteria.free.findcafeteria.view.MapActivity;
import com.cafeteria.free.findcafeteria.view.RecyclerViewAdapter;
import com.cafeteria.free.findcafeteria.viewmodel.MainViewModel;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SearchFragment extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private FloatingActionButton mapFab;
    private GestureDetector gestureDetector;
    private View noItemLayout;

    private String currentQuery;

    private boolean isDestroyed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.d("");

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return initView(view);
    }

    @NonNull
    private View initView(View view) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);

        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(layoutManager);

        noItemLayout = view.findViewById(R.id.no_item_layout);

        recyclerViewAdapter = new RecyclerViewAdapter(getContext());
        recyclerView.setAdapter(recyclerViewAdapter);

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mapFab = view.findViewById(R.id.map_fab);
        mapFab.setVisibility(View.GONE);

        mapFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    startMapActivity(currentQuery);
                    return false;
                } else {
                    return true;
                }
            }
        });

        MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        viewModel.getSubmitKeywordLiveData().observe(this, keyword -> {
            updateView(keyword);
        });

        return view;
    }


    private void updateView(String query) {
        Logger.d("");

        if (isDestroyed) {
            initView(getView());
        }

        currentQuery = query;

        CafeteriaDataProvider.getInstance()
                .getCafeteriaDataFilteredAddress(getContext(), query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<CafeteriaData>>() {
                    @Override
                    public void onSuccess(List<CafeteriaData> result) {

                        if (result.isEmpty()) {
                            showErrorDialog(query);
                            mapFab.setVisibility(View.GONE);
                            noItemLayout.setVisibility(View.VISIBLE);

                            recyclerViewAdapter.reset();
                        } else {
                            updateRecycleView(result);
                            mapFab.setVisibility(View.VISIBLE);
                            noItemLayout.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }
                });
    }


    private void updateRecycleView(List<CafeteriaData> list) {
        recyclerViewAdapter.reset();
        recyclerViewAdapter.setCardViewDtos(list);
    }

    private void showErrorDialog(String keyword) {
        new AlertDialog.Builder(getContext())
                .setTitle("잘못된 검색어")
                .setMessage(keyword + " 을 찾을 수 없습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    private void startDetailActivity(View childView, CafeteriaData data) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("data", data);


        View name = childView.findViewById(R.id.name);

        View time = childView.findViewById(R.id.timeLayout);
        View location = childView.findViewById(R.id.locationLayout);
        View phone = childView.findViewById(R.id.phoneLayout);

        Pair[] pairs = new Pair[4];

        pairs[0] = new Pair<>(time, getString(R.string.timeTransition));
        pairs[1] = new Pair<>(location, getString(R.string.locationTransition));
        pairs[2] = new Pair<>(phone, getString(R.string.phoneTransition));
        pairs[3] = new Pair<>(name, getString(R.string.nameTransition));


        ActivityOptionsCompat options = (ActivityOptionsCompat) ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), pairs);

        startActivity(intent, options.toBundle());
    }

    private void startMapActivity(String query) {
        Logger.d("clicked" + query);

        Intent intent = new Intent(getActivity(), MapActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }
}
