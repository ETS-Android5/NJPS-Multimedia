/*
 * Copyright (c) 2020 Nurujjaman Pollob.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.nurujjamanpollob.njpsmultimedia;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import static com.nurujjamanpollob.njpsmultimedia.R.string.default_notification_channel_id;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MessageReceiver extends FirebaseMessagingService {


    Bitmap bitmap;
    public String notificationUrl;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        

        if(remoteMessage.toIntent().hasExtra("Link")){

            String target = "";

            notificationUrl = Objects.requireNonNull(remoteMessage.toIntent().getExtras()).getString("Link" , target);
        }

        String Title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();

        String link = remoteMessage.getNotification().getBody();

        String imageUri = String.valueOf(remoteMessage.getNotification().getImageUrl());

        bitmap = getBitmapfromUrl(imageUri);
        sendNotification(Title, link, bitmap);

    }


    private void sendNotification(String messageBody, String link, Bitmap image) {
        Intent intent = new Intent(this, MainBrowser.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", notificationUrl);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String Chennelid = getString(default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,Chennelid)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(messageBody)
                .setAutoCancel(true)
                .setContentText(link)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
                if(image != null){
                    notificationBuilder.setLargeIcon(image);

                }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = getString(default_notification_channel_id);

            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        Objects.requireNonNull(notificationManager).notify(0, notificationBuilder.build());
    }
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
