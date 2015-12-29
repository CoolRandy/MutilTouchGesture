package com.coolrandy.com.mutiltouchgesture;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by admin on 2015/12/28.
 */
public abstract class BaseGestureDetector {


    /**
     * This value is the threshold ratio between our previous combined pressure
     * and the current combined pressure. We will only fire an onScale event if
     * the computed ratio between the current and previous event pressures is
     * greater than this value. When pressure decreases rapidly between events
     * the position values can often be imprecise, as it usually indicates
     * that the user is in the process of lifting a pointer off of the device.
     * Its value was tuned experimentally.
     */
    protected static final float PRESSURE_THRESHOLD = 0.67f;

    protected final Context mContext;
    protected boolean mGestureInProgress;

    protected MotionEvent mPrevEvent;
    protected MotionEvent mCurrEvent;

    protected float mCurrFocusX;
    protected float mCurrFocusY;
    protected float mCurrPressure;
    protected float mPrevPressure;
    protected long mTimeDelta;


    public BaseGestureDetector(Context mContext) {
        this.mContext = mContext;
    }

    public boolean onTouchEvent(MotionEvent event){
        //使用switch (event.getAction() & MotionEvent.ACTION_MASK)就可以处理处理多点触摸的ACTION_POINTER_DOWN和ACTION_POINTER_UP事件
        final int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
        if(!mGestureInProgress){
            handleStartProgressEvent(actionCode, event);
        }else {
            handleInProgressEvent(actionCode, event);
        }

        return true;
    }

    protected abstract void handleStartProgressEvent(int actionCode, MotionEvent event);

    protected abstract void handleInProgressEvent(int actionCode, MotionEvent event);

    /**
     * 根据事件更新状态
     */
    protected void updateStateByEvent(MotionEvent curr){

        final MotionEvent prev = mCurrEvent;
        //Reset mCurrEvent
        if(mCurrEvent != null){
            mCurrEvent.recycle();
            mCurrEvent = null;
        }

        mCurrEvent = MotionEvent.obtain(curr);
        //Focus
        //手指抬起标志位
        final boolean pointerUp = curr.getActionMasked() == MotionEvent.ACTION_UP;
        //如果手指抬起，则其他手指的index会相应发生变化，但是每个手指的id不会变
        final int skipIndex = pointerUp ? curr.getActionIndex() : -1;
        //获取触摸手指的个数
        final int count = curr.getPointerCount();
        //手指抬起，个数减一，否则不变
        final int div = pointerUp ? count - 1 : count;
        float sumX = 0, sumY = 0;
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += curr.getX(i);
            sumY += curr.getY(i);
        }

        mCurrFocusX = sumX / div;//取两点横坐标的平均值
        mCurrFocusY = sumY / div;

        //Pressure
        //表示当前的触屏压力大小
//        Log.e("TAG", "prev: " + prev);

        if (prev != null) {
            mPrevPressure = prev.getPressure(prev.getActionIndex());
            // Delta time
            mTimeDelta = curr.getEventTime() - prev.getEventTime();

            // Pressure
            mCurrPressure = curr.getPressure(curr.getActionIndex());
        }
    }
    /**
     * 复位状态
     */
    protected void resetState(){
        if(mPrevEvent != null){
            mPrevEvent.recycle();
            mPrevEvent = null;
        }

        if(mCurrEvent != null){
            mCurrEvent.recycle();
            mCurrEvent = null;
        }

        mGestureInProgress = false;
    }

    public float getFocusX(){
        return mCurrFocusX;
    }

    public float getFocusY(){
        return mCurrFocusY;
    }

    /**
     * 获取之前接收缩放事件和当前接收缩放事件之间的微妙级别的差别
     * @return
     */
    public long getTimeDelta() {
        return mTimeDelta;
    }

    public boolean ismGestureInProgress() {
        return mGestureInProgress;
    }

    /**
     * 获取当前事件被处理的时间
     * @return
     */
    public long getEventTime() {
        return mCurrEvent.getEventTime();
    }

}
