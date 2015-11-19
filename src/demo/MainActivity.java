package demo;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.com.fastweb.fwmob.FWMobService;
import cn.com.fastweb.fwmob.FWMobServiceStatus;
import cn.com.fastweb.fwmob.FWMobServiceStatusCallback;
import cn.com.fastweb.fwmob.demo.R;
import cn.com.fastweb.fwmob.utils.StatisticsUtil;

public class MainActivity extends Activity implements OnClickListener,OnItemClickListener{
	
	private final static String URL_DEMO="http://demo.fwmob.com:8080/fastmobdemo/index.html";
	
	private ToggleButton serviceBtn,fullAccBtn;
	
	private AutoCompleteTextView editTextURL;

	private ListView urlListView;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		serviceBtn = (ToggleButton) findViewById(R.id.serviceBtn);
		fullAccBtn = (ToggleButton) findViewById(R.id.fullAccBtn);
		
		editTextURL=(AutoCompleteTextView) findViewById(R.id.txtURL);
		editTextURL.setText(URL_DEMO);
		
		
		URLArrayAdapter<String> adapter = new URLArrayAdapter<String>(this,  
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.http_array));  
		editTextURL.setAdapter(adapter);
		editTextURL.setThreshold(1);
		
		findViewById(R.id.btgo).setOnClickListener(this);
		findViewById(R.id.clear_cache_bt).setOnClickListener(this);
		
		urlListView = (ListView) findViewById(R.id.url_list);
		urlListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,getResources().getStringArray(R.array.url_array)));
		urlListView.setOnItemClickListener(this);
		
		editTextURL.setOnKeyListener(  new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) 
				{
					jumpToLoad(editTextURL.getText().toString());
					return true;
				}
				
				return false;
			}
		});
		
		
		editTextURL.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					jumpToLoad(editTextURL.getText().toString());
                    return true;
				}
				return false;
			}
		});
		
		serviceBtn.setOnClickListener(this);
		fullAccBtn.setOnClickListener(this);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		FWMobService.stopService();
		Process.killProcess(Process.myPid());
		throw new RuntimeException("exit");
	}

	/**
	 * 收起软键盘并设置提示文字
	 */
	public void collapseSoftInputMethod(){
		 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		 imm.hideSoftInputFromWindow(editTextURL.getWindowToken(),0);
	}
	
	private void jumpToLoad(String url) {
		if(TextUtils.isEmpty(url)){
			Toast.makeText(this, "URL为空！", Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, WebViewActivity.class);
		intent.putExtra("url", url);
		startActivity(intent);
		StatisticsUtil.beginTimestmp = System.currentTimeMillis();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		fullAccBtn.setChecked(StatisticsUtil.testAcc == 100);
		serviceBtn.setChecked(FWMobService.isHttpServiceRunning());
		if (!FWMobService.isHttpServiceRunning()) {
			FWMobService.setServiceCallback(fwServicecallback);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.clear_cache_bt:
			StatisticsUtil.size = 0;
			StatisticsUtil.beginTimestmp = System.currentTimeMillis();
			clearWebViewAndCookieCache();
//			webView.loadDataWithBaseURL(null, "","text/html", "utf-8",null);
			break;
		case R.id.btgo:
			String s = editTextURL.getText().toString();
			jumpToLoad(s);
			break;
		case R.id.fullAccBtn:
			if (fullAccBtn.isChecked()) {
				StatisticsUtil.testAcc = 100;
			} else {
				StatisticsUtil.testAcc = 0;
			}
			break;
		case R.id.serviceBtn:
			if (!FWMobService.isHttpServiceRunning()) {
				serviceBtn.setChecked(true);
				FWMobService.startService(this, DemoApplication.ACC_DEV_KEY);
				
				FWMobService.setServiceCallback(fwServicecallback);
			} else {
				serviceBtn.setChecked(false);
				FWMobService.stopService();
				Toast.makeText(MainActivity.this, "已关闭加速服务",Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}
	
	private void clearWebViewAndCookieCache() {

		CookieSyncManager.createInstance(getApplication());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		cookieManager.removeSessionCookie();
		CookieSyncManager.getInstance().sync();

		deleteFilesByDirectory(getApplicationContext().getCacheDir());

		try {
			getApplicationContext().deleteDatabase("webview.db");
			getApplicationContext().deleteDatabase("webviewCache.db");
		} catch (Exception e) {
		}
	}

	private void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String s = getResources().getStringArray(R.array.url_array)[arg2];
		editTextURL.setText(s);
		jumpToLoad(s);
	}  
	
	private FWMobServiceStatusCallback fwServicecallback = new FWMobServiceStatusCallback() {
		public void didGetTcpServiceStatus(FWMobServiceStatus status) {
		}
		
		@Override
		public void didGetHttpServiceStatus(FWMobServiceStatus status) {
			serviceBtn.setChecked(status == FWMobServiceStatus.FWMobServiceStatusSuccessful);
			if (status != FWMobServiceStatus.FWMobServiceStatusInit && status != FWMobServiceStatus.FWMobServiceStatusStopped) {
				Toast.makeText(MainActivity.this, "http service status: " + status, Toast.LENGTH_LONG).show();
			}
		}
	};
}
