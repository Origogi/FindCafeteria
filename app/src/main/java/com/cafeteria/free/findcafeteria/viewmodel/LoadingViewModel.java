package com.cafeteria.free.findcafeteria.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import io.reactivex.schedulers.Schedulers;


/**
 * Created by gimjeongtae on 14/02/2019.
 */

public class LoadingViewModel extends AndroidViewModel {

    private MutableLiveData<DataLoadState> loadedComplete = new MutableLiveData<>();
    private MutableLiveData<Boolean> networkConnectedLiveData = new MutableLiveData<>();

    private DBVersionDao dbVersionDao;
    private CafeteriaDataDao cafeteriaDataDao;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connected = isNetworkConnected(context);

            if (connected) {
                context.unregisterReceiver(this);
            }

            networkConnectedLiveData.setValue(connected);
        }
    };


    public LoadingViewModel(@NonNull Application application) {
        super(application);
        startToLoad(application);

        loadedComplete.setValue(DataLoadState.NOT_YET);
        networkConnectedLiveData.setValue(isNetworkConnected(application));

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(receiver, intentFilter);

    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void startToLoad(Context context) {
        dbVersionDao = AppDatabase.getInstance(context).getDBVersionDao();
        cafeteriaDataDao = AppDatabase.getInstance(context).getCafeteriaDataDao();

        CafeteriaDataProvider
                .getInstance()
                .getVersion()
                .observeOn(Schedulers.io())
                .switchMap(version -> getCafeteria(version))
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
        } else {
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

    public LiveData<Boolean> getNetworkConnectedLiveData() {
        return networkConnectedLiveData;
    }

}
