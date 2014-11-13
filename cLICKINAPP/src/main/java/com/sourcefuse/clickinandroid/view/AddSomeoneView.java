package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.facebook.android.Util;
import com.sourcefuse.clickinandroid.model.AuthManager;
import com.sourcefuse.clickinandroid.model.ModelManager;
import com.sourcefuse.clickinandroid.model.ProfileManager;
import com.sourcefuse.clickinandroid.model.bean.ContactBean;
import com.sourcefuse.clickinandroid.utils.AlertMessage;
import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.ContactAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class AddSomeoneView extends Activity implements View.OnClickListener,
		TextWatcher {

	private Button do_latter;
	private EditText search_phbook;
	private ListView listView;
	private ContactAdapter adapter;
	private RelativeLayout showContactlist;
	private ImageView keyIcon;
    private boolean mFrom = false;
    private Typeface typefaceBold;
    AuthManager authManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_addsomeone);
		this.overridePendingTransition(R.anim.slide_in_right ,R.anim.slide_out_right);
        typefaceBold = Typeface.createFromAsset(AddSomeoneView.this.getAssets(), Constants.FONT_FILE_PATH_AVENIRNEXTLTPRO_BOLD);

		do_latter = (Button) findViewById(R.id.btn_do_itlatter);
		search_phbook = (EditText) findViewById(R.id.edt_search_ph);

		keyIcon = (ImageView) findViewById(R.id.iv_keypad);
		listView = (ListView) findViewById(R.id.list_contact);

		showContactlist = (RelativeLayout) findViewById(R.id.rr_con_list);
        search_phbook.setTypeface(typefaceBold);
		search_phbook.addTextChangedListener(this);
		do_latter.setOnClickListener(this);
		keyIcon.setOnClickListener(this);

         authManager=ModelManager.getInstance().getAuthorizationManager();
     //   new FetchContactFromPhone(this).readContacts();

		
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
			{

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_phbook.getWindowToken(), 0);

				Intent intent = new Intent(AddSomeoneView.this,AddViaContactView.class);
				intent.putExtra("ConName", Utils.itData.get(position).getConName());

                //Monika- we need to append counntry code if it doesn't with contact num
                String phNum=Utils.itData.get(position).getConNumber();
                if(!(phNum.contains("+")))
                    phNum=authManager.getCountrycode()+phNum;
				intent.putExtra("ConNumber", phNum);
				if(!Utils.isEmptyString(Utils.itData.get(position).getConUri())){
					intent.putExtra("ConUri", Utils.itData.get(position).getConUri());
				}else{
					intent.putExtra("ConUri", "");	
				}
				startActivity(intent);
			}
		});

        ((RelativeLayout) findViewById(R.id.btn_add_someone_action)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_phbook.getWindowToken(), 0);

            }

        });


        mFrom = getIntent().getExtras().getBoolean("FromOwnProfile");
        if(mFrom){
            do_latter.setVisibility(View.GONE);
        }else{
            do_latter.setVisibility(View.VISIBLE);
        }


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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_do_itlatter:
			 Intent clickersView = new Intent(AddSomeoneView.this,CurrentClickersView.class);
                   clickersView.putExtra("FromSignup", true);
                   clickersView.putExtra("FromMenu", false);
			 startActivity(clickersView);
         //  finish();
			break;
		case R.id.iv_keypad:
			 Intent intent = new Intent(AddSomeoneView.this,AddViaNumberView.class);
			 startActivity(intent);
			break;
		}
	}



    @Override
    public void onStart() {
        super.onStart();
        search_phbook.setText("");
        EventBus.getDefault().register(this);
        if(Utils.itData.size()==0) {
            Utils.launchBarDialog(this);
            new FetchContactFromPhone(this).getClickerList(authManager.getPhoneNo(), authManager.getUsrToken(), 1);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String message){

        authManager=ModelManager.getInstance().getAuthorizationManager();
        if (message.equalsIgnoreCase("CheckFriend True")) {
            Utils.dismissBarDialog();
            adapter = new ContactAdapter(this, R.layout.row_contacts,Utils.itData);
            listView.setAdapter(adapter);

        } else if (message.equalsIgnoreCase("CheckFriend False")) {
            Utils.dismissBarDialog();
          //  Utils.showAlert(this,authManager.getMessage());
            fromSignalDialog(authManager.getMessage());
        } else if(message.equalsIgnoreCase("CheckFriend Network Error")){
            Utils.dismissBarDialog();
        //    Utils.showAlert(this, AlertMessage.connectionError);
            fromSignalDialog( AlertMessage.connectionError);
        }
    }



    // Akshit Code Starts
    public void fromSignalDialog(String str){

        final Dialog dialog = new Dialog(AddSomeoneView.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.alert_check_dialogs);
        dialog.setCancelable(false);
        TextView msgI = (TextView) dialog.findViewById(R.id.alert_msgI);
        msgI.setText(str);


        Button dismiss = (Button) dialog.findViewById(R.id.coolio);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
// Ends

}
