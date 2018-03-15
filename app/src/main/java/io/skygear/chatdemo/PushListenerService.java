package io.skygear.chatdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import java.util.Date;
import java.util.Random;

import io.skygear.plugins.chat.ChatContainer;
import io.skygear.plugins.chat.Conversation;
import io.skygear.plugins.chat.GetCallback;
import io.skygear.plugins.chat.ui.ConversationActivity;
import io.skygear.skygear.Container;
import io.skygear.skygear.Error;


public class PushListenerService extends com.google.android.gms.gcm.GcmListenerService {
    static final String TAG = "Push";
    private final Random random;
    ChatContainer mChatContainer;
    Container mSkygear;

    public PushListenerService() {
        this.random = new Random(new Date().getTime());
        mSkygear = Container.defaultContainer(this);
        mChatContainer = ChatContainer.getInstance(mSkygear);
    }

    private void createNotificationByConversation(String conversation, String title, String body) {
        Intent mainActivityIntent = new Intent(this, ConversationsListActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(ConversationActivity.ConversationIntentKey, conversation);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

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

        notificationManager.notify(this.random.nextInt(), notificationBuilder.build());
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


        mChatContainer.getConversation(conversationId, new GetCallback<Conversation>() {
            @Override
            public void onSucc(@Nullable Conversation conversation) {
                createNotificationByConversation(conversation.toJson().toString(), title, body);
            }

            @Override
            public void onFail(@NonNull Error error) {
                Log.w(TAG, "Failed to fetch JSON.");
            }
        } );
    }
}
