package com.example.beautycamera.filter;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import com.example.beautycamera.R;
import com.example.beautycamera.face.Face;

public class BigEyeFilter extends BaseFrameFilter {
    private final int left_eye; // 左眼坐标的属性索引
    private final int right_eye; // 右眼坐标的属性索引
    //    private final int right_eye1; // 右眼坐标的属性索引
//    private final int right_eye2; // 右眼坐标的属性索引
//    private final int right_eye3; // 右眼坐标的属性索引
//    private final int right_eye4; // 右眼坐标的属性索引
    private FloatBuffer left; // 左眼的buffer
    private FloatBuffer right; // 右眼的buffer
    //    private FloatBuffer right1; // 右眼的buffer
//    private FloatBuffer right2; // 右眼的buffer
//    private FloatBuffer right3; // 右眼的buffer
//    private FloatBuffer right4; // 右眼的buffer
    private Face mFace; // 人脸追踪+人脸5关键点 最终的成果

    public BigEyeFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.bigeye_fragment);
        left_eye = glGetUniformLocation(mProgramId, "left_eye"); // 左眼坐标的属性索引
        right_eye = glGetUniformLocation(mProgramId, "right_eye"); // 右眼坐标的属性索引
//        right_eye1 = glGetUniformLocation(mProgramId, "right_eye1"); // 右眼坐标的属性索引
//        right_eye2 = glGetUniformLocation(mProgramId, "right_eye2"); // 右眼坐标的属性索引
//        right_eye3 = glGetUniformLocation(mProgramId, "right_eye3"); // 右眼坐标的属性索引
//        right_eye4 = glGetUniformLocation(mProgramId, "right_eye4"); // 右眼坐标的属性索引

        left = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();  // 左眼buffer申请空间
        right = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); // 右眼buffer申请空间
//        right1 = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); // 右眼buffer申请空间
//        right2 = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); // 右眼buffer申请空间
//        right3 = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); // 右眼buffer申请空间
//        right4 = ByteBuffer.allocateDirect(2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); // 右眼buffer申请空间
    }

    @Override
    public int onDrawFrame(int textureID) {
        if (null == mFace) {
            return textureID;
        }
        // 1：设置视窗
        glViewport(0, 0, mWidth, mHeight);
        // 这里是因为要渲染到FBO缓存中，而不是直接显示到屏幕上
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers[0]);

        // 2：使用着色器程序
        glUseProgram(mProgramId);

        // 渲染 传值
        // 1：顶点数据
        mVertexBuffer.position(0);
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, mVertexBuffer); // 传值
        glEnableVertexAttribArray(vPosition); // 传值后激活

        // 2：纹理坐标
        mTextureBuffer.position(0);
        glVertexAttribPointer(vCoord, 2, GL_FLOAT, false, 0, mTextureBuffer); // 传值
        glEnableVertexAttribArray(vCoord); // 传值后激活

        float[] landmarks = mFace.landmarks; // 传 mFace 眼睛坐标 给着色器

        /*
          x = landmarks[2] / mFace.imgWidth 换算到纹理坐标0~1之间范围
          landmarks 他的相对位置是，是从C++里面得到的坐标，这个坐标是正对整个屏幕的
          但是我们要用OpenGL纹理的坐标才行，因为我们是OpenGL着色器语言代码，OpenGL纹理坐标是 0~1范围
          所以需要 / 屏幕的宽度480/高度800来得到 x/y 是等于 0~1范围
         */

        // 左眼： 的 x y 值，保存到 左眼buffer中
        float x = landmarks[2] / mFace.imgWidth;
        float y = landmarks[3] / mFace.imgHeight;
        left.clear();
        left.put(x);
        left.put(y);
        left.position(0);
        glUniform2fv(left_eye, 1, left);

        // 右眼： 的 x y 值，保存到 右眼buffer中
        x = landmarks[4] / mFace.imgWidth;
        y = landmarks[5] / mFace.imgHeight;
        right.clear();
        right.put(x);
        right.put(y);
        right.position(0);
        glUniform2fv(right_eye, 1, right);

//        // 右眼： 的 x y 值，保存到 右眼buffer中
//        x = landmarks[4] / mFace.imgWidth;
//        y = landmarks[5] / mFace.imgHeight;
//        right1.clear();
//        right1.put(x);
//        right1.put(y);
//        right1.position(0);
//        glUniform2fv(right_eye1, 1, right1);
//
//        // 右眼： 的 x y 值，保存到 右眼buffer中
//        x = landmarks[6] / mFace.imgWidth;
//        y = landmarks[7] / mFace.imgHeight;
//        right2.clear();
//        right2.put(x);
//        right2.put(y);
//        right2.position(0);
//        glUniform2fv(right_eye2, 1, right2);
//
//        // 右眼： 的 x y 值，保存到 右眼buffer中
//        x = landmarks[8] / mFace.imgWidth;
//        y = landmarks[9] / mFace.imgHeight;
//        right3.clear();
//        right3.put(x);
//        right3.put(y);
//        right3.position(0);
//        glUniform2fv(right_eye3, 1, right3);
//
//        // 右眼： 的 x y 值，保存到 右眼buffer中
//        x = landmarks[10] / mFace.imgWidth;
//        y = landmarks[11] / mFace.imgHeight;
//        right4.clear();
//        right4.put(x);
//        right4.put(y);
//        right4.position(0);
//        glUniform2fv(right_eye4, 1, right4);

        // 片元 vTexture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glUniform1i(vTexture, 0); // 传递参数

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        // 解绑fbo
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return mFrameBufferTextures[0];
    }

    public void setFace(Face mFace) { // C++层把人脸最终5关键点成果的(mFaceTrack.getFace()) 赋值给此函数
        this.mFace = mFace;
    }
}
