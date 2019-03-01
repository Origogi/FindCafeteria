package com.cafeteria.free.findcafeteria.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.CafeteriaData;
import com.cafeteria.free.findcafeteria.util.ImageSliderAdapter;
import com.cafeteria.free.findcafeteria.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CafeteriaData> cardViewDtos = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter( Context context) {
        this.context = context;
    }


    public void setCardViewDtos(List<CafeteriaData> cafeteriaDataList) {
        this.cardViewDtos = cafeteriaDataList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_cafeteria, parent, false);

        return new CafeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CafeteriaData cardViewDto = cardViewDtos.get(position);

        ((CafeViewHolder) holder).position = position;

        ArrayList<Integer> images = new ArrayList<>();
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(context, images, Glide.with(context));

        // TODO: 2019-02-10 파이어베이스에 저장된 URL경로를 가져오는 걸로 변경 (Integer -> String)
        images.add(R.drawable.common_google_signin_btn_icon_dark);
        images.add(R.drawable.common_full_open_on_phone);
        images.add(R.drawable.common_google_signin_btn_icon_dark_normal_background);
        images.add(R.drawable.loadingimage);
        ((CafeViewHolder) holder).autoScrollViewPager.setAdapter(imageSliderAdapter);

        ((CafeViewHolder) holder).nameTv.setText(cardViewDto.getFacilityName());
        ((CafeViewHolder) holder).addressTv.setText(cardViewDto.getAddress());
        ((CafeViewHolder) holder).phoneNumberTv.setText(cardViewDto.getPhone());
        ((CafeViewHolder) holder).timeTv.setText(cardViewDto.getStartTime());
    }

    @Override
    public int getItemCount() {
        return cardViewDtos.size();
    }

    public void reset() {
        cardViewDtos.clear();
        notifyDataSetChanged();
    }

    private class CafeViewHolder extends RecyclerView.ViewHolder {
        //ImageView thumbnailIv;

        AutoScrollViewPager autoScrollViewPager;

        TextView nameTv;
        TextView addressTv;
        TextView phoneNumberTv;
        TextView timeTv;

        int position;

        CafeViewHolder(View view) {
            super(view);
            nameTv = view.findViewById(R.id.name);
            autoScrollViewPager = view.findViewById(R.id.homeslider);
            addressTv = view.findViewById(R.id.address);
            phoneNumberTv = view.findViewById(R.id.phone_number);
            timeTv = view.findViewById(R.id.time);

            view.setOnClickListener(v ->{
                Logger.d("clicked" + cardViewDtos.get(position).toString());
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("data", cardViewDtos.get(position));
                v.getContext().startActivity(intent);
            });


        }
    }

}
