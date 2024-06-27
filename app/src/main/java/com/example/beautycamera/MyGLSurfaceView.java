package com.example.beautycamera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer mRenderer;

    private Speed mSpeed = Speed.MODE_NORMAL;

    public enum Speed {
        MODE_EXTRA_SLOW,
        MODE_SLOW,
        MODE_NORMAL,
        MODE_FAST,
        MODE_EXTRA_FAST
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer(this);
        setRenderer(mRenderer);

        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mRenderer.surfaceDestroyed();
        super.surfaceDestroyed(holder);
    }

    /**
     * 开始录制
     */
    public void startRecording() {
        float speed = 1.0f;
        switch (mSpeed) {
            case MODE_EXTRA_SLOW:
                speed = 0.3f;
                break;
            case MODE_SLOW:
                speed = 0.5f;
                break;
            case MODE_NORMAL:
                speed = 1.0f;
                break;
            case MODE_FAST:
                speed = 1.5f;
                break;
            case MODE_EXTRA_FAST:
                speed = 2.0f;
                break;

        }
        mRenderer.startRecording(speed);
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        mRenderer.stopRecording();
    }

    public void setSpeed(Speed speed) {
        mSpeed = speed;
    }

    /**
     * 开启大眼特效
     *
     * @param isChecked
     */
    public void enableBigEye(boolean isChecked) {
        mRenderer.enableBigEye(isChecked);
    }
}
