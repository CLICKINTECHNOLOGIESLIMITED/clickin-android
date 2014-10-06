package com.sourcefuse.clickinandroid.model.bean;

/**
 * Created by mukesh on 1/7/14.
 */
public class FetchUsersByNameBean {
    private String qBId;
    private String rId;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String name;
    private String phoneNo;
    private String userPic;
    private String userToken;

    private String city;
    private String country;

    public String getqBId() {
        return qBId;
    }

    public void setqBId(String qBId) {
        this.qBId = qBId;
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
