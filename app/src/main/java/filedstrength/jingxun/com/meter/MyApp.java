package filedstrength.jingxun.com.meter;

import android.app.Application;


import filedstrength.jingxun.com.meter.Utils.CrashHandler;
import filedstrength.jingxun.com.meter.Utils.LogcatHelper;
import filedstrength.jingxun.com.meter.service.LocationService;


public class MyApp extends Application {

    public LocationService locationService;
    public static int type=0;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().initialize(this);
        LogcatHelper.getInstance(this).start();

        locationService = new LocationService(getApplicationContext());
    }


}
