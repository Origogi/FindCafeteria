package com.cafeteria.free.findcafeteria.view;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.databinding.ActivityMapBinding;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

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

        getData();
        initView();
    }

    @SuppressLint("CheckResult")
    private void getData() {
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
    }

    private void initView() {

        mapPagerAdapter = new MapPagerAdapter(this, cafeteriaList);
        binding.mapindicator.setAdapter(mapPagerAdapter);
        binding.mapindicator.setClipToPadding(false);
        binding.mapindicator.setPageMargin(20);
        binding.mapindicator.setPadding(0, 0, 40, 0);

        //슬라이드 할때 마커 이동,카메라 이동
        binding.mapindicator.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                LatLng loc = new LatLng(Double.parseDouble(cafeteriaList.get(position).getLatitude()), Double.parseDouble(cafeteriaList.get(position).getLongitude()));
                CameraUpdate center = CameraUpdateFactory.newLatLngZoom(loc, 14);
                gMap.animateCamera(center);

                if (selectedMarker != null) {
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_pin_drop_black_18dp));
                    selectedMarker.setZIndex(0);
                }

                //선택한 마커 이미지 변경
                if (markers.size() != 0) {
                    selectedMarker = markers.get(position);
                    selectedMarker.setZIndex(99);
                    selectedMarker.setIcon(null);
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

        //초기 카메라 설정
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.565953, 126.976852), 11)); //기본 서울시청

        // 마커 추가
        List<LatLng> latLngList = new ArrayList<>();
        for (int i = 0; i < cafeteriaList.size(); i++) {

            CafeteriaData cafeteria = cafeteriaList.get(i);
            LatLng latLng = new LatLng(Double.parseDouble(cafeteria.getLatitude()), Double.parseDouble(cafeteria.getLongitude()));
            latLngList.add(latLng);
            markers.add(addMarker(cafeteria, i));
        }

        updateCamera(latLngList);
        gMap.setOnMarkerClickListener(this);

    }

    private Marker addMarker(CafeteriaData cafeteria, int index) {

        LatLng position = new LatLng(Double.parseDouble(cafeteria.getLatitude()), Double.parseDouble(cafeteria.getLongitude()));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(cafeteria.getFacilityName());
        markerOptions.position(position);
        markerOptions.snippet(String.valueOf(index));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_pin_drop_black_18dp));

        return gMap.addMarker(markerOptions);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //해당 뷰페이지 이동 -> 스니펫에 인덱스를 넣어 태그로 이용함. ( 마커를 클릭할때와 뷰페이지를 연결 )
        binding.mapindicator.setCurrentItem(Integer.parseInt(marker.getSnippet()));
        return true;
    }

    //카메라 중앙으로 이동
    public void updateCamera(List<LatLng> latLngList) {
        LatLng center = getCenterLatLng(latLngList);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 11));

    }
    /**
     * 여러개의 포인트중 가운데 포인트 리턴
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
}
