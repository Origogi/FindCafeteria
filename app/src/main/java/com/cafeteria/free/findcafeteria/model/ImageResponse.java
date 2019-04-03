package com.cafeteria.free.findcafeteria.model;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ImageResponse {

    Meta meta;

    @SerializedName("documents")
    public List<ImageInfo> imageInfos = new ArrayList<>();

    @Override
    public String toString() {
        return "ImageResponse{" +
                "meta=" + meta +
                ", imageInfos=" + imageInfos +
                '}';
    }

    public static class Meta {
        @SerializedName("total_count")
        int totalCount;
        @SerializedName("pageable_count")
        int pageableCount;
        @SerializedName("is_end")
        boolean isEnd;

        public Meta() {
        }

        @Override
        public String toString() {
            return "Meta{" +
                    "totalCount=" + totalCount +
                    ", pageableCount=" + pageableCount +
                    ", isEnd=" + isEnd +
                    '}';
        }
    }

    public static class ImageInfo {

        String collection;
        @SerializedName("thumbnail_url")
        String thumbnailUrl;
        @SerializedName("image_url")
        public String imageUrl;
        int width;
        int height;
        @SerializedName("display_sitename")
        String displaySitename;
        @SerializedName("doc_url")
        String docUrl;
        String datetime;

        public ImageInfo() {
        }

        @Override
        public String toString() {
            return "ImageInfo{" +
                    "collection='" + collection + '\'' +
                    ", thumbnailUrl='" + thumbnailUrl + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", displaySitename='" + displaySitename + '\'' +
                    ", docUrl='" + docUrl + '\'' +
                    ", datetime='" + datetime + '\'' +
                    '}';
        }
    }

}
