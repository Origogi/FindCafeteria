package com.cafeteria.free.findcafeteria.view;

import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.databinding.ActivityDetailBinding;
import com.cafeteria.free.findcafeteria.util.ImageSliderAdapter;

import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;

    private ArrayList<Integer> images = new ArrayList<>();
    private int dotsCount;
    private ImageView[] dots;
    private ImageSliderAdapter imageSliderAdapter;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail);
        binding.setActivity(this);

        imageSliderAdapter = new ImageSliderAdapter(this, images, Glide.with(this));

        // TODO: 2019-02-10 파이어베이스에 저장된 URL경로를 가져오는 걸로 변경 (Integer -> String)
        images.add(R.drawable.common_google_signin_btn_icon_dark);
        images.add(R.drawable.common_full_open_on_phone);
        images.add(R.drawable.common_google_signin_btn_icon_dark_normal_background);
        images.add(R.drawable.loadingimage);

        binding.homeslider.setAdapter(imageSliderAdapter);

        setUiPageViewController();
    }

    private void setUiPageViewController() {

        dotsCount = images.size();
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
                currentPosition = position;
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                }
                if (dots != null) {
                    dots[currentPosition % dotsCount].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
