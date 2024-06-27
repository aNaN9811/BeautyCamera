package com.example.beautycamera.face;

import java.util.Arrays;

public class Face {

     // float[] landmarks 细化后如下：12个元素
     //  0下标（保存：人脸框的 x）
     //  1下标（保存：人脸框的 y）
     //
     //  2下标（保存：左眼x）
     //  3下标（保存：左眼y）
     //
     //  4下标（保存：右眼x）
     //  5下标（保存：右眼y）
     //
     //  6下标（保存：鼻尖x）
     //  7下标（保存：鼻尖y）
     //
     //  8下标（保存：左边嘴角x）
     //  9下标（保存：左边嘴角y）
     //
     // 10下标（保存：右边嘴角x）
     // 11下标（保存：右边嘴角y）
    public float[] landmarks;

    public int width;        // 保存人脸的框 的宽度
    public int height;       // 保存人脸的框 的高度
    public int imgWidth;    // 屏幕宽
    public int imgHeight;   // 屏幕高

    public Face(int width, int height, int imgWidth,int imgHeight, float[] landmarks) {
        this.landmarks = landmarks;
        this.width = width;
        this.height = height;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    @Override
    public String toString() {
        return "Face{" +
                "landmarks=" + Arrays.toString(landmarks) +
                ", width=" + width +
                ", height=" + height +
                ", imgWidth=" + imgWidth +
                ", imgHeight=" + imgHeight +
                '}';
    }
}
