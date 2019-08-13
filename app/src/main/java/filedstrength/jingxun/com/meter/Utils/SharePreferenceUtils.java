package filedstrength.jingxun.com.meter.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import filedstrength.jingxun.com.meter.Constant.Constant;


/**
 * Created by dh on 2015/7/23.
 */
public class SharePreferenceUtils {
    public static void putInt(Context context, String key, int value){
        putInt(context, Constant.SharePerference,key,value);
    }

    public static void putInt(Context context, String spName, String key, int value){
        SharedPreferences sp=context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    public static  int getIntValue(Context context, String key, int defaultValue){
        return getIntValue(context,Constant.SharePerference,key,defaultValue);
    }

    public static  int getIntValue(Context context, String spName, String key, int defaultValue){
        SharedPreferences sp=context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getInt(key,defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value){
        SharedPreferences sp=context.getSharedPreferences(Constant.SharePerference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static  boolean getBooleanValue(Context context, String key, boolean defaultValue){
        SharedPreferences sp=context.getSharedPreferences(Constant.SharePerference, Context.MODE_PRIVATE);
        return sp.getBoolean(key,defaultValue);
    }
}
