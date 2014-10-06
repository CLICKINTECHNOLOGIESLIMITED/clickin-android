/*
package com.sourcefuse.clickinandroid.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.quickblox.module.chat.QBChatRoom;
import com.quickblox.module.chat.smack.SmackAndroid;
import com.sourcefuse.clickinapp.R;

import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import de.greenrobot.event.EventBus;

*/
/**
 * Created by mukesh on 3/10/14.
 *//*

public class amits2 {
}



package com.example.footgloryflow.fragment;

        import android.app.Fragment;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.content.ServiceConnection;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.ListView;

        import com.example.footgloryflow.adapter.ChatAdapter;
        import com.example.footgloryflow.app.AddVenue;
        import com.example.footgloryflow.app.ChatService;
        import com.example.footgloryflow.app.FootGloryBaseView;
        import com.example.footgloryflow.app.MatchOrganizer;
        import com.example.footgloryflow.app.R;
        import com.example.footgloryflow.com.example.footgloryflow.common.GlobalsFG;
        import com.example.footgloryflow.events.ChatBean;
        import com.example.footgloryflow.events.FgEvent;
        import com.example.footgloryflow.session.Session;
        import com.quickblox.core.QBCallbackImpl;
        import com.quickblox.core.QBSettings;
        import com.quickblox.core.result.Result;
        import com.quickblox.module.auth.QBAuth;
        import com.quickblox.module.auth.result.QBSessionResult;
        import com.quickblox.module.chat.QBChatRoom;
        import com.quickblox.module.chat.QBChatService;
        import com.quickblox.module.chat.listeners.ChatMessageListener;
        import com.quickblox.module.chat.listeners.RoomListener;
        import com.quickblox.module.chat.listeners.SessionCallback;
        import com.quickblox.module.chat.smack.SmackAndroid;
        import com.quickblox.module.users.model.QBUser;

        import org.jivesoftware.smack.XMPPException;
        import org.jivesoftware.smack.packet.Message;

        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Collection;
        import java.util.List;

        import de.greenrobot.event.EventBus;

*/
/**
 * Created by prafull on 13/8/14.
 *//*

public class TeamChatFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = TeamChatFragment.class.getSimpleName();
    private ChatService mChatService;


    public static ArrayList<ChatBean> chatList = new ArrayList<ChatBean>();
    View rootview;
    private EditText chatBox;
    private ImageView chatSend;
    private ListView chatListView;
    private QBChatRoom chatRoom;
    // private boolean addInListRec,addInListsend = true;
    private String mChatMsg = "chamukesh";
    private String mchatText, mUsrName, msendtime, mUserPic, mIsVenue, mVenueName, mVenueAdd, mVenueImage;
    private int mproposeVenueId;
    private boolean addHistroy = true;
    private boolean addaddchat = true;
    private ChatAdapter adapter;
    GlobalsFG globalsFG;
    private boolean mIsBound;
    private String mRoomName;

    public static boolean isEmptyString(String str) {
        if (str == null || str.equalsIgnoreCase("null")
                || str.equalsIgnoreCase("") || str.length() < 1) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            // Detach our existing connection.
            getActivity().unbindService(mConnection);
            mIsBound = false;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle bundle) {

        rootview = inflater.inflate(R.layout.chat_fragment, group, false);
        //this.rootview=rootView;
        chatBox = (EditText) rootview.findViewById(R.id.add_comment);
        chatSend = (ImageView) rootview.findViewById(R.id.send_comment);
        chatListView = (ListView) rootview.findViewById(R.id.chat_list_view);

        ((Button) rootview.findViewById(R.id.btn_propose_venue)).setOnClickListener(this);

        Log.e("onCreateView", "--> " + "onCreateView");
        globalsFG = (GlobalsFG) GlobalsFG.getInstance();

        chatList.clear();


        loginToQuickBlox();

        chatSend.setOnClickListener(this);

        setlist();
        return rootview;
    }

    public void setlist() {
        adapter = new ChatAdapter(getActivity(), R.layout.row_chat, chatList);
        chatListView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (GlobalsFG.getInstance().isProposeVenue) {
            try {
                Log.e(TAG, "send successfully onResume");
                addHistroy = false;
                mChatMsg = null;
                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                mChatMsg = "truee" + "”" + "" + "”" + Session.getInstance(getActivity()).getName() + "”" + mydate + "”" + Session.getInstance(getActivity()).getAvatar() + "”" + GlobalsFG.getInstance().proposeVenueName + "”" + GlobalsFG.getInstance().proposeVenueAddress + "”"
                        + GlobalsFG.getInstance().proposeVenueImage + "”" + GlobalsFG.getInstance().proposeVenueId;

                String s[] = mChatMsg.split("”");

                mIsVenue = s[0];
                mchatText = s[1];
                mUsrName = s[2];
                msendtime = s[3];
                mUserPic = s[4];
                mVenueName = s[5];
                mVenueAdd = s[6];
                mVenueImage = s[7];
                mproposeVenueId = Integer.parseInt(s[8]);

                chatRoom.sendMessage(mChatMsg);
                ChatBean addChat = new ChatBean();
                addChat.setSentVenue(mIsVenue);
                addChat.setSentUsrName(mUsrName);
                addChat.setSentTime(msendtime);
                addChat.setSentUsrPic(mUserPic);
                addChat.setSentVenueText(mVenueName);
                addChat.setSentVenueAddress(mVenueAdd);
                addChat.setSentVenueImage(mVenueImage);
                addChat.setsendvenueid(mproposeVenueId);

                addChat.setRecMessage("");
                addChat.setSentMessage("");

                chatList.add(addChat);
                adapter.notifyDataSetChanged();

                GlobalsFG.getInstance().isProposeVenue = false;
                addHistroy = false;
                if (globalsFG.flag_team_cordinator) {
                    // showdialog();
                }

            } catch (Exception e) {
                Log.e(TAG, "XMPPException " + e);
                e.printStackTrace();
            }
        }
    }

    public void onEventMainThread(FgEvent event) {
        if(event.getType() == FgEvent.CHAT_LOGGED_IN) {
//            Session.getInstance(getActivity()).getChatRoom(GlobalsFG.getInstance().cur_match_id);
            if (globalsFG.userteam.equalsIgnoreCase("team1")) {
                mRoomName = GlobalsFG.getInstance().cur_match_id + "team1";
            } else {
                mRoomName = GlobalsFG.getInstance().cur_match_id + "team2";
            }
            mChatService.createRoom(mRoomName);
        } else if(event.getType() == FgEvent.CHAT_ROOM_CREATED) {
            QBChatRoom chatRoom = (QBChatRoom) event.getValue();
            if(chatRoom.getName().equals(mRoomName)) {
                List<Integer> userIds = new ArrayList<Integer>();

                for (int i = 0; i <= GlobalsFG.getInstance().chat_team.size(); i++) {
                    userIds.add((int) Long.parseLong(GlobalsFG.getInstance().chat_team.get(i).fb_id));
                }

                try {
                    chatRoom.addRoomUsers(userIds);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }

                try {
                    Collection<String> roomUsers = chatRoom.getRoomUsers();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        } else if(event.getType() == FgEvent.CHAT_ROOM_JOINED) {
            chatRoom = (QBChatRoom) event.getValue();
            if(chatRoom.getName().equals(mRoomName)) {
                ((FootGloryBaseView) getActivity()).dismissLoading();
            }
        } else if(event.getType() == FgEvent.CHAT_MESSAGE) {
            Message message = (Message) event.getValue();
            if(message.getTo().equals(mRoomName)) {
                globalsFG.chatCount = 0;
                getActivity().findViewById(R.id.chat_count).setVisibility(View.GONE);
                String testString = message.getBody();

                String getVenue = testString.substring(0, 5);
                Log.e("get venue", getVenue);
                if (getVenue.equals("truee") && addHistroy) {
                    String s[] = testString.split("”");
                    try {
                        mIsVenue = s[0];
                        mchatText = s[1];
                        mUsrName = s[2];
                        msendtime = s[3];
                        mUserPic = s[4];
                        mVenueName = s[5];
                        mVenueAdd = s[6];
                        mVenueImage = s[7];
                        mproposeVenueId = Integer.parseInt(s[8]);

                        ChatBean addChat = new ChatBean();
                        addChat.setSentVenue(mIsVenue);
                        addChat.setSentUsrName(mUsrName);
                        addChat.setSentTime(msendtime);
                        addChat.setSentUsrPic(mUserPic);
                        addChat.setSentVenueText(mVenueName);
                        addChat.setSentVenueAddress(mVenueAdd);
                        addChat.setSentVenueImage(mVenueImage);
                        addChat.setRecMessage("");
                        addChat.setSentMessage(mchatText);
                        addChat.setsendvenueid(mproposeVenueId);
                        addChat.setrecivevenueid(mproposeVenueId);
                        chatList.add(addChat);
                        adapter.notifyDataSetChanged();
                        Log.e("propose venue::::::", "propose venue::::::");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (!getVenue.matches("truee") && addaddchat) {
                    String s[] = testString.split("”");
                    mIsVenue = s[0];
                    mchatText = s[1];

                    mUsrName = s[2];
                    Log.e("user name", mUsrName);
                    msendtime = s[3];
                    mUserPic = s[4];

                    if (!mChatMsg.matches(message.getBody()) && !mUsrName.equalsIgnoreCase(Session.getInstance(getActivity()).getName())) {
                        Log.e("recive user:::::::::::::::::::", "recive user:::::::::::::::::");
                        ChatBean addChat = new ChatBean();
                        addChat.setSentVenue(mIsVenue);

                        addChat.setRecMessage(mchatText);
                        addChat.setRecUsrName(mUsrName);
                        addChat.setRecTime(msendtime);
                        addChat.setRecUsrPic(mUserPic);

                        addChat.setSentUsrName("");
                        addChat.setSentMessage("");
                        addChat.setSentUsrName("");
                        addChat.setSentTime("");
                        addChat.setSentUsrPic("");
                        chatList.add(addChat);
                        adapter.notifyDataSetChanged();


                    } else {
                        Log.e("send user:::::::::::::::", "send user:::::::::::::");
                        ChatBean addChat = new ChatBean();
                        addChat.setSentVenue(mIsVenue);
                        addChat.setSentMessage(mchatText);
                        addChat.setSentUsrName(mUsrName);
                        addChat.setSentTime(msendtime);
                        addChat.setSentUsrPic(mUserPic);

                        addChat.setRecMessage("");
                        addChat.setRecUsrName("");
                        addChat.setRecTime("");
                        addChat.setRecUsrPic("");


                        chatList.add(addChat);
                        adapter.notifyDataSetChanged();
                    }
                }
                addHistroy = true;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_comment:
                String ss = chatBox.getText().toString();
                if (!isEmptyString(ss)) {
                    addHistroy = true;
                    addaddchat = false;
                    try {
                        Log.e(TAG, "send successfully" + ss);
                        addHistroy = false;

                        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        mChatMsg = "false" + "”" + ss + "”" + Session.getInstance(getActivity()).getName() + "”" + mydate + "”" + Session.getInstance(getActivity()).getAvatar();
                        String s[] = mChatMsg.split("”");

                        mIsVenue = s[0];
                        mchatText = s[1];
                        mUsrName = s[2];
                        msendtime = s[3];
                        mUserPic = s[4];

                        chatRoom.sendMessage(mChatMsg);

                        ChatBean addChat = new ChatBean();
                        addChat.setSentVenue(mIsVenue);
                        addChat.setSentMessage(mchatText);
                        addChat.setSentUsrName(mUsrName);
                        addChat.setSentTime(msendtime);
                        addChat.setSentUsrPic(mUserPic);
                        //  addChat.setSentVenue(mIsVenue);

                        addChat.setRecMessage("");
                        addChat.setRecTime("");
                        addChat.setRecUsrName("");
                        addChat.setRecUsrPic("");
                        //addChat.setRecVenue(mIsVenue);

                        // for testing only
                        chatList.add(addChat);
                        chatBox.setText("");
                        adapter.notifyDataSetChanged();
                        chatBox.setText("");
                    } catch (Exception e) {
                        Log.e(TAG, "XMPPException " + e);
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.btn_propose_venue:
                Intent intent = new Intent(getActivity(), AddVenue.class);
                intent.putExtra("fromChat", true);
                startActivity(intent);

                break;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mChatService = ((ChatService.LocalBinder)service).getService();
            // Tell the user about this for our demo.
//            Toast.makeText(Binding.this, R.string.local_service_connected,
//                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mChatService = null;
//            Toast.makeText(Binding.this, R.string.local_service_disconnected,
//                    Toast.LENGTH_SHORT).show();
        }
    };

    private void loginToQuickBlox() {
        SmackAndroid.init(getActivity());
        ((FootGloryBaseView) getActivity()).showLoading("Loading..");
        Log.e("onComplete", "--> " + Session.getInstance(getActivity()).getFb_id() + "--" + Session.getInstance(getActivity()).getchat_pwd() + " MatchId--> " + GlobalsFG.getInstance().cur_match_id);

//        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
//        final QBUser user = new QBUser(Session.getInstance(getActivity()).getFb_id(), Session.getInstance(getActivity()).getchat_pwd());
        getActivity().bindService(new Intent(getActivity(), ChatService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
}*/
