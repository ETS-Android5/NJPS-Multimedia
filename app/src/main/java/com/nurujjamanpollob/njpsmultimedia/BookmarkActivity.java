package com.nurujjamanpollob.njpsmultimedia;



import static com.nurujjamanpollob.njpsmultimedia.Variables.PREFERENCES;
import static com.nurujjamanpollob.njpsmultimedia.Variables.PREFS_NAME;
import static com.nurujjamanpollob.njpsmultimedia.Variables.WEB_LINKS;
import static com.nurujjamanpollob.njpsmultimedia.Variables.WEB_TITLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nurujjamanpollob.njpsmultimedia.loaders.BookMarkLoader;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import dev.nurujjamanpollob.njpollobutilities.BackgroundWorker.CustomAsyncTask;
import dev.nurujjamanpollob.njpollobutilities.BackgroundWorker.ThreadFixer;


public class BookmarkActivity extends AppCompatActivity {

  public static  ArrayList<HashMap<String, String>> listRowData;

    public static String TAG_TITLE = "title";
    public static String TAG_LINK = "link";

  @SuppressLint("StaticFieldLeak")
  public static  ListView listView;
 public static SimpleAdapter adapter;
  @SuppressLint("StaticFieldLeak")
  public static  LinearLayout linearLayout;
 @SuppressLint("StaticFieldLeak")
 public static SwipeRefreshLayout mSwipeRefreshLayout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        doBackgroundWork();

        /*

        //Calling BookMark Loader

        WorkManager workManager = WorkManager.getInstance(this);

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(BookMarkLoader.class)
               .setInputData()
                .build();
        workManager.enqueue(request);

        workManager.getWorkInfoByIdLiveData(request.getId()).observe(this, workInfo -> {
            if(workInfo != null){
                WorkInfo.State state = workInfo.getState();

                if(state.isFinished()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

*
         */



        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("BOOKMARKS");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        listView =  findViewById(R.id.listView);
        linearLayout = findViewById(R.id.emptyList);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        SharedPreferences sharedPref =
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        boolean darkMode = sharedPref.getBoolean("dark_ui", true);

        if(darkMode){

            setTheme(R.style.NjpollobDark_NoActionBar);



        }


        mSwipeRefreshLayout.setOnRefreshListener(this::doBackgroundWork);

      //  new LoadBookmarks().execute();


        listView.setOnItemClickListener((parent, view, position, id) -> {

            Object o = listView.getAdapter().getItem(position);
            if (o instanceof Map) {
                Map map = (Map) o;
                Intent in = new Intent(BookmarkActivity.this, MainBrowser.class);
                in.putExtra("url", String.valueOf(map.get(TAG_LINK)));
                startActivity(in);
            }


        });

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Object o = listView.getAdapter().getItem(i);
            if (o instanceof Map) {
                Map map = (Map) o;
                deleteBookmark(String.valueOf(map.get(TAG_TITLE)), String.valueOf(map.get(TAG_LINK)));
            }

            return true;
        });

    }





/*
    @SuppressLint("StaticFieldLeak")
    private class LoadBookmarks extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            runOnUiThread(() -> {

                SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
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

                    adapter = new SimpleAdapter(BookmarkActivity.this,
                            listRowData, R.layout.bookmark_list_row,
                            new String[]{TAG_TITLE, TAG_LINK},
                            new int[]{R.id.title, R.id.link});

                    listView.setAdapter(adapter);
                }

                linearLayout.setVisibility(View.VISIBLE);
                listView.setEmptyView(linearLayout);


            });
            return null;
        }



        protected void onPostExecute(String args) {
            mSwipeRefreshLayout.setRefreshing(false);
        }




    }


 */

    private void deleteBookmark(final String title, final String link) {



        new AlertDialog.Builder(this)
                .setTitle("DELETE")
                .setMessage("Confirm that you want to delete this bookmark?")
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                    String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
                    String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);


                    if (jsonLink != null && jsonTitle != null) {


                        Gson gson = new Gson();
                        ArrayList<String> linkArray = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
                        }.getType());

                        ArrayList<String> titleArray = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
                        }.getType());


                        linkArray.remove(link);
                        titleArray.remove(title);


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(WEB_LINKS, new Gson().toJson(linkArray));
                        editor.putString(WEB_TITLE, new Gson().toJson(titleArray));
                        editor.apply();

                        doBackgroundWork();


                    }
                    dialogInterface.dismiss();
                }).setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss()).show();




    }



   private void doBackgroundWork(){

        SyncTask sc = new SyncTask();
        sc.runThread();


/*
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
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

                    adapter = new SimpleAdapter(BookmarkActivity.this,
                            listRowData, R.layout.bookmark_list_row,
                            new String[]{TAG_TITLE, TAG_LINK},
                            new int[]{R.id.title, R.id.link});

                    listView.setAdapter(adapter);
                }

             //   linearLayout.setVisibility(View.VISIBLE);
//                listView.setEmptyView(linearLayout);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        service.shutdown();


 */
   }


   private class SyncTask extends CustomAsyncTask<Void, Void>{


       @Override
       protected Void doBackgroundTask() {

           SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
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

               ThreadFixer fx = new ThreadFixer(new Handler(Looper.getMainLooper()));
               fx.setListenerForFixThread(() -> {

                   adapter = new SimpleAdapter(BookmarkActivity.this,
                           listRowData, R.layout.bookmark_list_row,
                           new String[]{TAG_TITLE, TAG_LINK},
                           new int[]{R.id.title, R.id.link});

                   listView.setAdapter(adapter);

               });
               /*
               adapter = new SimpleAdapter(BookmarkActivity.this,
                       listRowData, R.layout.bookmark_list_row,
                       new String[]{TAG_TITLE, TAG_LINK},
                       new int[]{R.id.title, R.id.link});

               listView.setAdapter(adapter);
               *
                */
           }

           ThreadFixer fx = new ThreadFixer(new Handler(Looper.getMainLooper()));
           fx.setListenerForFixThread(() -> {
               linearLayout.setVisibility(View.VISIBLE);
               listView.setEmptyView(linearLayout);
           });




           return null;
       }

       @Override
       protected void onTaskFinished(Void aVoid) {

           mSwipeRefreshLayout.setRefreshing(false);
           super.onTaskFinished(aVoid);
       }
   }

}






