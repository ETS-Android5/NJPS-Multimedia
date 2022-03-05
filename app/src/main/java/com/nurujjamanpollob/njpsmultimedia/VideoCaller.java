package com.nurujjamanpollob.njpsmultimedia;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class VideoCaller extends Activity {

    WebView web;

    TextView textProgress;

    int load = 1;

    String url = "https://www.deeplearningsolution.com";



    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_caller_layout);

        textProgress = findViewById(R.id.progress_load_view);

        web = findViewById(R.id.videoWebview);

        if (getIntent().getExtras() != null) {

            load = 2;

            url = getIntent().getStringExtra("url");


        }

        if(load == 2){

            web.loadUrl(url);


        }else{
            Toast.makeText(this, "Error Fetch URL", Toast.LENGTH_SHORT).show();
        }


        web.setWebChromeClient(new WebChromeClient(){

            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if(newProgress <= 99){

                    textProgress.setText(web.getUrl()+ "  Loaded:  "+newProgress+"%");
                }else{

                    textProgress.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {

                runOnUiThread(() -> request.grant(request.getResources()));


             //   super.onPermissionRequest(request);
            }
        });

        WebSettings set = web.getSettings();

        // Enable Javascript
        set.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        set.setUseWideViewPort(true);
        set.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        set.setBuiltInZoomControls(true);

        // Allow use of Local Storage
        set.setDomStorageEnabled(true);

        // Hide the zoom controls for HONEYCOMB+
        set.setDisplayZoomControls(false);

        // Enable remote debugging via chrome://inspect
            WebView.setWebContentsDebuggingEnabled(true);


        web.setWebViewClient(new WebViewClient());

        // AppRTC requires third party cookies to work
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(web, true);

    }



    public void onStop() {
        super.onStop();

        /*
         * When the application falls into the background we want to stop the media stream
         * such that the camera is free to use by other apps.
         */
        web.evaluateJavascript("if(window.localStream){window.localStream.stop();}", null);
    }


}
