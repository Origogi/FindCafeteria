package com.cafeteria.free.findcafeteria.view.fragment;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.cafeteria.free.findcafeteria.view.DetailActivity;
import com.cafeteria.free.findcafeteria.view.MapActivity;
import com.cafeteria.free.findcafeteria.view.RecyclerViewAdapter;
import com.cafeteria.free.findcafeteria.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableMaybeObserver;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private FloatingActionButton mapFab;
    private GestureDetector gestureDetector;
    private View noItemLayout;

    private String currentQuery;

    private boolean isDestroyed = false;

    private RecyclerView.OnItemTouchListener itemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());

            if (childView != null && gestureDetector.onTouchEvent(e)) {
                ImageView favorite = childView.findViewById(R.id.favorite_img);

                if (TextUtils.isEmpty((String) favorite.getTag())) {
                    int currentPosition = rv.getChildAdapterPosition(childView);
                    startDetailActivity(childView, recyclerViewAdapter.get(currentPosition));
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

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d("");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.d("");

        View view = inflater.inflate(R.layout.fragment_search, container, false);


        return initView(view);
    }

    @NonNull
    private View initView(View view) {
        Logger.d("");

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

        viewModel.getFavoriteCafeteriaLiveData().observe(this, favorites -> {
            if (!TextUtils.isEmpty(currentQuery)) {
                updateView(currentQuery);
            }
        });


        viewModel.getSubmitKeywordLiveData().observe(this, keyword-> {
            updateView(keyword);

        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    private void updateView(String query) {
        Logger.d("");

        if (isDestroyed) {
            initView(getView());
        }

        currentQuery = query;

        Maybe<List<CafeteriaData>> observable = CafeteriaDataProvider.getInstance().getCafeteriaDataFilteredAddress(getContext(), query);

        observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableMaybeObserver<List<CafeteriaData>>() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(List<CafeteriaData> result) {
                    updateRecycleView(result);
                    mapFab.setVisibility(View.VISIBLE);
                    noItemLayout.setVisibility(View.GONE);

                }

                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                }

                @Override
                public void onComplete() {
                    showErrorDialog(query);
                    mapFab.setVisibility(View.GONE);
                    noItemLayout.setVisibility(View.VISIBLE);

                    recyclerViewAdapter.reset();
                }
            });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
