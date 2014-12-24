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

}
