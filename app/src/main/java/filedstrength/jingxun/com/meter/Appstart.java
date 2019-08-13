package filedstrength.jingxun.com.meter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import filedstrength.jingxun.com.meter.Utils.CrashHandler;
import filedstrength.jingxun.com.meter.Utils.PermissionsUtil;

public class Appstart extends Activity {
	private Handler mHandler;
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			"android.permission.READ_EXTERNAL_STORAGE",
			"android.permission.WRITE_EXTERNAL_STORAGE" };
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
		setContentView(R.layout.appstart);
		verifyStoragePermissions(this);


		mHandler = new Handler();
		mHandler.postDelayed(new Runnable(){
			@Override
			public void run(){

				//2s后app跳转至主页，检查app是否打开位置权限
				PermissionsUtil.checkAndRequestPermissions(Appstart.this, new PermissionsUtil.PermissionCallbacks() {
					@Override
					public void onPermissionsGranted() {
						toMainActivity();
					}

					@Override
					public void onPermissionsDenied(int requestCode, List<String> perms) {
						// TODO Auto-generated method stub

					}

				});
			}
		}, 2000);
   }

	public static void verifyStoragePermissions(Activity activity) {

		try {
			//检测是否有写的权限
			int permission = ActivityCompat.checkSelfPermission(activity,
					"android.permission.WRITE_EXTERNAL_STORAGE");
			if (permission != PackageManager.PERMISSION_GRANTED) {
				// 没有写的权限，去申请写的权限，会弹出对话框
				ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 跳转至MainActivity
	 */
	private void toMainActivity() {
		Intent intent = new Intent (Appstart.this, MainActivity.class);
		startActivity(intent);
		Appstart.this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mHandler!=null){
			mHandler.removeCallbacksAndMessages(null);
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == PermissionsUtil.REQUEST_STATUS_CODE) {

			if (permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {//位置权限
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//同意
					PermissionsUtil.checkAndRequestPermissions(this, new PermissionsUtil.PermissionCallbacks() {
						@Override
						public void onPermissionsGranted() {
							toMainActivity();
						}

						@Override
						public void onPermissionsDenied(int requestCode, List<String> perms) {

						}

					});//请求
				} else {//不同意
					createLoadedAlertDialog(getString(R.string.LocationDialogHead)+ getString(R.string.app_name) +getString(R.string.LocationDialogTail));
				}
			}

			if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//电话权限
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//同意
					PermissionsUtil.checkAndRequestPermissions(this, new PermissionsUtil.PermissionCallbacks() {
						@Override
						public void onPermissionsGranted() {
							toMainActivity();
						}

						@Override
						public void onPermissionsDenied(int requestCode, List<String> perms) {

						}

					});
				} else {//不同意
					createLoadedAlertDialog(getString(R.string.PhoneDialogHead) + getString(R.string.app_name) + getString(R.string.PhoneDialogTail));
				}
			}

//			if (permissions[0].equals(Manifest.permission.CAMERA)) {//相机权限
//				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//同意
//					//所有权限均获取
//					toMainActivity();
//				} else {//不同意
//					createLoadedAlertDialog(getString(R.string.CameraDialogHead)+ getString(R.string.app_name) +getString(R.string.CameraDialogTail));
//				}
//			}
		}

	}

	public void createLoadedAlertDialog(String title) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();

		if (!dialog.isShowing()) {
			dialog.show();
		}
		dialog.setCancelable(true);
		final Window window = dialog.getWindow();

		window.setContentView(R.layout.alert_dialog);
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		TextView titleTv = (TextView) window.findViewById(R.id.title_tv);//内容
		TextView titleNoticeTv = (TextView) window.findViewById(R.id.title_notice_tv);//标题
		titleNoticeTv.setText(R.string.Apply);
		titleTv.setText(title);
		TextView cancelTv = (TextView) window.findViewById(R.id.cancel_tv); // 取消点击
		TextView okTv = (TextView) window.findViewById(R.id.ok_tv); // 确认点击

		cancelTv.setText(R.string.Cancel);
		okTv.setText(R.string.Settings);

		// #1 取消键
		cancelTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				finish();
			}
		});
		// #2 确认键
		okTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", getPackageName(), null);
				intent.setData(uri);
				startActivityForResult(intent, PermissionsUtil.REQUEST_PERMISSION_SETTING);
				finish();
				dialog.cancel();
			}

		});
	}
	
}