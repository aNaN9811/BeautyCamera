package com.example.beautycamera;

import static android.opengl.GLES10.glClear;
import static android.opengl.GLES10.glClearColor;
import static android.opengl.GLES10.glGenTextures;
import static javax.microedition.khronos.opengles.GL10.GL_COLOR_BUFFER_BIT;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;

import com.example.beautycamera.face.FaceTrack;
import com.example.beautycamera.filter.BigEyeFilter;
import com.example.beautycamera.filter.CameraFilter;
import com.example.beautycamera.filter.ScreenFilter;
import com.example.beautycamera.record.MyMediaRecorder;
import com.example.beautycamera.utils.CameraHelper;
import com.example.beautycamera.utils.FileUtil;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements
        GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener,
        Camera.PreviewCallback {

    private final MyGLSurfaceView myGLSurfaceView;
    private CameraHelper mCameraHelper;
    private int[] mTextureID;
    private SurfaceTexture mSurfaceTexture;
    private CameraFilter mCameraFilter;
    private ScreenFilter mScreenFilter;
    float[] mtx = new float[16];
    private MyMediaRecorder myMediaRecorder;
    private BigEyeFilter mBigEyeFilter;
    private FaceTrack mFaceTrack;
    private int mWidth;
    private int mHeight;
    private String facePath;
    private String seetaPath;

    public MyGLRenderer(MyGLSurfaceView myGLSurfaceView) {
        this.myGLSurfaceView = myGLSurfaceView;
        facePath = FileUtil.copyAssets2SDCard(myGLSurfaceView.getContext(), "lbpcascade_frontalface.xml",
                "lbpcascade_frontalface.xml"); // OpenCV模型
        seetaPath = FileUtil.copyAssets2SDCard(myGLSurfaceView.getContext(), "seeta_fa_v1.1.bin",
                "seeta_fa_v1.1.bin"); // 中科院模型
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraHelper = new CameraHelper((Activity) myGLSurfaceView.getContext(),
                Camera.CameraInfo.CAMERA_FACING_FRONT,
                1080, 1920);
        mCameraHelper.setPreviewCallback(this);
        mTextureID = new int[1];
        glGenTextures(mTextureID.length, mTextureID, 0);
        mSurfaceTexture = new SurfaceTexture(mTextureID[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCameraFilter = new CameraFilter(myGLSurfaceView.getContext());
        mScreenFilter = new ScreenFilter(myGLSurfaceView.getContext());
        EGLContext eglContext = EGL14.eglGetCurrentContext();
        myMediaRecorder = new MyMediaRecorder(1080, 1920, eglContext,
                myGLSurfaceView.getContext());
        mFaceTrack = new FaceTrack(facePath, seetaPath, mCameraHelper);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;
        mFaceTrack.startTrack();
        mCameraHelper.stopPreview();
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width, height);
        mScreenFilter.onReady(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClearColor(255, 0, 0, 0);
        // GL_COLOR_BUFFER_BIT 颜色缓冲区
        // GL_DEPTH_BUFFER_BIT 深度缓冲区
        // GL_STENCIL_BUFFER_BIT 模型缓冲区
        glClear(GL_COLOR_BUFFER_BIT);

        // bind its texture to the GL_TEXTURE_EXTERNAL_OES texture target.
        mSurfaceTexture.updateTexImage();

        mSurfaceTexture.getTransformMatrix(mtx);

        mCameraFilter.setMatrix(mtx);
        int textureId = mCameraFilter.onDrawFrame(mTextureID[0]);

        if (mBigEyeFilter != null) {
            mBigEyeFilter.setFace(mFaceTrack.getFace());
            textureId = mBigEyeFilter.onDrawFrame(textureId);
        }

        mScreenFilter.onDrawFrame(textureId);

        myMediaRecorder.encodeFrame(textureId, mSurfaceTexture.getTimestamp());
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        myGLSurfaceView.requestRender();
    }

    public void surfaceDestroyed() {
        mFaceTrack.stopTrack();
        mCameraHelper.stopPreview();
    }

    public void startRecording(float speed) {
        try {
            myMediaRecorder.start(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        myMediaRecorder.stop();
    }

    public void enableBigEye(final boolean isChecked) {
        myGLSurfaceView.queueEvent(() -> {
            if (isChecked) {
                mBigEyeFilter = new BigEyeFilter(myGLSurfaceView.getContext());
                mBigEyeFilter.onReady(mWidth, mHeight);
            } else {
                mBigEyeFilter.release();
                mBigEyeFilter = null;
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mFaceTrack.detector(data);
    }
}
