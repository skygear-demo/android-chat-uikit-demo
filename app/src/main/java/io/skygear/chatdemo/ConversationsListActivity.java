package io.skygear.chatdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import io.skygear.chatdemo.adapter.ConversationsAdapter;
import io.skygear.plugins.chat.ChatContainer;
import io.skygear.plugins.chat.Conversation;
import io.skygear.plugins.chat.GetCallback;
import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.LogoutResponseHandler;

/**
 * Created by carmenlau on 10/17/17.
 */

public class ConversationsListActivity extends AppCompatActivity {

    Container mSkygear;
    ChatContainer mChatContainer;
    ConversationsAdapter mAdapter;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list);

        mSkygear = Container.defaultContainer(this);
        mChatContainer = ChatContainer.getInstance(mSkygear);
        mAdapter = new ConversationsAdapter(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.conversations_rv);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatContainer.getConversations(new GetCallback<List<Conversation>>(){
            @Override
            public void onSuccess(@Nullable List<Conversation> object) {
                mAdapter.setConversationList(object);
            }

            @Override
            public void onFail(@NonNull Error error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversations_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.users_list:
                startActivity(new Intent(this, UsersListActivity.class));
                return true;
            case R.id.log_out:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void logout() {
        mSkygear.getAuth().logout(new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                finish();
            }

            @Override
            public void onLogoutFail(Error error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
