package com.cafeteria.free.findcafeteria.model.room.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.cafeteria.free.findcafeteria.model.room.dao.CafeteriaDataDao;
import com.cafeteria.free.findcafeteria.model.room.dao.DBVersionDao;
import com.cafeteria.free.findcafeteria.model.room.dao.SearchHistoryDao;
import com.cafeteria.free.findcafeteria.model.room.entity.CafeteriaData;
import com.cafeteria.free.findcafeteria.model.room.entity.DBVersion;
import com.cafeteria.free.findcafeteria.model.room.entity.SearchHistory;

/**
 * Created by gimjeongtae on 27/03/2019.
 */

@Database(entities = {DBVersion.class, CafeteriaData.class, SearchHistory.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DBVersionDao getDBVersionDao();
    public abstract CafeteriaDataDao getCafeteriaDataDao();
    public abstract SearchHistoryDao getSearchHistoryDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (null == INSTANCE) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "word_database")
                        .build();

            }
        }
        return INSTANCE;
    }

}
