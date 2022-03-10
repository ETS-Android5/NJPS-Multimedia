package com.nurujjamanpollob.njpsmultimedia;


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
import android.provider.MediaStore;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.nurujjamanpollob.njpsmultimedia.interfaces.OnScreenShotTakeListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import dev.nurujjamanpollob.extra.bacgroudworkrunner.NJPollobCustomAsyncTask;


@SuppressWarnings({"unused"})
public class ScreenShotTaker {

    private final String title;
    private final float contentHeight;
    private final int windowWidth;
    private final Activity context;
    private final WebView webView;
    private OnScreenShotTakeListener screenShotTakeListener;


    public ScreenShotTaker(Activity activityContext, WebView webViewInstance){

        this.context = activityContext;
        this.webView = webViewInstance;
        this.windowWidth = ViewUnit.getWindowWidth(context);
        this.contentHeight = webView.getContentHeight() * ViewUnit.getDensity(context);
        this.title = HelperUnit.fileName(webView.getUrl());

    }


    public void takeScreenShot(){

        Sync nc = new Sync();
        nc.runThread();


    }

    public void setScreenShotTakeListener(OnScreenShotTakeListener listener){

        this.screenShotTakeListener = listener;
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


    }


    private class Sync extends NJPollobCustomAsyncTask<Void, Bitmap> {
        @Override
        protected Bitmap doBackgroundTask() {
            //start background work and get all snapshot from screen
            if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29) {


                int hasWRITE_EXTERNAL_STORAGE = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                    HelperUnit.grantPermissionsStorage(context);
                } else {

                    return ViewUnit.capture(webView, windowWidth, contentHeight);

                }
            } else {
                return ViewUnit.capture(webView, windowWidth, contentHeight);
            }

            return null;
        }

        @Override
        protected void onTaskFinished(Bitmap bitmap) {

            if(bitmap != null){

                try {
                    saveImage(bitmap, title);
                    if(screenShotTakeListener != null){
                        screenShotTakeListener.onBitmapCaptureSuccess();
                    }
                } catch (IOException e) {

                    if(screenShotTakeListener != null){
                        screenShotTakeListener.onBitmapCaptureError(e.toString());
                    }
                }

            }else {

                if(screenShotTakeListener != null){
                    screenShotTakeListener.onBitmapCaptureError("It is likely the View instance is null so the view capture thrown error.");
                }
            }

            super.onTaskFinished(bitmap);
        }



    }



}


