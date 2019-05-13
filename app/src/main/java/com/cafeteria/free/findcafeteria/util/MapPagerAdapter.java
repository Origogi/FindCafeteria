package com.cafeteria.free.findcafeteria.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.ImageProvider;
import com.cafeteria.free.findcafeteria.model.ImageResponse;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.view.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;


public class MapPagerAdapter extends PagerAdapter {

    private Activity activity;
    private LayoutInflater mLayoutInflater;
    private List<CafeteriaData> cafeteriaDataList;
    private List<String> thumbnailList;


    public MapPagerAdapter(Activity activity, List<CafeteriaData> cafeteriaDataList) {
        this.activity = activity;
        this.mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.cafeteriaDataList = cafeteriaDataList;
    }

    //뷰페이저를 리프레쉬하려면 이 메소드 써야한대요
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return cafeteriaDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @SuppressLint("CheckResult")
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.viewpager_mapinfo, container, false);

        ImageView mapinfo_thumbnail = itemView.findViewById(R.id.mapinfo_thumbnail);
        TextView mapinfo_cafename = itemView.findViewById(R.id.mapinfo_cafename);
        TextView mapinfo_location = itemView.findViewById(R.id.mapinfo_location);

        CafeteriaData cafeteria = cafeteriaDataList.get(position);

        //썸네일용 이미지 한개 가져옴
        thumbnailList = new ArrayList<>();
        Observable<ImageResponse> obser = ImageProvider.get(cafeteria.getFacilityName());
        obser.observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> {
                    Glide.with(activity).load(it.imageInfos.get(0).imageUrl).placeholder(R.drawable.loadingimage).error(R.drawable.loadingimage).into(mapinfo_thumbnail);
                });

        mapinfo_cafename.setText(cafeteria.getFacilityName());
        mapinfo_location.setText(cafeteria.getAddress());

        itemView.setOnClickListener(v -> startDetailActivity(itemView, cafeteria));

        container.addView(itemView);
        return itemView;
    }


//    private void startDetailActivity(CafeteriaData data) {
////        Intent intent = new Intent(activity, DetailActivity.class);
////        intent.putExtra("data", data);
////        activity.startActivity(intent);
//
//        selectedCafeteriaData.onNext(data);
//    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    private void startDetailActivity(View childView, CafeteriaData data) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("data", data);


        View name = childView.findViewById(R.id.mapinfo_cafename);
        View layout = childView.findViewById(R.id.cardLayout);
        View image = childView.findViewById(R.id.mapinfo_thumbnail);


        Pair[] pairs = new Pair[3];

        pairs[0] = new Pair<>(layout, activity.getString(R.string.cardTransition));
        pairs[1] = new Pair<>(name, activity.getString(R.string.nameTransition));
        pairs[2] = new Pair<>(image, activity.getString(R.string.imageTransition));


        ActivityOptionsCompat options = (ActivityOptionsCompat) ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity, pairs);

        activity.startActivity(intent, options.toBundle());
    }


}
