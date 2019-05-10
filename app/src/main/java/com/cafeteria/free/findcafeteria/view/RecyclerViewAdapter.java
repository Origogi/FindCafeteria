package com.cafeteria.free.findcafeteria.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cafeteria.free.findcafeteria.R;
import com.cafeteria.free.findcafeteria.model.room.dao.CafeteriaDataDao;
import com.cafeteria.free.findcafeteria.model.room.db.AppDatabase;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.ImageProvider;
import com.cafeteria.free.findcafeteria.model.ImageResponse;
import com.cafeteria.free.findcafeteria.util.ImageSliderAdapter;
import com.cafeteria.free.findcafeteria.util.Logger;
import com.cafeteria.free.findcafeteria.util.MyScaleAnimation;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CafeViewHolder> {

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
    public RecyclerViewAdapter.CafeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_cafeteria, parent, false);
        return new CafeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.CafeViewHolder holder, int position) {
        CafeteriaData cardViewDto = cardViewDtos.get(position);

        holder.stopAutoScroll();
        holder.position = position;
        holder.nameTv.setText(cardViewDto.getFacilityName());
        holder.addressTv.setText(cardViewDto.getAddress());
        holder.phoneNumberTv.setText(cardViewDto.getPhone());
        holder.timeTv.setText(cardViewDto.getTime());

        if (cardViewDto.isFavorite()) {
            holder.favorite.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red));
        }
        else {
            holder.favorite.setImageDrawable(context.getDrawable(R.drawable.ic_not_favorite_red));
        }

        holder.favorite.setOnTouchListener(new View.OnTouchListener() {
            boolean checked = cardViewDto.isFavorite();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setTag("touched");
                    v.startAnimation(MyScaleAnimation.instance);
                    if (checked) {
                        ((ImageView)v).setImageDrawable(context.getDrawable(R.drawable.ic_not_favorite_red));
                        checked = false;
                    }
                    else {
                        ((ImageView)v).setImageDrawable(context.getDrawable(R.drawable.ic_favorite_red));
                        checked = true;
                    }

                    new Thread(()->{
                        CafeteriaDataDao dao = AppDatabase.getInstance(context).getCafeteriaDataDao();
                        cardViewDto.setFavorite(checked);
                        int result = dao.update(cardViewDto);
                        Logger.d("" + result);
                    }).start();

                    return false;
                }
                return true;
            }
        });
        
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(context, Glide.with(context));
        holder.viewPager.setAdapter(imageSliderAdapter);

        Observable<ImageResponse> observable = ImageProvider.get(cardViewDto.getFacilityName());


        observable.subscribe(new DisposableObserver<ImageResponse>() {
            @Override
            public void onNext(ImageResponse imageResponse) {
                updateImage(imageSliderAdapter,imageResponse);
                if(imageSliderAdapter.getCount() > 0) {
                    holder.startAutoScroll(imageSliderAdapter.getCount());
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.d((e.toString()));

            }

            @Override
            public void onComplete() {
            }
        });

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

    class CafeViewHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;
        TextView nameTv;
        TextView addressTv;
        TextView phoneNumberTv;
        TextView timeTv;
        ImageView favorite;

        int position;
        boolean isRunning = false;
        Disposable disposable;

        CafeViewHolder(View view) {
            super(view);
            nameTv = view.findViewById(R.id.name);
            viewPager = view.findViewById(R.id.homeslider);
            addressTv = view.findViewById(R.id.address);
            phoneNumberTv = view.findViewById(R.id.phone_number);
            timeTv = view.findViewById(R.id.time);
            favorite = view.findViewById(R.id.favorite_img);
        }

        void stopAutoScroll() {
            if (disposable != null) {
                disposable.dispose();
            }
            isRunning = false;
        }

        void startAutoScroll(int imageCount) {
            if (isRunning) {
                return;
            }
            isRunning = true;

            disposable = Observable.interval(2000L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(idx ->{
                    int currentIdx = (int)(idx % imageCount);
                    viewPager.setCurrentItem(currentIdx);
                });
        }
    }



}
