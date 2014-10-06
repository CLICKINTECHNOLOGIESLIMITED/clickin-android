package com.sourcefuse.clickinandroid.model;


public class ModelManager {

	private final AuthManager authMgr;
	private final ProfileManager profileMgr;
	private final RelationManager relationManager;
	private final NewsFeedManager newsFeedManager;
	private final ChatManager chatManager;
    private final ClickInNotificationManager notificationManager;
    private final SettingManager msettingManager;

	private static ModelManager modelMgr = null;

	private ModelManager() {

		authMgr = new AuthManager();
		profileMgr = new ProfileManager();
		relationManager = new RelationManager();
		newsFeedManager = new NewsFeedManager();
		chatManager = new ChatManager();
        notificationManager = new ClickInNotificationManager();
        msettingManager=new SettingManager();

	}

	public static ModelManager getInstance() {
		if (modelMgr == null) {
			modelMgr = new ModelManager();
		}

		return modelMgr;
	}

    public static boolean getInstanceModelManager() {
        if (modelMgr != null) {
            return true;
        }else{
            return false;
        }
    }

	public AuthManager getAuthorizationManager() {

		return this.authMgr;
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
    public SettingManager getSettingManager()
    {
        return this.msettingManager;
    }
}
