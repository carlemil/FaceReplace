package se.kjellstrand.facereplace.domain;

import android.graphics.Point;

public class Face {

    /*
        face.confidence()
        face.eyesDistance()
        face.pose(FaceDetector.Face.EULER_X)
        face.pose(FaceDetector.Face.EULER_Y)
        face.pose(FaceDetector.Face.EULER_Z)
        eyesMidPts[i].x
        eyesMidPts[i].y
     */
    private byte[] image;
    private String tag;
    private float confidence;
    private float eyesDistance;
    private float poseEulerX;
    private float poseEulerY;
    private float poseEulerZ;
    private float eyesMidPointX;
    private float eyesMidPointY;
    
    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public float getEyesDistance() {
        return eyesDistance;
    }

    public void setEyesDistance(float eyesDistance) {
        this.eyesDistance = eyesDistance;
    }

    public float getPoseEulerX() {
        return poseEulerX;
    }

    public void setPoseEulerX(float poseEulerX) {
        this.poseEulerX = poseEulerX;
    }

    public float getPoseEulerY() {
        return poseEulerY;
    }

    public void setPoseEulerY(float poseEulerY) {
        this.poseEulerY = poseEulerY;
    }

    public float getPoseEulerZ() {
        return poseEulerZ;
    }

    public void setPoseEulerZ(float poseEulerZ) {
        this.poseEulerZ = poseEulerZ;
    }
    
    public float getEyesMidPointX() {
        return eyesMidPointX;
    }

    public void setEyesMidPointX(float eyesMidPointX) {
        this.eyesMidPointX = eyesMidPointX;
    }

    public float getEyesMidPointY() {
        return eyesMidPointY;
    }

    public void setEyesMidPointY(float eyesMidPointY) {
        this.eyesMidPointY = eyesMidPointY;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    public void setImage(byte[] image) {
        this.image = image;
    }
    
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
