package com.example.beautycamera.filter;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;

import android.content.Context;

import com.example.beautycamera.utils.BufferHelper;
import com.example.beautycamera.utils.ShaderHelper;
import com.example.beautycamera.utils.TextResourceReader;

import java.nio.FloatBuffer;

public class BaseFilter {
    protected FloatBuffer mVertexBuffer; // 顶点坐标数据缓冲区
    protected FloatBuffer mTextureBuffer; // 纹理坐标数据缓冲区

    protected int mProgramId; // 着色器程序

    protected int vPosition;  // 顶点着色器：顶点位置
    protected int vCoord; // 顶点着色器：纹理坐标
    protected int vMatrix; // 顶点着色器：变换矩阵

    protected int vTexture; // 片元着色器：采样器

    protected int mWidth;
    protected int mHeight;

    public BaseFilter(Context context, int vertexSourceId, int fragmentSourceId) {
        // 顶点相关 坐标系
        float[] VERTEX = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,};
        mVertexBuffer = BufferHelper.getFloatBuffer(VERTEX); // 保存到 顶点坐标数据缓冲区

        // 纹理相关 坐标系
        float[] TEXTURE = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,};
        mTextureBuffer = BufferHelper.getFloatBuffer(TEXTURE); // 保存到 纹理坐标数据缓冲区

        init(context, vertexSourceId, fragmentSourceId);
    }

    private void init(Context context, int mVertexSourceId, int mFragmentSourceId) {
        String vertexSource = TextResourceReader.readTextFileFromResource(context, mVertexSourceId);
        String fragmentSource = TextResourceReader.readTextFileFromResource(context, mFragmentSourceId);

        int vertexShaderId = ShaderHelper.compileVertexShader(vertexSource);
        int fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentSource);

        mProgramId = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);

        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        vPosition = glGetAttribLocation(mProgramId, "vPosition"); // 顶点着色器索引值
        vCoord = glGetAttribLocation(mProgramId, "vCoord"); // 顶点着色器纹理坐标，采样器采样图片的坐标索引值
        vMatrix = glGetUniformLocation(mProgramId, "vMatrix"); // 顶点着色器变换矩阵索引值

        vTexture = glGetUniformLocation(mProgramId, "vTexture"); // 片元着色器：采样器
    }

    public void onReady(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    /**
     * 绘制操作
     *
     * @param textureId 画布 纹理ID
     */
    public int onDrawFrame(int textureId) {
        // 设置视窗大小
        glViewport(0, 0, mWidth, mHeight);
        glUseProgram(mProgramId);

        mVertexBuffer.position(0);
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, mVertexBuffer);
        glEnableVertexAttribArray(vPosition);

        mTextureBuffer.position(0);
        glVertexAttribPointer(vCoord, 2, GL_FLOAT, false, 0, mTextureBuffer);
        glEnableVertexAttribArray(vCoord);

        // 只需要把OpenGL的纹理ID，渲染到屏幕上就可以了，不需要矩阵数据传递给顶点着色器
        // 变换矩阵 把mtx矩阵数据 传递到 vMatrix
        // glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);

        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, textureId);

        // 因为CameraFilter已经做过了，我就直接显示，我用OepnGL 2D GL_TEXTURE_2D 显示就行
        // glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId); // 由于这种方式并不是通用的，所以先去除

        glUniform1i(vTexture, 0); // 传递采样器
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4); // 通知 opengl 绘制

        return textureId; // 返回纹理ID，可以告诉下一个过滤器
    }

    protected void changeTextureData() {
    }

    public void release() {
        glDeleteProgram(mProgramId);
    }
}
