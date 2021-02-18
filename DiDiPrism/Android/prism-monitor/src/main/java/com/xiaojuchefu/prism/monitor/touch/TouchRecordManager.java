package com.xiaojuchefu.prism.monitor.touch;

import android.view.MotionEvent;

/**
 * 触摸事件记录者，其实就是对 MotionEvent 的解析及记录，会将 MotionEvent 转为 TouchRecord
 */
public class TouchRecordManager {

    private static TouchRecordManager sTouchRecordManager;
    private TouchRecord mTouchRecord;

    public static TouchRecordManager getInstance() {
        synchronized (TouchRecordManager.class) {
            if (sTouchRecordManager == null) {
                sTouchRecordManager = new TouchRecordManager();
            }
            return sTouchRecordManager;
        }
    }

    public void touchEvent(MotionEvent ev) {
        int pointIndex = ev.getActionIndex();
        int pointerId = ev.getPointerId(pointIndex);
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mTouchRecord = new TouchRecord();
            mTouchRecord.onActionDown(ev);
        } else {
            if (mTouchRecord == null || mTouchRecord.mPointerId != pointerId) {
                return;
            }

            if (action == MotionEvent.ACTION_UP) {
                mTouchRecord.onActionUp(ev);
            } else if (action == MotionEvent.ACTION_CANCEL) {
                mTouchRecord = null;
            }
        }
    }

    public TouchRecord getTouchRecord() {
        return mTouchRecord;
    }

}
