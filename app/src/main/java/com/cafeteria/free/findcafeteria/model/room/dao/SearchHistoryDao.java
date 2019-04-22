package com.cafeteria.free.findcafeteria.model.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.cafeteria.free.findcafeteria.model.room.entity.SearchHistory;

import java.util.List;

@Dao
public interface SearchHistoryDao {

    @Query("DELETE FROM search_table")
    public void deleteAll();

    @Query("SELECT * FROM search_table")
    public List<SearchHistory> getAll();

    @Insert(onConflict  = OnConflictStrategy.IGNORE)
    public void add(SearchHistory searchHistory);

}
