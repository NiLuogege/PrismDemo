package com.xiaojuchefu.prism.monitor.event;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.xiaojuchefu.prism.monitor.PrismConstants;
import com.xiaojuchefu.prism.monitor.PrismMonitor;
import com.xiaojuchefu.prism.monitor.core.WindowCallbacks;
import com.xiaojuchefu.prism.monitor.model.EventData;
import com.xiaojuchefu.prism.monitor.touch.TouchEventHelper;
import com.xiaojuchefu.prism.monitor.touch.TouchRecord;
import com.xiaojuchefu.prism.monitor.touch.TouchRecordManager;
import com.xiaojuchefu.prism.monitor.touch.TouchTracker;

/**
 * 真正处理 触摸时间 及 window 添加 移除的地方
 * <p>
 * 使用  Window.Callback 确实使 这些事件 可以集中处理
 */
public class PrismMonitorWindowCallbacks extends WindowCallbacks {

    PrismMonitor mPrismMonitor;
    private Window window;

    public PrismMonitorWindowCallbacks(Window window) {
        super(window.getCallback());
        this.window = window;
        mPrismMonitor = PrismMonitor.getInstance();
    }

    @Override
    public boolean touchEvent(MotionEvent event) {
        if (mPrismMonitor.isMonitoring()) {

            //记录及解析 MotionEvent 为 TouchRecord
            TouchRecordManager.getInstance().touchEvent(event);

            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_UP) {
                //获取转换好的  TouchRecord
                TouchRecord touchRecord = TouchRecordManager.getInstance().getTouchRecord();

                Log.e("PrismMonitorWindow..", touchRecord.toString());

                if (touchRecord != null && touchRecord.isClick) {
                    int[] location = new int[]{(int) touchRecord.mDownX, (int) touchRecord.mDownY};
                    //通过相对于父亲的坐标找到 具体 view
                    View targetView = TouchTracker.findTargetView((ViewGroup) window.getDecorView(), touchRecord.isClick ? location : null);
                    if (targetView != null) {
                        //创建一个 EventData 并让 PrismMonitor 记录
                        EventData eventData = TouchEventHelper.createEventData(window, targetView, touchRecord);
                        if (eventData != null) {
                            mPrismMonitor.post(eventData);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean dispatchBackKeyEvent() {
        if (mPrismMonitor.isMonitoring()) {
            mPrismMonitor.post(PrismConstants.Event.BACK);
        }
        return false;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.e("PrismMonitorWindow..", "window type = " + window.getAttributes().type);

        if (mPrismMonitor.isMonitoring() && window.getAttributes().type == 2) {
            mPrismMonitor.post(PrismConstants.Event.DIALOG_SHOW);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPrismMonitor.isMonitoring() && window.getAttributes().type == 2) {
            mPrismMonitor.post(PrismConstants.Event.DIALOG_CLOSE);
        }
    }

}
