package com.cafeteria.free.findcafeteria.util;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.RequestManager;
import com.cafeteria.free.findcafeteria.R;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<String> mResources = new ArrayList<>();
    private final RequestManager glide;

    public ImageSliderAdapter(Context context, RequestManager glide) {
        this.glide = glide;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    public void clear() {
        mResources.clear();
    }

    public void addImageUri(List<String> uris) {
        mResources.addAll(uris);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (mResources.isEmpty()) {
            return null;
        }

        View itemView = mLayoutInflater.inflate(R.layout.viewpager_image, container, false);

        int realPos = position % mResources.size();
        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_viewpager_childimage);
        glide.load(mResources.get(realPos)).placeholder(R.drawable.loadingimage).error(R.drawable.loadingimage).into(imageView);

        container.addView(itemView, 0);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
