package com.example.claimit;

public class ImageItem {

    private String imageUrl;

    private String severity;


    public ImageItem(String imageUrl, String severity) {
        this.imageUrl = imageUrl;
        this.severity = severity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }



}
