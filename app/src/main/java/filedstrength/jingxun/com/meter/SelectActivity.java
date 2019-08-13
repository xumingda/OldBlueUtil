package filedstrength.jingxun.com.meter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;

import filedstrength.jingxun.com.meter.Constant.Constant;
import filedstrength.jingxun.com.meter.Fragment.FragmentFactory;
import filedstrength.jingxun.com.meter.Utils.BluetoothUtils;


public class SelectActivity extends AppCompatActivity {

    private Button btn_old;
    private Button btn_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        btn_old=(Button) findViewById(R.id.btn_old);
        btn_new=(Button) findViewById(R.id.btn_new);
        //屏幕长亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        btn_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.type=0;
                Intent intent=new Intent(SelectActivity.this,Appstart.class);
                startActivity(intent);
            }
        });
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.type=1;
                Intent intent=new Intent(SelectActivity.this,Appstart.class);
                startActivity(intent);
            }
        });
    }









    @Override
    protected void onDestroy() {
        super.onDestroy();
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
