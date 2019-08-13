package filedstrength.jingxun.com.meter.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限控制工具类：
 * 为了适配API23，即Android M 在清单文件中配置use permissions后，还要在程序运行的时候进行申请。
 * <p/>
 * ***整个权限的申请与处理的过程是这样的：
 * *****1.进入主Activity，首先申请所有的权限；
 * *****2.用户对权限进行授权，有2种情况：
 * ********1).用户Allow了权限，则表示该权限已经被授权，无须其它操作；
 * ********2).用户Deny了权限，则下次启动Activity会再次弹出系统的Permisssions申请授权对话框。
 * *****3.如果用户Deny了权限，那么下次再次进入Activity，会再次申请权限，这次的权限对话框上，会有一个选项“dont ask me again”：
 * ********1).如果用户勾选了“dont ask me again”的checkbox，下次启动时就必须自己写Dialog或者Snackbar引导用户到应用设置里面去手动授予权限；
 * ********2).如果用户未勾选上面的选项，若选择了Allow，则表示该权限已经被授权，无须其它操作；
 * ********3).如果用户未勾选上面的选项，若选择了Deny，则下次启动Activity会再次弹出系统的Permisssions申请授权对话框。
 */
public class PermissionsUtil {

    // 状态码、标志位
    public static final int REQUEST_STATUS_CODE = 0x001;
    public static final int REQUEST_PERMISSION_SETTING = 0x002;

    //常量字符串数组，将需要申请的权限写进去，同时必须要在Androidmanifest.xml中声明。
    public static String[] PERMISSIONS_GROUP_SORT = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.CAMERA
    };

    private static PermissionCallbacks callbacks;

    public interface PermissionCallbacks {

        void onPermissionsGranted();//权限都有

        void onPermissionsDenied(int requestCode, List<String> perms);

    }

    public static void checkAndRequestPermissions(final Activity activity, PermissionCallbacks callback) {
        callbacks = callback;
        if (Build.VERSION.SDK_INT >= 23) {
//            callbacks = callback;
            // 一个list，用来存放没有被授权的权限
            ArrayList<String> denidArray = new ArrayList<>();

            // 遍历PERMISSIONS_GROUP，将没有被授权的权限存放进denidArray
            for (String permission : PERMISSIONS_GROUP_SORT) {
                int grantCode = ActivityCompat.checkSelfPermission(activity, permission);
                if (grantCode == PackageManager.PERMISSION_DENIED) {
                    Log.i("Permission", "-----PERMISSION_DENIED---permission:"+permission);
                    denidArray.add(permission);
                }
            }

            Log.i("Permission", "denidArray.size():"+denidArray.size());
            // 如果该字符串数组长度大于0，说明有未被授权的权限
            if (denidArray.size() > 0) {
                //循环处理所有未授权的权限，每次只添加一个权限进行获取
                ArrayList<String> denidArrayNew = new ArrayList<>();
                denidArrayNew.add(denidArray.get(0));
                // 将denidArray转化为字符串数组，方便下面调用requestPermissions来请求授权
                String[] denidPermissions = denidArrayNew.toArray(new String[denidArrayNew.size()]);
                requestPermissions(activity, denidPermissions);
            } else {
                //已授权
                callbacks.onPermissionsGranted();
            }

        }else {
            //已授权
            callbacks.onPermissionsGranted();
        }
    }

    /**
     * 关于shouldShowRequestPermissionRationale函数的一点儿注意事项：
     * ***1).应用安装后第一次访问，则直接返回false；
     * ***2).第一次请求权限时，用户Deny了，再次调用shouldShowRequestPermissionRationale()，则返回true；
     * ***3).第二次请求权限时，用户Deny了，并选择了“dont ask me again”的选项时，再次调用shouldShowRequestPermissionRationale()时，返回false；
     * ***4).设备的系统设置中，禁止了应用获取这个权限的授权，则调用shouldShowRequestPermissionRationale()，返回false。
     */
    public static boolean showRationaleUI(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * 对权限字符串数组中的所有权限进行申请授权，如果用户选择了“dont ask me again”，则不会弹出系统的Permission申请授权对话框
     */
    public static void requestPermissions(Activity activity, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_STATUS_CODE);
    }

    /**
     * 用来判断，App是否是首次启动：
     * ***由于每次调用shouldShowRequestPermissionRationale得到的结果因情况而变，因此必须判断一下App是否首次启动，才能控制好出现Dialog和SnackBar的时机
     */
    public static boolean isAppFirstRun(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (sp.getBoolean("first_run", true)) {
            editor.putBoolean("first_run", false);
            editor.commit();
            return true;
        } else {
            editor.putBoolean("first_run", false);
            editor.commit();
            return false;
        }
    }
    
}
