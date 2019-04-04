package com.cafeteria.free.findcafeteria.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.model.DataLoadState;
import com.cafeteria.free.findcafeteria.model.room.dao.CafeteriaDataDao;
import com.cafeteria.free.findcafeteria.model.room.dao.DBVersionDao;
import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.room.entity.DBVersion;
import com.cafeteria.free.findcafeteria.util.Logger;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by gimjeongtae on 14/02/2019.
 */

public class LoadingViewModel extends AndroidViewModel {

    MutableLiveData<DataLoadState> loadedComplete = new MutableLiveData<>();

    DBVersionDao dbVersionDao;
    CafeteriaDataDao cafeteriaDataDao;

    public LoadingViewModel(@NonNull Application application) {
        super(application);
        loadedComplete.setValue(DataLoadState.NOT_YET);
        startToLoad(application);
    }


    private void startToLoad(Context context) {
        dbVersionDao = AppDatabase.getInstance(context).getDBVersionDao();
        cafeteriaDataDao = AppDatabase.getInstance(context).getCafeteriaDataDao();

        CafeteriaDataProvider
                .getInstance()
                .getVersion()
                .observeOn(Schedulers.io())
                .switchMap(version->getCafeteria(version))
                .subscribe(cafeteriaDataList -> {
                    Logger.d(cafeteriaDataList.size() + "");

                    if (!cafeteriaDataList.isEmpty()) {
                        cafeteriaDataDao.deleteAll();
                        cafeteriaDataDao.insertAll(cafeteriaDataList);
                        loadedComplete.postValue(DataLoadState.SUCCESS);
                    }
                });
    }

    private Observable<List<CafeteriaData>> getCafeteria(Integer version) {
        DBVersion dbVersion = dbVersionDao.get();

        if (null != dbVersion && version != dbVersion.getVersion()) {
            dbVersionDao.deleteAll();
            dbVersionDao.insert(new DBVersion());
            return CafeteriaDataProvider.getInstance().getCafeteriaObservable();
        }
        else {
            List<CafeteriaData> cafeteriaDataList = cafeteriaDataDao.getAllCafeteria();
            if (cafeteriaDataList.isEmpty()) {
                return CafeteriaDataProvider.getInstance()
                        .getCafeteriaObservable();
            }
            return Observable
                    .just(cafeteriaDataDao.getAllCafeteria());
        }

    }

    public LiveData<DataLoadState> isLoadedComplete() {
        return loadedComplete;
    }

 }
