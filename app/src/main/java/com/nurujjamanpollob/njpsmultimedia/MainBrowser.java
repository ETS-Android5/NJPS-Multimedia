package com.nurujjamanpollob.njpsmultimedia;


import static android.os.Build.VERSION_CODES.Q;
import static com.nurujjamanpollob.njpsmultimedia.Variables.HISTORY;
import static com.nurujjamanpollob.njpsmultimedia.Variables.PREFERENCES;
import static com.nurujjamanpollob.njpsmultimedia.Variables.PREFS_NAME;
import static com.nurujjamanpollob.njpsmultimedia.Variables.WEB_LINKS;
import static com.nurujjamanpollob.njpsmultimedia.Variables.WEB_TITLE;
import static java.util.Objects.requireNonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.palette.graphics.Palette;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nurujjamanpollob.njpsmultimedia.codecollection.CodeCollection;
import com.nurujjamanpollob.njpsmultimedia.downloader.DownloadActivity;
import com.nurujjamanpollob.njpsmultimedia.loaders.NJPollobExceptionWriter;
import com.nurujjamanpollob.njpsmultimedia.mailsender.GMailSender;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dev.nurujjamanpollob.njpollobutilities.BackgroundWorker.ThreadFixer;
import dev.nurujjamanpollob.njpollobutilities.HTMLThemeColorExtractor.HTMLThemeColorGetter;

public class MainBrowser extends AppCompatActivity {



    private WebView webView;
    private String current_page_url = "https://njpsmultimedia.blogspot.com";
    private RelativeLayout LinearLayout;
    private EditText edittext;
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int in = 1234;
    private View mCustomView;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ImageButton homebutton;
    public RelativeLayout nav;
    private Bitmap ico;
    private Bitmap home;
    public Bitmap back;
    private SwipeRefreshLayout swipe;
    private ImageButton saveButton;
    private ImageButton voiceDetector;
    private Toolbar toolbar;
    private boolean isVoiceSupportPresent = true;
    private ImageButton shareUrl;
    private  WebSettings WebSettings;




    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    setContentView(R.layout.multimedia_activity);
    webView = new WebView(this);




    PackageManager pm = getPackageManager();

    List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

    if(activities.size() == 0) {

        isVoiceSupportPresent = false;


    }


    decodebitmap();


    swipe = findViewById(R.id.web_rl);
    swipe.setOnRefreshListener(() -> webView.reload());
    swipe.setColorSchemeColors(getResources().getColor(R.color.pink));
    voiceDetector = findViewById(R.id.get_voice_browser);

    toolbar = findViewById(R.id.toolbar_browser);
    ContentLoadingProgressBar progressBar = findViewById(R.id.progressbar_webview);
    saveButton = findViewById(R.id.save_button);


    setSupportActionBar(toolbar);
    requireNonNull(getSupportActionBar()).setTitle("");
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(view -> {


       appCloseDialog();

        });



    if (getIntent().getExtras() != null) {

        current_page_url = getIntent().getStringExtra("url");

    }

    //set up listener for go button

        Button goButton = findViewById(R.id.launch_website);


    goButton.setOnClickListener(v -> go());


    nav = findViewById(R.id.navigation_layout);

    webView = findViewById(R.id.webView_browser);
    webView.loadUrl(current_page_url);
    shareUrl = findViewById(R.id.share_url);



    LinearLayout = findViewById(R.id.main_content_browser);
    edittext = findViewById(R.id.search_load_edit_text_browser);


