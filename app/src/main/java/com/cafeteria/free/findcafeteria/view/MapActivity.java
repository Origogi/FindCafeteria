package com.cafeteria.free.findcafeteria.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.observers.DisposableMaybeObserver;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap gMap;
    List<CafeteriaData> cafeteriaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        String query = getIntent().getStringExtra("query");

        // TODO: 2019-03-10 getData
        Maybe<List<CafeteriaData>> observable = CafeteriaDataProvider.getInstance().getCafeteriaDataFilteredAddress(query);
        observable.subscribeWith(new DisposableMaybeObserver<List<CafeteriaData>>() {
            @Override
            public void onStart() {
                cafeteriaList = new ArrayList<>();
            }

            @Override
            public void onSuccess(List<CafeteriaData> result) {
                cafeteriaList = result;
                updateCamera();
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this::onMapReady);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.565953, 126.976852), 10)); //기본 서울시청
    }

    public void updateCamera() {
//        System.out.println(cafeteriaList.toString());

        List<LatLng> latLngList = new ArrayList<>();

        for (CafeteriaData data : cafeteriaList) {

            LatLng latLng = new LatLng(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude()));

            latLngList.add(latLng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(data.getFacilityName());
            gMap.addMarker(markerOptions);
        }
        //카메라 중앙으로 이동
        LatLng center = getCenterLatLng(latLngList);

        System.out.println(center.latitude + " / " + center.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));
    }

    /**
     * 여러개의 포인트중 가운데 포인트 리턴
     * 로직이 틀릴 수 있음 FIXME
     */
    public LatLng getCenterLatLng(List<LatLng> latLng) {

        double maxLat = Double.MAX_VALUE;
        double minLat = Double.MIN_VALUE;
        double maxLon = Double.MAX_VALUE;
        double minLon = Double.MIN_VALUE;

        //최대,최소값 찾기
        for (LatLng item : latLng) {
            double lat = item.latitude;
            double lon = item.longitude;

            maxLat = Math.max(lat, maxLat);
            minLat = Math.min(lat, minLat);
            maxLon = Math.max(lon, maxLon);
            minLon = Math.min(lon, minLon);
        }

        return new LatLng((maxLat + minLat) / 2, (maxLon + minLon) / 2);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
