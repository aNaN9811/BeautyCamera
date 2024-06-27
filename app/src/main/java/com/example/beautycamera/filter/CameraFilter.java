package com.example.beautycamera.filter;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import android.content.Context;
import android.opengl.GLES11Ext;

import com.example.beautycamera.R;

public class CameraFilter extends BaseFrameFilter {
    private float[] matrix; // 变换矩阵

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vertex, R.raw.camera_fragment);
    }

    public int onDrawFrame(int textureId) {
        glViewport(0, 0, mWidth, mHeight);

        // 绑定FBO缓存（否则会绘制到屏幕上） 我们最终的效果是 离屏渲染
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBuffers[0]);

        glUseProgram(mProgramId);

        mVertexBuffer.position(0); // 顶点坐标赋值
        glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, mVertexBuffer);
        glEnableVertexAttribArray(vPosition);

        mTextureBuffer.position(0); // 纹理坐标赋值
        glVertexAttribPointer(vCoord, 2, GL_FLOAT, false, 0, mTextureBuffer);
        glEnableVertexAttribArray(vCoord);

        glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);

        // 片元 vTexture
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        glUniform1i(vTexture, 0);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        // 解绑 fbo
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return mFrameBufferTextures[0];
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }
}