    edittext.setOnKeyListener((v, keyCode, event) -> {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            go();
            return true;
        }

        return false;
    });


   WebSettings = webView.getSettings();
    webView.setWebChromeClient(new WebChromeClient() {


        @Override
        public boolean onShowFileChooser(
                WebView webView, ValueCallback<Uri[]> filePathCallback,
                FileChooserParams fileChooserParams) {

            fileChooserDialog(filePathCallback, fileChooserParams);

            return true;
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onProgressChanged(WebView view, int newProgress) {


            progressChanged(view, newProgress);

            super.onProgressChanged(view, newProgress);

        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {

            HTMLThemeColorGetter getter = new HTMLThemeColorGetter(view.getUrl());
            getter.ListenOnLoadEvent(new HTMLThemeColorGetter.ListenOnGetEvent() {
                @Override
                public void onSuccessfullColorExtraction(String colorStr) {

                    if(colorStr != null) {

                        ThreadFixer fixer = new ThreadFixer(new Handler(Looper.getMainLooper()));
                        fixer.setListenerForFixThread(() -> {

                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(Color.parseColor(colorStr));
                            window.setNavigationBarColor(Color.parseColor(colorStr));

                            nav.setBackgroundColor(Color.parseColor(colorStr));
                            toolbar.setBackgroundColor(Color.parseColor(colorStr));


                        });

                    }

                }

                @Override
                public void onExceptionOccured(String exceptionMsg) {

                    ThreadFixer fixer = new ThreadFixer(new Handler(Looper.getMainLooper()));
                    fixer.setListenerForFixThread(() -> {



                        Palette palette = Palette.from(icon).generate();

                        toolbar.setBackgroundColor(palette.getVibrantColor(0x00000));
                        nav.setBackgroundColor(palette.getVibrantColor(0x00000));


                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(palette.getVibrantColor(0x00000));
                        window.setNavigationBarColor(palette.getVibrantColor(0x00000));


                    });


                }
            });


            super.onReceivedIcon(view, icon);


        }

        @Override
        public Bitmap getDefaultVideoPoster() {

            return BitmapFactory.decodeResource(MainBrowser.this.getApplicationContext().getResources(),
                    R.drawable.video_poster);
        }

        @Override
        public void onShowCustomView(View view,
                                     CustomViewCallback callback) {

            showCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {

            hideCustomView();

        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {

            String asker = request.getOrigin().toString();


            AlertDialog.Builder builder = new AlertDialog.Builder(MainBrowser.this);
            builder.setTitle(asker);
            builder.setMessage("This webpage asking for special permission.\nas it may high risk! \n\nthis request is thrown >>  " + Arrays.toString(request.getResources()) + " \n    Will allow it???");
            builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", (dialog, which) -> request.grant(request.getResources()));
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.cancel();
                request.deny();
            });
            AlertDialog alert = builder.create();
            alert.show();

            super.onPermissionRequest(request);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {


            EditText data = new EditText(view.getContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(MainBrowser.this);
            builder.setTitle(view.getTitle());
            builder.setView(data);
            builder.setMessage(message);
            builder.setOnCancelListener(dialog -> result.cancel());
            builder.setIcon(R.drawable.ic_launcher);

            builder.setPositiveButton("Yes", (dialog, which) -> result.confirm());
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.cancel();
                result.cancel();
            });
            AlertDialog alert = builder.create();
            alert.show();

            return super.onJsAlert(view, url, message, result);
        }
    });


    WebSettings.setAllowFileAccess(true);
    WebSettings.setAppCacheEnabled(true);
    webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (MainBrowser.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                CodeCollection.makeDownloadDialogAndDownloadFile(MainBrowser.this, url, userAgent, contentDisposition, mimetype);

            } else {


                ActivityCompat.requestPermissions(MainBrowser.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }
        } else {

            CodeCollection.makeDownloadDialogAndDownloadFile(MainBrowser.this, url, userAgent, contentDisposition, mimetype);

        }

    });


    webView.setWebViewClient(new WebViewClient() {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            if (url.startsWith("http://") || url.startsWith("https://")) {


                view.loadUrl(url);


            }

            if (url.startsWith("intent://")) {
                try {
                    Context context = view.getContext();
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                    if (intent != null) {
                        view.stopLoading();

                        PackageManager packageManager = context.getPackageManager();
                        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (info != null) {
                            context.startActivity(intent);
                        } else {
                            String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                            assert fallbackUrl != null;
                            view.loadUrl(fallbackUrl);

                        }

                        return true;
                    }
                } catch (URISyntaxException e) {

                    Toast.makeText(MainBrowser.this, "Failed to process this intent >>>  " + e, Toast.LENGTH_LONG).show();
                }
            }


            if (url.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));


                startActivity(intent);

            }


            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(url));
                startActivity(intent);
            }


            return true;
        }


        @RequiresApi(24)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


                view.loadUrl(request.getUrl().toString());

                if (request.getUrl().toString().startsWith("mailto:")) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(request.getUrl().toString()));
                    startActivity(intent);

                }


                if (request.getUrl().toString().startsWith("intent://")) {
                    try {
                        Context context = view.getContext();
                        Intent intent = Intent.parseUri(request.getUrl().toString(), Intent.URI_INTENT_SCHEME);

                        if (intent != null) {
                            view.stopLoading();

                            PackageManager packageManager = context.getPackageManager();
                            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                            if (info != null) {
                                context.startActivity(intent);
                            } else {
                                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                                assert fallbackUrl != null;
                                view.loadUrl(fallbackUrl);
                            }

                            return true;
                        }
                    } catch (URISyntaxException e) {

                        Toast.makeText(MainBrowser.this, "Failed to process this intent >>>  " + e, Toast.LENGTH_LONG).show();
                    }
                }


                if (request.getUrl().toString().startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(request.getUrl().toString()));
                    startActivity(intent);
                }


                if (request.getUrl().toString().startsWith("http://") || request.getUrl().toString().startsWith("ftp:") || request.getUrl().toString().startsWith("localhost:") || request.getUrl().toString().startsWith("file:///") || request.getUrl().toString().startsWith("https://")) {

                    view.loadUrl(request.getUrl().toString());


                }
            }

            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            if (!"edit_url".equals(url)) {

                edittext.setText(url);

            } else {
                edittext.setText("");
            }


            super.onPageStarted(view, url, favicon);


        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            invalidateOptionsMenu();
            swipe.setRefreshing(false);

        }


        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {

            SharedPreferences sharedPreferences = getSharedPreferences(HISTORY, Context.MODE_PRIVATE);
            String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
            String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);


            if (jsonLink != null && jsonTitle != null) {

                Gson gson = new Gson();
                ArrayList<String> linkList = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
                }.getType());

                ArrayList<String> titleList = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
                }.getType());


                if (requireNonNull(view.getUrl()).contains((url))) {

                    linkList.add(url);
                    titleList.add(requireNonNull(view.getTitle()).trim());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                    editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                    editor.apply();


                }
            } else {

                ArrayList<String> linkList = new ArrayList<>();
                ArrayList<String> titleList = new ArrayList<>();
                linkList.add(url);
                titleList.add(requireNonNull(view.getTitle()).trim());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                editor.apply();


            }


        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            invalidateOptionsMenu();
            swipe.setRefreshing(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainBrowser.this);
            builder.setTitle("Hmm this page can't be reached :(");
            builder.setMessage("Possible Reasons: \nError Description: " + description + "\nFailing URL: " + failingUrl + "\n Error Code: " + errorCode + "\nI hope it helps you understand the error!");
            builder.setIcon(R.drawable.ic_launcher);
            builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
            builder.setNeutralButton("Fix URL", (dialogInterface, i) -> {
                if (failingUrl.startsWith("https://")) {

                    webView.loadUrl(failingUrl.replace("https://", "http://"));
                }
                if (failingUrl.startsWith("http://")) {

                    webView.loadUrl(failingUrl.replace("http://", "https://"));
                }

            });
            AlertDialog alert = builder.create();
            alert.show();


        }


    });


    webViewConfig();
    onClickItems();


}

    private void appCloseDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(MainBrowser.this);

        builder.setTitle("Warning!");
        builder.setMessage("This will close this Application. Click Exit to proceed!");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Exit", (dialogInterface, i) -> MainBrowser.this.finish());
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void webViewConfig() {


        SharedPreferences sharedPref =
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        boolean darkMode = sharedPref.getBoolean("dark_ui", true);

        if (darkMode) {

            setTheme(R.style.NjpollobDark_NoActionBar);

            if (Build.VERSION.SDK_INT >= Q) {
                webView.setForceDarkAllowed(true);
            }
        }


        boolean fast_hardware = sharedPref.getBoolean("fast_hardware", true);

        if (fast_hardware) {

            WebView.enableSlowWholeDocumentDraw();


        }

        boolean js_enable = sharedPref.getBoolean("js_enable", true);


        WebSettings.setJavaScriptEnabled(js_enable);

        boolean safeBrowsing = sharedPref.getBoolean("safe_browsing_api", true);

        if (safeBrowsing) {


            if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {

                WebSettingsCompat.setSafeBrowsingEnabled(WebSettings, true);

            }


        }


        boolean load_cookies = sharedPref
                .getBoolean("load_cookies", true);

        WebSettings.setDatabaseEnabled(load_cookies);

        boolean zoom_control = sharedPref
                .getBoolean("zoom_control", true);

        if (zoom_control) {

            WebSettings.setBuiltInZoomControls(true);
            WebSettings.setDisplayZoomControls(false);


        } else {

            WebSettings.setBuiltInZoomControls(false);

        }

        boolean no_cache = sharedPref.getBoolean("no_cache", true);

        if (no_cache) {

            WebSettings.setCacheMode(android.webkit.WebSettings.LOAD_NO_CACHE);


        }

        boolean load_picture = sharedPref
                .getBoolean("load_picture", true);

        WebSettings.setBlockNetworkImage(!load_picture);

        boolean allow_geolocation = sharedPref.getBoolean("allow_geolocation", true);

        WebSettings.setGeolocationEnabled(allow_geolocation);


        String Chrome = sharedPref.getString("browser_mode", "chrome");
        if (requireNonNull(Chrome).equals("chrome")) {


            WebSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36");


        }


        String IE = sharedPref.getString("browser_mode", "ie");
        if (requireNonNull(IE).equals("ie")) {


            WebSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Trident/7.0; rv:11.0) like Gecko");


        }

        String Safari = sharedPref.getString("browser_mode", "safari");
        if (requireNonNull(Safari).equals("safari")) {


            WebSettings.setUserAgentString("Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7");

        }


        String Nokia = sharedPref.getString("browser_mode", "nokia");
        if (requireNonNull(Nokia).equals("nokia")) {


            WebSettings.setUserAgentString("Mozilla/5.0 (Symbian/3; Series60/5.2 NokiaN8-00/012.002; Profile/MIDP-2.1 Configuration/CLDC-1.1 ) AppleWebKit/533.4 (KHTML, like Gecko) NokiaBrowser/7.3.0 Mobile Safari/533.4 3gpp-gba");


        }


        WebSettings.setDomStorageEnabled(true);

    }

    private void hideCustomView() {


        // 1. Remove the custom view
        FrameLayout decor = (FrameLayout) MainBrowser.this.getWindow().getDecorView();
        decor.removeView(mCustomView);
        mCustomView = null;

        // 2. Restore the state to it's original form
        MainBrowser.this.getWindow().getDecorView()
                .setSystemUiVisibility(mOriginalSystemUiVisibility);
        MainBrowser.this.setRequestedOrientation(mOriginalOrientation);

        // 3. Call the custom view callback
        mCustomViewCallback.onCustomViewHidden();
        mCustomViewCallback = null;

    }

    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {

        // if a view already exists then immediately terminate the new one
        if (mCustomView != null) {
           hideCustomView();
            return;
        }

        // 1. Stash the current state
        mCustomView = view;


        mOriginalSystemUiVisibility = MainBrowser.this.getWindow().getDecorView().getSystemUiVisibility();
        mOriginalOrientation = getRequestedOrientation();

        // 2. Stash the custom view callback
        mCustomViewCallback = callback;

        // 3. Add the custom view to the view hierarchy
        FrameLayout decor = (FrameLayout) MainBrowser.this.getWindow().getDecorView();
        decor.addView(mCustomView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


        // 4. Change the state of the window
        MainBrowser.this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE);
        MainBrowser.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void progressChanged(WebView view, int newProgress) {


        if (requireNonNull(view.getUrl()).startsWith("://youtu.be/") || view.getUrl().startsWith("://youtu.be/")) {

            Snackbar snake = Snackbar.make(swipe, "Download this Video", Snackbar.LENGTH_LONG);

            snake.setAction("Download", v -> {

                Intent i = new Intent(MainBrowser.this, DownloadActivity.class);
                i.putExtra("url", view.getUrl());
                startActivity(i);

            });

            snake.show();

        }


        if (newProgress >= 10) {

            swipe.setRefreshing(true);
            swipe.setColorSchemeColors(getResources().getColor(R.color.red));

            homebutton.setImageBitmap(ico);


        }

        if (newProgress >= 80) {

            swipe.setColorSchemeColors(getResources().getColor(R.color.green));

        }
        if (newProgress == 100) {

            homebutton.setImageBitmap(home);
            swipe.setRefreshing(false);

        }

    }

    private void fileChooserDialog(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
        }
        mFilePathCallback = filePathCallback;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (Exception e) {

                e.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "NJPS Multimedia File Uploader");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {




        Uri [] results = null;

        if(requestCode == in){


            if(resultCode == Activity.RESULT_OK){

                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                String topResult = requireNonNull(matches).get(0);
                edittext.setText(topResult);

                SharedPreferences sharedPref =
                        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


                boolean voice_auto_search = sharedPref
                        .getBoolean("voice_auto_search", true);


                if(voice_auto_search){

                    go();


                }


            }

            else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Voice Command Cancelled!", Toast.LENGTH_SHORT).show();
            }


        }


        else if(requestCode== INPUT_FILE_REQUEST_CODE){

            if(resultCode ==Activity.RESULT_OK){

                if(data == null) {
                    if(mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }


                }
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback= null;


            }

            else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "File Upload Request Cancelled!", Toast.LENGTH_SHORT).show();
            }


        }



        super.onActivityResult(requestCode, resultCode, data);


    }







    private void onClickItems(){

        saveButton.setOnClickListener(v -> {

            PopupMenu menu = new PopupMenu(MainBrowser.this, v);

            MenuInflater inflater = menu.getMenuInflater();

            inflater.inflate(R.menu.save_menu, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.save_as_pdf) {

                    print();
                    return true;
                }
                if (item.getItemId() == R.id.show_history) {

                    Intent i = new Intent(MainBrowser.this, NjpHistory.class);
                    startActivity(i);
                    return true;
                }

                if (item.getItemId() == R.id.show_bookmark) {

                    Intent i = new Intent(MainBrowser.this, BookmarkActivity.class);
                    startActivity(i);

                    return true;
                }


                if (item.getItemId() == R.id.download_videos) {

                    String url = webView.getUrl();

                    if(url.contains("youtu.be") || url.contains("youtube.com/watch?v=")) {

                        Intent i = new Intent(MainBrowser.this, DownloadActivity.class);
                        i.putExtra("url", webView.getUrl());
                        startActivity(i);

                    }else {


                        Snackbar snackbar = Snackbar.make(LinearLayout, "This URL is not a valid YouTube URL", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    }

                    return true;
                }

                if (item.getItemId() == R.id.save_as_screenshot){

                    ScreenShotTaker tk = new ScreenShotTaker(MainBrowser.this, webView);
                    tk.takeScreenShot();
                }

                return false;
            });


            menu.show();

        });








        shareUrl.setOnClickListener(v -> {


            String Get_uri = webView.getUrl();


            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, Get_uri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "URL");

            startActivity(Intent.createChooser(shareIntent, "Share with your friends"));


        });
        homebutton = findViewById(R.id.homebutton);
        homebutton.setOnClickListener(v -> {

            if (webView.getProgress() < 100) {

                webView.stopLoading();
            }
            if (webView.getProgress() == 100) {


                webView.loadUrl("about:blank");
            }


        });



        voiceDetector.setOnClickListener(view -> {

            if(isVoiceSupportPresent) {

                takeVoice();
            }

            if(!isVoiceSupportPresent){

                Toast.makeText(MainBrowser.this, "Voice support doesn't present, disabled or not installed google app", Toast.LENGTH_SHORT).show();
            }


        });





    }





    private File createImageFile() throws IOException {

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = MainBrowser.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
    }






    public void decodebitmap()
    {

        Drawable d = ContextCompat.getDrawable(MainBrowser.this, R.drawable.njps_load_cancel);

        Bitmap bitmap = Bitmap.createBitmap(requireNonNull(d).getIntrinsicWidth(),
                d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        ico = bitmap;

        decodehome();


    }

    private void decodehome()
    {


        Drawable dh = ContextCompat.getDrawable(MainBrowser.this, R.drawable.ic_home_black_24dp);

        Bitmap bit = Bitmap.createBitmap(requireNonNull(dh).getIntrinsicWidth(),
                dh.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(bit);
        dh.setBounds(0, 0, can.getWidth(), can.getHeight());
        dh.draw(can);

        home = bit;

        decodeback();


    }

    private void decodeback() {

        Drawable dh = ContextCompat.getDrawable(MainBrowser.this, R.drawable.ic_arrow_back_black_24dp);

        Bitmap bit = Bitmap.createBitmap(requireNonNull(dh).getIntrinsicWidth(),
                dh.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(bit);
        dh.setBounds(0, 0, can.getWidth(), can.getHeight());
        dh.draw(can);

        back = bit;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        String Matcher_url = webView.getUrl();

        getMenuInflater().inflate(R.menu.browser, menu);



        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String links = sharedPreferences.getString(WEB_LINKS, null);


        if (links != null) {

            Gson gson = new Gson();
            ArrayList<String> linkList = gson.fromJson(links, new TypeToken<ArrayList<String>>() {
            }.getType());

            if (linkList.contains(Matcher_url)) {
                menu.getItem(0).setIcon(R.drawable.ic_bookmark_black_24dp);
            }



            else {
                menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_black_24dp);

            }
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_black_24dp);


        }


        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {





        if(item.getItemId() == R.id.open_translator){




            String frst = "https://translate.googleusercontent.com/translate_c?depth=1&hl=en&nv=17rurl=translate.google.com&sl=auto&sp=nmt4&tl=en&u=";
            String suffix = webView.getUrl()+"/";
            String second = "https://translate.googleusercontent.com/translate_c?depth=1&hl=en&nv=17rurl=translate.google.com&sl=auto&sp=nmt4&tl=bn&u=";


            SharedPreferences sharedPref =
                    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            String Translate= sharedPref.getString("translator_model", "english");
            if(requireNonNull(Translate).equals("english")) {

                webView.loadUrl(frst+suffix);

            }



            String two = sharedPref.getString("translator_model", "bangla");

            assert two != null;
            if(two.equals("bangla")){

                webView.loadUrl(second+suffix);

            }




        }

        if (item.getItemId() == R.id.open_in_chrome){



            AlertDialog.Builder builder=new AlertDialog.Builder(MainBrowser.this);
            builder.setTitle("Warning!");
            builder.setMessage("Make sure you have Chrome installed \n Otherwise, this app will crash!");
            builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", (dialog, which) -> {




                String url = webView.getUrl();


                CustomTabsIntent.Builder intent = new CustomTabsIntent.Builder();

                CustomTabsIntent tab = intent.build();

                tab.intent.setPackage("com.android.chrome");

                intent.setStartAnimations(MainBrowser.this, android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                        .setExitAnimations(MainBrowser.this, android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right)
                        .setCloseButtonIcon(back)
                        .addDefaultShareMenuItem()
                        .setInstantAppsEnabled(true)
                        .setToolbarColor(Color.parseColor("#43A047"))
                        .enableUrlBarHiding()
                        .build();
                intent.build().launchUrl(MainBrowser.this, Uri.parse(url));



            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alert =builder.create();
            alert.show();


        }




        if (item.getItemId() == R.id.learn){

            webView.loadUrl("http://www.w3schools.com");


        }

        if(item.getItemId() == R.id.settings){

            Intent i = new Intent(MainBrowser.this, Settings.class);
            startActivity(i);

        }

        if (item.getItemId()== R.id.downloads){


            Intent i = new Intent (DownloadManager.ACTION_VIEW_DOWNLOADS);
            startActivity(i);


        }
        if (item.getItemId() == R.id.action_bookmark) {

            String web_url = webView.getUrl();

            String message;

            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
            String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);


            if (jsonLink != null && jsonTitle != null) {

                Gson gson = new Gson();
                ArrayList<String> linkList = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
                }.getType());

                ArrayList<String> titleList = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
                }.getType());

                if (linkList.contains(web_url)) {
                    linkList.remove(web_url);
                    titleList.remove(requireNonNull(webView.getTitle()).trim());
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                    editor.putString(WEB_TITLE, new Gson().toJson(titleList));


                    editor.apply();


                    message = "Bookmark Removed";

                } else {
                    linkList.add(web_url);
                    titleList.add(requireNonNull(webView.getTitle()).trim());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                    editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                    editor.apply();

                    message = "Bookmarked";
                }
            } else {

                ArrayList<String> linkList = new ArrayList<>();
                ArrayList<String> titleList = new ArrayList<>();
                linkList.add(web_url);
                titleList.add(webView.getTitle());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                editor.apply();

                message = "Bookmarked";
            }

            Snackbar snackbar = Snackbar.make(LinearLayout, message, Snackbar.LENGTH_LONG);
            snackbar.show();

            invalidateOptionsMenu();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {


        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (webView != null) {
                if (webView.canGoBack()) {

                    webView.goBack();
                } else {

                    appCloseDialog();

                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public void print(){

        if(webView.getProgress() <100) {


            AlertDialog.Builder builder=new AlertDialog.Builder(MainBrowser.this);

            builder.setTitle("Error!");

            builder.setMessage("It is looking you try to convert this page to pdf when this app is still loading.as it may cause crash to main application.please, let the engine load this page, than try again.");
            builder.setIcon(R.drawable.ic_launcher);

            builder.setNegativeButton("Okay", (dialog, which) -> dialog.cancel());
            AlertDialog alert =builder.create();
            alert.show();


        } else {

            String link = webView.getUrl();

            String title = webView.getTitle();

            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            assert link != null;
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(link);
            assert title != null;
            requireNonNull(printManager).print(title, printAdapter, new PrintAttributes.Builder().build());

        }

    }





    @Override
    public void onPause() {


        super.onPause();


    }



    @Override
    public void onResume() {

//        webView.resumeTimers();

        super.onResume();


    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }






    public void searchOrLoad(String txt)

    {

        SharedPreferences sharedPref =
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String Google=sharedPref.getString("search_engine", "google");

        if(requireNonNull(Google).equals("google")){


            if (Patterns.WEB_URL.matcher(txt.toLowerCase()).matches())
            {

                if (txt.contains("http://") || txt.contains("https://"))
                {
                    webView.loadUrl(txt);
                }
                else
                {
                    webView.loadUrl("http://" + txt);
                }
                hideKeyboard();
            }
            else
            {
                webView.loadUrl("https://www.google.com/search?q=" + txt);
            }
            hideKeyboard();
        }




    }

    public void hideKeyboard()
    {
        InputMethodManager inputMethodManager= (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view=getCurrentFocus();
        requireNonNull(inputMethodManager).hideSoftInputFromWindow(requireNonNull(view).getWindowToken(), 0);
    }


    private void go()
    {


        String text=edittext.getText().toString();
        searchOrLoad(text);





    }





    public void back(View view) {

        if(webView.canGoBack()){

            webView.goBack();
        }


    }

    public void forward(View view) {

        if(webView.canGoForward()){

            webView.goForward();

        }


    }

    private void takeVoice(){


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireNonNull(getClass().getPackage()).getName());


        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "This Feature by Nurujjaman Pollob");

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        startActivityForResult(intent, in);


    }

    public void reload(View view) {

        webView.reload();


    }
}




