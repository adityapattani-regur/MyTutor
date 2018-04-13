package com.storytellers.tutor.app;

import java.io.Serializable;

public class Tutor implements Serializable {
    public String id;
    public String image;
    public String name;
    public String email;
    public String knows;
    public String description;
    public String reviews;
    public long recommendations;

    private Tutor() {}

    Tutor(String name, String email) {
        this.name = name;
        this.email = email;
    }

    Tutor(String image, String name, String email, String know, String description) {
        this.image = image;
        this.name = name;
        this.email = email;
        this.knows = knows;
        this.description = description;
    }

    Tutor(String image, String name, String email, String knows, String description, String reviews, long recommendations) {
        this.image = image;
        this.name = name;
        this.email = email;
        this.knows = knows;
        this.description = description;
        this.reviews = reviews;
        this.recommendations = recommendations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKnows() {
        return knows;
    }

    public void setKnows(String knows) {
        this.knows = knows;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    public long getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(long recommendations) {
        this.recommendations = recommendations;
    }
}
