package com.cafeteria.free.findcafeteria.model.room.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by gimjeongtae on 27/03/2019.
 */

@Entity(tableName = "version_table")
public class DBVersion {
    @PrimaryKey(autoGenerate = true)
    long id;
    int version;

    public DBVersion(int version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


}
