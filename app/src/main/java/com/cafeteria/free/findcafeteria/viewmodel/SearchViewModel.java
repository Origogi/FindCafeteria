package com.cafeteria.free.findcafeteria.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private LiveData<List<CafeteriaData>> favoriteCafeteriaLiveData;

    public SearchViewModel(@NonNull Application application) {
        super(application);

        favoriteCafeteriaLiveData = AppDatabase.getInstance(application).getCafeteriaDataDao().getFavoriteCafeteria();
    }

    public LiveData<List<CafeteriaData>> getFavoriteCafeteriaLiveData() {
        return favoriteCafeteriaLiveData;
    }
}
