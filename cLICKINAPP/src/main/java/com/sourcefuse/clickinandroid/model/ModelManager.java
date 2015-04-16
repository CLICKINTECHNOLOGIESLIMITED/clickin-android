package com.sourcefuse.clickinandroid.model;


public class ModelManager {

    private static ModelManager modelMgr = null;
    private AuthManager authMgr;
    private ProfileManager profileMgr;
    private RelationManager relationManager;
    private NewsFeedManager newsFeedManager;
    private ChatManager chatManager;
    private ClickInNotificationManager notificationManager;
    private SettingManager msettingManager;

    private ModelManager() {

        authMgr = new AuthManager();
        profileMgr = new ProfileManager();
        relationManager = new RelationManager();
        newsFeedManager = new NewsFeedManager();
        chatManager = new ChatManager();
        notificationManager = new ClickInNotificationManager();
        msettingManager = new SettingManager();

    }

    public static ModelManager getInstance() {
        if (modelMgr == null) {
            modelMgr = new ModelManager();
        }

        return modelMgr;
    }

    public static void setInstance() {

        modelMgr = new ModelManager();

    }


    public static boolean getInstanceModelManager() {
        return modelMgr != null;
    }

    public AuthManager getAuthorizationManager() {

        return this.authMgr;
    }

    public void AuthorizationManager() {

        this.authMgr = null;
        this.profileMgr = null;
        this.relationManager = null;
        this.newsFeedManager = null;
        this.chatManager = null;
        this.notificationManager = null;
        this.msettingManager = null;
    }

    public ProfileManager getProfileManager() {

        return this.profileMgr;
    }

    public RelationManager getRelationManager() {

        return this.relationManager;
    }

    public NewsFeedManager getNewsFeedManager() {

        return this.newsFeedManager;
    }

    public ChatManager getChatManager() {

        return this.chatManager;
    }

    public ClickInNotificationManager getNotificationManagerManager() {

        return this.notificationManager;
    }

    public SettingManager getSettingManager() {
        return this.msettingManager;
    }


    /* code to set manager null*/

}
