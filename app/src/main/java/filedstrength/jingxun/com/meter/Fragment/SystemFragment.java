package filedstrength.jingxun.com.meter.Fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import filedstrength.jingxun.com.meter.Constant.Constant;
import filedstrength.jingxun.com.meter.Constant.SendCommendHelper;
import filedstrength.jingxun.com.meter.R;
import filedstrength.jingxun.com.meter.Utils.CHexConver;

/**
 * Created by admin on 13-11-23.
 */
public class SystemFragment extends BaseFragment {

    private TextView mcu_version_tv;
    private TextView app_version_tv;
    private TextView fpga_version_tv;
    private ReceiveDataThread mReceiveDataThread;
    private String TAG = "SystemFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.systemfragment, null);
        init(view);
        return view;
    }

    private void init(View view) {

        app_version_tv = (TextView) view.findViewById(R.id.app_version_tv);
        fpga_version_tv = (TextView) view.findViewById(R.id.fpga_version_tv);
        mcu_version_tv = (TextView) view.findViewById(R.id.mcu_version_tv);
        app_version_tv.setText(getAppVersionName(getActivity()));

        mcu_version_tv.setText(Constant.mcu_version);
        fpga_version_tv.setText(Constant.fpga_version);
    }

    @Override
    public void onResume() {
        super.onResume();
        //接收蓝牙数据
        startReceiveData();
        mHandler.postDelayed(getMcuVersion, 600);
//        mHandler.postDelayed(getFpgaVersion, 3500);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiveDataThread != null) {
            mReceiveDataThread.stopRunnable();
            mReceiveDataThread = null;
        }
    }

    Runnable getMcuVersion = new Runnable() {
        @Override
        public void run() {
            sendCommand(SendCommendHelper.getMcuVer());
        }
    };

    Runnable getFpgaVersion = new Runnable() {
        @Override
        public void run() {
            sendCommand(SendCommendHelper.getFpgaVer());
        }
    };

    private void sendCommand(String str)
    {
        String cmd = CHexConver.str2HexStr(str)+"0D0A";
        if (isConnect()){
            int i;
            if (cmd.length() <= 0)
                return; /* Loop/switch isn't completed */
            if (mOutputMode != 0)
            {
                Log.i(TAG, "-----mOutputMode != 0-----");
                byte byte0 = mOutputMode;
                i = 0;
                if (1 == byte0)
                {
                    Log.i(TAG, "-----1 == byte0-----");
                    if (CHexConver.checkHexStr(cmd))
                    {
                        Log.i(TAG, "-----connect_ok-----");
                        i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
                    }
                    else
                    {
                        i = 0;
                    }
                }
            }
            else
                i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
            if (i < 0) {
                Log.i(TAG, "-----SendBytesErr-----");
            }
        }else{
            mHandler.sendEmptyMessage(100);

        }
    }

    //获取App版本
    public static String getAppVersionName(Context context) {
        String versionName = "";

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;

            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        return versionName;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String str = getString(R.string.msg_connect_ok) + "\r\n";
                    break;
                case 2:
                    String data = msg.getData().getString("str");
                    Log.i(TAG, "----version--data: " + data);
                    if (data.length() >= 6) {
                        if (data.substring(0, 4).equals("0A01")) {
                            int data1 = Integer.parseInt(data.substring(6,8), 16);
                            int high1 = getHeight3((byte)data1);
                            int low1 = getLow5((byte)data1);
                            int data2 = Integer.parseInt(data.substring(4,6), 16);
                            int high2 = getHeight4((byte)data2);
                            int low2 = getLow4((byte)data2);
                            mcu_version_tv.setText((low2+2011)+String.format("%02d", high2)+String.format("%02d", low1));
                            Constant.mcu_version = (low2+2011)+String.format("%02d", high2)+String.format("%02d", low1);
                        } else if (data.substring(1, 5).equals("0A02")) {
                            int data1 = Integer.parseInt(data.substring(7,9), 16);
                            int high1 = getHeight3((byte)data1);
                            int low1 = getLow5((byte)data1);
                            int data2 = Integer.parseInt(data.substring(5,7), 16);
                            int high2 = getHeight4((byte)data2);
                            int low2 = getLow4((byte)data2);
                            fpga_version_tv.setText((low2+2011)+String.format("%02d", high2)+String.format("%02d", low1));
                            Constant.fpga_version = (low2+2011)+String.format("%02d", high2)+String.format("%02d", low1);
                        }else if (data.substring(0, 4).equals("0A02")){
                            int data1 = Integer.parseInt(data.substring(6,8), 16);
                            int high1 = getHeight3((byte)data1);
                            int low1 = getLow5((byte)data1);
                            int data2 = Integer.parseInt(data.substring(4,6), 16);
                            int high2 = getHeight4((byte)data2);
                            int low2 = getLow4((byte)data2);
                            fpga_version_tv.setText((low2+2011)+String.format("%02d", high2)+String.format("%02d", low1));
                            Constant.fpga_version = (low2+2011)+String.format("%02d", high2)+String.format("%02d", low1);
                        }
                    }


                    break;
                case 3:
                    mcu_version_tv.setText(msg.getData().getString("str"));
                    break;
                case 100:
//                    Toast.makeText(getActivity(), getString(R.string.msg_Bluetooth_conn_lost), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    private void startReceiveData() {
        if (mReceiveDataThread == null) {
            mReceiveDataThread = new ReceiveDataThread();
            mReceiveDataThread.startRunable();
            mReceiveDataThread.start();
        }
    }

    private class ReceiveDataThread extends Thread {
        boolean isStopRunnable = false;

        public ReceiveDataThread() {
        }

        public void startRunable() {
            isStopRunnable = false;
        }

        public void stopRunnable() {
            isStopRunnable = true;
        }

        @Override
        public void run() {
            if (isConnect()) {
                int i;
                byte bytebuf[] = new byte[200];
                String str;
                while (!isStopRunnable) {
                    i = ReceiveData(bytebuf);
                    if (i <= 0)
                        break;
                    if (mInputMode == 0) {
                        str = new String(bytebuf, 0, i);
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("str", str);
                        message.setData(bundle);
                        message.what = 2;
                        mHandler.sendMessage(message);

                    } else if (1 == mInputMode) {
                        str = (new StringBuilder(String.valueOf(CHexConver.byte2HexStr(bytebuf, i)))).append(" ").toString();
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("str", str);
                        message.setData(bundle);//bundle传值
                        message.what = 3;
                        mHandler.sendMessage(message);

                    }
                }
            }

        }
    }


    public static int getHeight4(byte data){//获取高四位
        int height;
        height = ((data & 0xf0) >> 4);
        return height;
    }

    public static int getLow4(byte data){//获取低四位
        int low;
        low = (data & 0x0f);
        return low;
    }

    public static int getHeight3(byte data){//获取高三位
        int height;
        height = ((data & 0xe0) >> 5);
        return height;
    }

    public static int getLow5(byte data){//获取低五位
        int low;
        low = (data & 0x1f);
        return low;
    }

}
