package com.cafeteria.free.findcafeteria.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private GestureDetector gestureDetector;

    class DiffCallback extends DiffUtil.Callback {

        List<CafeteriaData> newItems;
        List<CafeteriaData> oldItems;


        DiffCallback(List<CafeteriaData> newItems, List<CafeteriaData> oldItems) {
            this.newItems = newItems;
            this.oldItems = oldItems;
        }

        @Override
        public int getOldListSize() {
            return oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldIndex, int newIndex) {

            CafeteriaData oldItem = oldItems.get(oldIndex);
            CafeteriaData newItem = newItems.get(newIndex);
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(int oldIndex, int newIndex) {

            CafeteriaData oldItem = oldItems.get(oldIndex);
            CafeteriaData newItem = newItems.get(newIndex);

            return oldItem.equals(newItem);
        }
    }



    public RecyclerViewAdapter(Context context) {
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

    }


    public void setCardViewDtos(List<CafeteriaData> cafeteriaDataList) {

        DiffCallback diffCallback = new DiffCallback(cafeteriaDataList, cardViewDtos);

        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);


        diffResult.dispatchUpdatesTo(this);
        cardViewDtos.clear();
        cardViewDtos.addAll(cafeteriaDataList);


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

        holder.favorite.setSelected(cardViewDto.isFavorite());

        holder.itemView.setOnTouchListener((v, e) -> {
            if (gestureDetector.onTouchEvent(e)) {
                startDetailActivity(holder.itemView, cardViewDto);
                return false;
            }
            return true;
        });

        holder.viewPager.setOnTouchListener((v, e) -> {
            if (gestureDetector.onTouchEvent(e)) {
                startDetailActivity(holder.itemView, cardViewDto);

                return false;
            }
            return true;
        });


        holder.favorite.setOnTouchListener((v, event) -> {
            if (gestureDetector.onTouchEvent(event)) {

                v.startAnimation(MyScaleAnimation.instance);
                v.setSelected(!v.isSelected());

                if (v.isSelected()) {
                    Toast.makeText(context, "즐겨찾기에 추가가 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "즐겨찾기에 삭제가 되었습니다.", Toast.LENGTH_SHORT).show();
                }

                cardViewDto.setFavorite(v.isSelected());

                new Thread(() -> {
                    CafeteriaDataDao dao = AppDatabase.getInstance(context).getCafeteriaDataDao();
                    cardViewDto.setFavorite(v.isSelected());
                    int result = dao.update(cardViewDto);
                    Logger.d("" + result);
                }).start();
                return false;
            }
            return true;

        });

        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(context, Glide.with(context));
        holder.viewPager.setAdapter(imageSliderAdapter);

        Observable<ImageResponse> observable = ImageProvider.get(cardViewDto.getFacilityName());


        observable.subscribe(new DisposableObserver<ImageResponse>() {
            @Override
            public void onNext(ImageResponse imageResponse) {
                updateImage(imageSliderAdapter, imageResponse);
                if (imageSliderAdapter.getCount() > 0) {
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

        for (int i = 0; i < 3 && i < imageResponse.imageInfos.size(); i++) {
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
                    .subscribe(idx -> {
                        int currentIdx = (int) (idx % imageCount);
                        viewPager.setCurrentItem(currentIdx);
                    });
        }
    }


    private void startDetailActivity(View childView, CafeteriaData data) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("data", data);


        View name = childView.findViewById(R.id.name);

        View time = childView.findViewById(R.id.timeLayout);
        View location = childView.findViewById(R.id.locationLayout);
        View phone = childView.findViewById(R.id.phoneLayout);

        Pair[] pairs = new Pair[4];

        pairs[0] = new Pair<>(time, context.getString(R.string.timeTransition));
        pairs[1] = new Pair<>(location, context.getString(R.string.locationTransition));
        pairs[2] = new Pair<>(phone, context.getString(R.string.phoneTransition));
        pairs[3] = new Pair<>(name, context.getString(R.string.nameTransition));


        ActivityOptionsCompat options = (ActivityOptionsCompat) ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, pairs);

        context.startActivity(intent, options.toBundle());
    }

}
