package filedstrength.jingxun.com.meter.Fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import filedstrength.jingxun.com.meter.BluetoothCtrl;
import filedstrength.jingxun.com.meter.Constant.Constant;
import filedstrength.jingxun.com.meter.Constant.SendCommendHelper;
import filedstrength.jingxun.com.meter.MyApp;
import filedstrength.jingxun.com.meter.R;
import filedstrength.jingxun.com.meter.Utils.BluetoothUtils;
import filedstrength.jingxun.com.meter.Utils.CHexConver;
import filedstrength.jingxun.com.meter.Utils.SharePreferenceUtils;
import filedstrength.jingxun.com.meter.Utils.SpeechUtils;
import filedstrength.jingxun.com.meter.Widget.ChartView;
import filedstrength.jingxun.com.meter.service.LocationService;
import filedstrength.jingxun.com.meter.service.MyOrientationListener;

/**
 * Created by admin on 13-11-23.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "MainFragment";
    @SuppressWarnings("unused")
    private static final int SINGLECHART = 1;
    private static final int TOTALCHART = 2;
    private LinearLayout layout_chart;
    private Button btn_single;
    private Button btn_total;
    private Button connectDevice;
    private ChartView chartView;
    private BluetoothUtils mBT;
    private TextView fieldStrength;
    private TextView connectStatus;
    private TextView synState;
    private TextView batteryTv;
    private TextView channelTv;
    private ImageView speakImg;
    private ImageView batteryImg;
    private int display_Tag;
    private long lastClick = 0L;
    private int num1 = 0;
    private int num2 = 1;
    private ReceiveDataThread mReceiveDataThread;
    //定位
    private LocationService locService;
    /**
     * 方向传感器的监听器
     */
    private MyOrientationListener myOrientationListener;
    /**
     * 方向传感器X方向的值
     */
    private int mXDirection;
    private String disPlayTag = "DisPlayTag";

    private Timer mTimer;
    private boolean mbKeyboardMode;
    private boolean isStartSpeaking = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conConnectDevicetainer, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainfragment, null);
        init(view);
        return view;
    }

    //处理蓝牙通讯数据
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (isAdded()) {
                        String str = getResources().getString(R.string.msg_connect_ok) + "\r\n";
                        connectStatus.setText(str);
                    }
                    break;
                case 2:
                    String data = msg.getData().getString("str");
                    Log.i(TAG, "----fieldStrength--data: " + data);
                    if (MyApp.type == 0) {
                        if (isConnect() && data.length() >= 6) {
                            if (data.substring(0, 4).equals("0C01") && data.length() >= 8) {

                                int value = Integer.parseInt(data.substring(4, 6), 16) + 256 * Integer.parseInt(data.substring(6, 8), 16);
                                DecimalFormat df = new DecimalFormat("#.0");
                                String fieldStrengthPow = "-" + df.format(value / 100.0);
                                String fieldStrengthValue = (255 - 2 * value / 100) + "";
//                            value = 11830;
                                Constant.curr_value = value;
                                Log.i(TAG, "----isStartSpeaking: " + isStartSpeaking);
                                Log.i(TAG, "----Constant.isSpeak: " + Constant.isSpeak);
                                Log.i(TAG, "----display_Tag: " + display_Tag);
                                if (Constant.isSpeak) {
                                    if (display_Tag == 1) {
//                                    SpeechUtils.getInstance(getActivity()).speakText("负"+df.format(value / 100.0));

                                    } else {
                                        SpeechUtils.getInstance(getActivity()).speakText(fieldStrengthValue);
                                    }
                                }

                                Log.i(TAG, "----fieldStrength: " + fieldStrengthPow);
                                if (display_Tag == 1) {
                                    fieldStrength.setText(fieldStrengthPow + "dBm");
                                    chartView.setFieldStrength((255 - 2 * value / 100) + "", mXDirection);
                                } else {
                                    fieldStrength.setText(fieldStrengthValue);
                                    chartView.setFieldStrength((255 - 2 * value / 100) + "", mXDirection);
                                }
                            } else if (data.substring(0, 4).equals("0C02")) {
                                int battertValue = Integer.parseInt(data.substring(4, 6), 16);
                                Constant.curr_battery = battertValue;
                                setBatteryImg(battertValue);
                                batteryTv.setText(battertValue + "%");
                            } else if (data.substring(1, 5).equals("0C03") && data.length() >= 7) {
                                if (data.substring(5, 7).equals("00")) {
                                    synState.setText(Constant.synchro + "失步");
                                } else {
                                    synState.setText(Constant.synchro + "同步");
                                }
                            } else if (data.substring(0, 4).equals("0C03")) {
                                if (data.substring(4, 6).equals("00")) {
                                    synState.setText(Constant.synchro + "失步");
                                } else {
                                    synState.setText(Constant.synchro + "同步");
                                }
                            }
                        }
                    } else {
                        byte[] bBuf = (byte[]) msg.obj;
                        String newdate = bytesToHexString(bBuf);

                        if (isConnect()) {
                            if(newdate.length()>30){
                                fieldStrength.setText(""+Integer.parseInt(newdate.substring(24,26),16) + "dBm");
//                                chartView.setFieldStrength((255 - 2 * value / 100) + "", mXDirection);
                            }else if (newdate.endsWith("21")&&newdate.length()>8&&newdate.length()<=30){
                                fieldStrength.setText(""+Integer.parseInt(newdate.substring(newdate.length()-8,newdate.length()-6),16) + "dBm");
//                                chartView.setFieldStrength((255 - 2 * value / 100) + "", mXDirection);
                            }

                        }

                    }


                    break;
                case 3:
                    fieldStrength.setText(msg.getData().getString("str"));

                    break;
                case 4:
                    if (isAdded()) {
                        String str = getResources().getString(R.string.msg_Bluetooth_conn_lost) + "\r\n";
                        connectStatus.setText(str);
                        synState.setText(Constant.synchro + "失步");
                    }
                    break;
            }
        }
    };



    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    Runnable speakData = new Runnable() {
        @Override
        public void run() {
            DecimalFormat df = new DecimalFormat("#.0");
            SpeechUtils.getInstance(getActivity()).speakText("负" + df.format(Constant.curr_value / 100.0));
        }
    };

    private void setBatteryImg(int value) {
        if (value >= 80) {
            batteryImg.setBackgroundResource(R.drawable.battery100);
        } else if (value >= 60 && value < 80) {
            batteryImg.setBackgroundResource(R.drawable.battery80);
        } else if (value >= 40 && value < 60) {
            batteryImg.setBackgroundResource(R.drawable.battery60);
        } else if (value >= 20 && value < 40) {
            batteryImg.setBackgroundResource(R.drawable.battery40);
        } else if (value > 0 && value < 20) {
            batteryImg.setBackgroundResource(R.drawable.battery20);
        } else {
            batteryImg.setBackgroundResource(R.drawable.battery20);
        }
    }

    public void onActivityResult(int i, int j, Intent intent) {

        if (3 == j) {
//            finish();
            return;
        }
        if (i == 1 && j == -1) {
            BluetoothDevice bluetoothdevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            if (bluetoothdevice.getBondState() == BluetoothDevice.BOND_NONE) {
                try {
                    Log.i(TAG, "----onActivityResult-----");
                    BluetoothCtrl.createBond(bluetoothdevice);
                    Toast.makeText(getActivity(), getString(R.string.msg_actDiscovery_Bluetooth_Bond_msg), Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    Toast.makeText(getActivity(), getString(R.string.msg_actDiscovery_Bluetooth_Bond_fail), Toast.LENGTH_SHORT).show();
                }
            } else {
                msBluetoothMAC = bluetoothdevice.getAddress();
                if (!isConnect()) {
                    createBluetoothConnect();
                }
                startReceiveData();
            }
        }
    }

    private void sendCommand(String str) {
        String cmd = CHexConver.str2HexStr(str) + "0D0A";
        Log.i(TAG, "----每隔3s发查询命令-sendCommand-----");
        if (isConnect()) {
            mHandler.sendEmptyMessage(1);
            int i;
            if (cmd.length() <= 0)
                return; /* Loop/switch isn't completed */
            i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
            if (i < 0) {
                Log.i(TAG, "-----SendBytesErr-----");
            }
        } else {
            mHandler.sendEmptyMessage(4);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "-----onCreate-----");
        InitLoad();

        //接收蓝牙数据
        startReceiveData();
        mBT = new BluetoothUtils();
        initService();
        // 初始化传感器
        initOritationListener();
        // 开启方向传感器
        myOrientationListener.start();

        num1 = 0;
        num2 = 2;


        if (MyApp.type == 0) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    num1++;
                    num2++;
                    if (num1 % 2 == 0 && display_Tag == 1 && isConnect() && Constant.isSpeak) {
                        mHandler.postDelayed(speakData, 500);
                    }
                    if (num2 == 5) {
                        num2 = 0;
                        Log.i(TAG, "-----发送命令2-----");
                        sendCommand(SendCommendHelper.getElectricity());
                        lastClick = System.currentTimeMillis();
                    }

                    if (System.currentTimeMillis() - lastClick > 1000) {
                        Log.i(TAG, "-----发送命令1-----");
                        sendCommand(SendCommendHelper.getPower());
                    }

                }
            }, 500, 1800);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "-----onResume-----");
