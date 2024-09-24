package com.example.tejas_demo.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.tejas_demo.R;

import java.util.ArrayList;

public class PushTemplateRenderer {

    private static PushTemplateRenderer instance;

    public static PushTemplateRenderer getInstance() {
        if (instance == null) {
            return new PushTemplateRenderer();
        } else {
            return instance;
        }
    }

    public void render(Context applicationContext, Bundle extras, PushNotificationListener listener) {

        if (extras.getString("pt_id").equals("pt_progress_bar")) {
            renderProgressBarNotification(applicationContext, extras, listener);
        } else if (extras.getString("pt_id").equals("pt_gif")) {
            renderGIFNotification(applicationContext, extras, listener);
        } else {
            listener.onPushFailed();
        }
    }

    private void renderGIFNotification(Context applicationContext, Bundle extras, PushNotificationListener listener) {
        try {
            ArrayList<String> smallImageList = getImageListFromExtras(extras, "small");
            ArrayList<String> largeImageList = getImageListFromExtras(extras, "large");

            String pushTitle = extras.getString("pt_title");
            String pushMessage = extras.getString("pt_msg");
            String deepLink = extras.getString("pt_dl");

            if (pushTitle == null || pushMessage == null || smallImageList.isEmpty() || largeImageList.isEmpty() || deepLink == null) {
                throw new IllegalArgumentException();
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(applicationContext);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext, extras.getString("wzrk_cid"));

            RemoteViews gifExpandedContentView = new RemoteViews(applicationContext.getPackageName(), R.layout.gif_notification);
            RemoteViews gifCollapsedContentView = new RemoteViews(applicationContext.getPackageName(), R.layout.gif_collapsed);

            gifExpandedContentView.setTextViewText(R.id.title, pushTitle);
            gifExpandedContentView.setTextViewText(R.id.msg, pushMessage);


            for (String image : smallImageList) {
                RemoteViews imageContentView = new RemoteViews(applicationContext.getPackageName(), R.layout.image_view);
                Utils.loadImageURLIntoRemoteView(R.id.fimg, image, imageContentView, applicationContext);
                gifCollapsedContentView.addView(R.id.view_flipper, imageContentView);
            }

            for (String image : largeImageList) {
                RemoteViews imageContentView = new RemoteViews(applicationContext.getPackageName(), R.layout.image_view);
                Utils.loadImageURLIntoRemoteView(R.id.fimg, image, imageContentView, applicationContext);
                gifExpandedContentView.addView(R.id.view_flipper, imageContentView);
            }

            notificationManager.notify(2, builder.setContentTitle("title")
                    .setSmallIcon(R.drawable.custom_progress_drawable)
                    .setCustomContentView(gifCollapsedContentView)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setCustomBigContentView(gifExpandedContentView)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH).build());

            listener.onPushRendered();
        } catch (Exception e) {
            listener.onPushFailed();
        }
    }

    static ArrayList<String> getImageListFromExtras(Bundle extras, String size) {
        ArrayList<String> imageList = new ArrayList<>();
        for (String key : extras.keySet()) {
            if (key.contains("pt_" + size + "_img")) {
                imageList.add(extras.getString(key));
            }
        }
        return imageList;
    }

    private void renderProgressBarNotification(Context applicationContext, Bundle extras, PushNotificationListener listener) {

        try {
            NotificationCompat.Builder builder;
            NotificationManagerCompat notificationManager;

            notificationManager = NotificationManagerCompat.from(applicationContext);
            builder = new NotificationCompat.Builder(applicationContext, extras.getString("wzrk_cid"));

            String pushTitleStart = extras.getString("pt_title");
            String timerThreshold = extras.getString("pt_timer_threshold");
            String pushTitleEnd = extras.getString("pt_title_alt");
            String pushMessageEnd = extras.getString("pt_msg_alt");
            String image = extras.getString("pt_big_img");
            String deepLink = extras.getString("pt_dl");

            if (pushTitleStart == null || timerThreshold == null ||
                    pushTitleEnd == null || pushMessageEnd == null || image == null || deepLink == null) {
                throw new IllegalArgumentException();
            }

            int PROGRESS_STEPS = 5;

            RemoteViews collapsed = new RemoteViews(applicationContext.getPackageName(), R.layout.custom_layout_collapsed);
            collapsed.setTextViewText(R.id.title, pushTitleStart);
            collapsed.setOnClickPendingIntent(R.id.wrapper, Utils.getActivityIntent(extras, applicationContext));

            RemoteViews expanded = new RemoteViews(applicationContext.getPackageName(), R.layout.custom_layout_expanded);
            Utils.loadImageURLIntoRemoteView(R.id.big_image, image, expanded, applicationContext);
            expanded.setTextViewText(R.id.title, pushTitleStart);
            expanded.setOnClickPendingIntent(R.id.wrapper, Utils.getActivityIntent(extras, applicationContext));

            for (int i = 0; i < PROGRESS_STEPS; i++) {
                int drawableId = applicationContext.getResources().getIdentifier("progress_" + (i + 1), "drawable", applicationContext.getPackageName());
                RemoteViews imageContentView = new RemoteViews(applicationContext.getPackageName(), R.layout.image_progress);
                imageContentView.setImageViewResource(R.id.fimg, drawableId);

                collapsed.addView(R.id.progress_flipper, imageContentView);
                expanded.addView(R.id.progress_flipper, imageContentView);
            }
            collapsed.setInt(R.id.view_flipper, "setFlipInterval", 500);
            expanded.setInt(R.id.view_flipper, "setFlipInterval", 500);

            notificationManager.notify(2, getNotification(pushTitleStart, builder, collapsed, expanded).build());

            listener.onPushRendered();

        } catch (Exception e) {
            listener.onPushFailed();
        }
    }

    private NotificationCompat.Builder getNotification(String title, NotificationCompat.Builder builder, RemoteViews collapsed, RemoteViews expanded) {
        builder.setContentTitle(title)
                .setSmallIcon(R.drawable.custom_progress_drawable)
                .setCustomContentView(collapsed)
                .setCustomBigContentView(expanded)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder;
    }
}