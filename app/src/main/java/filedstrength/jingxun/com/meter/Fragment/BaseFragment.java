package filedstrength.jingxun.com.meter.Fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import filedstrength.jingxun.com.meter.BluetoothCtrl;
import filedstrength.jingxun.com.meter.R;
import filedstrength.jingxun.com.meter.Utils.ActDiscovery;
import filedstrength.jingxun.com.meter.Utils.Sysinfo;

/**
 * Created by admin on 13-11-23.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class BaseFragment extends Fragment {

    protected static final byte IO_MODE_CHAR = 0;
    protected static final byte IO_MODE_HEX = 1;
    protected static final byte REQUEST_DISCOVERY = 1;
    protected static final byte REQUEST_ENABLE = 2;
    protected static final byte REQUEST_KEYBOARD = 3;
    private static boolean mbConectOk = false;
    private static BluetoothSocket mbsSocket = null;
    public static InputStream misIn = null;
    private static OutputStream mosOut = null;
    protected byte mInputMode;
    protected byte mOutputMode;
    protected String msBluetoothMAC;
    private String TAG = "BaseCommonActivity";

    public BaseFragment()
    {
        msBluetoothMAC = null;
        mInputMode = 0;
        mOutputMode = 0;
    }

    protected final void InitLoad()
    {
        mInputMode = (byte)getIntData("InputMode");
        mOutputMode = (byte)getIntData("OutputMode");
        msBluetoothMAC = getStrData("BluetoothMAC");
    }

    protected int ReceiveData(byte bytebuf[])
    {
        if (!mbConectOk)
            return -2;
        try
        {
            return misIn.read(bytebuf);
        }
        catch (IOException ioexception)
        {
            terminateConnect();
            return -3;
        }
    }

    protected int SendData(byte bytebuf[])
    {
        int bytelength;
        if (mbConectOk)
        {
            try
            {
                Log.i(TAG, "-----SendData-----"+bytebuf);
                mosOut.write(bytebuf);
                bytelength = bytebuf.length;
            }
            catch (IOException ioexception)
            {
                terminateConnect();
                bytelength = -3;
            }
        }
        else
            bytelength = -2;
        return bytelength;
    }

    protected boolean createBluetoothConnect()
    {
        BluetoothDevice bluetoothdevice;
        if (mbConectOk)
        {
            try {
                misIn.close();
                mosOut.close();
                mbsSocket.close();
            } catch (IOException ioexception1) {
                misIn = null;
                mosOut = null;
                mbsSocket = null;
                mbConectOk = false;
            }
        }
        try {
            bluetoothdevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(msBluetoothMAC);
            mbsSocket = bluetoothdevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            mbsSocket.connect();
            saveData("BluetoothMAC", msBluetoothMAC);
            mosOut = mbsSocket.getOutputStream();
            misIn = mbsSocket.getInputStream();
            mbConectOk = true;
            return true;
        } catch (IOException ioexception) {
            msBluetoothMAC = null;
            saveData("BluetoothMAC", ((String) (null)));
            mbConectOk = false;
            return false;
        }
//		}
    }

    protected int getIntData(String s)
    {
        return getActivity().getSharedPreferences((new Sysinfo(getActivity())).getPackageName(), 0).getInt(s, 0);
    }

    protected String getStrData(String s)
    {
        return getActivity().getSharedPreferences((new Sysinfo(getActivity())).getPackageName(), 0).getString(s, null);
    }

    protected boolean isConnect()
    {
        return mbConectOk;
    }

    protected void openButetooth()
    {
        Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
        Toast.makeText(getActivity(), getString(R.string.msg_actDiscovery_Bluetooth_Open_Fail), Toast.LENGTH_SHORT).show();
        startActivityForResult(intent, 2);
    }

    protected String readBluetoothMAC()
    {
        return getActivity().getSharedPreferences((new Sysinfo(getActivity())).getPackageName(), 0).getString("BluetoothMAC", null);
    }

    protected void saveBluetoothMAC(String s)
    {
        android.content.SharedPreferences.Editor editor = getActivity().getSharedPreferences((new Sysinfo(getActivity())).getPackageName(), 0).edit();
        editor.putString("BluetoothMAC", s);
        editor.commit();
    }

    protected void saveData(String s, int i)
    {
        android.content.SharedPreferences.Editor editor = getActivity().getSharedPreferences((new Sysinfo(getActivity())).getPackageName(), 0).edit();
        editor.putInt(s, i);
        editor.commit();
    }

    protected void saveData(String s, String s1)
    {
        android.content.SharedPreferences.Editor editor = getActivity().getSharedPreferences((new Sysinfo(getActivity())).getPackageName(), 0).edit();
        editor.putString(s, s1);
        editor.commit();
    }

    protected void showBluetootchDiscovery()
    {
        Intent intent = new Intent(getActivity(), ActDiscovery.class);
        Toast.makeText(getActivity(), getString(R.string.msg_actDiscovery_select_device), Toast.LENGTH_SHORT).show();
        startActivityForResult(intent, 1);
    }

    protected void terminateConnect()
    {
        Log.i(TAG, "-----terminateConnect-----");
        if (mbConectOk)
        {
            try
            {
                mbConectOk = false;
                mbsSocket.close();
                misIn.close();
                mosOut.close();
            }
            catch (IOException localIOException)
            {
                misIn = null;
                mosOut = null;
                mbsSocket = null;
            }
        }
    }



}
