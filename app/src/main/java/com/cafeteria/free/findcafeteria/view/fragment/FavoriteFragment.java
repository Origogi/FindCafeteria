package com.cafeteria.free.findcafeteria.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.view.DetailActivity;
import com.cafeteria.free.findcafeteria.view.RecyclerViewAdapter;
import com.cafeteria.free.findcafeteria.viewmodel.MainViewModel;


public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private GestureDetector gestureDetector;
    private View noItemLayout;

    private RecyclerView.OnItemTouchListener itemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());

            if (childView != null && gestureDetector.onTouchEvent(e)) {
                ImageView favorite = childView.findViewById(R.id.favorite_img);

                if (TextUtils.isEmpty((String) favorite.getTag())) {
                    int currentPosition = rv.getChildAdapterPosition(childView);
                    startDetailActivity(recyclerViewAdapter.get(currentPosition));
                }
                favorite.setTag("");
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

    };

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favorite, container, false);

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

        recyclerView.addOnItemTouchListener(itemTouchListener);

        MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        viewModel.getFavoriteCafeteriaLiveData().observe(this, favorites -> {
            if (favorites.isEmpty()) {
                noItemLayout.setVisibility(View.VISIBLE);
            }
            else {
                noItemLayout.setVisibility(View.GONE);
            }

            recyclerViewAdapter.setCardViewDtos(favorites);
            recyclerViewAdapter.notifyDataSetChanged();
        });

        return view;
    }


    private void startDetailActivity(CafeteriaData data) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

}
