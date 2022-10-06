package com.firstprog.universityitschool.Model;

public class VideoModel {

    String videoName, videoUploadDate, videoUrl, videosMainChildID;

    public VideoModel(){}

    public VideoModel(String videoName, String videoUploadDate, String videoUrl, String videosMainChildID) {
        this.videoName = videoName;
        this.videoUploadDate = videoUploadDate;
        this.videoUrl = videoUrl;
        this.videosMainChildID = videosMainChildID;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUploadDate() {
        return videoUploadDate;
    }

    public void setVideoUploadDate(String videoUploadDate) {
        this.videoUploadDate = videoUploadDate;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideosMainChildID() {
        return videosMainChildID;
    }

    public void setVideosMainChildID(String videosMainChildID) {
        this.videosMainChildID = videosMainChildID;
    }
}
