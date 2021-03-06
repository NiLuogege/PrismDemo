package com.xiaojuchefu.prism.monitor.core;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.List;

public class GlobalWindowManager {

    private static GlobalWindowManager sGlobalWindowManager;

    private final WindowObserver mWindowObserver = new WindowObserver();
    private boolean mInitialized;

    public static GlobalWindowManager getInstance() {
        synchronized (GlobalWindowManager.class) {
            if (sGlobalWindowManager == null) {
                sGlobalWindowManager = new GlobalWindowManager();
            }
            return sGlobalWindowManager;
        }
    }

    private GlobalWindowManager() {
    }

    public void init(Context context) {
        if (mInitialized) {
            return;
        }
        mInitialized = true;
        //hook WindowManagerGlobal 下的 mViews 为 WindowObserver
        reflectProxyWindowManager(context);
    }

    public WindowObserver getWindowObserver() {
        return mWindowObserver;
    }

    /**
     * 将 WindowManagerImpl - > mGlobal ->mViews 替换为 mWindowObserver，已达到 添加view和移除view能感知到的效果
     *
     * mWindowObserver 中存放是的 每个activity的 decoreVeiw
     */
    private void reflectProxyWindowManager(Context context) {
        try {
            Object windowManager = context.getSystemService(Context.WINDOW_SERVICE);
            Class windowManagerImplClass = windowManager.getClass();
            Field windowManagerGlobalField = windowManagerImplClass.getDeclaredField("mGlobal");
            windowManagerGlobalField.setAccessible(true);
            Object windowManagerGlobal = windowManagerGlobalField.get(windowManager);

            Field mViewsField = windowManagerGlobal.getClass().getDeclaredField("mViews");
            mViewsField.setAccessible(true);
            Object value = mViewsField.get(windowManagerGlobal);
            if (value instanceof List) {
                List views = (List) mViewsField.get(windowManagerGlobal);
                mWindowObserver.addAll(views);
                mViewsField.set(windowManagerGlobal, mWindowObserver);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
