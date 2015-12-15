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

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.rest.client.app.noactivities.AppGuardService;
import com.rest.client.app.noactivities.NetworkChangeReceiver;


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

	public static String URL  = "https://rest-20121015.firebaseio.com";
	private static String AUTH = "IJ0kevPaQaMof0DxBXkwM54DdJ36cWK8wbedkoMe";
	public Firebase DB;
	public boolean  DB_CONNECTED;

	@Override
	public void onCreate() {
		super.onCreate();
		DB_CONNECTED = NetworkChangeReceiver.isNetworkAvailable(Instance);
		startAppGuardService( this );
		Firebase.setAndroidContext( this );
		DB = new Firebase( URL );
		DB.keepSynced( true );
		DB.authWithCustomToken(
				AUTH,
				new AuthResultHandler() {
					@Override
					public void onAuthenticated( AuthData authData ) {

					}

					@Override
					public void onAuthenticationError( FirebaseError firebaseError ) {

					}
				}
		);
	}


	public static void startAppGuardService( Context cxt ) {
		long   scheduleSec = 10800L;//
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


		//		Calendar notifyTime = Calendar.getInstance();
		//		notifyTime.add(
		//				Calendar.MINUTE,
		//				2
		//		);
		//		long   current = System.currentTimeMillis();
		//		long   nextFireWindow = (notifyTime.getTimeInMillis() - current) / 1000;
		//		long   flexSecs       = 30L; // the task can run as early as 10 minutes from the scheduled time
		//		String tag            = System.currentTimeMillis() + "";
		//		OneoffTask onceTask = new OneoffTask.Builder().setService( AppGuardService.class )
		//													  .setExecutionWindow(
		//															  nextFireWindow + flexSecs,
		//															  nextFireWindow + flexSecs * 2
		//													  )
		//													  .setTag( tag )
		//													  .setPersisted( true )
		//													  .setRequiredNetwork( com.google.android.gms.gcm.Task.NETWORK_STATE_ANY )
		//													  .setRequiresCharging( false )
		//													  .build();
		//		GcmNetworkManager.getInstance( cxt )
		//						 .schedule( onceTask );
	}

}
