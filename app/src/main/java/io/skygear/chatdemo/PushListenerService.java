package io.skygear.chatdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Random;

import io.skygear.plugins.chat.ui.ConversationActivity;
import io.skygear.skygear.Container;


public class PushListenerService extends com.google.firebase.messaging.FirebaseMessagingService {
    static final String TAG = "Push";
    private final Random random;
    static final String channelId = "chat";
    static final String channelName = "Chat";

    public PushListenerService() {
        this.random = new Random(new Date().getTime());
    }

    private void createNotificationByConversationId(String conversationId, String title, String body) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ConversationActivity.ConversationIdIntentKey, conversationId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        if (title != null) {
            notificationBuilder.setContentTitle(title);
        } else {
            notificationBuilder.setContentTitle("Notification");
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            notificationBuilder.setChannelId(channel.getId());
        }

        notificationManager.notify(channelId, this.random.nextInt(), notificationBuilder.build());
    }

    @Override
    public void onNewToken(String token) {
        // send registration to skygear server
        Container container = Container.defaultContainer(this.getApplicationContext());
        container.getPush().registerDeviceToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        final String title = remoteMessage.getData().get("title");
        final String body = remoteMessage.getData().get("body");

        if (body == null) {
            Log.w(TAG, "Got null notification body");
            return;
        }

        String conversationId = remoteMessage.getData().get("conversation_id");

        if (conversationId == null) {
            Log.w(TAG, "conversationId is not provided.");
            return;
        }

        createNotificationByConversationId(conversationId, title, body);
    }
}
