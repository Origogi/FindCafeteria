package com.cafeteria.free.findcafeteria.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.model.DataLoadState;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.util.Logger;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Created by gimjeongtae on 14/02/2019.
 */

public class LoadingViewModel extends AndroidViewModel {

    MutableLiveData<DataLoadState> loadedComplete = new MutableLiveData<>();

    public LoadingViewModel(@NonNull Application application) {
        super(application);
        loadedComplete.setValue(DataLoadState.NOT_YET);
    }


    public void startToLoad() {
        CafeteriaDataProvider
                .getInstance()
                .getVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(version -> Logger.d("version:" + version));

        CafeteriaDataProvider.getInstance().startToLoadData((result) -> {
            if (null != loadedComplete) {
                loadedComplete.setValue(result);
            }
        });
    }

    public LiveData<DataLoadState> isLoadedComplete() {
        return loadedComplete;
    }

 }
