package com.sourcefuse.clickinandroid.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.sourcefuse.clickinapp.R;



public class CustomProgressDialog extends AlertDialog
{
	AnimationDrawable mailAnimation;
	Context _context;
	protected CustomProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		_context = context;


	}

	@Override
	public void show() {

		super.show();



		ImageView iv = new ImageView(_context);

		iv.setScaleType(ScaleType.FIT_XY);
		iv.setBackgroundResource(R.drawable.anim_progress);
		mailAnimation = (AnimationDrawable) iv.getBackground();
		iv.post(new Runnable() {
			public void run() {
				if ( mailAnimation != null ) mailAnimation.start();
			}
		});

		setContentView(iv);

		final float scale = _context.getResources().getDisplayMetrics().density;
		int pixels = (int) (80 * scale + 0.5f);
		WindowManager.LayoutParams lp=getWindow().getAttributes();
		//set transparency of background
		lp.dimAmount=0.0f;  // dimAmount between 0.0f and 1.0f, 1.0f is completely dark
		lp.width = pixels;
		lp.height =  pixels;
		getWindow().setAttributes(lp);
	}



}