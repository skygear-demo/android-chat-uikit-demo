package io.skygear.chatdemo;

import io.skygear.skygear.SkygearApplication;

/**
 * Created by carmenlau on 10/16/17.
 */

public class MyApplication extends SkygearApplication {
    @Override
    public String getSkygearEndpoint() {
        return "https://chatdemoapp.skygeario.com/";
    }

    @Override
    public String getApiKey() {
        return "18855ba80c4a4058a8b527aabb2cb9c6";
    }
}
