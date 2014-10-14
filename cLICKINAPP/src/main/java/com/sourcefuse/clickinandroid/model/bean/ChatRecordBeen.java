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


    private String _id;
    private String chatId;
    private String sharedMessage;
    private String video_thumb;
    private String senderUserToken;
    private String relationshipId;
    private String userId;
    private String location_coordinates;
    private String isDelivered;
    private String imageRatio;
    private String cards;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSharedMessage() {
        return sharedMessage;
    }

    public void setSharedMessage(String sharedMessage) {
        this.sharedMessage = sharedMessage;
    }

    public String getVideo_thumb() {
        return video_thumb;
    }

    public void setVideo_thumb(String video_thumb) {
        this.video_thumb = video_thumb;
    }

    public String getSenderUserToken() {
        return senderUserToken;
    }

    public void setSenderUserToken(String senderUserToken) {
        this.senderUserToken = senderUserToken;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation_coordinates() {
        return location_coordinates;
    }

    public void setLocation_coordinates(String location_coordinates) {
        this.location_coordinates = location_coordinates;
    }

    public String getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(String isDelivered) {
        this.isDelivered = isDelivered;
    }

    public String getImageRatio() {
        return imageRatio;
    }

    public void setImageRatio(String imageRatio) {
        this.imageRatio = imageRatio;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }








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
