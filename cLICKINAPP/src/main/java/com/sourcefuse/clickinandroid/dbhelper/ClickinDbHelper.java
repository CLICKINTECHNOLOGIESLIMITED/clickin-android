    package com.sourcefuse.clickinandroid.dbhelper;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;
    import com.sourcefuse.clickinandroid.utils.Log;

    import java.sql.SQLException;
    import java.util.ArrayList;

    /**
     * Created by mukesh on 8/10/14.
     */


    public class ClickinDbHelper extends SQLiteOpenHelper implements ChatRecordI {

        private String TAG = ClickinDbHelper.class.getName();
        public static SQLiteDatabase dbObj;
        public static final String TABLE_CHATRECORD = "ChatRecord";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_SID = "sId";
        public static final String COLUMN_RID = "rId";
        public static final String COLUMN_MSGID = "msgId";
        public static final String COLUMN_MSG = "message";
        public static final String COLUMN_MSG_TYPE = "msgType";
        public static final String COLUMN_CLICKS = "clicks";
        public static final String COLUMN_TIMESTAMP = "timeStamp";
        public static final String COLUMN_FILEID = "fileId";
        public static final String COLUMN_USERID = "userId";

        public static final String COLUMN_SHAREEDMESSAGE = "sharedMessage";
        public static final String COLUMN_VIDEOTHAUMB = "video_thumb";
        public static final String COLUMN_SENDERUSERTOKEN = "senderUserToken";
        public static final String COLUMN_RELATIONSHIPID = "relationshipId";
        //public static final String COLUMN_USERID = "userId";
        public static final String COLUMN_LOCATION = "location_coordinates";
        public static final String COLUMN_ISDELIVERED = "isDelivered";
        public static final String COLUMN_IMAGERATIO = "imageRatio";
        public static final String COLUMN_CARDS = "cards";



        private static final String DATABASE_NAME = "ClickInChatRecords.sqlite";
        private static final int DATABASE_VERSION = 1;

        // Database creation sql statement
       /* private static final String DATABASE_CREATE = " CREATE TABLE "
                + TABLE_CHATRECORD + "(" + COLUMN_ID + " integer primary key autoincrement,"
                + COLUMN_SID + " text, "
                + COLUMN_RID + " text, "
                + COLUMN_MSGID + " text, "
                + COLUMN_MSG + " text, "
                + COLUMN_MSG_TYPE + " text, "
                + COLUMN_CLICKS + " text, "
                + COLUMN_TIMESTAMP + " text, "
                + COLUMN_SHAREEDMESSAGE + " text, "
                + COLUMN_VIDEOTHAUMB + " text, "
                + COLUMN_SENDERUSERTOKEN + " text, "
                + COLUMN_RELATIONSHIPID + " text, "
                + COLUMN_USERID + " text, "
                + COLUMN_LOCATION + " text, "
                + COLUMN_ISDELIVERED + " text, "
                + COLUMN_IMAGERATIO + " text, "
                + COLUMN_CARDS + " text, "
                + COLUMN_FILEID + " text);";*/


        private static final String DATABASE_CREATE = " CREATE TABLE "
                + TABLE_CHATRECORD + "(" + COLUMN_ID + " integer primary key autoincrement,"
                + COLUMN_SID + " text, "
                + COLUMN_RID + " text, "
                + COLUMN_MSGID + " text, "
                + COLUMN_MSG + " text, "
                + COLUMN_MSG_TYPE + " text, "
                + COLUMN_CLICKS + " text, "
                + COLUMN_TIMESTAMP + " text, "
                + COLUMN_USERID + " text, "
                + COLUMN_FILEID + " text);";

        private int i;

        public ClickinDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(TAG,
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data"
            );
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATRECORD);
            onCreate(db);
        }


        public synchronized SQLiteDatabase openDataBase() throws SQLException {
            // Open the database
            if (dbObj == null) {
                dbObj = getWritableDatabase();
            }
            return dbObj;
        }

        @Override
        public int addChatList(ArrayList<ChatRecordBeen> chatList) throws SQLException {

            if (dbObj == null )
                openDataBase();

        try {

            if(chatList.size()>20){
                int limit=0;

                    limit=chatList.size()-20;
                Log.e(TAG,"----i..>"+limit);
                for(int k = (chatList.size()-1) ;k>(limit);k--) {
                    ChatRecordBeen chat = chatList.get(k);
                    ContentValues contentValues = new ContentValues();
                    Log.e(TAG,"----i..>"+k);
                    contentValues.put(COLUMN_USERID, chat.getUserId());
                    contentValues.put(COLUMN_SID, chat.getSenderQbId());
                    contentValues.put(COLUMN_RID, chat.getRecieverQbId());
                    contentValues.put(COLUMN_MSGID, chat.getMessageId());
                    contentValues.put(COLUMN_MSG, chat.getChatText());
                    contentValues.put(COLUMN_MSG_TYPE, chat.getChatType());
                    contentValues.put(COLUMN_CLICKS, chat.getClicks());
                    contentValues.put(COLUMN_TIMESTAMP, chat.getTimeStamp());
                    contentValues.put(COLUMN_FILEID, chat.getChatImageUrl());
                    dbObj.insert(TABLE_CHATRECORD, null, contentValues);
                }
            }else{
                long naa=0;
                for(int j = 0;j<chatList.size();j++) {
                    ChatRecordBeen chat = chatList.get(j);
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(COLUMN_SID, chat.getSenderQbId());
                    contentValues.put(COLUMN_USERID, chat.getUserId());
                    contentValues.put(COLUMN_RID, chat.getRecieverQbId());
                    contentValues.put(COLUMN_MSGID, chat.getMessageId());
                    contentValues.put(COLUMN_MSG, chat.getChatText());
                    contentValues.put(COLUMN_MSG_TYPE, chat.getChatType());
                    contentValues.put(COLUMN_CLICKS, chat.getClicks());
                    contentValues.put(COLUMN_TIMESTAMP, chat.getTimeStamp());
                    contentValues.put(COLUMN_FILEID, chat.getChatImageUrl());
                    naa=dbObj.insert(TABLE_CHATRECORD, null, contentValues);
                }
            }

        } catch (Exception exception) {
                    Log.e(TAG, exception.getMessage().toString());
                    return -1;
                }

            return 1;
        } 



        @Override
        public ArrayList<ChatRecordBeen> getAllChat(String sQbId, String rQbId) throws SQLException {

            if (dbObj == null || !dbObj.isOpen())
                openDataBase();

            ArrayList<ChatRecordBeen> chatList = new ArrayList<ChatRecordBeen>();
            ChatRecordBeen chat;
           //String selectUserChats = "SELECT  * FROM " + TABLE_CHATRECORD + " ORDER BY "+COLUMN_TIMESTAMP +" DESC  WHERE ("+ COLUMN_SID + " = "+sQbId+" AND " +COLUMN_RID +" = "+rQbId+" ) OR ( "+ COLUMN_SID + " = "+rQbId+" AND " +COLUMN_RID +" = "+sQbId +" )";
            //String selectUserChats = "SELECT * FROM " + TABLE_CHATRECORD +" WHERE("+ COLUMN_SID + "="+sQbId+" AND " +COLUMN_RID +"="+rQbId+") OR ("+ COLUMN_SID + "="+rQbId+" AND " +COLUMN_RID +" = "+sQbId +")";
            String selectUserChats = "SELECT * FROM " + TABLE_CHATRECORD +  " WHERE("+ COLUMN_SID + "="+sQbId+" AND " +COLUMN_RID +"="+rQbId+") OR ("+ COLUMN_SID + "="+rQbId+" AND " +COLUMN_RID +" = "+sQbId +")"+" ORDER BY "+COLUMN_TIMESTAMP;
            Log.e(TAG,"selectUserChats--> "+selectUserChats);

            Cursor chatCursor = dbObj. rawQuery( selectUserChats, null );
            int si=chatCursor.getCount();

            if (chatCursor.moveToFirst()) {
                do{
                    chat = new ChatRecordBeen();
                    chat.setSenderQbId(chatCursor.getString(chatCursor.getColumnIndex(COLUMN_SID)));
                    chat.setUserId(chatCursor.getString(chatCursor.getColumnIndex(COLUMN_USERID)));
                    chat.setRecieverQbId(chatCursor.getString(chatCursor.getColumnIndex(COLUMN_RID)));
                    chat.setMessageId(chatCursor.getString(chatCursor.getColumnIndex(COLUMN_MSGID)));
                    chat.setChatText(chatCursor.getString(chatCursor.getColumnIndex(COLUMN_MSG)));
                    chat.setChatType(chatCursor.getString(chatCursor.getColumnIndex(COLUMN_MSG_TYPE)));
                    chat.setClicks(chatCursor.getString(chatCursor.getColumnIndex(COLUMN_CLICKS)));
                    chat.setTimeStamp(chatCursor.getString(chatCursor.getColumnIndex(COLUMN_TIMESTAMP)));
                    chat.setChatImageUrl(chatCursor.getString(chatCursor.getColumnIndex(COLUMN_FILEID)));

                    Log.e(TAG,""+chat.getChatType()+"--"+chat.getSenderQbId()+"--"+chat.getRecieverQbId()+"--"+chat.getMessageId()+"--"+chat.getChatText()+"--"+chat.getClicks()+chat.getTimeStamp()+"--"+chat.getChatImageUrl());

                    chatList.add(chat);
                }
                while(chatCursor.moveToNext());

            }
            return chatList;
        }

        @Override
        public int deleteChat(String sQbId, String rQbId) throws SQLException {

            if (dbObj == null || !dbObj.isOpen())
                openDataBase();

        //String deleteUserChats = "DELETE FROM " + TABLE_CHATRECORD +" WHERE ("+ COLUMN_SID + "="+sQbId+" AND " +COLUMN_RID +"="+rQbId+") OR ("+ COLUMN_SID + "="+rQbId+" AND " +COLUMN_RID +"="+sQbId +")";

            String deleteUserChats  = "( "+ COLUMN_SID + " = "+sQbId+" AND " +COLUMN_RID +" = "+rQbId+" ) OR ( "+ COLUMN_SID + " = "+rQbId+" AND " +COLUMN_RID +" = "+sQbId+" )";
            Log.e(TAG,"deleteUserChats--> "+deleteUserChats);
            dbObj.delete(TABLE_CHATRECORD, deleteUserChats, null);

            return 1;
        }


    }
