package com.nurujjamanpollob.njpsmultimedia;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Objects;

import static com.nurujjamanpollob.njpsmultimedia.Settings.getPrefs;

public class Splash extends AppCompatActivity {
  //  Animation anim;
//    ImageView imageView;
    static final String PREFS_NAME = "com.nurujjamanpollob.njpsmultimedia_preferences";
    String njpslink="https://njpsmultimedia.blogspot.com";
    private static final int NJP_PERM = 0;

    public String notificationUrl = "noUrl";

    public boolean isLayoutOnline = false;

    TextView getErrorMsg;

  //  LottieAnimationView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getErrorMsg = findViewById(R.id.error_description);
//        errorView = findViewById(R.id.anim_error);

        getPrefs(this);

        checkInternetConnection(Splash.this);

        int newUiOptions = getWindow().getDecorView().getSystemUiVisibility();
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkperm();
   //     imageView= findViewById(R.id.imageView2);


        checkIntent(getIntent());



// get version of currently running app
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packageInfo != null;
        Variables.versionname=packageInfo.versionName;



        /*
         * Showing splash screen with a timer. This will be useful when you
         * want to show case your app logo / company
         */
        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            // Start your app main activity

            if (checkInternetConnection(Splash.this)) {


                if(notificationUrl.matches("noUrl")){


                    SharedPreferences sharedPref =
                            getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

                    String Menu = sharedPref.getString("activity_priority", "");

                    String News = sharedPref.getString("activity_priority", "njplive");//
                    if (Objects.requireNonNull(News).equals("njplive")) {

                        Intent i = new Intent(Splash.this, MainBrowser.class);
                        i.putExtra("url", "http://www.w3schools.com");
                        startActivity(i);
                        finish();


                    }
                    String NJPS = sharedPref.getString("activity_priority", "njpsmultimedia");

                    if (Objects.requireNonNull(NJPS).equals("njpsmultimedia")) {

                        try {

                            Intent i = new Intent(Splash.this, MainBrowser.class);
                            i.putExtra("url", njpslink);
                            startActivity(i);
                            finish();
                        }catch (Exception ess){

                            Toast.makeText(Splash.this, ess.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                        }

                    }



                }else {

                    Intent i = new Intent(Splash.this, MainBrowser.class);
                    i.putExtra("url", notificationUrl);
                    startActivity(i);
                    finish();


                }

            }

            else{


                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

                BottomSheetDialog dialog = new BottomSheetDialog(Splash.this);
                dialog.setContentView(view);
                dialog.show();
            //    errorView.playAnimation();

                dialog.setCancelable(false);
            //    errorView.setAnimation(R.raw.error);
                dialog.setOnCancelListener(dialog1 -> {

                    Toast.makeText(Splash.this, "Killing Progress", Toast.LENGTH_LONG).show();
                    Splash.this.finish();
                });



            }

        }, 1500);




    }

    private void checkIntent(Intent intent) {


        if (intent.hasExtra("Link")) {

            String target = "";

            notificationUrl = Objects.requireNonNull(intent.getExtras()).getString("Link", target);}




    }


    private void checkperm()
    {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {


            blank();
        } else {


            njpper();

        }

    }

    private void njpper()
    {




        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {



            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, NJP_PERM);

        }



    }

    private void blank()
    {


    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        checkIntent(intent);


}


    public boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;

        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;

                }
            }
        }
        return false;
    }

    public void refreshNet(View view) {

        checkConnection(Splash.this);
        isLayoutOnline = true;


    }



    public void quitApp(View view) {

        Splash.this.finish();
        isLayoutOnline = true;

    }


    public void checkConnection(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
           // return false;

            if(isLayoutOnline){

                Log.d("Layout Mode: ", "Online");

            }else{


                @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

                BottomSheetDialog dialog = new BottomSheetDialog(this);
                dialog.setContentView(view);
                dialog.show();
                dialog.setCancelable(false);
                dialog.setOnCancelListener(dialog1 -> {

                    Toast.makeText(Splash.this, "Killing Progress", Toast.LENGTH_LONG).show();
                    Splash.this.finish();
                });


            }


        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                 //   return true;

                    isLayoutOnline = false;

                    if(notificationUrl.matches("noUrl")){


                        SharedPreferences sharedPref =
                                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

                        String Menu = sharedPref.getString("activity_priority", "");


                        String News = sharedPref.getString("activity_priority", "njplive");//
                        if (Objects.requireNonNull(News).equals("njplive")) {

                            Intent i = new Intent(Splash.this, MainBrowser.class);
                            i.putExtra("url", "http://www.w3schools.com");
                            startActivity(i);
                            finish();


                        }
                        String NJPS = sharedPref.getString("activity_priority", "njpsmultimedia");

                        if (Objects.requireNonNull(NJPS).equals("njpsmultimedia")) {

                            Intent i = new Intent(Splash.this, MainBrowser.class);
                            i.putExtra("url", njpslink);
                            startActivity(i);
                            finish();

                        }



                    }else {

                        Intent i = new Intent(Splash.this, MainBrowser.class);
                        i.putExtra("url", notificationUrl);
                        startActivity(i);
                        finish();


                    }


                }
            }
        }
      //  return false;

        if(isLayoutOnline){

            Log.d("Layout Mode: ", "Online");

        }else{



            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

            BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);
            dialog.show();
            dialog.setCancelable(false);
            dialog.setOnCancelListener(dialog1 -> {

                Toast.makeText(Splash.this, "Killing Progress", Toast.LENGTH_LONG).show();
                Splash.this.finish();
            });




        }
    }

    @Override
    public void onBackPressed() {

        if(isLayoutOnline){

            finish();

        }else{

            checkConnection(Splash.this);

        }
        super.onBackPressed();
    }
}

