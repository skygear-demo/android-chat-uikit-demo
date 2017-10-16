package io.skygear.chatdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import io.skygear.plugins.chat.ui.ConversationActivity;
import io.skygear.skygear.Container;

public class MainActivity extends AppCompatActivity {

    private Container mSkygear;
    private Button mSignUpBtn;
    private Button mLogInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
