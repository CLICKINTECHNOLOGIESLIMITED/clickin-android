package com.sourcefuse.clickinandroid.model.bean;

/**
 * Created by mukesh on 17/11/14.
 */
public class ChatMessageBody {
    public String partnerQbId = null;
    public String textMsg = null;
    public String clicks = null;
    public int chatType;
    public String content_url = null;
    public String imageRatio = null;

//Card params

    public String card_owner = null;
    public String card_content = null;
    public Boolean is_CustomCard = false;
    public String card_DB_ID = null;
    public String card_Accepted_Rejected = null;
    public String card_heading = null;
    public String card_url = null;
    public String card_id = null;
    public String card_Played_Countered = null;
    public String card_originator = null;
    public String _id = null;
    public String deliveredChatID = null;

    //for video
    public String video_thumb = null;
    public String chatId = null;
    public String sentOn = null;
    public String location_coordinates = null;
    public String locationId = null;

    public String sharedMessage = null;
    public String isDelivered = null;
    public String relationshipId = null;
    public String userId = null;
    public String senderUserToken = null;
    public String senderQbId;

// Share message

    public String isMessageSender = null;
    public String shareStatus = null;
    public String shareComment = " ";
    public String originalMessageID = null;
    public String sharingMedia = null;
    public String isAccepted = null;
    public String facebookToken = null;
    //monika- to show image from uri
    public String content_uri = null;

    public ChatMessageBody() {

    }

    public ChatMessageBody(ChatMessageBody body) {
        this.textMsg = body.textMsg;
        partnerQbId = body.partnerQbId;
        clicks = body.clicks;
        chatType = body.chatType;
        content_url = body.content_url;
        imageRatio = body.imageRatio;
        card_owner = body.card_owner;

        //Card params
        card_content = body.card_content;
        is_CustomCard = body.is_CustomCard;
        card_DB_ID = body.card_DB_ID;
        card_Accepted_Rejected = body.card_Accepted_Rejected;
        card_heading = body.card_url;
        card_url = body.card_url;
        card_id = body.card_id;
        card_Played_Countered = body.card_Played_Countered;
        card_originator = body.card_originator;
        _id = body._id;
        deliveredChatID = body.deliveredChatID;
        //for video
        video_thumb = body.video_thumb;
        chatId = body.chatId;
        sentOn = body.sentOn;
        location_coordinates = body.location_coordinates;
        locationId = body.locationId;
        sharedMessage = body.sharedMessage;
        isDelivered = body.isDelivered;
        relationshipId = body.relationshipId;
        userId = body.userId;
        senderUserToken = body.senderUserToken;
        senderQbId = body.senderQbId;
        isMessageSender = body.isMessageSender;

        // Share message
        shareStatus = body.shareStatus;
        shareComment = body.shareComment;
        originalMessageID = body.originalMessageID;
        sharingMedia = body.sharingMedia;
        isAccepted = body.isAccepted;
        facebookToken = body.facebookToken;

    }


}
