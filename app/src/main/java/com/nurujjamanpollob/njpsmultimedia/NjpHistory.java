package com.nurujjamanpollob.njpsmultimedia;



import static com.nurujjamanpollob.njpsmultimedia.Variables.HISTORY;
import static com.nurujjamanpollob.njpsmultimedia.Variables.PREFS_NAME;
import static com.nurujjamanpollob.njpsmultimedia.Variables.WEB_LINKS;
import static com.nurujjamanpollob.njpsmultimedia.Variables.WEB_TITLE;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.nurujjamanpollob.njpollobutilities.BackgroundWorker.CustomAsyncTask;
import dev.nurujjamanpollob.njpollobutilities.BackgroundWorker.ThreadFixer;


public class NjpHistory extends AppCompatActivity {

    ArrayList<HashMap<String, String>> listRowData;

    public static String TAG_TITLE_HISTORY = "title";
    public static String TAG_LINK_HISTORY = "link";

    ListView listView;
    SimpleAdapter adapter;
    LinearLayout linearLayout;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        doBackgroundWork();

        SharedPreferences sharedPref =
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        boolean darkMode = sharedPref.getBoolean("dark_ui", true);

        if(darkMode){

            setTheme(R.style.NjpollobDark_NoActionBar);


        }

        Toolbar toolbar = findViewById(R.id.toolbar_history);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Browser History");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        listView = findViewById(R.id.listView_history);
        linearLayout =  findViewById(R.id.emptyList_history);
        mSwipeRefreshLayout =  findViewById(R.id.swipeToRefresh_history);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this::doBackgroundWork);


        /*
        WorkManager workManager = WorkManager.getInstance(NjpHistory.this);

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(HistoryLoader.class).build();

        workManager.enqueue(request);

        workManager.getWorkInfoByIdLiveData(request.getId()).observe(this, workInfo -> {
            if(workInfo != null){
                WorkInfo.State state = workInfo.getState();

                if(state.isFinished()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });


*?
         */

        listView.setOnItemClickListener((parent, view, position, id) -> {

            Object o = listView.getAdapter().getItem(position);
            if (o instanceof Map) {
                Map map = (Map) o;
                Intent in = new Intent(NjpHistory.this, MainBrowser.class);
                in.putExtra("url", String.valueOf(map.get(TAG_LINK_HISTORY)));
                startActivity(in);
            }


        });

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Object o = listView.getAdapter().getItem(i);
            if (o instanceof Map) {
                Map map = (Map) o;
                deleteBookmark(String.valueOf(map.get(TAG_TITLE_HISTORY)), String.valueOf(map.get(TAG_LINK_HISTORY)));
            }

            return true;
        });

    }


    private void deleteBookmark(final String title, final String link) {



        new AlertDialog.Builder(this)
                .setTitle("DELETE")
                .setMessage("Confirm that you want to delete this bookmark?")
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences(HISTORY, Context.MODE_PRIVATE);
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
                })

                .setNeutralButton("Delete All", (p1, p2) -> {

                sharedPreferences = getSharedPreferences(HISTORY, Context.MODE_PRIVATE);
                    String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
                    String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);


                    if (jsonLink != null && jsonTitle != null) {


                        Gson gson = new Gson();
                        ArrayList<String> linkArray = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
                        }.getType());

                        ArrayList<String> titleArray = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
                        }.getType());


                        linkArray.clear();
                        titleArray.clear();


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(WEB_LINKS, new Gson().toJson(linkArray));
                        editor.putString(WEB_TITLE, new Gson().toJson(titleArray));
                        editor.apply();



                        doBackgroundWork();
                     //   new LoadHistory().execute();
                    }



                })



                .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss()).show();


    }



    private void doBackgroundWork(){

        /*

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences(HISTORY, Context.MODE_PRIVATE);
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
                            map.put(TAG_TITLE_HISTORY, "Bookmark " + (i + 1));
                        else
                            map.put(TAG_TITLE_HISTORY, titleArray.get(i));

                        map.put(TAG_LINK_HISTORY, linkArray.get(i));
                        listRowData.add(map);
                    }

                    adapter = new SimpleAdapter(NjpHistory.this,
                            listRowData, R.layout.history_list_row,
                            new String[]{TAG_TITLE_HISTORY, TAG_LINK_HISTORY},
                            new int[]{R.id.title_history, R.id.title_link});

                    listView.setAdapter(adapter);
                }

                linearLayout.setVisibility(View.VISIBLE);
                listView.setEmptyView(linearLayout);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        service.shutdown();


         */

        new SyncTask().runThread();
    }


    private class SyncTask extends CustomAsyncTask<Void, Void>{


        @Override
        protected Void doBackgroundTask() {

            SharedPreferences sharedPreferences = getSharedPreferences(HISTORY, Context.MODE_PRIVATE);
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
                        map.put(TAG_TITLE_HISTORY, "Bookmark " + (i + 1));
                    else
                        map.put(TAG_TITLE_HISTORY, titleArray.get(i));

                    map.put(TAG_LINK_HISTORY, linkArray.get(i));
                    listRowData.add(map);
                }

                ThreadFixer t = new ThreadFixer(new Handler(Looper.getMainLooper()));
                t.setListenerForFixThread(new ThreadFixer.WrongThreadFixer() {
                    @Override
                    public void onSuccessFullFix() {

                        adapter = new SimpleAdapter(NjpHistory.this,
                                listRowData, R.layout.history_list_row,
                                new String[]{TAG_TITLE_HISTORY, TAG_LINK_HISTORY},
                                new int[]{R.id.title_history, R.id.title_link});

                        listView.setAdapter(adapter);

                    }
                });


            }


            ThreadFixer t = new ThreadFixer(new Handler(Looper.getMainLooper()));
            t.setListenerForFixThread(new ThreadFixer.WrongThreadFixer() {
                @Override
                public void onSuccessFullFix() {

                    linearLayout.setVisibility(View.VISIBLE);
                    listView.setEmptyView(linearLayout);


                }
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






