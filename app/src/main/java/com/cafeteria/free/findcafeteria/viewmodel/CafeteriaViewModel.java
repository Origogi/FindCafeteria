package com.cafeteria.free.findcafeteria.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.util.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class CafeteriaViewModel extends ViewModel {

    private CafeteriaDataProvider cafeteriaDataProvider;

    private MutableLiveData<String> cafeteriaInfo;

    private PublishSubject<String> keywordSubject = PublishSubject.create();

    private Disposable disposable;

    public CafeteriaViewModel() {

        cafeteriaDataProvider = CafeteriaDataProvider.getInstance();
        initSubject();
    }

    private void initSubject() {
//        disposable = keywordSubject
//                .debounce(500, TimeUnit.MILLISECONDS)
//                .doOnNext(keyword -> Logger.d("search=" + keyword))
//                .switchMap(keyword ->
//                        cafeteriaDataProvider.getCafeteriaDataFilteredAddress(keyword)
//                )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete(()->Logger.d("onComplete()"))
//                .subscribe(cafeteriaDataList -> {
//                    cafeteriaInfo.setValue(cafeteriaDataList.get(0).toString());
//                }, error -> {
//                    cafeteriaInfo.setValue(error.getMessage());
//                });
    }

    public LiveData<String> getCafeteriaInfo() {
        if (null == cafeteriaInfo) {
            cafeteriaInfo = new MutableLiveData<String>();
        }
        return cafeteriaInfo;
    }

    public void onKeywordChanged(String keyword) {
        if (disposable != null) {
            if (disposable.isDisposed()) {
                initSubject();
            }
        }

        Logger.d("keyword=" + keyword);
        keywordSubject.onNext(keyword);
    }

}
