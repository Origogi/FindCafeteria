package com.cafeteria.free.findcafeteria.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.room.entity.SearchHistory;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<CafeteriaData>> favoriteCafeteriaLiveData;
    private MutableLiveData<String> submitKeywordLiveData;
    private MutableLiveData<String> changeKeywordLiveData;
    private Context context;

    public MainViewModel(@NonNull Application application) {
        super(application);

        favoriteCafeteriaLiveData = AppDatabase.getInstance(application).getCafeteriaDataDao().getFavoriteCafeteriaLiveData();
        submitKeywordLiveData = new MutableLiveData<>();
        changeKeywordLiveData = new MutableLiveData<>();
        context = application;
    }

    public LiveData<List<CafeteriaData>> getFavoriteCafeteriaLiveData() {
        return favoriteCafeteriaLiveData;
    }

    public LiveData<String> getSubmitKeywordLiveData() {
        return submitKeywordLiveData;
    }

    public LiveData<String> getChangeKeywordLiveData() {
        return changeKeywordLiveData;
    }

    public void submit(String keyword) {
        new Thread(() -> {
            submitKeywordLiveData.postValue(keyword);
            AppDatabase.getInstance(context).getSearchHistoryDao().add(new SearchHistory(keyword));
        }).start();
    }

    public void change(String keyword) {
        changeKeywordLiveData.setValue(keyword);
    }
}
