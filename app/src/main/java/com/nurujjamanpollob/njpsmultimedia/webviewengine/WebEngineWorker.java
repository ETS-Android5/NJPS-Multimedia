package com.nurujjamanpollob.njpsmultimedia.webviewengine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;

@SuppressLint("ViewConstructor")
public class WebEngineWorker extends View {

    WebView webView;
    String url;

    public WebEngineWorker(Context context, String inputURL){
        super(context);

        this.webView = new WebView(context);
       this.url = inputURL;
    }

    public void goBack(){

        if(webView != null && webView.canGoBack()){
            webView.goBack();
        }
    }

    public void goForward(){

        if(webView != null && webView.canGoForward()){

            webView.goForward();
        }
    }

    public boolean isURL(String URL){

        return URLUtil.isValidUrl(URL);
    }

    public void LoadWithText(String textInput){

        if (Patterns.WEB_URL.matcher(textInput.toLowerCase()).matches())
        {

            if (textInput.contains("http://") || textInput.contains("https://"))
            {
                webView.loadUrl(textInput);
            }
            else
            {
                webView.loadUrl("http://" + textInput);
            }
        }
        else
        {
            webView.loadUrl("https://www.google.com/search?q=" + textInput);
        }
    }

    public void runWebEngine(){

        webView.loadUrl(url);
    }

}
