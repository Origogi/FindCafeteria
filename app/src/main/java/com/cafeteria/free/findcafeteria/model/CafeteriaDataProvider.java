package com.cafeteria.free.findcafeteria.model;

import android.content.Context;
import android.text.TextUtils;

import com.cafeteria.free.findcafeteria.model.room.dao.CafeteriaDataDao;
import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.MaybeSubject;
import io.reactivex.subjects.PublishSubject;

public class CafeteriaDataProvider {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference recordsRefer = firebaseDatabase.getReference().child("records");
    private DatabaseReference versionRefer = firebaseDatabase.getReference().child("version");

    private static CafeteriaDataProvider sCafeteriaDataProvider = new CafeteriaDataProvider();

    private CafeteriaDataProvider() {
    }

    public Observable<Integer> getVersion() {

        PublishSubject<Integer> subject = PublishSubject.create();

        versionRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int version = dataSnapshot.getValue(Integer.class);
                Logger.d("version=" + version);
                subject.onNext(version);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO

            }
        });

        return subject;
    }

    public static CafeteriaDataProvider getInstance() {
        return sCafeteriaDataProvider;
    }

    public Observable<List<CafeteriaData>> getCafeteriaObservable() {

        PublishSubject<List<CafeteriaData>> subject = PublishSubject.create();

        recordsRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Logger.d("size=" + dataSnapshot.getChildrenCount());

                new Thread(() -> {
                    List<CafeteriaData> cafeteriaDataList = new ArrayList<>();

                    dataSnapshot.getChildren().forEach((data) -> {

                        CafeteriaData cafeteriaData = data.getValue(CafeteriaData.class);
                        cafeteriaDataList.add(cafeteriaData);
                    });

                    subject.onNext(cafeteriaDataList);
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //TODO error Handling
            }
        });

        return subject;
    }

    public Single<List<CafeteriaData>> getCafeteriaDataFilteredAddress(Context context, String keyword) {
        Logger.d("search from list=" + keyword);

        return Single.create(sub -> {
                    CafeteriaDataDao cafeteriaDataDao = AppDatabase.getInstance(context).getCafeteriaDataDao();
                    List<CafeteriaData> filteredData = cafeteriaDataDao.getAllCafeteria().stream().filter(data ->
                            data.getAddress().contains(keyword) || data.getAddress2().contains(keyword)
                    ).collect(Collectors.toList());

                    sub.onSuccess(filteredData);
                }
        );
//
//        MaybeSubject<List<CafeteriaData>> maybeSubject = MaybeSubject.create();
//
//        if (TextUtils.isEmpty(keyword)) {
//            throw new IllegalArgumentException("Keyword is empty");
//        }
//
//        new Thread(()-> {
//            CafeteriaDataDao cafeteriaDataDao = AppDatabase.getInstance(context).getCafeteriaDataDao();
//            List<CafeteriaData> filteredData = cafeteriaDataDao.getAllCafeteria().stream().filter( data ->
//                    data.getAddress().contains(keyword) || data.getAddress2().contains(keyword)
//            ).collect(Collectors.toList());
//
//            if ( filteredData.isEmpty() ) {
//                maybeSubject.onComplete();
//            }
//            else {
//                maybeSubject.onSuccess(filteredData);
//            }
//        }).start();
//
//        return maybeSubject;
    }
}
