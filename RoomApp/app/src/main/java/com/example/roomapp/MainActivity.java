package com.example.roomapp;

import static com.example.roomapp.ByteBitmap.getByteObject;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ストレージから持ってくる
    ImageView imageView;
    Bitmap image;
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                try {
                    Bitmap bmp = getBitmapFromUri(uri);
                    imageView.setImageBitmap(bmp);
                    image = bmp;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    /**
     * 画像パスから画像を取得
     * @param uri
     * @return
     * @throws IOException
     */
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.index);
        Button bt = findViewById(R.id.saveBtn);
        imageView = findViewById(R.id.image);
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());

        bt.setOnClickListener(new ButtonClickListener(this, db, tv));
        findViewById(R.id.openFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
        findViewById(R.id.list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), listDisp.class);
                startActivity(intent);
            }
        });

    }

    private class ButtonClickListener implements View.OnClickListener {
        private Activity activity;
        private AppDatabase db;
        private TextView tv;

        private ButtonClickListener(Activity activity, AppDatabase db, TextView tv) {
            this.activity = activity;
            this.db = db;
            this.tv = tv;
        }

        @Override
        public void onClick(View view) {
            new DataStoreAsyncTask(db, activity, tv, image).execute();
        }
    }

    private static class DataStoreAsyncTask extends AsyncTask<Void, Void, Integer> {

        private WeakReference<Activity> weakActivity;
        private AppDatabase db;
        private TextView textView;
        private Bitmap image;
        private StringBuilder sb;

        public DataStoreAsyncTask(AppDatabase db, Activity activity, TextView textView,Bitmap image) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
            this.textView = textView;
            this.image = image;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            // タイムスタンプを生成する処理
            AccessTimeDao accessTimeDao = db.accessTimeDao();

            // bitmapからbyte[]に変換
            byte[] byteImg;
            try {
                byteImg = getByteObject(image);
                // 生成したタイムスタンプを格納する処理
                accessTimeDao.insert(new AccessTime(new Timestamp(System.currentTimeMillis()).toString(),byteImg));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // これまで生成したタイムスタンプを取得し文字列として連結する処理
            sb = new StringBuilder();
            List<AccessTime> atList = accessTimeDao.getAll();
            for (AccessTime at: atList) {
                sb.append(at.getAccessTime()).append("\n");
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer code) {

            // 表示したい文字列をTextViewにセットする処理
            Activity activity = weakActivity.get();
            if(activity == null) {
                return;
            }
            textView.setText(sb.toString());
        }
    }
}