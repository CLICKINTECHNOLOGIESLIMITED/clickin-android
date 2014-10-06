package com.sourcefuse.clickinandroid.model.bean;

public class FollowerFollowingBean {

	private String accepted;
    private String followeeId;
    private String rFollowerId;
    private String followeeName;
    private String followeePic;

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    private String phoneNo;
    private String isFollowing;
    private String followingAccepted;
    private String followingId;

    public String getrFollowerId() {
        return rFollowerId;
    }

    public void setrFollowerId(String rFollowerId) {
        this.rFollowerId = rFollowerId;
    }



    public String getFollowingAccepted() {
        return followingAccepted;
    }

    public void setFollowingAccepted(String followingAccepted) {
        this.followingAccepted = followingAccepted;
    }


	/**
	 * @return the accepted
	 */
	public String getAccepted() {
		return accepted;
	}
	/**
	 * @param accepted the accepted to set
	 */
	public void setAccepted(String accepted) {
		this.accepted = accepted;
	}
	/**
	 * @return the followeeId
	 */
	public String getFolloweeId() {
		return followeeId;
	}
	/**
	 * @param followeeId the followeeId to set
	 */
	public void setFolloweeId(String followeeId) {
		this.followeeId = followeeId;
	}
	/**
	 * @return the followeeName
	 */
	public String getFolloweeName() {
		return followeeName;
	}
	/**
	 * @param followeeName the followeeName to set
	 */
	public void setFolloweeName(String followeeName) {
		this.followeeName = followeeName;
	}
	/**
	 * @return the followeePic
	 */
	public String getFolloweePic() {
		return followeePic;
	}
	/**
	 * @param followeePic the followeePic to set
	 */
	public void setFolloweePic(String followeePic) {
		this.followeePic = followeePic;
	}
	/**
	 * @return the phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}
	/**
	 * @param phoneNo the phoneNo to set
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	/**
	 * @return the isFollowing
	 */
	public String getIsFollowing() {
		return isFollowing;
	}
	/**
	 * @param isFollowing the isFollowing to set
	 */
	public void setIsFollowing(String isFollowing) {
		this.isFollowing = isFollowing;
	}
	
}
