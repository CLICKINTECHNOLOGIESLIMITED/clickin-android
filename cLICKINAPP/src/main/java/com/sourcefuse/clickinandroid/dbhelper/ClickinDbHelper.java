package com.sourcefuse.clickinandroid.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mukesh on 8/10/14.
 */


public class ClickinDbHelper extends SQLiteOpenHelper implements ChatRecordI {

    public static final String TABLE_CHATRECORD = "ChatRecord";
    public static final String COLUMN_ID = "_id";
    public static final String partnerQbId = "partnerQbId";
    public static final String textMsg = "textMsg";
    public static final String clicks = "clicks";
    public static final String chatType = "chatType";
    public static final String content_url = "content_url";
    public static final String imageRatio = "imageRatio";
    //Card parameter start
    public static final String card_owner = "card_owner";
    public static final String card_content = "card_content";
    public static final String is_CustomCard = "is_CustomCard";
    public static final String card_DB_ID = "card_DB_ID";
    public static final String card_Accepted_Rejected = "card_Accepted_Rejected";
    public static final String card_heading = "card_heading";
    public static final String card_url = "card_url";
    public static final String card_id = "card_id";
    public static final String card_Played_Countered = "card_Played_Countered";
    public static final String card_originator = "card_originator";
    // Card parameter end
    public static final String video_thumb = "video_thumb";
    public static final String chatId = "chatId";
    public static final String sentOn = "sentOn";
    public static final String location_coordinates = "location_coordinates";
    public static final String sharedMessage = "sharedMessage";
    public static final String isDelivered = "isDelivered";
    public static final String deliveredChatId = "deliveredChatId";
    public static final String relationshipId = "relationshipId";
    public static final String userId = "userId";
    public static final String senderUserToken = "senderUserToken";
    public static final String senderQbId = "senderQbId";
    public static final String originalMessageID = "originalMessageID";
    private static final String DATABASE_CREATE = " CREATE TABLE "
            + TABLE_CHATRECORD + "(" + COLUMN_ID + " integer primary key autoincrement,"
            + partnerQbId + " text, "
            + textMsg + " text, "
            + clicks + " text, "
            + chatType + " text, "
            + content_url + " text, "
            + imageRatio + " text, "
            + card_owner + " text, "
            + card_content + " text, "
            + is_CustomCard + " text, "
            + card_DB_ID + " text, "
            + card_Accepted_Rejected + " text, "
            + card_heading + " text, "
            + card_url + " text, "
            + card_id + " text, "
            + originalMessageID + " text, "
            + card_Played_Countered + " text, "
            + card_originator + " text, "
            + video_thumb + " text, "
            + chatId + " text, "
            + sentOn + " text, "
            + location_coordinates + " text, "
            + sharedMessage + " text, "
            + isDelivered + " text, "
            + deliveredChatId + " text, "
            + relationshipId + " text, "
            + userId + " text, "
            + senderUserToken + " text, "
            + senderQbId + " text);";
    private static final String DATABASE_NAME = "ClickInChatRecords.sqlite";
    private static final int DATABASE_VERSION = 2;
    public static SQLiteDatabase dbObj;
    private String TAG = ClickinDbHelper.class.getName();


