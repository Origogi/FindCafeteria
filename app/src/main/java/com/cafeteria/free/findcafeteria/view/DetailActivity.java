package com.cafeteria.free.findcafeteria.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.databinding.ActivityDetailBinding;
import com.cafeteria.free.findcafeteria.model.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.ImageProvider;
import com.cafeteria.free.findcafeteria.model.ImageResponse;
import com.cafeteria.free.findcafeteria.util.ImageSliderAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityDetailBinding binding;
    String longitude;
    String latitude;
    private int dotsCount;
    private ImageView[] dots;
    private ImageSliderAdapter imageSliderAdapter;
    private CafeteriaData cafeteriaData;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.setActivity(this);

        imageSliderAdapter = new ImageSliderAdapter(this, Glide.with(this));
        binding.homeslider.setAdapter(imageSliderAdapter);

        setData();
        setUiPageViewController();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(cafeteriaData.getFacilityName());
        mMap.addMarker(markerOptions);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setUiPageViewController() {

        dotsCount = 3;
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);

            binding.viewPagerCountDots.addView(dots[i], params);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

        binding.homeslider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                }
                if (dots != null) {
                    dots[position % dotsCount].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setData() {

        cafeteriaData = getIntent().getParcelableExtra("data");
        if (cafeteriaData == null)
            return;


        binding.address.setText(cafeteriaData.getAddress());
        binding.date.setText(cafeteriaData.getDate());
        binding.time.setText(cafeteriaData.getTime());
        binding.phone.setText(cafeteriaData.getPhone());
        binding.target.setText(cafeteriaData.getTarget());
        binding.tvTitlebar.setText(cafeteriaData.getFacilityName());

        latitude = cafeteriaData.getLatitude();
        longitude = cafeteriaData.getLongitude();

        ImageProvider imageProvider = new ImageProvider();
        Observable<ImageResponse> obser = imageProvider.get(cafeteriaData.getFacilityName());
        obser.observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> updateImage(it));



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(this);

    }

    private void updateImage(ImageResponse imageResponse) {

        //3개만 가져오는걸로 변경
        List<String> images = new ArrayList<>();

        for (int i = 0; i < 3 && i < imageResponse.imageInfos.size(); i++) {
            images.add(imageResponse.imageInfos.get(i).imageUrl);
        }

        imageSliderAdapter.addImageUri(images);
        imageSliderAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.homeslider.setInterval(5000);
        binding.homeslider.startAutoScroll(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.homeslider.stopAutoScroll();
    }

}
