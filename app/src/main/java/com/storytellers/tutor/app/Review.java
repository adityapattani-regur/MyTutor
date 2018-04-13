package com.storytellers.tutor.app;

import java.io.Serializable;

public class Review implements Serializable {
    public String reviewId;
    public String tutorId;
    public String tutorName;
    public String userName;
    public String userId;
    public String userImage;
    public String desc;

    Review() {}

    public Review(String tutorId, String tutorName, String userId, String userName, String userImage, String desc) {
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
        this.desc = desc;
    }

    public Review(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }
}
