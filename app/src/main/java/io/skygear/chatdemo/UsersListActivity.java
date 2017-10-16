package io.skygear.chatdemo;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import io.skygear.chatdemo.adapter.ChatUsersAdapter;
import io.skygear.plugins.chat.ChatContainer;
import io.skygear.plugins.chat.ChatUser;
import io.skygear.plugins.chat.GetCallback;
import io.skygear.skygear.Container;

public class UsersListActivity extends AppCompatActivity {

    Container mSkygear;
    ChatContainer mChatContainer;
    ChatUsersAdapter mAdapter;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mSkygear = Container.defaultContainer(this);
        mChatContainer = ChatContainer.getInstance(mSkygear);
        mAdapter = new ChatUsersAdapter(mSkygear.getAuth().getCurrentUser().getId());

        mRecyclerView = (RecyclerView) findViewById(R.id.chat_users_rv);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mChatContainer.getChatUsers(new GetCallback<List<ChatUser>>(){
            @Override
            public void onSucc(@Nullable List<ChatUser> object) {
                mAdapter.setChatUserList(object);
            }

            @Override
            public void onFail(@Nullable String failReason) {
                Toast.makeText(getBaseContext(), failReason, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
