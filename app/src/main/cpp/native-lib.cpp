#include <jni.h>
#include <string>
#include "FaceTrack.h"
#include "opencv2/imgproc/types_c.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_beautycamera_face_FaceTrack_native_1create(JNIEnv *env, jobject thiz,
                                                            jstring model_, jstring seeta_) {
    const char *model = env->GetStringUTFChars(model_, nullptr);
    const char *seeta = env->GetStringUTFChars(seeta_, nullptr);

    FaceTrack *faceTrack = new FaceTrack(model, seeta);

    env->ReleaseStringUTFChars(model_, model);
    env->ReleaseStringUTFChars(seeta_, seeta);
    return reinterpret_cast<jlong>(faceTrack);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_beautycamera_face_FaceTrack_native_1start(JNIEnv *env, jobject thiz, jlong self) {
    if (self == 0) {
        return;
    }
    FaceTrack *faceTrack = reinterpret_cast<FaceTrack *>(self);
    faceTrack->startTracking();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_beautycamera_face_FaceTrack_native_1stop(JNIEnv *env, jobject thiz, jlong self) {
    if (self == 0) {
        return;
    }
    FaceTrack *faceTrack = reinterpret_cast<FaceTrack *>(self);
    faceTrack->stopTracking();
    delete faceTrack;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_beautycamera_face_FaceTrack_native_1detector(JNIEnv *env, jobject thiz, jlong self,
                                                              jbyteArray data_, jint camera_id,
                                                              jint width, jint height) {
    if (self == 0) {
        return NULL;
    }

    jbyte *data = env->GetByteArrayElements(data_, 0);
    FaceTrack *faceTrack = reinterpret_cast<FaceTrack *>(self); // 通过地址反转CPP对象

    // OpenCV旋转数据操作
    Mat src(height + height / 2, width, CV_8UC1, data); // 摄像头数据data 转成 OpenCv的 Mat
    cvtColor(src, src, CV_YUV2RGBA_NV21); // 把YUV转成RGBA

    // 使用opencv画面旋转会导致src异常

    // OpenCV基础操作
    cvtColor(src, src, COLOR_RGBA2GRAY); // 灰度化
    equalizeHist(src, src); // 均衡化处理（直方图均衡化，增强对比效果）
    vector<Rect2f> rects;
    faceTrack->detector(src, rects); // 人脸检测
    env->ReleaseByteArrayElements(data_, data, 0);

    // rects 已有丰富的人脸框框的信息，后面封装操作Face.java
    int imgWidth = src.cols; // 构建 Face.java的 int imgWidth; 送去检测图片的宽
    int imgHeight = src.rows; // 构建 Face.java的 int imgHeight; 送去检测图片的高
    int ret = rects.size();
    if (ret) {
        jclass clazz = env->FindClass("com/example/beautycamera/face/Face");
        jmethodID construct = env->GetMethodID(clazz, "<init>", "(IIII[F)V");
        // int width, int height,int imgWidth,int imgHeight, float[] landmark
        int size = ret * 2; // 乘以2是因为，有x与y， 其实size===2，因为rects就一个人脸

        // 构建 Face.java的 float[] landmarks;
        jfloatArray floatArray = env->NewFloatArray(size);
        for (int i = 0, j = 0; i < size; ++j) {  // 前两个就是人脸的x与y
            // 只考虑前置摄像头上下左右颠倒
            float f[2] = {imgWidth - rects[j].x, imgHeight - rects[j].y};
            env->SetFloatArrayRegion(floatArray, i, 2, f);
            i += 2;
        }

        Rect2f faceRect = rects[0];
        int faceWidth = faceRect.width; // 构建 Face.java的 int width; 保存人脸的宽
        int faceHeight = faceRect.height; // 构建 Face.java的 int height; 保存人脸的高

        // 实例化Face.java对象
        jobject face = env->NewObject(clazz, construct, faceWidth, faceHeight, imgWidth, imgHeight,
                                      floatArray);
        return face;
    }
    src.release();
    return NULL;
}