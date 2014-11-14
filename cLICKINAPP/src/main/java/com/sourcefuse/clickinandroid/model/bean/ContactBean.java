package com.sourcefuse.clickinandroid.model.bean;

<<<<<<< HEAD
import java.util.Comparator;

=======
>>>>>>> 5065e320d5c0053fcdd426eaf2c980eab897baca
public class ContactBean {

    private String conName;
    private String conNumber;
    private String conUri;
<<<<<<< HEAD
    private boolean isChecked;


  /*  public ContactBean(String conName, String conNumber, String conUri,boolean isChecked) {
        this.conName = conName;
        this.conNumber = conNumber;
        this.conUri = conUri;
        this.isChecked = isChecked;
    }*/

=======
>>>>>>> 5065e320d5c0053fcdd426eaf2c980eab897baca

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


<<<<<<< HEAD
  /*  @Override
    public int compareTo(ContactBean other) {

        *//* For Ascending order*//*
       // return this.getConName().compareTo(other.getConName());

        if (this.getConName().equals(other.getConName())) {
            return this.getConName().compareTo(other.getConName());
        } else {
            return this.getConName().compareTo(other.getConName());
        }

    }
*/

=======

>>>>>>> 5065e320d5c0053fcdd426eaf2c980eab897baca
}
