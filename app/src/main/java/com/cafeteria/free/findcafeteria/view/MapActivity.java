package com.cafeteria.free.findcafeteria.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.databinding.ActivityMapBinding;
import com.cafeteria.free.findcafeteria.model.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.CafeteriaDataProvider;
import com.cafeteria.free.findcafeteria.util.MapPagerAdapter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.observers.DisposableMaybeObserver;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public ArrayList<Marker> markers = new ArrayList<>();
    public Marker selectedMarker;  // 현재 선택 돼 있는 마커를 지정
    GoogleMap gMap;
    List<CafeteriaData> cafeteriaList;
    ActivityMapBinding binding;

    private MapPagerAdapter mapPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        binding.setActivity(this);
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
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });

        mapPagerAdapter = new MapPagerAdapter(this, cafeteriaList);

        binding.mapindicator.setAdapter(mapPagerAdapter);
        binding.mapindicator.setClipToPadding(false);
        binding.mapindicator.setPageMargin(20);
        binding.mapindicator.setPadding(0,0,40,0);


        //슬라이드 할때 마커 이동,카메라 이동
        binding.mapindicator.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //카메라 해당 가게로 이동
                LatLng loc = new LatLng(Double.parseDouble(cafeteriaList.get(position).getLatitude()), Double.parseDouble(cafeteriaList.get(position).getLongitude()));
                CameraUpdate center = CameraUpdateFactory.newLatLng(loc);
                gMap.animateCamera(center);

                // 마커 이미지 변경  ->  스니펫으로 마커들마다 인덱스 지정한 후 가져오는데 사실은 스니펫 이렇게 사용하는건 아님 / 플래그 넣은 효과
                if (selectedMarker != null) {
                    int index = Integer.parseInt(selectedMarker.getSnippet());      // TODO: 2019-03-21 마커 인덱스
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_36dp));
                    selectedMarker.setZIndex(0);
                }

                if (markers.size() != 0) {
                    selectedMarker = markers.get(position);
                    selectedMarker.setZIndex(99);
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_36dp));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
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
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.565953, 126.976852), 10)); //기본 서울시청
        updateCamera();
    }

    public void updateCamera() {

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

        //최대,최소값 찾기
        List<Double> latList = new ArrayList<>();
        List<Double> lonList = new ArrayList<>();

        for (LatLng item : latLng) {
            latList.add(item.latitude);
            lonList.add(item.longitude);
        }

        double maxLat = Collections.max(latList);
        double minLat = Collections.min(latList);
        double maxLon = Collections.max(lonList);
        double minLon = Collections.min(lonList);

        maxLat = Double.parseDouble(String.format("%.6f", maxLat));
        minLat = Double.parseDouble(String.format("%.6f", minLat));
        maxLon = Double.parseDouble(String.format("%.6f", maxLon));
        minLon = Double.parseDouble(String.format("%.6f", minLon));

        return new LatLng((maxLat + minLat) / 2.0, (maxLon + minLon) / 2.0);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