//        num1 = 3;

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    //注册百度定位服务（方向中暂未用到）
    private void initService() {
        locService = ((MyApp) getActivity().getApplication()).locationService;
        locService.registerListener(listener);
        locService.start();
    }

    /**
     * 初始化方向传感器
     */
    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(getActivity().getApplication());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        if (x != 0) {
                            mXDirection = (int) x;
                            Log.i("BDLocationListener", "---mXDirection = " + mXDirection);
                        }
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "-----onDestroy-----");
        mHandler.removeCallbacks(speakData);
        //注销百度定位服务（方向中暂未用到）
        locService.unregisterListener(listener);
        locService.stop();
        // 关闭方向传感器
        myOrientationListener.stop();

        if (mReceiveDataThread != null) {
            mReceiveDataThread.stopRunnable();
            mReceiveDataThread = null;
        }
        if (null != mTimer) {
            mTimer.cancel();
            mTimer.purge();
        }


//        SpeechUtils.getInstance(getActivity()).closeVoice();
    }

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

                if (MyApp.type == 1) {
                    byte[] bufRecv = new byte[1024*2];
                    int nRecv = 0;
                    while (!isStopRunnable) {
                        try {
                            nRecv = BaseFragment.misIn.read(bufRecv);
                            if (nRecv < 1) {
                                Thread.sleep(100);
                                continue;
                            }

                            byte[] nPacket = new byte[nRecv];
                            System.arraycopy(bufRecv, 0, nPacket, 0, nRecv);
                            mHandler.obtainMessage(2,
                                    nRecv, -1, nPacket).sendToTarget();
                            Thread.sleep(100);
                        } catch (Exception e) {
                            break;
                        }
                    }
                } else {
                    int i;
                    byte bytebuf[] = new byte[1024 * 4];
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
    }

    private void init(View view) {
        layout_chart = (LinearLayout) view.findViewById(R.id.chartArea);
        btn_single = (Button) view.findViewById(R.id.singleChart);
        btn_total = (Button) view.findViewById(R.id.totalChart);
        chartView = new ChartView(getActivity(), SINGLECHART, layout_chart.getWidth(), layout_chart.getHeight());
        layout_chart.removeAllViews();
        layout_chart.addView(chartView);
        btn_single.setOnClickListener(this);
        btn_total.setOnClickListener(this);
        fieldStrength = (TextView) view.findViewById(R.id.FieldStrength);
        connectStatus = (TextView) view.findViewById(R.id.ConnectStatus);
        synState = (TextView) view.findViewById(R.id.SynState);
        connectStatus = (TextView) view.findViewById(R.id.ConnectStatus);
        connectDevice = (Button) view.findViewById(R.id.ConnectDevice);
        batteryTv = (TextView) view.findViewById(R.id.batteryTv);
        channelTv = (TextView) view.findViewById(R.id.ChannelTv);
        channelTv.setText(Constant.channel);
        batteryImg = (ImageView) view.findViewById(R.id.BatteryImg);
        speakImg = (ImageView) view.findViewById(R.id.SpeakImg);
        speakImg.setOnClickListener(this);
        if (Constant.isSpeak) {
            speakImg.setBackgroundResource(R.drawable.speaker_on);
        } else {
            speakImg.setBackgroundResource(R.drawable.speaker_off);
        }
        if (isConnect()) {
            String str = getResources().getString(R.string.msg_connect_ok) + "\r\n";
            connectStatus.setText(str);
        }
        connectDevice.setOnClickListener(this);
        display_Tag = SharePreferenceUtils.getIntValue(getActivity(), disPlayTag, 1);
        if (display_Tag == 1) {
            Constant.display = getString(R.string.display_state1);
        } else if (display_Tag == 2) {
            Constant.display = getString(R.string.display_state2);
        }

        if (Constant.curr_value != 0) {
            DecimalFormat df = new DecimalFormat("#.0");
            String fieldStrengthPow = "-" + df.format(Constant.curr_value / 100.0);
            String fieldStrengthValue = (255 - 2 * Constant.curr_value / 100) + "";
            if (display_Tag == 1) {
                fieldStrength.setText(fieldStrengthPow + "dBm");
            } else if (display_Tag == 2) {
                fieldStrength.setText(fieldStrengthValue);
            }
        }

        if (Constant.curr_battery != 0) {
            setBatteryImg(Constant.curr_battery);
            batteryTv.setText(Constant.curr_battery + "%");
        }
//        String newdate="78454546521010121";
//        fieldStrength.setText(""+Integer.parseInt(newdate.substring(newdate.length()-8,newdate.length()-6),16) + "dBm");
        Log.i(TAG, "---init--display_Tag----" + display_Tag);
    }

    //切换显示图
    public void onClick(View v) {
        int id = v.getId();
        int which = 0;
        Constant.POWTAG = 2;
        Log.i(TAG, "-----layout_chart.getHeight----" + layout_chart.getHeight());
        Log.i(TAG, "-----layout_chart.getWidth----" + layout_chart.getWidth());
        Log.i(TAG, "-----Constant.SCREEN_WIDTH----" + Constant.SCREEN_WIDTH);
        Log.i(TAG, "-----Constant.SCREEN_HEIGHT----" + Constant.SCREEN_HEIGHT);
        if (id == R.id.singleChart) {
            btn_single.setBackgroundResource(R.drawable.cancel_pressed);
            btn_total.setBackgroundResource(R.drawable.save_normal);
            which = SINGLECHART;
            chartView = new ChartView(getActivity(), which, layout_chart.getWidth(), layout_chart.getHeight());
            layout_chart.removeAllViews();
            layout_chart.addView(chartView);

        } else if (id == R.id.totalChart) {
            btn_single.setBackgroundResource(R.drawable.cancel_normal);
            btn_total.setBackgroundResource(R.drawable.save_pressed);
            which = TOTALCHART;
            chartView = new ChartView(getActivity(), which, layout_chart.getWidth(), layout_chart.getHeight());
            layout_chart.removeAllViews();
            layout_chart.addView(chartView);

        } else if (id == R.id.ConnectDevice) {
            mbKeyboardMode = false;
            onBlueToothConnection();
        } else if (id == R.id.SpeakImg) {

            if (Constant.isSpeak) {
                speakImg.setBackgroundResource(R.drawable.speaker_off);
                Constant.isSpeak = false;
            } else {
                speakImg.setBackgroundResource(R.drawable.speaker_on);
                Constant.isSpeak = true;
            }
        }
    }

    public void onBlueToothConnection() {
        Log.i(TAG, "-----onMenuConnection---");
        if (mBT.isBluetoothOpen()) {
            Log.i(TAG, "-----isConnect()----" + isConnect());
            if (isConnect())
                Toast.makeText(getActivity(), getString(R.string.msg_re_connect), Toast.LENGTH_SHORT).show();
            else {
                Log.i(TAG, "-----isConnect-false----");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if (msBluetoothMAC != null) {
                    builder.setTitle(getString(R.string.menu_main_Connection));
                    builder.setMessage(getString(R.string.msg_connect_history));

                    builder.setPositiveButton(R.string.btn_connect, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialoginterface, int i) {
                            //连接已连过的蓝牙设备，开启数据接收
                            createBluetoothConnect();
                            if (mReceiveDataThread != null) {
                                mReceiveDataThread.stopRunnable();
                                mReceiveDataThread = null;
                            }
                            startReceiveData();
                        }
                    });

                    builder.setNegativeButton(R.string.btn_reSearch, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialoginterface, int i) {
                            showBluetootchDiscovery();//搜索蓝牙设备
                        }
                    });
                    builder.create().show();
                } else
                    showBluetootchDiscovery();//搜索蓝牙设备
            }
        } else
            openButetooth();
    }

    /***
     * 百度定位结果回调(未用到)
     */
    BDLocationListener listener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            Log.i("BDLocationListener", "city = " + location.getCity());
            Log.i("BDLocationListener", "getDirection = " + location.getDirection());
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Log.i("BDLocationListener", "longitude=" + location.getLongitude()
                        + "latitude=" + location.getLatitude() + "location.getCity() = " + location.getCity());
            }
        }
    };


}
