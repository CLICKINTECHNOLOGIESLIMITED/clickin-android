package com.sourcefuse.clickinandroid.model.bean;

public class ChatRecordBeen {

    private String chatType;// 1 For->Text,2 For-> Image,3 For-> Audio,4 For->Video,5 For-> Cards;
    private String chatText;
    private String senderQbId;
    private String recieverQbId;
    private String clicksTxt;
    private String chatImageUrl;
    private String messageId;
    private String timeStamp;

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

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getClicks() {
        return clicksTxt;
    }

    public void setClicks(String clicks) {
        this.clicksTxt = clicks;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


}
