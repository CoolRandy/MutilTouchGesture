package com.coolrandy.com.mutiltouchgesture;

import android.content.Context;
import android.view.MotionEvent;

/**
 * Created by admin on 2015/12/28.
 */
public class RotateGestureDetector extends TwoFingerGestureDetector {

    /**
     * 旋转手势监听
     */
    public interface OnRotateGestureListener{

        public boolean onRotate(RotateGestureDetector detector);
        public boolean onRotateBegin(RotateGestureDetector detector);
        public void onRotateEnd(RotateGestureDetector detector);
    }

    /**
     * 这里采用了一个比较好的编程实践：
     * 采用一个辅助类，实现OnRotateGestureListener接口，这样其他类在实现该监听器时不需要实现所有的方法
     */
    public static class SimpleOnRotateGestureListener implements OnRotateGestureListener{

        public boolean onRotate(RotateGestureDetector detector) {
            return false;
        }

        public boolean onRotateBegin(RotateGestureDetector detector) {
            return true;
        }

        public void onRotateEnd(RotateGestureDetector detector) {
//            return false;
        }
    }

    private final OnRotateGestureListener mListener;
    private boolean mSloppyGesture;

    public RotateGestureDetector(Context mContext, OnRotateGestureListener listener) {
        super(mContext);
        mListener = listener;
    }

    @Override
    protected void handleStartProgressEvent(int actionCode, MotionEvent event) {

        switch (actionCode){

            case MotionEvent.ACTION_POINTER_DOWN:
                //至少有第二个手指在屏幕上了
                resetState(); // In case we missed an UP/CANCEL event
                mPrevEvent = MotionEvent.obtain(event);
                mTimeDelta = 0;

                updateStateByEvent(event);

                // See if we have a sloppy gesture
                mSloppyGesture = isSloppyGesture(event);
                if(!mSloppyGesture){
                    // No, start gesture now
                    mGestureInProgress = mListener.onRotateBegin(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!mSloppyGesture){
                    break;
                }
                // See if we still have a sloppy gesture
                mSloppyGesture = isSloppyGesture(event);
                if(!mSloppyGesture){
                    // No, start normal gesture now
                    mGestureInProgress = mListener.onRotateBegin(this);
                }

                break;
            case MotionEvent.ACTION_POINTER_UP:
                if(!mSloppyGesture){
                    break;
                }
                break;
        }
    }

    @Override
    protected void handleInProgressEvent(int actionCode, MotionEvent event) {

        switch (actionCode){

            case MotionEvent.ACTION_POINTER_UP:
                updateStateByEvent(event);
                if(!mSloppyGesture){
                    mListener.onRotateEnd(this);
                }

                resetState();
                break;
            case MotionEvent.ACTION_CANCEL:
                if(!mSloppyGesture){
                    mListener.onRotateEnd(this);
                }

                resetState();
                break;
            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);
                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted.
                if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
                    final boolean updatePrevious = mListener.onRotate(this);
                    if (updatePrevious) {
                        mPrevEvent.recycle();
                        mPrevEvent = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    @Override
    protected void resetState() {
        super.resetState();
        mSloppyGesture = false;
    }

    /**
     * Return the rotation difference from the previous rotate event to the current
     * event.
     *
     * @return The current rotation //difference in degrees.
     */
    public float getRotationDegreesDelta() {
        //反正切，求解旋转的弧度
        double diffRadians = Math.atan2(mPrevFingerDiffY, mPrevFingerDiffX) - Math.atan2(mCurrFingerDiffY, mCurrFingerDiffX);
        return (float) (diffRadians * 180 / Math.PI);
    }
}
