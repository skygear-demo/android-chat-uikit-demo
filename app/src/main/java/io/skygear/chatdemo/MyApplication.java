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
        return "c0d796f60a9649d78ade26e65c460459";
    }
}
