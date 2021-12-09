package com.example.roomapp;

import android.graphics.Bitmap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteBitmap {

    /**
     * Bitmapオブジェクトをbyte配列に変換
     * @param bitmap
     * @return
     * @throws IOException
     */
    public static byte[] getByteObject(final Bitmap bitmap) throws IOException
    {
        ByteArrayOutputStream byteos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CachedBitmap cachedBitmap = new CachedBitmap(bitmap);
        out.writeObject(cachedBitmap);

        return byteos.toByteArray();
    }

    /**
     * byte配列をBitmapオブジェクトに変換
     * @param objByte
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Bitmap getBitmapObject(final byte[] objByte) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream byteis = new ByteArrayInputStream(objByte);
        ObjectInputStream in = new ObjectInputStream(byteis);

        CachedBitmap cachedBitmap = (CachedBitmap) in.readObject();

        return cachedBitmap.getBitmap();
    }
}
