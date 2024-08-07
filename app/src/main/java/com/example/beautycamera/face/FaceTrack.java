package com.example.beautycamera.face;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.example.beautycamera.utils.CameraHelper;

public class FaceTrack {

    static {
        System.loadLibrary("beautycamera");
    }

    private CameraHelper mCameraHelper;
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private long self; // FaceTrack.cpp对象的地址指向
    private Face mFace; // 最终人脸跟踪的结果

    /**
     * @param model        OpenCV人脸的模型的文件路径
     * @param seeta        中科院的那个模型（五个关键点的特征点的文件路径）
     * @param cameraHelper 需要把CameraID传递给C++层
     */
    public FaceTrack(String model, String seeta, CameraHelper cameraHelper) {
        mCameraHelper = cameraHelper;
        self = native_create(model, seeta);

        mHandlerThread = new HandlerThread("FaceTrack");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                synchronized (FaceTrack.this) {
                    // 定位 线程中检测
                    mFace = native_detector(self, (byte[]) msg.obj, mCameraHelper.getCameraID(), 1080, 1920);
                    if (mFace != null) {
                        Log.d("拍摄了人脸mFace.toString:", mFace.toString());
                    }
                }
            }
        };
    }

    public void startTrack() {
        native_start(self);
    }

    public void stopTrack() {
        synchronized (this) {
            mHandlerThread.quitSafely();
            mHandler.removeCallbacksAndMessages(null);
            native_stop(self);
            self = 0;
        }
    }

    public void detector(byte[] data) {
        mHandler.removeMessages(11);
        Message message = mHandler.obtainMessage(11);
        message.obj = data;
        mHandler.sendMessage(message);
    }

    public Face getFace() {
        return mFace;
    }

    private native long native_create(String model, String seeta);

    private native void native_start(long self);

    private native void native_stop(long self);

    private native Face native_detector(long self, byte[] data, int cameraId, int width, int height);
}
