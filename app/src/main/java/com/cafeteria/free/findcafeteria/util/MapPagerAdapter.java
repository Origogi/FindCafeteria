package com.cafeteria.free.findcafeteria.util;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.ImageProvider;
import com.cafeteria.free.findcafeteria.model.ImageResponse;
import com.cafeteria.free.findcafeteria.view.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class MapPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<CafeteriaData> cafeteriaDataList;
    private List<String> thumbnailList;


    public MapPagerAdapter(Context mContext, List<CafeteriaData> cafeteriaDataList) {
        this.mContext = mContext;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.viewpager_mapinfo, container, false);

        ImageView mapinfo_thumbnail = itemView.findViewById(R.id.mapinfo_thumbnail);
        TextView mapinfo_index = itemView.findViewById(R.id.mapinfo_index);
        TextView mapinfo_cafename = itemView.findViewById(R.id.mapinfo_cafename);
        TextView mapinfo_location = itemView.findViewById(R.id.mapinfo_location);

        CafeteriaData cafeteria = cafeteriaDataList.get(position);

        //썸네일용 이미지 한개 가져옴
        thumbnailList=new ArrayList<>();
        ImageProvider imageProvider = new ImageProvider();
        Observable<ImageResponse> obser = imageProvider.get(cafeteria.getFacilityName());
        obser.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        it -> thumbnailList.add(it.imageInfos.get(0).imageUrl)
                );

        // FIXME: 2019-03-24 썸네일 이미지를 가져온후에 하는 방법?
//        Glide.with(mContext).load(thumbnailList.get(position)).placeholder(R.drawable.loadingimage).into(mapinfo_thumbnail);

        mapinfo_index.setText(String.valueOf(position + 1) + ". ");
        mapinfo_cafename.setText(cafeteria.getFacilityName());
        mapinfo_location.setText(cafeteria.getAddress());

        itemView.setOnClickListener(v -> startDetailActivity(cafeteria));


        // TODO: 2019-03-21 맵 요소들 넣기
        container.addView(itemView);
        return itemView;
    }

    private void startDetailActivity(CafeteriaData data) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("data", data);
        mContext.startActivity(intent);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


}
