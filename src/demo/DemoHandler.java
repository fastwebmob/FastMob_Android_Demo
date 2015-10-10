package demo;

import cn.com.fastweb.maasdk.utils.StatisticsUtil;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

public class DemoHandler implements Callback {
	
	Handler handler;
	
	DemoHandler(Handler handler){
		this.handler = handler;
		StatisticsUtil.setCallBack(this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		if(msg != null){
			Message forword = Message.obtain(msg);
			handler.sendMessage(forword);
		}
		return false;
	}
	
}