    public ClickinDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //   super.onCreate(database);
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
    public int addChatList(ArrayList<ChatMessageBody> chatList) throws SQLException {
        int numRecordInsert = 0;
        if (dbObj == null)
            openDataBase();

        int limit = 0;
        int size = chatList.size();
        if (size > 20)
            limit = size - 20;
        for (int k = (size - 1); k >= limit; k--) {
            ChatMessageBody chat = chatList.get(k);
            ContentValues contentValues = new ContentValues();
            contentValues.put(partnerQbId, chat.partnerQbId);
            contentValues.put(textMsg, chat.textMsg);
            contentValues.put(clicks, chat.clicks);
            contentValues.put(chatType, chat.chatType);
            contentValues.put(content_url, chat.content_url);
            contentValues.put(imageRatio, chat.imageRatio);
            // card parameter start
            contentValues.put(card_owner, chat.card_owner);
            contentValues.put(card_content, chat.card_content);
            contentValues.put(is_CustomCard, Boolean.toString(chat.is_CustomCard));
            contentValues.put(card_DB_ID, chat.card_DB_ID);
            contentValues.put(card_Accepted_Rejected, chat.card_Accepted_Rejected);
            contentValues.put(card_heading, chat.card_heading);
            contentValues.put(card_url, chat.card_url);
            contentValues.put(card_id, chat.card_id);
            contentValues.put(originalMessageID, chat.originalMessageID);
            contentValues.put(card_Played_Countered, chat.card_Played_Countered);
            contentValues.put(card_originator, chat.card_originator);
            // card parameter end
            contentValues.put(video_thumb, chat.video_thumb);
            contentValues.put(chatId, chat.chatId);
            contentValues.put(sentOn, chat.sentOn);
            contentValues.put(location_coordinates, chat.location_coordinates);
            contentValues.put(sharedMessage, chat.sharedMessage);
            contentValues.put(isDelivered, chat.isDelivered);
            contentValues.put(deliveredChatId, chat.deliveredChatID);

            contentValues.put(relationshipId, chat.relationshipId);
            contentValues.put(userId, chat.userId);
            contentValues.put(senderUserToken, chat.senderUserToken);
            contentValues.put(senderQbId, chat.senderQbId);
            long n = dbObj.insert(TABLE_CHATRECORD, null, contentValues);
            numRecordInsert++;
        }
        return numRecordInsert;
    }


