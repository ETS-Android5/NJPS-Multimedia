package com.nurujjamanpollob.njpsmultimedia.downloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.nurujjamanpollob.njpsmultimedia.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;


public class DownloadActivity extends AppCompatActivity {

    private static final int ITAG_FOR_AUDIO = 140;

    private static String youtubeLink;

    private LinearLayout mainLayout;
    private ProgressBar mainProgressBar;
    private List<YtFragmentedVideo> formatsToShowList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download);
        mainLayout = findViewById(R.id.main_layout);
        mainProgressBar = findViewById(R.id.prgrBar);



        if (getIntent().getExtras() != null) {

            String ytLink   = getIntent().getStringExtra("url");

            if (ytLink != null && (ytLink.contains("youtu.be") || ytLink.contains("youtube.com/watch?v="))) {
                youtubeLink = ytLink;
                // We have a valid link
                getYoutubeDownloadUrl(youtubeLink);
            }
        } else if (savedInstanceState != null && youtubeLink != null) {
            getYoutubeDownloadUrl(youtubeLink);
        } else {
            finish();

        }


    }





    @SuppressLint("StaticFieldLeak")
    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(this) {

            @SuppressLint("SetTextI18n")
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {



                mainProgressBar.setVisibility(View.GONE);
                if (ytFiles == null) {
                    TextView tv = new TextView(DownloadActivity.this);
                    tv.setText("This YouTube Downloader Plugin is requires a update to run properly.");
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    mainLayout.addView(tv);
                    return;
                }

                System.out.println("Found This Array: " + ytFiles);

                formatsToShowList = new ArrayList<>();
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    YtFile ytFile = ytFiles.get(itag);

                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        addFormatToList(ytFile, ytFiles);
                    }
                }
                Collections.sort(formatsToShowList, (lhs, rhs) -> lhs.height - rhs.height);
                for (YtFragmentedVideo files : formatsToShowList) {
                    addButtonToMainLayout(vMeta.getTitle(), files);
                }
            }
        }.extract(youtubeLink, true, true);
    }

    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles) {
        int height = ytFile.getFormat().getHeight();
        if (height != -1) {
            for (YtFragmentedVideo frVideo : formatsToShowList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getFormat().getFps() == ytFile.getFormat().getFps())) {
                    return;
                }
            }
        }
        YtFragmentedVideo frVideo = new YtFragmentedVideo();
        frVideo.height = height;
        if (ytFile.getFormat().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }
        formatsToShowList.add(frVideo);
    }


    private void addButtonToMainLayout(final String videoTitle, final YtFragmentedVideo ytFrVideo) {
        // Display some buttons and let the user choose the format
        String btnText;
        if (ytFrVideo.height == -1)
            btnText = "Audio " + ytFrVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s";
        else
            btnText = (ytFrVideo.videoFile.getFormat().getFps() == 60) ? ytFrVideo.height + "p60" :
                    ytFrVideo.height + "p";
        Button btn = new Button(this);
        btn.setText(btnText);
        btn.setOnClickListener(v -> {
            String filename;
            if (videoTitle.length() > 55) {
                filename = videoTitle.substring(0, 55);
            } else {
                filename = videoTitle;
            }
            filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
            filename += (ytFrVideo.height == -1) ? "" : "-" + ytFrVideo.height + "p";
            String downloadIds = "";
            boolean hideAudioDownloadNotification = false;
            if (ytFrVideo.videoFile != null) {
                downloadIds += downloadFromUrl(ytFrVideo.videoFile.getUrl(), videoTitle,
                        filename + "." + ytFrVideo.videoFile.getFormat().getExt(), false);
                downloadIds += "-";
                hideAudioDownloadNotification = true;
            }
            if (ytFrVideo.audioFile != null) {
                downloadIds += downloadFromUrl(ytFrVideo.audioFile.getUrl(), videoTitle,
                        filename + "." + ytFrVideo.audioFile.getFormat().getExt(), hideAudioDownloadNotification);
            }
            if (ytFrVideo.audioFile != null)
                cacheDownloadIds(downloadIds);
            finish();
        });
        mainLayout.addView(btn);
    }

    private long downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName, boolean hide) {


        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);
        if (hide) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setVisibleInDownloadsUi(false);
        } else
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        assert manager != null;
        return manager.enqueue(request);
    }

    private void cacheDownloadIds(String downloadIds) {
        File dlCacheFile = new File(this.getCacheDir().getAbsolutePath() + "/" + downloadIds);
        try {
            dlCacheFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class YtFragmentedVideo {
        int height;
        YtFile audioFile;
        YtFile videoFile;
    }

}
