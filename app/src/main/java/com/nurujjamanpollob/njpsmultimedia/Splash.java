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
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {




                if(notificationUrl.matches("noUrl")){


                    SharedPreferences sharedPref =
                            getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

                    String Menu = sharedPref.getString("activity_priority", "");
                    if (Objects.requireNonNull(Menu).equals("menu")) {

                        Intent i = new Intent(Splash.this, MainActivity.class);
                        startActivity(i);
                        finish();


                    }

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
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        imageView.startAnimation(anim);


         */


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



                /*
                int[] items = new int[] {

                        R.string.retry,
                        R.string.quit



                };

                int[] icons = new int[] {

                        R.drawable.ic_retry,
                        R.drawable.ic_close_black_24dp


                };0

                 */

                /*Custom Bottom Sheet Dialog Implementation */

                /*

                BottomSheet.Builder builder = new BottomSheet.Builder(Splash.this);
                builder.setTitle("Ah UH ErrrroR!");


                builder.setItems(items, icons, (dialogInterface, i) -> {
                    // Toast.makeText(MainBrowser.this, items[i], Toast.LENGTH_SHORT).show();

                    if(items[i] == R.string.retry){

                        checkConnection(Splash.this);
                        isLayoutOnline = true;

                    }

                    if(items[i] == R.string.report){


                        Splash.this.finish();
                        isLayoutOnline = true;


                    }

                });

                builder.show();


                 */





            }

        }, 1500);


        /*

        if (checkInternetConnection(Splash.this)) {


            if(notificationUrl.matches("noUrl")){


                SharedPreferences sharedPref =
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

                String Menu = sharedPref.getString("activity_priority", "");
                if (Objects.requireNonNull(Menu).equals("menu")) {

                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                    finish();


                }

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

        else{

            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

            BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);
            dialog.show();

        }


         */

    }

    private void checkIntent(Intent intent) {



     //   String action = intent.getAction();
    //    String url = intent.getStringExtra(Intent.EXTRA_TEXT);





        if (intent.hasExtra("Link")) {


                String target = "";
                String newT = Objects.requireNonNull(intent.getExtras()).getString("Link", target);

                notificationUrl = newT;

                Toast.makeText(this, "Received intent data >>  "+ newT , Toast.LENGTH_LONG).show();

                }




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
         //       errorView.playAnimation();
              //  errorView.setAnimation(R.raw.error);
                dialog.setCancelable(false);
                dialog.setOnCancelListener(dialog1 -> {

                    Toast.makeText(Splash.this, "Killing Progress", Toast.LENGTH_LONG).show();
                    Splash.this.finish();
                });



                /*

                int[] items = new int[] {

                        R.string.retry,
                        R.string.quit



                };

                int[] icons = new int[] {

                        R.drawable.ic_retry,
                        R.drawable.ic_close_black_24dp


                };

                 */

                /*Custom Bottom Sheet Dialog Implementation */

                /*

                BottomSheet.Builder builder = new BottomSheet.Builder(Splash.this);
                builder.setTitle("Ah UH ErrrroR!");


                builder.setItems(items, icons, (dialogInterface, i) -> {
                    // Toast.makeText(MainBrowser.this, items[i], Toast.LENGTH_SHORT).show();

                    if(items[i] == R.string.retry){

                        checkConnection(Splash.this);
                        isLayoutOnline = true;

                    }

                    if(items[i] == R.string.report){


                        Splash.this.finish();
                        isLayoutOnline = true;


                    }

                });

                builder.show();


                 */

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
       //     errorView.playAnimation();
         //   errorView.setAnimation(R.raw.error);
            dialog.setCancelable(false);
            dialog.setOnCancelListener(dialog1 -> {

                Toast.makeText(Splash.this, "Killing Progress", Toast.LENGTH_LONG).show();
                Splash.this.finish();
            });



            /*


            int[] items = new int[] {

                    R.string.retry,
                    R.string.quit



            };

            int[] icons = new int[] {

                    R.drawable.ic_retry,
                    R.drawable.ic_close_black_24dp


            };

             */

            /*Custom Bottom Sheet Dialog Implementation */

            /*

            BottomSheet.Builder builder = new BottomSheet.Builder(Splash.this);
            builder.setTitle("Ah UH ErrrroR!");



            builder.setItems(items, icons, (dialogInterface, i) -> {
                // Toast.makeText(MainBrowser.this, items[i], Toast.LENGTH_SHORT).show();

                if(items[i] == R.string.retry){

                    checkConnection(Splash.this);
                    isLayoutOnline = true;

                }

                if(items[i] == R.string.report){


                    Splash.this.finish();
                    isLayoutOnline = true;


                }

            });

            builder.show();


             */


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

