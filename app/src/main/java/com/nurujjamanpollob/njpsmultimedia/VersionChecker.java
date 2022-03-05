package com.nurujjamanpollob.njpsmultimedia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by AQEEL on 12/4/2018.
 */

// this class will get the latest version of app from playtore if the app is publish into playstore

public class VersionChecker extends AsyncTask<String, String, String> {

    private String newVersion;

    @SuppressLint("StaticFieldLeak")
    private Activity context;

    VersionChecker(Activity context) {
       this.context=context;
    }


    @Override
    protected String doInBackground(String... params) {

        try {
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id="+context.getPackageName()+"&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".IQ1z0d .htlgb")
                    .get(7)
                    .ownText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newVersion;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

     //   String latestVersion = newVersion;

        int main = Integer.parseInt(newVersion);
        int orig = Integer.parseInt(Variables.versionname);

        if(main > orig){

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setIcon(R.drawable.ic_launcher);
            alert.setTitle("Update NJPS Multimedia!")
                    .setMessage("This app may be outdated! Please Install new update!")
                    .setPositiveButton("Ignore", (dialog, which) -> dialog.cancel())
                    .setNegativeButton("Update", (dialog, id) -> {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+context.getPackageName())));
                        context.finish();
                    });

            alert.setCancelable(false);
            alert.show();


        }else {

            Toast.makeText(context, "You're on Latest Version: "+main, Toast.LENGTH_LONG).show();

        }

        /*

        if(!Variables.versionname.equals(latestVersion) && latestVersion!=null){

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
            alert.setTitle("Update NJPS Multimedia!")
                    .setMessage("This app may be outdated! Please Install new update!")
                    .setPositiveButton("Ignore", (dialog, which) -> dialog.cancel())
                    .setNegativeButton("Update", (dialog, id) -> {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+context.getPackageName())));
                        context.finish();
                    });

            alert.setCancelable(false);
            alert.show();
    }

         */
}
}