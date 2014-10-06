package com.sourcefuse.clickinandroid.model.bean;

public class ChatRecordBeen {


    private String isSender;// sender -->1,Receiver-->0
    private String chatType;// 1 For->Text,2 For-> Image,3 For-> Audio,4 For->Video,5 For-> Cards;

    private String chatText;
    private String receiveText;
    private String sendingtimeStamp;
    private String recievingTime;
    private String chatImageUrl;

    public String getChatImageUrl() {
        return chatImageUrl;
    }

    public void setChatImageUrl(String chatImageUrl) {
        this.chatImageUrl = chatImageUrl;
    }

    public String getSenderQbId() {
        return senderQbId;
    }

    public void setSenderQbId(String senderQbId) {
        this.senderQbId = senderQbId;
    }

    public String getRecieverQbId() {
        return recieverQbId;
    }

    public void setRecieverQbId(String recieverQbId) {
        this.recieverQbId = recieverQbId;
    }

    private String senderQbId;
    private String recieverQbId;


    public String isSender() {
        return isSender;
    }

    public void setSender(String isSender) {
        this.isSender = isSender;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }


//sending Image

    private String sImagePath;

    public String getsImagePath() {
        return sImagePath;
    }

    public void setsImagePath(String sImagePath) {
        this.sImagePath = sImagePath;
    }
/*
    public String getsClicks() {
        return sClicks;
    }

    public void setsClicks(String sClicks) {
        this.sClicks = sClicks;
    }*/

   /* public String getsClicksText() {
        return sClicksText;
    }

    public void setsClicksText(String sClicksText) {
        this.sClicksText = sClicksText;
    }*/

    public String getsImageSentTs() {
        return sImageSentTs;
    }

    public void setsImageSentTs(String sImageSentTs) {
        this.sImageSentTs = sImageSentTs;
    }

    public String getrImagePath() {
        return rImagePath;
    }

    public void setrImagePath(String rImagePath) {
        this.rImagePath = rImagePath;
    }

    public String getClicks() {
        return clicksTxt;
    }

    public void setClicks(String rClicks) {
        this.clicksTxt = rClicks;
    }

    public String getrClicksText() {
        return rClicksText;
    }

    public void setrClicksText(String rClicksText) {
        this.rClicksText = rClicksText;
    }

    public String getrImageSentTs() {
        return rImageSentTs;
    }

    public void setrImageSentTs(String rImageSentTs) {
        this.rImageSentTs = rImageSentTs;
    }

    //private String sClicks;
    private String sClicksText;
    private String sImageSentTs;

    //Recieve Image

    private String rImagePath;
    private String clicksTxt;
    private String rClicksText;
    private String rImageSentTs;

    /**
     * @return the recievingTime
     */
    public String getRecievingTime() {
        return recievingTime;
    }

    /**
     * @param recievingTime the recievingTime to set
     */
    public void setRecievingTime(String recievingTime) {
        this.recievingTime = recievingTime;
    }

    /**
     * @return the sendText
     */
    public String getChatText() {
        return chatText;
    }

    /**
     * @param sendText the sendText to set
     */
    public void setChatText(String sendText) {
        this.chatText = sendText;
    }

    /**
     * @return the receiveText
     */
    public String getReceiveText() {
        return receiveText;
    }

    /**
     * @param receiveText the receiveText to set
     */
    public void setReceiveText(String receiveText) {
        this.receiveText = receiveText;
    }

    /**
     * @return the timeStamp
     */
    public String getTimeStamp() {
        return sendingtimeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(String timeStamp) {
        this.sendingtimeStamp = timeStamp;
    }
}
