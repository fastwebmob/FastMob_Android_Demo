package demo;

import cn.com.fastweb.fwmob.demo.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Window;
/**
 * Splash
 * 
 * */
public class SplashActivity extends Activity{
	
	public static final int MSG_INIT_OK = 1;
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_INIT_OK:
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				break;

			default:
				break;
		}
	}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		mHandler.sendEmptyMessageDelayed(MSG_INIT_OK, 2 * 1000);
	}
}
