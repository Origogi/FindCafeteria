package com.cafeteria.free.findcafeteria.view.fragment;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;

public class SettingFragment extends Fragment {

    private View removeHistoryLayout;
    private View removeFavoriteLayout;

    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        removeFavoriteLayout = view.findViewById(R.id.remove_favorite);
        removeHistoryLayout = view.findViewById(R.id.remove_history);

        removeHistoryLayout.setOnClickListener(v -> {
            showHistoryDialog();
        });

        removeFavoriteLayout.setOnClickListener(v -> {
            showFavoriteDialog();
        });

        return view;
    }

    private void showHistoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(null);
        builder.setMessage("검색 기록을 삭제하시겠습니까?");
        builder.setPositiveButton("예", (dialog, which) -> {
            new Thread(() -> {
                AppDatabase.getInstance(getContext()).getSearchHistoryDao().deleteAll();
            }).start();
        });
        builder.setNegativeButton("아니오", null);
        builder.show();
    }

    private void showFavoriteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(null);
        builder.setMessage("즐겨찾기 목록을 삭제하시겠습니까?");
        builder.setPositiveButton("예", (dialog, which) -> {
            new Thread(()->{
                AppDatabase.getInstance(getContext()).getCafeteriaDataDao().deleteFavoriteAll();
            }).start();
        });
        builder.setNegativeButton("아니오", null);
        builder.show();

    }

}
