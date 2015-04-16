package com.sourcefuse.clickinandroid.model.bean;

/**
 * Created by mukesh on 5/9/14.
 */
public class CardBean {


    private String categoriesName;
    private String categoriesActive;
    private String card_Id;
    private String cardUrl;
    private String cardDescription;
    private String cardTitle;
    private String cardActive;

    public String getCard_Id() {
        return card_Id;
    }

    public void setCard_Id(String card_Id) {
        this.card_Id = card_Id;
    }

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
