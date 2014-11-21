package com.sourcefuse.clickinandroid.dbhelper;

import com.sourcefuse.clickinandroid.model.bean.ChatMessageBody;
import com.sourcefuse.clickinandroid.model.bean.ChatRecordBeen;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mukesh on 8/10/14.
 */
public interface ChatRecordI {

    public int addChatList(ArrayList<ChatMessageBody> chatList) throws SQLException;
    public ArrayList<ChatMessageBody> getAllChat(String sQbId, String rQbId) throws SQLException;
    public int deleteChat(String sQbId, String rQbId) throws SQLException;


  /*  public void deleteAll()
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_NAME,null,null);
        //db.execSQL("delete * from"+ TABLE_NAME);
        db.execSQL("TRUNCATE table" + TABLE_NAME);
        db.close();
    }*/

}
