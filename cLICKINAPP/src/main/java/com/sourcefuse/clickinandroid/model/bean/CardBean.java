package com.sourcefuse.clickinandroid.model.bean;

/**
 * Created by mukesh on 5/9/14.
 */
public class CardBean {


    private String categoriesName;
    private String categoriesActive;

    public String getCategoriesName() {
        return categoriesName;
    }

    public void setCategoriesName(String categoriesName) {
        this.categoriesName = categoriesName;
    }

    public String getCategoriesActive() {
        return categoriesActive;
    }

    public void setCategoriesActive(String categoriesActive) {
        this.categoriesActive = categoriesActive;
    }


    private String cardUrl;
    private String cardDescription;
    private String cardTitle;
    private String cardActive;

    public String getCardDescription() {
        return cardDescription;
    }

    public void setCardDescription(String cardDescription) {
        this.cardDescription = cardDescription;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getCardActive() {
        return cardActive;
    }

    public void setCardActive(String cardActive) {
        this.cardActive = cardActive;
    }

    public String getCardUrl() {
        return cardUrl;
    }

    public void setCardUrl(String cardUrl) {
        this.cardUrl = cardUrl;
    }
}
