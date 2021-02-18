package com.xiaojuchefu.prism.monitor;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.xiaojuchefu.prism.monitor.core.GlobalWindowManager;
import com.xiaojuchefu.prism.monitor.core.WindowCallbacks;
import com.xiaojuchefu.prism.monitor.core.WindowObserver;
import com.xiaojuchefu.prism.monitor.event.ActivityLifecycleCallbacks;
import com.xiaojuchefu.prism.monitor.event.AppLifecycleObserver;
import com.xiaojuchefu.prism.monitor.event.PrismMonitorWindowCallbacks;
import com.xiaojuchefu.prism.monitor.model.EventData;

import java.util.ArrayList;
import java.util.List;

public class PrismMonitor {

    private static PrismMonitor sPrismMonitor;

    public static int sTouchSlop = -1;

    public Application mApplication;

    //是否已经初始化
    private boolean isInitialized;

    //是否正在检测
    private boolean isMonitoring;

    private List<OnPrismMonitorListener> mListeners;

    private AppLifecycleObserver mAppLifecycleObserver;
    private ActivityLifecycleCallbacks mActivityLifecycleCallbacks;

    private WindowObserver.WindowObserverListener mWindowObserverListener;

    private PrismMonitor() {
    }

    //DCL（双重检测） 形式的 单例
    public static PrismMonitor getInstance() {
        if (sPrismMonitor == null) {
            synchronized (PrismMonitor.class) {
                if (sPrismMonitor == null) {
                    sPrismMonitor = new PrismMonitor();
                }
            }
        }
        return sPrismMonitor;
    }

    public boolean isMonitoring() {
        return isMonitoring;
    }

    public void init(Application application) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;

        mApplication = application;

        mListeners = new ArrayList<>();

        Context context = application.getApplicationContext();

        //获取 view配置文件
        ViewConfiguration vc = ViewConfiguration.get(context);
        //获取touchSlop （系统 滑动距离的最小值，大于该值可以认为滑动）
        sTouchSlop = vc.getScaledTouchSlop();

        //添加APP的生命周期观察者，来观察 APP是退到后台还是进入前台
        mAppLifecycleObserver = new AppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(mAppLifecycleObserver);

        //hook WindowManagerGlobal 下的 mViews 为 WindowObserver
        GlobalWindowManager.getInstance().init(context);

        mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks();
        mWindowObserverListener = new WindowObserver.WindowObserverListener() {
            @Override
            public void add(final Window window) {
                setWindowCallback(window);
            }

            @Override
            public void remove(Window window) {

            }
        };
    }

    public void start() {
        if (!isInitialized || isMonitoring) return;

        //添加activity生命周期监控
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

        //添加window 添加移除监控
        WindowObserver windowObserver = GlobalWindowManager.getInstance().getWindowObserver();
        windowObserver.addWindowObserverListener(mWindowObserverListener);

        for (int i = 0; i < windowObserver.size(); i++) {
            View view = windowObserver.get(i);
            Window window = (Window) view.getTag(R.id.prism_window);
            if (window == null) {
                windowObserver.bindWindow(view);
                window = (Window) view.getTag(R.id.prism_window);
            }
            if (window != null && !(window.getCallback() instanceof WindowCallbacks)) {
                //给window 添加 PrismMonitorWindowCallbacks
                setWindowCallback(window);
            }
        }
        isMonitoring = true;
    }

    public void stop() {
        if (!isInitialized || !isMonitoring) return;

        isMonitoring = false;
        mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
        WindowObserver windowObserver = GlobalWindowManager.getInstance().getWindowObserver();
        windowObserver.removeWindowObserverListener(mWindowObserverListener);
        for (int i = 0; i < windowObserver.size(); i++) {
            View view = windowObserver.get(i);
            Window window = (Window) view.getTag(R.id.prism_window);
            if (window != null && window.getCallback() instanceof WindowCallbacks) {
                WindowCallbacks windowCallbacks = (WindowCallbacks) window.getCallback();
                window.setCallback(windowCallbacks.getCallBack());
            }
        }
    }

    private void setWindowCallback(final Window window) {
        if (window == null) return;
        Window.Callback mCallBack = window.getCallback();
        if (mCallBack instanceof WindowCallbacks) {
            return;
        }
        window.setCallback(new PrismMonitorWindowCallbacks(window));
    }

    public void post(int eventType) {
        post(new EventData(eventType));
    }

    public void post(EventData eventData) {
        if (!isInitialized || !isMonitoring) {
            return;
        }
        for (int i = 0; i < mListeners.size(); i++) {
            OnPrismMonitorListener listener = mListeners.get(i);
            if (listener != null) {
                listener.onEvent(eventData);
            }
        }
    }

    public void addOnPrismMonitorListener(OnPrismMonitorListener listener) {
        if (!isInitialized) {
            return;
        }
        mListeners.add(listener);
    }

    public void removeOnPrismMonitorListener(OnPrismMonitorListener listener) {
        if (!isInitialized) {
            return;
        }
        mListeners.remove(listener);
    }

    public interface OnPrismMonitorListener {

        void onEvent(EventData eventData);

    }

}
