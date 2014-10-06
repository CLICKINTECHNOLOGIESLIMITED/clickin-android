package com.sourcefuse.clickinandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sourcefuse.clickinandroid.utils.Constants;
import com.sourcefuse.clickinandroid.utils.FetchContactFromPhone;
import com.sourcefuse.clickinandroid.utils.Utils;
import com.sourcefuse.clickinandroid.view.adapter.ContactAdapter;
import com.sourcefuse.clickinapp.R;

import de.greenrobot.event.EventBus;

public class AddSomeoneView extends Activity implements View.OnClickListener,
		TextWatcher {
    private static final String TAG = SignInView.class.getSimpleName();
	private Button do_latter;
	private EditText search_phbook;
	private ListView listView;
	private ContactAdapter adapter;
	private RelativeLayout showContactlist;
	private ImageView keyIcon;
    private boolean mFrom = false;
    private Typeface typefaceBold;


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

        new FetchContactFromPhone(this).readContacts();
		adapter = new ContactAdapter(this, R.layout.row_contacts,Utils.itData);
		listView.setAdapter(adapter);
		
		
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
			{ 
				
				Intent intent = new Intent(AddSomeoneView.this,AddViaContactView.class);
				intent.putExtra("ConName", Utils.itData.get(position).getConName());
				intent.putExtra("ConNumber", Utils.itData.get(position).getConNumber());
				if(!Utils.isEmptyString(Utils.itData.get(position).getConUri())){
					intent.putExtra("ConUri", Utils.itData.get(position).getConUri());
				}else{
					intent.putExtra("ConUri", "");	
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				//finish();
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
           finish();
			break;
		case R.id.iv_keypad:
			 Intent intent = new Intent(AddSomeoneView.this,AddViaNumberView.class);
			 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 startActivity(intent);
			 //finish();
			break;
		}
	}



    @Override
    public void onStart() {
        super.onStart();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEventMainThread(String getMsg){
        Log.d(TAG, "onEventMainThread->"+getMsg);
        if (getMsg.equalsIgnoreCase("SearchResult true")) {
        }
    }


	


}
