package com.cafeteria.free.findcafeteria.model.room.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import android.support.annotation.NonNull;

@Entity(tableName = "search_table")

public class SearchHistory {
    @NonNull
    @PrimaryKey
    String keyword = "temp";

    public SearchHistory(@NonNull String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(@NonNull String keyword) {
        this.keyword = keyword;
    }


}
