package com.sourcefuse.clickinandroid.model;




public interface AuthManagerI {
	
	public abstract void signIn(String username, String password,String deviceToken, String deviceType);
	public abstract void signUpAuth(String phone, String deviceToken);
	public abstract void getVerifyCode(String phone,String deviceToken,String vCode,String deviceType);
	public abstract void playItSafeAuth(String password,String phone,String email,String urserToken);
	public abstract void reSendVerifyCode(String phone,String deviceToken);
	public abstract void getProfileInfo(String othersphone,String phone, String usertoken);
	public abstract void sendNewRequest(String phone, String partner ,String usertoken);


}
