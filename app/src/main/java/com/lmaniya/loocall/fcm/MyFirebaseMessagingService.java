package com.lmaniya.loocall.fcm;

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
import com.lmaniya.loocall.Main_page_activity;
import com.lmaniya.loocall.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    NotificationCompat.Builder notificationBuilder;
    Bitmap image;
    Context context;
   public String channelID = "channel-01";
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        String tag = remoteMessage.getData().get("tag");
        String msg = remoteMessage.getData().get("message");
        String img = remoteMessage.getData().get("image");
        image = getBitmapFromURL(img);
        try {
            sendNotification(tag, msg, image);
        } catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

    }
    // [END receive_message]



    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String tag, String messageBody, Bitmap img) {
        try {
            Intent intent = new Intent(context, Main_page_activity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



            if (tag.equalsIgnoreCase("image")) {
                notificationBuilder = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Android Learning")
                    .setContentText(messageBody)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(img))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            } else {
                notificationBuilder = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Android Learning")
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            }
            NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        } catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

    }

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
