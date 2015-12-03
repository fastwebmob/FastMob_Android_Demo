package demo;

import android.app.Application;
import cn.com.fastweb.fwmob.FWMobService;
import cn.com.fastweb.fwmob.logger.FWLogLevel;

public class DemoApplication extends Application {

	public static final String ACC_DEV_KEY= "$2a$08$AUVlxIABlOyOQ51jJBMoz.JE637qd2wtrqKVrdWhkY6/eEA39IDqW";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//应用程序启动时启动FWmobService 
		FWMobService.startService(this, DemoApplication.ACC_DEV_KEY);
		
		// 开启debug模式
		FWMobService.enableDebug(true);
		
		// 设置日志级别 （可直接设置日志界别，DISABLE为关闭）
		FWMobService.setLogLevel(FWLogLevel.VERBOSE);

	}
}
