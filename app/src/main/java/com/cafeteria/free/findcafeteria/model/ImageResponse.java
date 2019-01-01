package com.cafeteria.free.findcafeteria.model;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ImageResponse {

    Meta meta;

    @SerializedName("documents")
    List<ImageInfo> imageInfos = new ArrayList<>();

    @Override
    public String toString() {
        return "ImageResponse{" +
                "meta=" + meta +
                ", imageInfos=" + imageInfos +
                '}';
    }

    public static class Meta {
        int total_count;
        int pageable_count;
        boolean is_end;

        public Meta() {

        }

        @Override
        public String toString() {
            return "Meta{" +
                    "total_count=" + total_count +
                    ", pageable_count=" + pageable_count +
                    ", is_end=" + is_end +
                    '}';
        }
    }

    public static class ImageInfo {

        String collection;
        String thumbnail_url;
        String image_url;
        int  width;
        int height;
        String display_sitename;
        String doc_url;
        String datetime;

        public ImageInfo() {
        }

        @Override
        public String toString() {
            return "ImageInfo{" +
                    "collection='" + collection + '\'' +
                    ", thumbnail_url='" + thumbnail_url + '\'' +
                    ", image_url='" + image_url + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", display_sitename='" + display_sitename + '\'' +
                    ", doc_url='" + doc_url + '\'' +
                    ", datetime='" + datetime + '\'' +
                    '}';
        }
    }

}
