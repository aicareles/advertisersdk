package cn.com.heaton.avertisetest.app;

import cn.com.heaton.advertisersdk.interceptor.ParseStrategy;
import cn.com.heaton.advertisersdk.interceptor.Strategy;

public class LogParseStrategy implements ParseStrategy {

    private static final String TAG = "LogParseStrategy";

    @Override
    public Strategy resStrategy() {
        return new Strategy.Builder()
                .commandIndex(4)
                .productIndex(1)
                .rollingIndex(6)
                .build();
    }

    @Override
    public Strategy reqStrategy() {
        return new Strategy.Builder()
                .commandIndex(4)
                .productIndex(1)
                .rollingIndex(6)
                .build();
    }
}
