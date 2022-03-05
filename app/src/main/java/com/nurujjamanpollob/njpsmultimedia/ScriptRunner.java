package com.nurujjamanpollob.njpsmultimedia;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import static com.nurujjamanpollob.njpsmultimedia.Settings.getPrefs;

public class ScriptRunner extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packageInfo != null;
        Variables.versionname=packageInfo.versionName;


        getPrefs(this);

        if (savedInstanceState == null && Intent.ACTION_SEND.equals(getIntent().getAction())
                && getIntent().getType() != null && "text/plain".equals(getIntent().getType())) {


            String sharelink = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            Intent i = new Intent(ScriptRunner.this, MainBrowser.class);
            i.putExtra("url", sharelink);
            startActivity(i);
            finish();



        }

    }





}

