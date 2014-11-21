package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.ContactAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class AddSomeoneView extends Activity implements
        TextWatcher {

      private Button do_latter,do_invited;
      private EditText search_phbook;
      private ListView listView;
      private TextView bottom_text,back,title_top,title_bottom;//akshit code to hide textview
      private ContactAdapter adapter;
      private RelativeLayout showContactlist,layout_back;
      AuthManager authManager;


      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.view_addsomeone);
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

            bottom_text= (TextView)findViewById(R.id.edt_text);//akshit code
            title_bottom=(TextView)findViewById(R.id.title_text_bottom);
            title_top=(TextView)findViewById(R.id.title_text_top);
            do_latter = (Button) findViewById(R.id.btn_do_itlatter);
            do_invited = (Button) findViewById(R.id.btn_been_invited);
            back= (TextView)findViewById(R.id.btn_back);
            layout_back=(RelativeLayout)findViewById(R.id.rl_back);
            search_phbook = (EditText) findViewById(R.id.edt_search_ph);
            listView = (ListView) findViewById(R.id.list_contact);
            showContactlist = (RelativeLayout) findViewById(R.id.rr_con_list);
            search_phbook.addTextChangedListener(this);
            authManager = ModelManager.getInstance().getAuthorizationManager();

            listView.setOnItemClickListener(new OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                                                                              INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(search_phbook.getWindowToken(), 0);

                        Intent intent = new Intent(AddSomeoneView.this, AddViaContactView.class);
                        intent.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
                        intent.putExtra("ConName", Utils.itData.get(position).getConName());

                        //Monika- we need to append counntry code if it doesn't with contact num
                        String phNum = Utils.itData.get(position).getConNumber();
                        if (!(phNum.contains("+")))
                              phNum = authManager.getCountrycode() + phNum;
                        intent.putExtra("ConNumber", phNum);
                        if (!Utils.isEmptyString(Utils.itData.get(position).getConUri())) {
                              intent.putExtra("ConUri", Utils.itData.get(position).getConUri());
                        } else {
                              intent.putExtra("ConUri", "");
                        }
                        startActivity(intent);
                  }
            });

            ((RelativeLayout) findViewById(R.id.btn_add_someone_action)).setOnClickListener(new View.OnClickListener() {

                  @Override
                  public void onClick(View arg0) {

                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                        imm.hideSoftInputFromWindow(search_phbook.getWindowToken(), 0);

                  }

            });



          // akshit code starts

           Bundle data = getIntent().getExtras();
           if(data!=null){
               if(data.containsKey("FromOwnProfile")){
                   boolean mFrom = data.getBoolean("FromOwnProfile");
                   if(mFrom){
                       do_latter.setVisibility(View.GONE);
                       do_invited.setVisibility(View.GONE);
                       bottom_text.setVisibility(View.GONE);
                       layout_back.setVisibility(View.VISIBLE);
                       back.setVisibility(View.VISIBLE);
                       title_top.setText("ADD SOMEONE");
                       title_bottom.setText("TO CLICK WITH");

                   }else{
                       do_latter.setVisibility(View.VISIBLE);
                       do_invited.setVisibility(View.VISIBLE);
                       bottom_text.setVisibility(View.VISIBLE);
                       layout_back.setVisibility(View.GONE);
                       back.setVisibility(View.GONE);
                   }
               }
           }

          //akshit code ends
//          data.containsKey("FromOwnProfile"")
//                    .getBoolean("FromOwnProfile");
//            if (mFrom) {
//                  do_latter.setVisibility(View.GONE);
//                  do_invited.setVisibility(View.GONE);
//                  bottom_text.setVisibility(View.GONE);
//
//            } else {
//                  do_latter.setVisibility(View.VISIBLE);
//                  do_invited.setVisibility(View.VISIBLE);
//                  bottom_text.setVisibility(View.VISIBLE);
//            }

          back.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  finish();
              }
          });

            ((ImageView) findViewById(R.id.iv_keypad)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddSomeoneView.this, AddViaNumberView.class);
                    intent.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
                    startActivity(intent);
                }
            });


            do_latter.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        Intent clickersView = new Intent(AddSomeoneView.this, CurrentClickersView.class);
                        clickersView.putExtra("FromSignup", true);
                        clickersView.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
                        clickersView.putExtra("FromMenu", false);
                        startActivity(clickersView);
                  }
            });

          //akshit code for invited button
          do_invited.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  Intent clickersView = new Intent(AddSomeoneView.this, CurrentClickersView.class);
                  clickersView.putExtra("FromSignup", true);
                  clickersView.putExtra("fromsignup", getIntent().getBooleanExtra("fromsignup", false));
                  clickersView.putExtra("FromMenu", false);
                  startActivity(clickersView);

              }
          });
          //akshit code end

            adapter = new ContactAdapter(this, R.layout.row_contacts, Utils.itData);
            listView.setAdapter(adapter);


      }


      @Override
      public void afterTextChanged(Editable s) {
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
                                    int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (search_phbook.getText().toString().length() > 0) {
                  showContactlist.setVisibility(View.VISIBLE);
                  AddSomeoneView.this.adapter.filter(search_phbook.getText().toString());
            } else {
                  showContactlist.setVisibility(View.GONE);
            }
      }

      @Override
      public void onStart() {
            super.onStart();
            search_phbook.setText("");
            EventBus.getDefault().register(this);
            if (Utils.itData.size() == 0) {
                  Utils.launchBarDialog(this);
                  new FetchContactFromPhone(this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
            }
      }

      @Override
      public void onStop() {
            super.onStop();
            if (EventBus.getDefault().isRegistered(this)) {
                  EventBus.getDefault().unregister(this);
            }
      }

      public void onEventMainThread(String message) {

            authManager = ModelManager.getInstance().getAuthorizationManager();
            if (message.equalsIgnoreCase("CheckFriend True")) {
                  Utils.dismissBarDialog();
                  adapter = new ContactAdapter(this, R.layout.row_contacts, Utils.itData);
                  listView.setAdapter(adapter);

            } else if (message.equalsIgnoreCase("CheckFriend False")) {
                  Utils.dismissBarDialog();
                  //  Utils.showAlert(this,authManager.getMessage());
                  Utils.fromSignalDialog(this, authManager.getMessage());
            } else if (message.equalsIgnoreCase("CheckFriend Network Error")) {
                  Utils.dismissBarDialog();
                  //    Utils.showAlert(this, AlertMessage.connectionError);
                  Utils.fromSignalDialog(this, AlertMessage.connectionError);
            }
      }


}
