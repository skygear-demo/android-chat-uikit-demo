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


import java.util.Date;
import java.util.Random;

import io.skygear.plugins.chat.ui.ConversationActivity;


public class PushListenerService extends com.google.android.gms.gcm.GcmListenerService {
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
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);
        final String title = bundle.getString("title");
        final String body = bundle.getString("body");

        if (body == null) {
            Log.w(TAG, "Got null notification body");
            return;
        }

        String conversationId = bundle.getString("conversation_id");

        if (conversationId == null) {
            Log.w(TAG, "conversationId is not provided.");
            return;
        }

        createNotificationByConversationId(conversationId, title, body);
    }
}
