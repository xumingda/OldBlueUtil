package filedstrength.jingxun.com.meter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;




import java.util.List;

import filedstrength.jingxun.com.meter.Constant.Constant;
import filedstrength.jingxun.com.meter.Utils.BluetoothUtils;
import filedstrength.jingxun.com.meter.Fragment.FragmentFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;
    private BluetoothUtils mBT;
    private PowerManager.WakeLock wakeLock = null;
    private static final int BASIC_PERMISSION_REQUEST_CODE = 1000;
    private static final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };
    final int REQUEST_WRITE=1;//申请权限的请求码
    private boolean isOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //屏幕长亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Fragment实现主页、配置页、系统信息页切换
        fragmentManager = getFragmentManager();

        radioGroup = (RadioGroup) findViewById(R.id.main_radio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = FragmentFactory.getInstanceByIndex(checkedId);
                transaction.replace(R.id.content, fragment);
                transaction.commit();
            }
        });


        requestBasicPermission();
        initSystemConfig();
        acquire();

    }
    private void requestBasicPermission() {
        boolean isOpen=false;
        for(int i=0;i<BASIC_PERMISSIONS.length;i++){
            if (ContextCompat.checkSelfPermission(this,BASIC_PERMISSIONS[i])
                    == PackageManager.PERMISSION_GRANTED){
                isOpen=true;
            }
        }
        if (!isOpen) {
            ActivityCompat.requestPermissions(this,
                    BASIC_PERMISSIONS, BASIC_PERMISSION_REQUEST_CODE);
        }
    }
    @SuppressLint("InvalidWakeLockTag")
    private void acquire() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "MyService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    private void release() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }


    private void initSystemConfig() {

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = FragmentFactory.getInstanceByIndex(1);
        transaction.replace(R.id.content, fragment);
        transaction.commit();

        //获取屏幕长宽，画图使用
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Constant.SCREEN_WIDTH = display.getWidth();
        Constant.SCREEN_HEIGHT = display.getHeight();

        //开启手机蓝牙
        mBT = new BluetoothUtils();
        mBT.BluetoothOpen();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBT.BluetoothClose();
        release();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_HOME))
                && event.getRepeatCount() == 0) {
            dialog_Exit(this);
        }
        return false;
    }

    public void dialog_Exit(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.confirm_exit);
        builder.setTitle(R.string.point_out);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton(R.string.ensure,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                    }
                });

        builder.setNegativeButton(R.string.remove,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
}
