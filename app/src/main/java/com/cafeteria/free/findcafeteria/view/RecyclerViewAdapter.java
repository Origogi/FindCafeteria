package com.cafeteria.free.findcafeteria.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.ImageProvider;
import com.cafeteria.free.findcafeteria.model.ImageResponse;
import com.cafeteria.free.findcafeteria.util.ImageSliderAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
        ((CafeViewHolder) holder).nameTv.setText(cardViewDto.getFacilityName());
        ((CafeViewHolder) holder).addressTv.setText(cardViewDto.getAddress());
        ((CafeViewHolder) holder).phoneNumberTv.setText(cardViewDto.getPhone());
        ((CafeViewHolder) holder).timeTv.setText(cardViewDto.getStartTime());
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(context, Glide.with(context));
        ((CafeViewHolder) holder).viewPager.setAdapter(imageSliderAdapter);

        ImageProvider imageProvider = new ImageProvider();
        Observable<ImageResponse> observable = imageProvider.get(cardViewDto.getFacilityName());
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> updateImage(imageSliderAdapter,it));
    }

    private void updateImage(ImageSliderAdapter imageSliderAdapter, ImageResponse imageResponse) {
        ArrayList<String> images = new ArrayList<>();

        for (int i = 0;i<3 && i<imageResponse.imageInfos.size();i++) {
            images.add(imageResponse.imageInfos.get(i).imageUrl);
        }
        imageSliderAdapter.addImageUri(images);
        imageSliderAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cardViewDtos.size();
    }

    public void reset() {
        cardViewDtos.clear();
        notifyDataSetChanged();
    }

    public CafeteriaData get(int position) {
        return cardViewDtos.get(position);
    }

    private class CafeViewHolder extends RecyclerView.ViewHolder {
        CustomViewPager viewPager;

        TextView nameTv;
        TextView addressTv;
        TextView phoneNumberTv;
        TextView timeTv;

        int position;

        CafeViewHolder(View view) {
            super(view);
            nameTv = view.findViewById(R.id.name);
            viewPager = view.findViewById(R.id.homeslider);
            addressTv = view.findViewById(R.id.address);
            phoneNumberTv = view.findViewById(R.id.phone_number);
            timeTv = view.findViewById(R.id.time);
        }
    }
}
