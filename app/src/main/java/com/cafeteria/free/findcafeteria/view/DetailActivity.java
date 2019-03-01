package com.cafeteria.free.findcafeteria.view;

import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;

    private ArrayList<String> images = new ArrayList<>();
    private int dotsCount;
    private ImageView[] dots;
    private ImageSliderAdapter imageSliderAdapter;
    private CafeteriaData cafeteriaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail);
        binding.setActivity(this);

        binding.collapsingToolbar.setTitle("급식소");
        binding.collapsingToolbar.setExpandedTitleTextAppearance(R.style.CollapsedAppBar);
        binding.collapsingToolbar.setExpandedTitleMargin(0, 10, 0, 5);

        imageSliderAdapter = new ImageSliderAdapter(this, images, Glide.with(this));
        binding.homeslider.setAdapter(imageSliderAdapter);

        setUiPageViewController();
        setData();

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
        if(cafeteriaData==null)
            return;

        TextView offerName = findViewById(R.id.offerName);
        TextView address = findViewById(R.id.address);
        TextView date = findViewById(R.id.date);
        TextView time = findViewById(R.id.time);
        TextView phone = findViewById(R.id.phone);
        TextView target = findViewById(R.id.target);

        offerName.setText(cafeteriaData.getOfferName());
        address.setText(cafeteriaData.getAddress());
        date.setText(cafeteriaData.getDate());
        time.setText(cafeteriaData.getTime());
        phone.setText(cafeteriaData.getPhone());
        target.setText(cafeteriaData.getTarget());

        //이미지 추가
        images.clear();
        ImageProvider imageProvider = new ImageProvider();
        Observable<ImageResponse> obser = imageProvider.get(cafeteriaData.getFacilityName());
        obser
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(it -> updateImage(it));
    }

    private void updateImage(ImageResponse imageResponse) {

        Log.d("dd", "updateImage: "+ imageResponse.imageInfos.size());
//        for(int i=0;i<imageResponse.imageInfos.size();i++){
//            images.add(imageResponse.imageInfos.get(i).imageUrl);
//        }

        //3개만 가져오는걸로 변경
        images.add(imageResponse.imageInfos.get(0).imageUrl);
        images.add(imageResponse.imageInfos.get(1).imageUrl);
        images.add(imageResponse.imageInfos.get(2).imageUrl);
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
