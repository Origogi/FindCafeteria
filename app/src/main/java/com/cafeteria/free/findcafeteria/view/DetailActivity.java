package com.cafeteria.free.findcafeteria.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.databinding.ActivityDetailBinding;
import com.cafeteria.free.findcafeteria.model.ImageProvider;
import com.cafeteria.free.findcafeteria.model.ImageResponse;
import com.cafeteria.free.findcafeteria.model.room.dao.CafeteriaDataDao;
import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.util.ImageSliderAdapter;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.cafeteria.free.findcafeteria.util.MyScaleAnimation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;


public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityDetailBinding binding;
    private String longitude;
    private String latitude;
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

        binding.backButton.setOnClickListener(v -> {
            super.onBackPressed();
        });

        Logger.d(cafeteriaData.toString());

        binding.favoriteButton.setSelected(cafeteriaData.isFavorite());

        binding.favoriteButton.setOnTouchListener(new View.OnTouchListener() {
            boolean checked = cafeteriaData.isFavorite();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setTag("touched");

                    v.startAnimation(MyScaleAnimation.instance);
                    v.setSelected(!v.isSelected());

                    if (v.isSelected()) {
                        Toast.makeText(DetailActivity.this, "즐겨찾기에 추가가 되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DetailActivity.this, "즐겨찾기에 삭제가 되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    new Thread(() -> {
                        CafeteriaDataDao dao = AppDatabase.getInstance(getApplication()).getCafeteriaDataDao();
                        cafeteriaData.setFavorite(checked);
                        int result = dao.update(cafeteriaData);
                        Logger.d("" + result);
                    }).start();

                    return false;
                }
                return true;
            }
        });

        String phone = cafeteriaData.getPhone();

        if (TextUtils.isEmpty(phone)) {
            binding.startCallButton.setEnabled(false);
        } else {
            binding.startCallButton.setOnClickListener(v -> {

                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(callIntent);
            });
        }


        binding.startShareButton.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");

            String shareContent = "";

            //시설명
            shareContent += cafeteriaData.getFacilityName() + "\n\n";

            //주소
            shareContent += "주소 : " + cafeteriaData.getAddress() + "\n";

            //전화번호
            shareContent += "전화번호 : " + cafeteriaData.getPhone();

            intent.putExtra(Intent.EXTRA_TEXT, shareContent);

            Intent chooser = Intent.createChooser(intent, "공유");
            startActivity(chooser);

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //TODO crash~~~~
        //latitude 이게 null인 case가 존재하는 거 같음
        Logger.d(latitude + "/" + longitude);
        //2019-05-06 16:46:20.341 21325-21325/com.cafeteria.free.findcafeteria D/CAFETERIA: [DetailActivity_110]: /
        //2019-05-06 16:46:18.665 21325-21325/com.cafeteria.free.findcafeteria D/CAFETERIA: [DetailActivity_110]: 37.672452/126.761677

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

    private void setUiPageViewController(int count) {
        ImageView[] dots;

        dots = new ImageView[count];

        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 5, 20, 0);


            binding.viewPagerCountDots.addView(dots[i], params);
        }

        Animation expandAni = AnimationUtils.loadAnimation(this, R.anim.expansion);
        Animation reduceAni = AnimationUtils.loadAnimation(this, R.anim.reduction);


        dots[0].startAnimation(expandAni);

        binding.homeslider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int prevIndex = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                dots[prevIndex].startAnimation(reduceAni);
                dots[position].startAnimation(expandAni);
                prevIndex = position;
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

        Observable<ImageResponse> obser = ImageProvider.get(cafeteriaData.getFacilityName());

        obser.subscribe(new DisposableObserver<ImageResponse>() {
            @Override
            public void onNext(ImageResponse imageResponse) {
                updateImage(imageResponse);
            }

            @Override
            public void onError(Throwable e) {
                Logger.d((e.toString()));
            }

            @Override
            public void onComplete() {
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_map);

        if (latitude == null || longitude == null) {
            Toast.makeText(this, "위치를 찾을 수 없습니다!", Toast.LENGTH_SHORT).show();
            mapFragment.getView().setVisibility(View.GONE);
            binding.imgNotfound.setVisibility(View.VISIBLE);
            return;
        }

        mapFragment.getMapAsync(this);

    }

    private void updateImage(ImageResponse imageResponse) {

        //3개만 가져오는걸로 변경
        List<String> images = new ArrayList<>();

        int imageCount = 0;

        if (imageResponse.imageInfos.size() > 5) {
            imageCount = 5;
        }
        else {
            imageCount = imageResponse.imageInfos.size();
        }

        for (int i = 0; i < 5 && i < imageCount; i++) {
            images.add(imageResponse.imageInfos.get(i).imageUrl);
        }

        imageSliderAdapter.addImageUri(images);
        imageSliderAdapter.notifyDataSetChanged();

        setUiPageViewController(imageCount);

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
