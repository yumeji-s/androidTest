package com.example.roomapp;

import static com.example.roomapp.ByteBitmap.getBitmapObject;
import static com.example.roomapp.ByteBitmap.getByteObject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.List;

public class listDisp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_disp);

        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());
        ListView list = findViewById(R.id.listView);

        findViewById(R.id.disp).setOnClickListener(new ButtonClickListener(this, db, list));
    }

    private class ButtonClickListener implements View.OnClickListener {
        private Activity activity;
        private AppDatabase db;
        private ListView list;

        private ButtonClickListener(Activity activity, AppDatabase db, ListView list) {
            this.activity = activity;
            this.db = db;
            this.list = list;
        }

        @Override
        public void onClick(View view) {
            new DataStoreAsyncTask(db, activity, list).execute();
        }
    }

    private static class DataStoreAsyncTask extends AsyncTask<Void, Void, Integer> {

        private WeakReference<Activity> weakActivity;
        private AppDatabase db;
        private ListView listView;
        private String[] strings;
        private Bitmap[] images;

        public DataStoreAsyncTask(AppDatabase db, Activity activity, ListView listView) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
            this.listView = listView;
        }

        @Override
        protected Integer doInBackground(Void... params) {

            AccessTimeDao accessTimeDao = db.accessTimeDao();
            List<AccessTime> list = accessTimeDao.getAll();

            strings = new String[list.size()];
            images = new Bitmap[list.size()];
            for(int i = 0; i < list.size(); i++){
                strings[i] = list.get(i).getAccessTime();
                byte[] bytes = list.get(i).getImageTest();
                try {
                    images[i] = getBitmapObject(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer code) {

            Activity activity = weakActivity.get();
            if(activity == null) {
                return;
            }
            // リストビューにセット
            BaseAdapter adapter = new ListViewAdapter(activity.getApplicationContext(),
                    R.layout.activity_list_disp, strings, images);
            listView.setAdapter(adapter);
        }
    }
}