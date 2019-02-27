package com.cafeteria.free.findcafeteria.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.CafeteriaData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CafeteriaData> cardViewDtos = new ArrayList<>();

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

        //((CafeViewHolder) holder).thumbnailIv.setImageResource(cardViewDto.imageView);
        ((CafeViewHolder) holder).thumbnailIv.setImageResource(R.drawable.sample);
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
        ImageView thumbnailIv;

        TextView nameTv;
        TextView addressTv;
        TextView phoneNumberTv;
        TextView timeTv;

        CafeViewHolder(View view) {
            super(view);
            nameTv = view.findViewById(R.id.name);
            thumbnailIv = view.findViewById(R.id.thumbnail);
            addressTv = view.findViewById(R.id.address);
            phoneNumberTv = view.findViewById(R.id.phone_number);
            timeTv = view.findViewById(R.id.time);
        }
    }

}
