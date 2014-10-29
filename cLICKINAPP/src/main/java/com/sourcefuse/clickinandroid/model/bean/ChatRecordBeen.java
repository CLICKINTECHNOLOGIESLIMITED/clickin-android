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

    public String getClicksTxt() {
        return clicksTxt;
    }

    public void setClicksTxt(String clicksTxt) {
        this.clicksTxt = clicksTxt;
    }



// Card View Details start
    private String card_clicks;
    private String card_owner;
    private String card_content;
    private String is_CustomCard;
    private String card_DB_ID;
    private String card_heading;
    private String card_Accepted_Rejected ;
    private String card_url;
    private String card_id ;
    private String card_Played_Countered ;
    private String card_originator;
    private String cardPartnerName;

    public String getCardPartnerName() {
        return cardPartnerName;
    }

    public void setCardPartnerName(String cardPartnerName) {
        this.cardPartnerName = cardPartnerName;
    }

    public String getCard_clicks() {
        return card_clicks;
    }

    public void setCard_clicks(String card_clicks) {
        this.card_clicks = card_clicks;
    }

    public String getCard_owner() {
        return card_owner;
    }

    public void setCard_owner(String card_owner) {
        this.card_owner = card_owner;
    }

    public String getCard_content() {
        return card_content;
    }

    public void setCard_content(String card_content) {
        this.card_content = card_content;
    }

    public String getIs_CustomCard() {
        return is_CustomCard;
    }

    public void setIs_CustomCard(String is_CustomCard) {
        this.is_CustomCard = is_CustomCard;
    }

    public String getCard_DB_ID() {
        return card_DB_ID;
    }

    public void setCard_DB_ID(String card_DB_ID) {
        this.card_DB_ID = card_DB_ID;
    }

    public String getCard_heading() {
        return card_heading;
    }

    public void setCard_heading(String card_heading) {
        this.card_heading = card_heading;
    }

    public String getCard_Accepted_Rejected() {
        return card_Accepted_Rejected;
    }

    public void setCard_Accepted_Rejected(String card_Accepted_Rejected) {
        this.card_Accepted_Rejected = card_Accepted_Rejected;
    }

    public String getCard_url() {
        return card_url;
    }

    public void setCard_url(String card_url) {
        this.card_url = card_url;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getCard_Played_Countered() {
        return card_Played_Countered;
    }

    public void setCard_Played_Countered(String card_Played_Countered) {
        this.card_Played_Countered = card_Played_Countered;
    }

    public String getCard_originator() {
        return card_originator;
    }

    public void setCard_originator(String card_originator) {
        this.card_originator = card_originator;
    }
///////////////// Card Details end




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
