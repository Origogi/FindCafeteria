package com.cafeteria.free.findcafeteria.model;

import android.text.TextUtils;

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

public class CafeteriaDataProvider {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference().child("records");

    private List<CafeteriaData> cafeteriaDataList = new ArrayList<>();

    private static CafeteriaDataProvider sCafeteriaDataProvider = new CafeteriaDataProvider();

    private CafeteriaDataProvider() {
    }

    public static CafeteriaDataProvider getInstance() {
        return sCafeteriaDataProvider;
    }


    public void startToLoadData(DataLoadListener listener) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Logger.d("size=" + dataSnapshot.getChildrenCount());

                dataSnapshot.getChildren().forEach((data)-> {
                    CafeteriaData cafeteriaData = data.getValue(CafeteriaData.class);
                    Logger.d(cafeteriaData.toString());
                    cafeteriaDataList.add(cafeteriaData);

                    listener.onComplete(DataLoadState.SUCCESS);
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onComplete(DataLoadState.FAIL);
            }
        });
    }

    public Maybe<List<CafeteriaData>> getCafeteriaDataFilteredAddress(String keyword) {
        Logger.d("search from list=" + keyword);

        if (TextUtils.isEmpty(keyword)) {
            throw new IllegalArgumentException("Keyword is empty");
        }

        List<CafeteriaData> filteredData = cafeteriaDataList.stream().filter( data ->
            data.getAddress().contains(keyword) || data.getAddress2().contains(keyword)
        ).collect(Collectors.toList());

        if ( filteredData.isEmpty() ) {
            return Maybe.empty();
        }
        return Maybe.just(filteredData);
    }
}
