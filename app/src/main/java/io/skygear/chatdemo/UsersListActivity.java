package io.skygear.chatdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.skygear.chatdemo.adapter.ChatUsersAdapter;
import io.skygear.plugins.chat.ChatContainer;
import io.skygear.plugins.chat.ChatUser;
import io.skygear.plugins.chat.Conversation;
import io.skygear.plugins.chat.GetCallback;
import io.skygear.plugins.chat.SaveCallback;
import io.skygear.plugins.chat.error.ConversationAlreadyExistsError;
import io.skygear.plugins.chat.ui.ConversationActivity;
import io.skygear.skygear.Container;
import io.skygear.skygear.Database;
import io.skygear.skygear.Error;
import io.skygear.skygear.Query;
import io.skygear.skygear.Record;
import io.skygear.skygear.RecordQueryResponseHandler;

public class UsersListActivity extends AppCompatActivity {
    final static String TAG = "UsersListActivity";

    Container mSkygear;
    ChatContainer mChatContainer;
    ChatUsersAdapter mAdapter;
    RecyclerView mRecyclerView;
    Button mNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mSkygear = Container.defaultContainer(this);
        mChatContainer = ChatContainer.getInstance(mSkygear);
        mAdapter = new ChatUsersAdapter(this, mSkygear.getAuth().getCurrentUser().getId());

        mRecyclerView = (RecyclerView) findViewById(R.id.chat_users_rv);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mNewButton = (Button) findViewById(R.id.create_conversation_btn);
        mNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ChatUser> selectedUsers = mAdapter.getSelectedChatUsers();
                if (!selectedUsers.isEmpty()) {
                    createConversation(selectedUsers);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Query query = new Query("user");
        query.setLimit(999);
        Database publicDB = mSkygear.getPublicDatabase();
        publicDB.query(query, new RecordQueryResponseHandler() {
            @Override
            public void onQuerySuccess(Record[] records) {
                ArrayList<ChatUser> chatUsers = new ArrayList();
                for (Record record: records) {
                    try {
                        chatUsers.add(ChatUser.fromJson(record.toJson()));
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                mAdapter.setChatUserList(chatUsers);
            }

            @Override
            public void onQueryError(Error error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createConversation(List<ChatUser> selectedUsers) {
        Set<String> participantIds = new HashSet<String>();
        String title = "";
        for (ChatUser user: selectedUsers) {
            participantIds.add(user.getId());
            String displayName = getUserDisplayName(user.getRecord());
            if (displayName != null) {
                title = title + displayName + ", ";
            }
        }

        String displayName = getUserDisplayName(mSkygear.getAuth().getCurrentUser());
        if (displayName != null) {
            title = title + displayName;
        }

        Map<Conversation.OptionKey, Object> options = new HashMap<>();
        options.put(Conversation.OptionKey.DISTINCT_BY_PARTICIPANTS, true);

        mChatContainer.createConversation(participantIds, title, null, options, new SaveCallback<Conversation>() {
            @Override
            public void onSucc(@Nullable Conversation conversation) {
                Log.i("MyApplication", "Created: " + conversation.getId());
                Intent i = new Intent(getApplicationContext(), ConversationActivity.class);
                i.putExtra(ConversationActivity.ConversationIntentKey, conversation.toJson().toString());
                startActivity(i);
                finish();
            }

            @Override
            public void onFail(@NonNull Error error) {
                Log.w("MyApplication", "Failed to save: " + error.getMessage());
                if (error instanceof ConversationAlreadyExistsError) {
                    String conversationId = ((ConversationAlreadyExistsError) error).getConversationId();
                    openConversationById(conversationId);
                } else {
                    Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openConversationById(String conversationId) {
        mChatContainer.getConversation(conversationId, new GetCallback<Conversation>() {
            @Override
            public void onSucc(@Nullable Conversation conversation) {
                Intent i = new Intent(getApplicationContext(), ConversationActivity.class);
                i.putExtra(ConversationActivity.ConversationIntentKey, conversation.toJson().toString());
                startActivity(i);
                finish();
            }

            @Override
            public void onFail(@NonNull Error error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserDisplayName(Record user) {
        if (user.get("name") != null && user.get("name") instanceof String) {
            return (String) user.get("name");
        } else if (user.get("username") != null && user.get("username") instanceof String) {
            return (String) user.get("username");
        }

        return null;
    }
}
