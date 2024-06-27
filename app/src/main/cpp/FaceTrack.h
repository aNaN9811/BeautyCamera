#ifndef FACETRACK_H
#define FACETRACK_H

#include <opencv2/opencv.hpp>
#include <opencv2/objdetect.hpp>
#include "FaceAlignment/include/face_alignment.h"
#include <vector>

using namespace std;
using namespace cv;

class CascadeDetectorAdapter : public DetectionBasedTracker::IDetector {
private:
    Ptr<CascadeClassifier> Detector;

public:
    explicit CascadeDetectorAdapter(const Ptr<CascadeClassifier> &detector) :
            IDetector(),
            Detector(detector) {
        CV_Assert(detector);
    }

    void detect(const cv::Mat &Image, std::vector<cv::Rect> &objects) override {
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize,
                                   maxObjSize);
    }

    ~CascadeDetectorAdapter() override = default;
};

class FaceTrack {
public:
    FaceTrack(const char *model, const char *seeta);

    void detector(Mat src, vector<Rect2f> &rects);

    void startTracking();

    void stopTracking();

private:
    Ptr<DetectionBasedTracker> tracker;
    Ptr<seeta::FaceAlignment> faceAlignment;
};

#endif
