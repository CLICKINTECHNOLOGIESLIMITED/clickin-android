package com.sourcefuse.clickinandroid.utils;

public class Constants {


    public static final String DEVICETYPE = "ANDROID";
    public static final int CROP_PROFILE_PIC = 3;
    public static final int CAMERA_REQUEST = 1888;
    public static final int SELECT_PICTURE = 1;
    public static final int CROP_PICTURE = 12;
    public static final int CALL_CHAT_INSTANC = 45 * 1000;
    //QB for staging

    public static final String CLICKIN_APP_ID = "6768";
    public static final String CLICKIN_AUTH_KEY = "QVr4uK5tt6cu6dN";
    public static final String CLICKIN_AUTH_SECRET = "4thHbq-eyLVJrhe";
    public static final String ACCOUNTKEY = "gBv3BjZnFzkVPUZEqEXm";
    public static final int sliderWidth = 60;

    public static final String mSplunk_Api = "119346bb";
    // QB for prod
    /*public static final String CLICKIN_APP_ID = "5";
    public static final String CLICKIN_AUTH_KEY = "6QQJq2FSKKzHK2-";
    public static final String CLICKIN_AUTH_SECRET = "k9cTQAeFWrkEAWv";

    public static final String ACCOUNTKEY = "gBv3BjZnFzkVPUZEqEXm";*/

    public static final int SMS_SEND = 100;
    public static final String PREFS_VALUE_PHONE = "phone";
    public static final String PREFS_VALUE_USER_TOKEN = "user-token";
    public static final String PREFS_VALUE_FBTOKEN = "fb-token";
    public static final String PREFS_VALUE_USER_EMAILID = "email-id";
    public static final String PREFS_VALUE_QB_ID = "qb-id";
    public static final String PREFS_VALUE_USER_ID = "user-id";
    public static final String FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD = "fonts/AvenirNextLTPro-BoldCn_0.otf";
    public static final String FONT_FILE_PATH_AVENIRNEXTLTPRO_MEDIUMCN = "fonts/AvenirNextLTPro-MediumCn_0.otf";
    public static final String SEND_REQUEST_WITH_SMS_MESSAGE = "Hey ! Lets you and I get Clickin' ! Download now - http://www.clickinapp.com";
    public static final String SEND_REQUEST_WITH_SMS_MESSAGE_SPREAD = "Hey! Come join us on Clickin' - Dowload Now - http://www.clickinapp.com";
    public static final String PRIVACY_LINK_URL = "https://api.clickinapp.com/pages/privacy-policy";
    public static final String TERMS_LINK_URL = "https://api.clickinapp.com/pages/term-of-use";
    public static final String APP_LINK_URL = "http://www.clickinapp.com/";
    public final static int CHAT_TYPE_TEXT = 1;
    public final static int CHAT_TYPE_IMAGE = 2;
    public final static int CHAT_TYPE_AUDIO = 3;
    public final static int CHAT_TYPE_VIDEO = 4;
    public final static int CHAT_TYPE_LOCATION = 6;
    public final static int CHAT_TYPE_CARD = 5;
    public final static int CHAT_TYPE_DELIVERED = 7;
    public final static int CHAT_TYPE_VIDEO_INITATING = 8;
    public final static int CHAT_TYPE_SHARING = 9;
    public final static int CHAT_TYPE_NOFITICATION = 10;

    //constant to check length of chat text in adapter
    public final static int CHAT_LENTH_LIMIT = 15;
    //constants for media delivery status
    public static final String MSG_SENDING = "SENDING";
    public static final String MSG_SENT = "SENT";
    public static final String MSG_DELIVERED = "DELIVERED";
    public static final int START_MAP = 200;
    public static boolean addChatMessageListener = false;
    public static int itemPosition = 0;
    public static boolean comments = false;
    public static String CUSTOM_CARD_URL = "https://s3.amazonaws.com/clickin-dev/cards/a/1080/custom_tradecart.jpg";

    public static boolean mInAppNotification = false;

    public static final String MIX_PANEL_TOKEN = "34b90dcc1a3f069eec5baa3208229d4b";

    //constants to filter notification type, to know which activity is to start
    public static final int USERPROFILE_NOTF=1;
    public static final int CHATRECORDVIEW_NOTF=2;
    public static final int FOLLOWER_FOLLOWING_NOTF=3;
    public static final int POSTVIEW_NOTF=4;
    public static final int JUMPOTHERPROFILEVIEW_NOTF=5;
    public static final int FEEDVIEW_NOTF=6;
}
