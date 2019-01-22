package com.cafeteria.free.findcafeteria.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cafeteria.free.findcafeteria.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CardViewDto> cardViewDtos = new ArrayList<>();


    public void setCardViewDtos(List<CardViewDto> cardViewDtos) {
        this.cardViewDtos = cardViewDtos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_cafeteria, parent, false);


        return new CafeViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        CardViewDto cardViewDto = cardViewDtos.get(position);


        ((CafeViewHolder) holder).imageView.setImageResource(cardViewDto.imageView);
        ((CafeViewHolder) holder).mainTitle.setText(cardViewDto.mainTitle);
        ((CafeViewHolder) holder).subTitle.setText(cardViewDto.subTitle);

    }

    @Override
    public int getItemCount() {
        return cardViewDtos.size();
    }

    private class CafeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView mainTitle;
        TextView subTitle;

        CafeViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.thumbnail);
            mainTitle = view.findViewById(R.id.title);
            subTitle = view.findViewById(R.id.subtitle);
        }
    }

}
