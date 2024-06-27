#include "FaceTrack.h"

FaceTrack::FaceTrack(const char *model, const char *seeta) {
    Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(model)); // OpenCV主探测器
    Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(model)); // OpenCV跟踪探测器
    DetectionBasedTracker::Parameters detectorParams;
    tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, detectorParams);
    faceAlignment = makePtr<seeta::FaceAlignment>(seeta);
}

void FaceTrack::startTracking() {
    tracker->run();
}

void FaceTrack::stopTracking() {
    tracker->stop();
}

void FaceTrack::detector(Mat src, vector<Rect2f> &rects) {
    vector<Rect> faces;
    tracker->process(src);
    tracker->getObjects(faces);
    if (faces.size()) {
        Rect face = faces[0];
        // 把跟踪出来的人脸保存到 rects
        rects.push_back(Rect2f(face.x, face.y, face.width, face.height));

        seeta::ImageData image_data(src.cols, src.rows);
        image_data.data = src.data;

        // 人脸追踪框信息绑定
        seeta::FaceInfo face_info; // 人脸信息
        seeta::Rect bbox; // 人脸框框信息
        bbox.x = face.x;
        bbox.y = face.y;
        bbox.width = face.width;
        bbox.height = face.height;
        face_info.bbox = bbox;

        seeta::FacialLandmark points[5];

        // 执行采集五个点
        faceAlignment->PointDetectLandmarks(image_data, face_info, points);

        for (int i = 0; i < 5; ++i) {
            rects.push_back(Rect2f(points[i].x, points[i].y, 0, 0));
        }
    }
}