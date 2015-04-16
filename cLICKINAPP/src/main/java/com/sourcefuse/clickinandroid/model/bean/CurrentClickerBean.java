package com.sourcefuse.clickinandroid.model.bean;

/**
 * Created by mukesh on 1/8/14.
 */
public class CurrentClickerBean {


    private String name;
    private int follow;
    private int exists;
    private String clickerPix;
    private String getClickerPhone;

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClickerPix() {
        return clickerPix;
    }

    public void setClickerPix(String clickerPix) {
        this.clickerPix = clickerPix;
    }

    public String getGetClickerPhone() {
        return getClickerPhone;
    }

    public void setGetClickerPhone(String getClickerPhone) {
        this.getClickerPhone = getClickerPhone;
    }

    public int getExists() {
        return exists;
    }

    public void setExists(int exists) {
        this.exists = exists;
    }


}
