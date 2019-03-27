package com.cafeteria.free.findcafeteria.model.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.cafeteria.free.findcafeteria.model.room.entity.DBVersion;

/**
 * Created by gimjeongtae on 27/03/2019.
 */

@Dao
public interface DBVersionDao {

    @Insert
    public void insert(DBVersion version);

    @Query("SELECT * FROM version_table")
    public DBVersion get();

    @Update
    public void update(DBVersion version);
}

