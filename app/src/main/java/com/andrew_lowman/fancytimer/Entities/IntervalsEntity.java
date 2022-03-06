package com.andrew_lowman.fancytimer.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "intervals_table")
public class IntervalsEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "intervalID")
    private int intervalID;

    @ColumnInfo(name = "intervalName")
    private String name;

    private String code;

    public IntervalsEntity(int intervalID, String name, String code) {
        this.intervalID = intervalID;
        this.name = name;
        this.code = code;
    }

    public int getIntervalID() {
        return intervalID;
    }

    public void setIntervalID(int intervalID) {
        this.intervalID = intervalID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "IntervalsEntity{" +
                "intervalID=" + intervalID +
                ", code='" + code + '\'' +
                '}';
    }
}
