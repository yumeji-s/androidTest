package com.example.roomapp;
import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class AccessTime {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "access_time")
    private String accessTime;

    @ColumnInfo(name = "image_test")
    private byte[] imageTest;

    public AccessTime(String accessTime,byte[] imageTest) {
        this.accessTime = accessTime;
        this.imageTest = imageTest;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }
    public String getAccessTime() {
        return accessTime;
    }

    public void setImageTest(byte[] imageTest) {
        this.imageTest = imageTest;
    }
    public byte[] getImageTest() {
        return imageTest;
    }
}