    @Override
    public ArrayList<ChatMessageBody> getAllChat(String rId) throws SQLException {

        if (dbObj == null || !dbObj.isOpen())
            openDataBase();

        ArrayList<ChatMessageBody> chatList = new ArrayList<ChatMessageBody>();
        ChatMessageBody chat;
        //String selectUserChats = "SELECT  * FROM " + TABLE_CHATRECORD + " ORDER BY "+COLUMN_TIMESTAMP +" DESC  WHERE ("+ COLUMN_SID + " = "+sQbId+" AND " +COLUMN_RID +" = "+rQbId+" ) OR ( "+ COLUMN_SID + " = "+rQbId+" AND " +COLUMN_RID +" = "+sQbId +" )";
        //String selectUserChats = "SELECT * FROM " + TABLE_CHATRECORD +" WHERE("+ COLUMN_SID + "="+sQbId+" AND " +COLUMN_RID +"="+rQbId+") OR ("+ COLUMN_SID + "="+rQbId+" AND " +COLUMN_RID +" = "+sQbId +")";
        //  String selectUserChats = "SELECT * FROM " + TABLE_CHATRECORD +  " WHERE "+ relationshipId + " = "+rId;

        //Cursor chatCursor = dbObj. rawQuery( selectUserChats, null );
        Cursor chatCursor =
                dbObj.query(TABLE_CHATRECORD, // a. table
                        null, // b. column names
                        " relationshipId = ?", // c. selections
                        new String[]{String.valueOf(rId)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        sentOn, // g. order by
                        null); // h. limit
        int si = chatCursor.getCount();

        if (chatCursor != null) {
            if (chatCursor.moveToFirst()) {
                do {
                    chat = new ChatMessageBody();
                    chat.partnerQbId = chatCursor.getString(chatCursor.getColumnIndex(partnerQbId));
                    chat.textMsg = chatCursor.getString(chatCursor.getColumnIndex(textMsg));
                    chat.clicks = chatCursor.getString(chatCursor.getColumnIndex(clicks));
                    chat.chatType = Integer.parseInt(chatCursor.getString(chatCursor.getColumnIndex(chatType)));
                    chat.content_url = chatCursor.getString(chatCursor.getColumnIndex(content_url));
                    chat.imageRatio = chatCursor.getString(chatCursor.getColumnIndex(imageRatio));
                    // Card Start
                    chat.card_owner = (chatCursor.getString(chatCursor.getColumnIndex(card_owner)));
                    chat.card_content = (chatCursor.getString(chatCursor.getColumnIndex(card_content)));
                    chat.is_CustomCard = Boolean.parseBoolean((chatCursor.getString(chatCursor.getColumnIndex(is_CustomCard))));
                    chat.card_DB_ID = (chatCursor.getString(chatCursor.getColumnIndex(card_DB_ID)));
                    chat.card_Accepted_Rejected = (chatCursor.getString(chatCursor.getColumnIndex(card_Accepted_Rejected)));
                    chat.card_heading = (chatCursor.getString(chatCursor.getColumnIndex(card_heading)));
                    chat.card_url = (chatCursor.getString(chatCursor.getColumnIndex(card_url)));
                    chat.card_id = (chatCursor.getString(chatCursor.getColumnIndex(card_id)));
                    chat.originalMessageID = (chatCursor.getString(chatCursor.getColumnIndex(originalMessageID)));
                    chat.card_Played_Countered = (chatCursor.getString(chatCursor.getColumnIndex(card_Played_Countered)));
                    chat.card_originator = (chatCursor.getString(chatCursor.getColumnIndex(card_originator)));
                    // Card End
                    chat.video_thumb = (chatCursor.getString(chatCursor.getColumnIndex(video_thumb)));
                    chat.chatId = (chatCursor.getString(chatCursor.getColumnIndex(chatId)));
                    chat.sentOn = (chatCursor.getString(chatCursor.getColumnIndex(sentOn)));
                    chat.location_coordinates = (chatCursor.getString(chatCursor.getColumnIndex(location_coordinates)));
                    chat.sharedMessage = (chatCursor.getString(chatCursor.getColumnIndex(sharedMessage)));
                    chat.isDelivered = (chatCursor.getString(chatCursor.getColumnIndex(isDelivered)));
                    chat.deliveredChatID = (chatCursor.getString(chatCursor.getColumnIndex(deliveredChatId)));

                    chat.relationshipId = (chatCursor.getString(chatCursor.getColumnIndex(relationshipId)));
                    chat.userId = (chatCursor.getString(chatCursor.getColumnIndex(userId)));
                    chat.senderUserToken = (chatCursor.getString(chatCursor.getColumnIndex(senderUserToken)));
                    chat.senderQbId = (chatCursor.getString(chatCursor.getColumnIndex(senderQbId)));


                    chatList.add(chat);
                }
                while (chatCursor.moveToNext());
            }
        }
        return chatList;
    }

    @Override
    public int deleteChat(String rId) throws SQLException {

        if (dbObj == null || !dbObj.isOpen())
            openDataBase();

        //String deleteUserChats = "DELETE FROM " + TABLE_CHATRECORD +" WHERE ("+ COLUMN_SID + "="+sQbId+" AND " +COLUMN_RID +"="+rQbId+") OR ("+ COLUMN_SID + "="+rQbId+" AND " +COLUMN_RID +"="+sQbId +")";

      /*  String deleteUserChats  = "( "+ relationshipId + " = "+rId +")";
        dbObj.delete(TABLE_CHATRECORD, deleteUserChats, null);*/

        //monika- correct query to delete chat

        // 2. delete
        dbObj.delete(TABLE_CHATRECORD, //table name
                relationshipId + " = ?",  // selections
                new String[]{rId}); //selections args

        return 1;
    }

    public void clearDB() throws SQLException {
        if (dbObj == null || !dbObj.isOpen())
            openDataBase();
        dbObj.delete(TABLE_CHATRECORD, null, null //table name
        ); //selections args
    }

    //monika-funtion to update msg delivered status in chat

    public int updateDeliverStatusInChat(String msgId) throws SQLException {

        if (dbObj == null || !dbObj.isOpen())
            openDataBase();
        ContentValues values = new ContentValues();
        values.put("deliveredChatId", msgId
        );

        long update = dbObj.update(TABLE_CHATRECORD, values,
                chatId + " =?",
                new String[]{msgId});

        //temp code-monika
        if (update >= 0) {



        } else {


        }

        return 1;
    }

}