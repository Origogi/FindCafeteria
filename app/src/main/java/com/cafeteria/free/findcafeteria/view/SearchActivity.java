package com.cafeteria.free.findcafeteria.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.util.Logger;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.observers.DisposableMaybeObserver;


public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    SearchView searchView;

    GestureDetector gestureDetector;

    RecyclerView.OnItemTouchListener itemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(),e.getY());
            if(childView != null && gestureDetector.onTouchEvent(e)){
                Logger.d("");
                int currentPosition = rv.getChildAdapterPosition(childView);
                startDetailActivity(recyclerViewAdapter.get(currentPosition));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(true);

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        gestureDetector= new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        recyclerView.addOnItemTouchListener(itemTouchListener);
        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Logger.d(query);

                Maybe<List<CafeteriaData>> observable = CafeteriaDataProvider.getInstance().getCafeteriaDataFilteredAddress(query);

                observable.subscribeWith(new DisposableMaybeObserver<List<CafeteriaData>>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(List<CafeteriaData> result) {
                        updateRecycleView(result);
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        showErrorDialog(query);
                        recyclerViewAdapter.reset();
                    }
                });
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

    private void showErrorDialog(String keyword) {
        new AlertDialog.Builder(SearchActivity.this)
                .setTitle("잘못된 검색어")
                .setMessage(keyword + " 을 찾을 수 없습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();

    }

    private void startDetailActivity(CafeteriaData data) {
        Logger.d("clicked" + data.toString());
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }


}
