FastMobile SDK开发者文档（Android）===1、	支持系统版本
---Android 2.3及以上版本。
2、	服务申请
---向Fastweb技术人员申请开通账号和开发者Key。

3、	SDK使用方式
---3.1集成SDK
将fastmobile_V1.0.0.jar放到工程“libs”目录(若工程中无libs目录,请创建此目录并引用此lib）
3.2 AndroidManifest.xml中添加网络权限
    <uses-permission android:name="android.permission.INTERNET" />    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />3.3 初始化本机加速代理
在Application 文件 或在 Activity文件中，在使用加速之前，开启本机加速代理服务。开启本地加速代理服务：

```json	FWMobService.startService(Context context,String devKey);```
第一个参数context通常app context，第二个参数为从网站申请的“开发者密钥”。

注意：startServer只需执行一次，推荐在Application类中执行。在程序关闭的时侯再关闭本地加速代理，代码：

	FWMobService.stopServer ();3.4　HttpURLConnection加速
所有使用UrlConnection的类，不需要修改原来的代码，即可实现全局加速。

UrlConnection会自动通过FastMobile Service开启的加速访问网络。 3.5　HttpClient加速
 HttpClient类不能设定全局加速，只能设置其实例的代理参数实现加速。用法如下：
* a.使用Android原生HttpClient

```json	HttpClient client = new DefaultHttpClient();	HttpHost proxyHost = 		new HttpHost(FWMobService.getHost(), FWMobService.getPort());	client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,proxyHost);
```
* b.使用第三方库commons-httpclient包中HttpClient

```json	import org.apache.commons.httpclient.HttpClient;
		HttpClient httpClient = new HttpClient();	httpClient.getHostConfiguration.setProxy(FWMobService.getHost(), FWMobService.getPort());```
3.6 FWMob服务状态回调
如果有需要可以监控FWMob状态的回调。

* didGetHttpServiceStatus：http服务状态改变
* didGetTcpServiceStatus：TCP服务状态改变

状态有如下几种：

* FWMobServiceStatusInit：服务初始化
* FWMobServiceStatusSuccessful：服务开启成功
* FWMobServiceStatusStopped：服务已停止
* FWMobServiceStatusFailed：服务开启失败