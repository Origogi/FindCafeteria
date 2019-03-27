package com.cafeteria.free.findcafeteria.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.model.DataLoadState;


/**
 * Created by gimjeongtae on 14/02/2019.
 */

public class LoadingViewModel extends ViewModel {

    MutableLiveData<DataLoadState> loadedComplete;

    public LoadingViewModel () {

    }

    public void startToLoad() {
        CafeteriaDataProvider.getInstance().startToLoadData((result) -> {
            if (null != loadedComplete) {
                loadedComplete.setValue(result);
            }
        });
    }

    public LiveData<DataLoadState> isLoadedComplete() {
        if (loadedComplete == null) {
            loadedComplete = new MutableLiveData<>();
            loadedComplete.setValue(DataLoadState.NOT_YET);
        }

        return loadedComplete;
    }

 }
