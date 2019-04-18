package com.cafeteria.free.findcafeteria.model.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;

import java.util.List;

/**
 * Created by gimjeongtae on 27/03/2019.
 */

@Dao
public interface CafeteriaDataDao {

    @Insert
    public void insertAll(List<CafeteriaData> cafeteriaDataList);

    @Query("SELECT * FROM cafeteria_table")
    public List<CafeteriaData> getAllCafeteria();

    @Query("DELETE FROM cafeteria_table")
    public void deleteAll();

    @Update
    public int update(CafeteriaData cafeteriaData);

}
