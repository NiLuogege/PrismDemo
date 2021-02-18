package com.xiaojuchefu.prism;

import android.app.Application;

import com.xiaojuchefu.prism.monitor.PrismMonitor;
import com.xiaojuchefu.prism.playback.PrismPlayback;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 检测模块
        PrismMonitor.getInstance().init(this);
        //初始化 回放模块
        PrismPlayback.getInstance().init(this);
    }
}
