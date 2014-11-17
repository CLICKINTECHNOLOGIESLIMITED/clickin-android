package com.sourcefuse.clickinandroid.model.bean;

import java.util.Comparator;


public class ContactBean {

    private String conName;
    private String conNumber;
    private String conUri;



  /*  public ContactBean(String conName, String conNumber, String conUri,boolean isChecked) {
        this.conName = conName;
        this.conNumber = conNumber;
        this.conUri = conUri;
        this.isChecked = isChecked;
    }*/



    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    private boolean isChecked;
    /**
     * @return the conName
     */
    public String getConName() {
        return conName;
    }
    /**
     * @param conName the conName to set
     */
    public void setConName(String conName) {
        this.conName = conName;
    }
    /**
     * @return the conNumber
     */
    public String getConNumber() {
        return conNumber;
    }
    /**
     * @param conNumber the conNumber to set
     */
    public void setConNumber(String conNumber) {
        this.conNumber = conNumber;
    }
    /**
     * @return the conUri
     */
    public String getConUri() {
        return conUri;
    }
    /**
     * @param conUri the conUri to set
     */
    public void setConUri(String conUri) {
        this.conUri = conUri;
    }



}
