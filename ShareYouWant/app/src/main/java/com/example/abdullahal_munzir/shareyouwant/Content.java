package com.example.abdullahal_munzir.shareyouwant;

/**
 * Created by Abdullah Al-Munzir on 13-Mar-18.
 */

public class Content {
    private String author, title, details, category,image_uri, platform;
    private long date_time;
    private boolean negotiable;
    int demand;

    public Content(){

    }

    public Content(String author, String title, String details, int demand, String category, String image_uri, long date_time, boolean negotiable, String platform) {
        this.author = author;
        this.title = title;
        this.details = details;
        this.demand = demand;
        this.category = category;
        this.image_uri = image_uri;
        this.date_time = date_time;
        this.negotiable = negotiable;
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public boolean isNegotiable() {
        return negotiable;
    }

    public void setNegotiable(boolean negotiable) {
        this.negotiable = negotiable;
    }
}
