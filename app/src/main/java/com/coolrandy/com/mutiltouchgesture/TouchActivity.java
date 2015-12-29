package com.coolrandy.com.mutiltouchgesture;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by admin on 2015/12/28.
 */
public class TouchActivity extends Activity implements View.OnTouchListener {

    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleSpan = 1.0f;
    private Matrix mMatrix = new Matrix();
    private float mScaleFactor = .4f;
    private float mRotationDegrees = 0.f;
    private float mFocusX = 0.f;
    private float mFocusY = 0.f;
    private int mAlpha = 255;
    private int mImageHeight, mImageWidth;

    private ScaleGestureDetector mScaleDetector;
    private RotateGestureDetector mRotateDetector;
//    private MoveGestureDetector mMoveDetector;
//    private ShoveGestureDetector mShoveDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Determine the center of the screen to center 'earth'
        Display display = getWindowManager().getDefaultDisplay();
        mFocusX = display.getWidth()/2f;
        mFocusY = display.getHeight()/2f;

        // Set this class as touchListener to the ImageView
        ImageView view = (ImageView) findViewById(R.id.imageView);
        view.setOnTouchListener(this);

        // Determine dimensions of 'earth' image
        Drawable d 		= this.getResources().getDrawable(R.drawable.earth);
        mImageHeight 	= d.getIntrinsicHeight();
        mImageWidth 	= d.getIntrinsicWidth();

        // View is scaled and translated by matrix, so scale and translate initially
        float scaledImageCenterX = (mImageWidth*mScaleFactor)/2;
        float scaledImageCenterY = (mImageHeight*mScaleFactor)/2;

        mMatrix.postScale(mScaleFactor, mScaleFactor);
        mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);
        view.setImageMatrix(mMatrix);

        // Setup Gesture Detectors
        mScaleDetector 	= new ScaleGestureDetector(getApplicationContext(), new ScaleListener());
        mRotateDetector = new RotateGestureDetector(getApplicationContext(), new RotateListener());
//        mMoveDetector 	= new MoveGestureDetector(getApplicationContext(), new MoveListener());
//        mShoveDetector 	= new ShoveGestureDetector(getApplicationContext(), new ShoveListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        scaleGestureDetector.onTouchEvent(event);
        // ScaleDetector handled event at this point.
        // Perform your magic with mScaleSpan now!

        mScaleDetector.onTouchEvent(event);
        mRotateDetector.onTouchEvent(event);
//        mMoveDetector.onTouchEvent(event);
//        mShoveDetector.onTouchEvent(event);

        float scaledImageCenterX = (mImageWidth*mScaleFactor)/2;
        float scaledImageCenterY = (mImageHeight*mScaleFactor)/2;

        mMatrix.reset();
        mMatrix.postScale(mScaleFactor, mScaleFactor);
        mMatrix.postRotate(mRotationDegrees,  scaledImageCenterX, scaledImageCenterY);
        mMatrix.postTranslate(mFocusX - scaledImageCenterX, mFocusY - scaledImageCenterY);

        ImageView view = (ImageView) v;
        view.setImageMatrix(mMatrix);
        view.setAlpha(mAlpha);

        return true;//表明事件被处理了
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleSpan = detector.getCurrentSpan();//手指之间的平均距离
            mScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            return true;//表明事件被处理了
        }
    }


    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            mRotationDegrees -= detector.getRotationDegreesDelta();
            return true;
        }
    }

}
