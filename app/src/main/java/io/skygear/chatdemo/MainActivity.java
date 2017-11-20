package io.skygear.chatdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.skygear.plugins.chat.Conversation;
import io.skygear.skygear.Container;
import io.skygear.skygear.Record;
import io.skygear.skygear.gcm.RegistrationIntentService;

public class MainActivity extends AppCompatActivity {

    private Container mSkygear;
//    private Button mSignUpBtn;
    private Button mLogInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mSignUpBtn = (Button) findViewById(R.id.sign_up_btn);

        mLogInBtn = (Button) findViewById(R.id.log_in_btn);

        mLogInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }
        });

        mSkygear = Container.defaultContainer(this);

        if (mSkygear.getPush().getGcmSenderId() != null) {
            Intent gcmTokenRegisterIntent = new Intent(this, RegistrationIntentService.class);
            this.startService(gcmTokenRegisterIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Record currentUser = mSkygear.getAuth().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, ConversationsListActivity.class));
        }
    }
}
