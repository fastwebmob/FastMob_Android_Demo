package demo;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import cn.com.fastweb.fwmob.demo.R;
import cn.com.fastweb.fwmob.utils.StatisticsUtil;

public class WebViewActivity extends Activity implements OnClickListener {

	private WebView webView;

	private TextView sizeTx, timeTx;

	private Button clearBt;

	long begin;
	long end;

	DemoHandler mDemoHandler;

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();  
		if(actionBar!=null){
			actionBar.setCustomView(R.layout.action_bar);  
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  
			actionBar.setDisplayShowCustomEnabled(true);  
			actionBar.getCustomView().findViewById(R.id.actionbar_back).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					finish();
				}
			});
		}

		setContentView(R.layout.activity_web);
		
		mDemoHandler = new DemoHandler(new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case StatisticsUtil.MSG_UPDATE_SIZE:
					sizeTx.setText((CharSequence) msg.obj);
					break;
				case StatisticsUtil.MSG_UPDATE_TIME:
					timeTx.setText((CharSequence) msg.obj);
					break;
				default:
					break;
				}
			};
		});
		webView = (WebView) findViewById(R.id.webView1);

		sizeTx = (TextView) findViewById(R.id.size_tx);
		timeTx = (TextView) findViewById(R.id.time_tx);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.clearCache(true);

		clearBt = (Button) findViewById(R.id.time_clear);
		clearBt.setOnClickListener(this);
		
		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				Log.i("tag", "url=" + url);
				Log.i("tag", "userAgent=" + userAgent);
				Log.i("tag", "contentDisposition=" + contentDisposition);
				Log.i("tag", "mimetype=" + mimetype);
				Log.i("tag", "contentLength=" + contentLength);
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		webView.setWebViewClient(new WebViewClient() {
			// 点击网页中按钮时，在原页面打开
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// handler.cancel(); // Android默认的处理方式
				handler.proceed(); // 接受所有网站的证书
				// handleMessage(Message msg); // 进行其他处理
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				StatisticsUtil.beginTimestmp = System.currentTimeMillis();
				begin = System.currentTimeMillis();
			}

			// 页面加载完成后执行
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				end = System.currentTimeMillis();
			}

		});

		if (getIntent() == null
				|| TextUtils.isEmpty(getIntent().getStringExtra("url"))) {
			finish();
		}
		loadUrl(getIntent().getStringExtra("url"));

		SlidingDrawer mDrawer = (SlidingDrawer) findViewById(R.id.drawer);
		mDrawer.close();
	}

	private void loadUrl(String url) {
		webView.loadUrl(url);
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
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

	private void doClear() {
		StatisticsUtil.size = 0;
		StatisticsUtil.beginTimestmp = System.currentTimeMillis();
		sizeTx.setText("");
		timeTx.setText("");
		clearWebViewAndCookieCache();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.time_clear:
			doClear();
			break;

		default:
			break;
		}

	}
	
	@Override
	protected void onDestroy() {
		webView.stopLoading();
		super.onDestroy();
	}
	

}
