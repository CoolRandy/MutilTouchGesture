package com.coolrandy.com.mutiltouchgesture;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by admin on 2015/12/28.
 * 两个手指的手势检测
 */
public class TwoFingerGestureDetector extends BaseGestureDetector {

    private final float mEdgeSlop;
    private float mRightSlopEdge;
    private float mBottomSlopEdge;

    //分别代表每个手指前一个状态的x和y轴的偏移量
    protected float mPrevFingerDiffX;
    protected float mPrevFingerDiffY;
    protected float mCurrFingerDiffX;
    protected float mCurrFingerDiffY;

    //分别代表当前和前一个状态两个手指之间的距离
    private float mCurrLen;
    private float mPrevLen;


    public TwoFingerGestureDetector(Context mContext) {
        super(mContext);
        //包含了方法和标准的常量用来设置UI的超时、大小和距离
        ViewConfiguration configuration = ViewConfiguration.get(mContext);
        //getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件，如viewpager就是用这个距离来判断用户是否翻页
        mEdgeSlop = configuration.getScaledEdgeSlop();
    }

    @Override
    protected void handleStartProgressEvent(int actionCode, MotionEvent event) {

    }

    @Override
    protected void handleInProgressEvent(int actionCode, MotionEvent event) {

    }

    /**
     * 当触摸动作一执行，手指的状态改变，就会触发该方法的调用，根据触摸事件重新更新相应的状态
     * @param curr
     */
    protected void updateStateByEvent(MotionEvent curr){

        super.updateStateByEvent(curr);
        //表示之前的状态
        final MotionEvent prev = mPrevEvent;

        mCurrLen = -1;
        mPrevLen = -1;

        final float px0 = prev.getX(0);
        final float px1 = prev.getX(1);
        final float py0 = prev.getY(0);
        final float py1 = prev.getY(1);
        final float pvx = px1 - px0;
        final float pvy = py1 - py0;
        mPrevFingerDiffX = pvx;
        mPrevFingerDiffY = pvy;


        final float cx0 = curr.getX(0);
        final float cx1 = curr.getX(1);
        final float cy0 = curr.getY(0);
        final float cy1 = curr.getY(1);
        final float cvx = cx1 - cx0;
        final float cvy = cy1 - cy0;
        mCurrFingerDiffX = cvx;
        mCurrFingerDiffY = cvy;
    }

    /**
     * 返回之前的两手指之间的距离
     * @return
     */
    protected float getPreviousSpan(){

        if(-1 == mPrevLen){//首先判断状态是否改变，即updateStateByEvent是否调用
            final float pvx = mPrevFingerDiffX;
            final float pvy = mPrevFingerDiffY;
            mPrevLen = (float)Math.sqrt(pvx * pvx + pvy * pvy);
        }

        return mPrevLen;
    }

    /**
     * 返回当前两手指的距离
     * @return
     */
    protected float getCurrentSpan(){

        if(-1 == mCurrLen){
            final float cvx = mCurrFingerDiffX;
            final float cvy = mCurrFingerDiffY;
            mCurrLen = (float)Math.sqrt(cvx * cvx + cvy * cvy);
        }
        return mCurrLen;
    }

    /**
     * MotionEvent has no getRawX(int) method; simulate it pending future API approval.
     * @param event
     * @param pointerIndex
     * @return
     */
    //TODO 这里offset值应该是负数，最后的语句应该改为减号吧
    protected static float getRawX(MotionEvent event, int pointerIndex) {
        float offset = event.getX() - event.getRawX();
        if(pointerIndex < event.getPointerCount()){
            return event.getX(pointerIndex) + offset;
        }
        return 0f;
    }

    /**
     * MotionEvent has no getRawY(int) method; simulate it pending future API approval.
     * @param event
     * @param pointerIndex
     * @return
     */
    protected static float getRawY(MotionEvent event, int pointerIndex) {
        float offset = event.getY() - event.getRawY();
        if(pointerIndex < event.getPointerCount()){
            return event.getY(pointerIndex) + offset;
        }
        return 0f;
    }

    /**
     * Check if we have a sloppy gesture. Sloppy gestures can happen if the edge
     * of the user's hand is touching the screen, for example.
     * 如果手指触碰屏幕移动的距离比较小，那么是不会触发手势动作的
     * @param event
     * @return
     */
    //TODO 这一块也还不是很理解  代码中的一些处理还不是很清楚目的
    protected boolean isSloppyGesture(MotionEvent event){
        // As orientation can change, query the metrics in touch down
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        mRightSlopEdge = metrics.widthPixels - mEdgeSlop;
        mBottomSlopEdge = metrics.heightPixels - mEdgeSlop;

        final float edgeSlop = mEdgeSlop;
        final float rightSlop = mRightSlopEdge;
        final float bottomSlop = mBottomSlopEdge;

        final float x0 = event.getRawX();
        final float y0 = event.getRawY();
        final float x1 = getRawX(event, 1);
        final float y1 = getRawY(event, 1);

        boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop
                || x0 > rightSlop || y0 > bottomSlop;
        boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop
                || x1 > rightSlop || y1 > bottomSlop;

        if (p0sloppy && p1sloppy) {
            return true;
        } else if (p0sloppy) {
            return true;
        } else if (p1sloppy) {
            return true;
        }
        return false;
    }
}
