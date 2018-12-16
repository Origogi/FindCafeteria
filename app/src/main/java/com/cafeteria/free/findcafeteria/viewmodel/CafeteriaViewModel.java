package com.cafeteria.free.findcafeteria.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.jakewharton.rxrelay2.PublishRelay;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CafeteriaViewModel extends ViewModel {

    private CafeteriaDataProvider cafeteriaDataProvider;

    private MutableLiveData<String> cafeteriaInfo;

    private PublishRelay<String> keywordSubject = PublishRelay.create();

    private Disposable disposable;

    public CafeteriaViewModel() {

        cafeteriaDataProvider = new CafeteriaDataProvider();
        initSubject();
    }

    private void initSubject() {
        disposable = keywordSubject
                .debounce(500, TimeUnit.MILLISECONDS)
                .doOnNext(keyword -> Logger.d("search=" + keyword))
                .switchMap(keyword ->
                        cafeteriaDataProvider.getCafeteriaDataFilteredAddress(keyword)
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(()->Logger.d("onComplete()"))
                .subscribe(data -> {
                    Logger.d(data.toString());
                    cafeteriaInfo.setValue(data.toString());
                }, error -> {
                    cafeteriaInfo.setValue(error.getMessage());
                });
    }

    public LiveData<String> getCafeteriaInfo() {
        if (null == cafeteriaInfo) {
            cafeteriaInfo = new MutableLiveData<String>();
        }
        return cafeteriaInfo;
    }

    public void onKeywordChanged(String keyword) {
        if (disposable != null) {
            Logger.d("Dispose=" + disposable.isDisposed());
            if (disposable.isDisposed()) {
                initSubject();
            }
        }

        Logger.d("keyword=" + keyword);
        keywordSubject.accept(keyword);
    }
}
