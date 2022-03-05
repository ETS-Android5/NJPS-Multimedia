package com.nurujjamanpollob.njpsmultimedia.loaders;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nurujjamanpollob.njpsmultimedia.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.nurujjamanpollob.njpsmultimedia.BookmarkActivity.TAG_LINK;
import static com.nurujjamanpollob.njpsmultimedia.BookmarkActivity.listRowData;
import static com.nurujjamanpollob.njpsmultimedia.BookmarkActivity.adapter;
import static com.nurujjamanpollob.njpsmultimedia.BookmarkActivity.listView;
import static com.nurujjamanpollob.njpsmultimedia.BookmarkActivity.linearLayout;
import static com.nurujjamanpollob.njpsmultimedia.BookmarkActivity.TAG_TITLE;
import static com.nurujjamanpollob.njpsmultimedia.Variables.PREFERENCES;
import static com.nurujjamanpollob.njpsmultimedia.Variables.WEB_LINKS;
import static com.nurujjamanpollob.njpsmultimedia.Variables.WEB_TITLE;


public class BookMarkLoader extends Worker {


    public BookMarkLoader(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public BookMarkLoader(@NonNull Context context, @NonNull WorkerParameters workerParams, @NonNull ListView listView, @NonNull LinearLayout linearLayout, @NonNull SwipeRefreshLayout swipeRefreshLayout){


        super(context, workerParams);


    }

    @NonNull
    @Override
    public Result doWork() {

      try{

          SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
          String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
          String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);
          listRowData = new ArrayList<>();

          if (jsonLink != null && jsonTitle != null) {

              Gson gson = new Gson();
              ArrayList<String> linkArray = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
              }.getType());

              ArrayList<String> titleArray = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
              }.getType());


              for (int i = 0; i < linkArray.size(); i++) {
                  HashMap<String, String> map = new HashMap<>();

                  if (titleArray.get(i).length() == 0)
                      map.put(TAG_TITLE, "Bookmark " + (i + 1));
                  else
                      map.put(TAG_TITLE, titleArray.get(i));

                  map.put(TAG_LINK, linkArray.get(i));
                  listRowData.add(map);
              }

              adapter = new SimpleAdapter(getApplicationContext(),
                      listRowData, R.layout.bookmark_list_row,
                      new String[]{TAG_TITLE, TAG_LINK},
                      new int[]{R.id.title, R.id.link});

              listView.setAdapter(adapter);
          }

          linearLayout.setVisibility(View.VISIBLE);
          listView.setEmptyView(linearLayout);

          return Result.success();
      }catch (Exception e){
          Log.d("BookMark Loading: ", Objects.requireNonNull(e.getMessage()));
          return Result.failure();
      }
    }
}
