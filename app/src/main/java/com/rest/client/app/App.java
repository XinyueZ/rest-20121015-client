/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱。

package com.rest.client.app;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.rest.client.app.noactivities.AppGuardService;
import com.rest.client.ds.Client;
import com.rest.client.ds.Response;
import com.rest.client.rest.RestApiManager;
import com.rest.client.rest.RestFireManager;


/**
 * The app-object of the project.
 *
 * @author Xinyue Zhao
 */
public final class App extends MultiDexApplication {
	/**
	 * Singleton.
	 */
	public static App Instance;

	{
		Instance = this;
	}

	private RestFireManager                  mClientRestFireManager  = new RestFireManager( Client.class );
	private RestApiManager<Client, Client>   mClientRestApiManager   = new RestApiManager<>();
	private RestApiManager<Client, Response> mResponseRestApiManager = new RestApiManager<>();

	@Override
	public void onCreate() {
		super.onCreate();
		startAppGuardService( this );
		mClientRestFireManager.init(
				0,
				this
		);


		mResponseRestApiManager.init(
				1,
				this
		);
		mResponseRestApiManager.setUseHistory( true );


		mClientRestApiManager.init(
				2,
				this
		);
		mClientRestApiManager.setUseHistory( false );
	}

	public RestFireManager getClientRestFireManager() {
		return mClientRestFireManager;
	}

	public RestApiManager<Client, Client> getClientRestApiManager() {
		return mClientRestApiManager;
	}

	public RestApiManager<Client, Response> getResponseRestApiManager() {
		return mResponseRestApiManager;
	}

	public static void startAppGuardService( Context cxt ) {
		long   scheduleSec = 60 * 5;
		//long   scheduleSec = 10800L;
		long   flexSecs    = 60L;
		String tag         = System.currentTimeMillis() + "";
		PeriodicTask scheduleTask = new PeriodicTask.Builder().setService( AppGuardService.class )
															  .setPeriod( scheduleSec )
															  .setFlex( flexSecs )
															  .setTag( tag )
															  .setPersisted( true )
															  .setRequiredNetwork( com.google.android.gms.gcm.Task.NETWORK_STATE_ANY )
															  .setRequiresCharging( false )
															  .build();
		GcmNetworkManager.getInstance( cxt )
						 .schedule( scheduleTask );
	}

}
