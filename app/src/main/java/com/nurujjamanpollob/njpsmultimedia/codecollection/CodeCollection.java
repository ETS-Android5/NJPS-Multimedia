package com.nurujjamanpollob.njpsmultimedia.codecollection;

import static android.content.Context.DOWNLOAD_SERVICE;
import static java.util.Objects.requireNonNull;

import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.nurujjamanpollob.njpsmultimedia.R;

public class CodeCollection {


    public static void makeDownloadDialogAndDownloadFile(Activity activity, @NonNull String url, String userAgent, String contentDisposition, String mimetype)
    {




        final String filename = URLUtil.guessFileName(url,contentDisposition,mimetype);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setTitle("NJPS Multimedia Downloader");
        builder.setMessage("Do you want to Download >>>" +filename);
        builder.setIcon(R.drawable.downloader_njp);
        builder.setPositiveButton("Yes", (dialog, which) -> {

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            String cookie= CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("Cookie",cookie);
            request.addRequestHeader("User-Agent",userAgent);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
            requireNonNull(downloadManager).enqueue(request);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());


        builder.create().show();

    }

}
