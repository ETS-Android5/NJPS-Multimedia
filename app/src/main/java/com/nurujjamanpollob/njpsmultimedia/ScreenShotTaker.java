package com.nurujjamanpollob.njpsmultimedia;


import static com.nurujjamanpollob.njpsmultimedia.Variables.excepTionMainFileName;
import static java.util.Objects.requireNonNull;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nurujjamanpollob.njpsmultimedia.loaders.NJPollobExceptionWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import dev.nurujjamanpollob.extra.bacgroudworkrunner.NJPollobCustomAsyncTask;
import dev.nurujjamanpollob.njpollobutilities.BackgroundWorker.ThreadFixer;

public class ScreenShotTaker {


    private  String title = "NJPS Multimedia screenshot taker";
    private  float contentHeight = 0;
    private  int windowWidth = 0;
    Activity context;
    WebView webView;

    public ScreenShotTaker(Activity activityContext, WebView webViewInstance){

        this.context = activityContext;
        this.webView = webViewInstance;



        try {

            this.windowWidth = ViewUnit.getWindowWidth(context);
            this.contentHeight = webView.getContentHeight() * ViewUnit.getDensity(context);
            this.title = HelperUnit.fileName(webView.getUrl());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void takeScreenShot(){

        Sync nc = new Sync();
        nc.runThread();


    }



    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException {


        OutputStream fos;

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(requireNonNull(uri));

        } else {

            String imagesDir = Environment.getExternalStoragePublicDirectory("NJPS Multimedia/Web Archive/Screenshots").toString() + File.separator;
            File file = new File(requireNonNull(imagesDir));

            if (!requireNonNull(file).exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }
            File image = new File(imagesDir, name + ".jpg");
            fos = new FileOutputStream(image);
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        requireNonNull(fos).flush();
        fos.close();

        Toast.makeText(context, "Picture Saved", Toast.LENGTH_LONG).show();
    }


    private class Sync extends NJPollobCustomAsyncTask<Void, Void> {


        @Override
        protected Void doBackgroundTask() {

            //start background work and get all snapshot from screen
            if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29) {
                int hasWRITE_EXTERNAL_STORAGE = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                    HelperUnit.grantPermissionsStorage(context);
                } else {
                    try {
                        ThreadFixer fx = new ThreadFixer(new Handler(Looper.getMainLooper()));
                        fx.setListenerForFixThread(() -> {
                            Bitmap bitmap = ViewUnit.capture(webView, windowWidth, contentHeight);
                            saveImage(bitmap, title);
                        });

                    } catch (Exception e) {

                        Toast.makeText(context, "Please see this file in your phone memory's root folder: "+excepTionMainFileName+" To see error!", Toast.LENGTH_LONG).show();
                        NJPollobExceptionWriter writer = new NJPollobExceptionWriter(Environment.getExternalStorageDirectory().getPath(), excepTionMainFileName, e.toString());
                        writer.isIncludeLogToExistingLogFile(true);
                        writer.setClassNameForLog("ScreenShotTaker.Java");
                        writer.performWriteOperation();
                    }
                }
            } else {
                try {

                        Bitmap bitmap = ViewUnit.capture(webView, windowWidth, contentHeight);
                        saveImage(bitmap, title);


                } catch (Exception e) {
                    Toast.makeText(context, "Please see this file in your phone memory's root folder: "+excepTionMainFileName+" To see error!", Toast.LENGTH_LONG).show();
                    NJPollobExceptionWriter writer = new NJPollobExceptionWriter(Environment.getExternalStorageDirectory().getPath(), excepTionMainFileName, e.toString());
                    writer.isIncludeLogToExistingLogFile(true);
                    writer.setClassNameForLog("ScreenShotTaker.Java");
                    writer.performWriteOperation();
                }
            }
            return null;
        }
    }

}


