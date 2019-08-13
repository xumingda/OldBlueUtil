package filedstrength.jingxun.com.meter.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import filedstrength.jingxun.com.meter.Constant.Constant;
import filedstrength.jingxun.com.meter.Constant.NewSendCommendHelper;
import filedstrength.jingxun.com.meter.Constant.SendCommendHelper;
import filedstrength.jingxun.com.meter.MyApp;
import filedstrength.jingxun.com.meter.R;
import filedstrength.jingxun.com.meter.Utils.CHexConver;
import filedstrength.jingxun.com.meter.Utils.DialogUtils;
import filedstrength.jingxun.com.meter.Utils.SharePreferenceUtils;

/**
 * Created by admin on 13-11-23.
 */
public class ConfigFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout standard_rl;
    private RelativeLayout synchro_rl;
    private RelativeLayout channel_rl;
    private RelativeLayout pci_rl;
    private RelativeLayout display_rl;
    private Dialog languageDialog = null;
    private Dialog synChooseDialog = null;
    private Dialog displayDialog = null;
    private Dialog synChooseNewDialog=null;
    private TextView mode_switch_tv;
    private TextView syn_sate_tv;
    private TextView msg_channel_tv;
    private TextView pci_tv;
    private TextView display_mode_tv;
    private String disPlayTag = "DisPlayTag";
    private String TAG = "ConfigFragment";
    private TextView syn_b40;
    private TextView syn_b39;
    private TextView syn_b38;
    private TextView syn_b41;
    private TextView syn_b5;
    private TextView syn_b8;
    //    private TextView test;
    private ReceiveDataThread mReceiveDataThread;
    private int commandTag = 0;
    //要发送的数据
    private String sendDate = "";
    private long lastClick = 0L;
    private long interval = 4000L;
    private Button btn_commit;
    private static String[] cmdTag = {"0B0100", "0B0101", "0B0200", "0B0201", "0B0202", "0B0203", "0B0204", "0B0205", "0B0206", "0B0207", "0B03", "0B04", "0B0500", "0B0501", "0B0208", "0B0209", "0B02"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.configfragment, null);
        init(view);

        return view;
    }

    private void init(View view) {
        btn_commit = (Button) view.findViewById(R.id.btn_commit);
        if(MyApp.type==0){
            btn_commit.setVisibility(View.GONE);
        }else{
            btn_commit.setVisibility(View.VISIBLE);
        }
        standard_rl = (RelativeLayout) view.findViewById(R.id.standard_rl);
        synchro_rl = (RelativeLayout) view.findViewById(R.id.synchro_rl);
        channel_rl = (RelativeLayout) view.findViewById(R.id.channel_rl);
        pci_rl = (RelativeLayout) view.findViewById(R.id.pci_rl);
        display_rl = (RelativeLayout) view.findViewById(R.id.display_rl);

        mode_switch_tv = (TextView) view.findViewById(R.id.mode_switch_tv);
        syn_sate_tv = (TextView) view.findViewById(R.id.syn_sate_tv);
        msg_channel_tv = (TextView) view.findViewById(R.id.msg_channel_tv);
        pci_tv = (TextView) view.findViewById(R.id.pci_tv);
        display_mode_tv = (TextView) view.findViewById(R.id.display_mode_tv);
//        test = (TextView) view.findViewById(R.id.Test);

        mode_switch_tv.setText(Constant.standard);
        syn_sate_tv.setText(Constant.synchro);
        msg_channel_tv.setText(Constant.channel);
        pci_tv.setText(Constant.pci);
        display_mode_tv.setText(Constant.display);
        btn_commit.setOnClickListener(this);
        standard_rl.setOnClickListener(this);
        synchro_rl.setOnClickListener(this);
        channel_rl.setOnClickListener(this);
        pci_rl.setOnClickListener(this);
        display_rl.setOnClickListener(this);
        if(MyApp.type==1){
            synchro_rl.setVisibility(View.GONE);
        }else {
            synchro_rl.setVisibility(View.VISIBLE);
        }
        Log.e("16","16:"+  strTo16("!"));
    }
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit: {
                if(commandTag==23||commandTag==26){
                    if(mode_switch_tv.getText().length() > 0){
                        sendDate=sendDate+"000000000000000021";
                        sendCommand(sendDate);
                    }else{
                        Toast.makeText(getActivity(), getString(R.string.error_message_all), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (msg_channel_tv.getText().length() > 0&&(syn_sate_tv.getText().length()>0||pci_tv.getText().length()>0) && mode_switch_tv.getText().length() > 0){
                        sendDate=sendDate+"000000000000000021";
                        sendCommand(sendDate);
                    }else{
                        Toast.makeText(getActivity(), getString(R.string.error_message_all), Toast.LENGTH_SHORT).show();
                    }
                }
                Log.e("---","---commandTag:"+commandTag);
                break;
            }
            case R.id.standard_rl:
                mode_switch_tv.setText("");
                msg_channel_tv.setText("");
                syn_sate_tv.setText("");
                pci_tv.setText("");
                sendDate="";
                showLanguageChooseDialog(v);
                break;
            case R.id.synchro_rl:
                //工作模式
                showSynChooseNewDialog(v);
//                showSynChooseDialog(v);
                break;
            case R.id.channel_rl:
                if (MyApp.type == 1) {
                    if (mode_switch_tv.getText().length() > 0) {
                        setChannelDialog(v);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_message_one), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    setChannelDialog(v);
                }
                break;
            case R.id.pci_rl:
                if (MyApp.type == 1) {
                    if (msg_channel_tv.getText().length() > 0 && mode_switch_tv.getText().length() > 0) {
                        setPCIDialog(v);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    setPCIDialog(v);
                }

                break;
            case R.id.display_rl:
                //显示二级选择
                showDisplayDialog(v);
                break;
            case R.id.select_cancel1:
                languageDialog.dismiss();
                break;
            case R.id.selectCancel2:
                synChooseDialog.dismiss();
                break;
            case R.id.selectsynCancel:
                synChooseNewDialog.dismiss();
                break;
            case R.id.selectCancel3:
                displayDialog.dismiss();
                break;
            case R.id.select_tdd:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                languageDialog.dismiss();
                commandTag = 1;
                mode_switch_tv.setText(getString(R.string.tdd));
                Constant.standard = mode_switch_tv.getText().toString();

                if (MyApp.type == 0) {
                    mHandler.removeCallbacks(sendCmdFail);
                    lastClick = System.currentTimeMillis();
                    mHandler.postDelayed(sendCmdFail, interval);
                    sendCommand(SendCommendHelper.setTDD());
                } else {
                    sendDate = NewSendCommendHelper.setTDD();
                }
                break;
            case R.id.select_fdd:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                languageDialog.dismiss();
                commandTag = 2;
                if (MyApp.type == 0) {
                    mHandler.removeCallbacks(sendCmdFail);
                    lastClick = System.currentTimeMillis();
                    mHandler.postDelayed(sendCmdFail, interval);
                    sendCommand(SendCommendHelper.setFDD());
                } else {
                    sendDate = NewSendCommendHelper.setFDD();
                }
                mode_switch_tv.setText(getString(R.string.fdd));
                Constant.standard = mode_switch_tv.getText().toString();

                break;
            case R.id.select_gsm:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                languageDialog.dismiss();
                commandTag = 23;
                sendDate = NewSendCommendHelper.setGSM();
                mode_switch_tv.setText(getString(R.string.gsm));
                Constant.standard = mode_switch_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
            case R.id.select_tds:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                languageDialog.dismiss();
                commandTag = 24;
                sendDate = NewSendCommendHelper.setTDS();
                mode_switch_tv.setText(getString(R.string.tds));
                Constant.standard = mode_switch_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
            case R.id.select_cdma:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                languageDialog.dismiss();
                commandTag = 25;
                sendDate = NewSendCommendHelper.setCDMA();
                mode_switch_tv.setText(getString(R.string.cdma));
                Constant.standard = mode_switch_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
            case R.id.select_wcd:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                languageDialog.dismiss();
                commandTag = 26;
                sendDate = NewSendCommendHelper.setWCDMA();
                mode_switch_tv.setText(getString(R.string.wcd));
                Constant.standard = mode_switch_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
            case R.id.syn_b40:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                synChooseDialog.dismiss();
                if (TextUtils.equals(Constant.standard, getString(R.string.tdd))) {
                    commandTag = 3;
                    sendCommand(SendCommendHelper.setSyn40());
                    syn_sate_tv.setText(getString(R.string.syn_b40));
                } else {
                    commandTag = 7;
                    sendCommand(SendCommendHelper.setSyn_b1_lian());
                    syn_sate_tv.setText(getString(R.string.syn_b1_lian));
                }
                Constant.synchro = syn_sate_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
            case R.id.syn_b39:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                synChooseDialog.dismiss();
                if (TextUtils.equals(Constant.standard, getString(R.string.tdd))) {
                    commandTag = 4;
                    sendCommand(SendCommendHelper.setSyn39());
                    syn_sate_tv.setText(getString(R.string.syn_b39));
                } else {
                    commandTag = 8;
                    sendCommand(SendCommendHelper.setSyn_b1_dian());
                    syn_sate_tv.setText(getString(R.string.syn_b1_dian));
                }
                Constant.synchro = syn_sate_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;

            case R.id.syn_b38:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                synChooseDialog.dismiss();
                if (TextUtils.equals(Constant.standard, getString(R.string.tdd))) {
                    commandTag = 5;
                    sendCommand(SendCommendHelper.setSyn38());
                    syn_sate_tv.setText(getString(R.string.syn_b38));
                } else {
                    commandTag = 9;
                    sendCommand(SendCommendHelper.setSyn_b3_lian());
                    syn_sate_tv.setText(getString(R.string.syn_b3_lian));
                }
                Constant.synchro = syn_sate_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
            case R.id.syn_b41:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                synChooseDialog.dismiss();
                if (TextUtils.equals(Constant.standard, getString(R.string.tdd))) {
                    commandTag = 6;
                    sendCommand(SendCommendHelper.setSyn41());
                    syn_sate_tv.setText(getString(R.string.syn_b41));
                } else {
                    commandTag = 10;
                    sendCommand(SendCommendHelper.setSyn_b3_dian());
                    syn_sate_tv.setText(getString(R.string.syn_b3_dian));
                }
                Constant.synchro = syn_sate_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;

            case R.id.syn_b5:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                synChooseDialog.dismiss();
                commandTag = 15;
                sendCommand(SendCommendHelper.setSyn_b5());
                syn_sate_tv.setText(getString(R.string.syn_b5));
                Constant.synchro = syn_sate_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
            case R.id.syn_b8:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                synChooseDialog.dismiss();
                commandTag = 16;
                sendCommand(SendCommendHelper.setSyn_b8());
                syn_sate_tv.setText(getString(R.string.syn_b8));
                Constant.synchro = syn_sate_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;

            case R.id.display_state1:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                displayDialog.dismiss();
                commandTag = 13;
                sendCommand(SendCommendHelper.setDisplay1());
                display_mode_tv.setText(getString(R.string.display_state1));
                SharePreferenceUtils.putInt(getActivity(), disPlayTag, 1);
                Constant.display = display_mode_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
            case R.id.display_state2:
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                displayDialog.dismiss();
                commandTag = 14;
                sendCommand(SendCommendHelper.setDisplay2());
                display_mode_tv.setText(getString(R.string.display_state2));
                SharePreferenceUtils.putInt(getActivity(), disPlayTag, 2);
                Constant.display = display_mode_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
            case R.id.near_state1:
                //近距离
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                synChooseNewDialog.dismiss();
                commandTag = 17;
                sendCommand(SendCommendHelper.setSyn() + CHexConver.toHex(1));
                syn_sate_tv.setText(getString(R.string.syn_sate_near));
                Constant.synchro = syn_sate_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);

                break;
            case R.id.yuan_state2:
                //远距离
                if (System.currentTimeMillis() - lastClick < interval) {
                    return;
                }
                synChooseNewDialog.dismiss();
                commandTag = 17;
                sendCommand(SendCommendHelper.setSyn() +CHexConver.toHex(0));
                syn_sate_tv.setText(getString(R.string.syn_sate_yuan));
                Constant.synchro = syn_sate_tv.getText().toString();
                mHandler.removeCallbacks(sendCmdFail);
                lastClick = System.currentTimeMillis();
                mHandler.postDelayed(sendCmdFail, interval);
                break;
//            case R.id.display_state3:
//                displayDialog.dismiss();
//                sendCommand(SendCommendHelper.setDisplay3());
//                display_mode_tv.setText(getString(R.string.display_state3));
//                SharePreferenceUtils.putInt(getActivity(),disPlayTag,3);
//                Constant.display = display_mode_tv.getText().toString();
//                break;
//            case R.id.display_state4:
//                displayDialog.dismiss();
//                sendCommand(SendCommendHelper.setDisplay4());
//                display_mode_tv.setText(getString(R.string.display_state4));
//                SharePreferenceUtils.putInt(getActivity(),disPlayTag,4);
//                Constant.display = display_mode_tv.getText().toString();
//                break;
        }
    }

    private void setChannelDialog(View V) {
        final EditText name = new EditText(getActivity());
        name.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        DialogUtils.createDialog(getActivity(), name, R.string.title_setChannel, android.R.drawable.ic_dialog_info, new DialogUtils.DialogListener() {
            @Override
            public void sure() {
                if (!TextUtils.isEmpty(name.getText())) {
                    int num = Integer.parseInt(name.getText().toString());
                    if (MyApp.type == 0) {
                        if (num < 65536 && num > -1) {
                            if (System.currentTimeMillis() - lastClick < interval) {
                                return;
                            }
                            String value = CHexConver.toHex(num);
                            Log.i(TAG, "-----setChannel----" + SendCommendHelper.setChannel() + value);
                            commandTag = 11;
                            sendCommand(SendCommendHelper.setChannel() + value);
                            msg_channel_tv.setText(name.getText().toString());
                            Constant.channel = name.getText().toString();
                            mHandler.removeCallbacks(sendCmdFail);
                            lastClick = System.currentTimeMillis();
                            mHandler.postDelayed(sendCmdFail, interval);

                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("str", "0");
                            message.setData(bundle);
                            message.what = 2;
                            mHandler.sendMessageDelayed(message, 2000);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.tip_channelValue), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (num < 39999 && num > 0) {

                            String value = CHexConver.toHex(num);

                            if (commandTag == 1) {
                                sendDate = sendDate.substring(0,6) + value;
                            } else if (commandTag == 2) {
                                sendDate = sendDate.substring(0,6)  + value;
                            } else if (commandTag == 23) {
                                sendDate = sendDate.substring(0,6)  + value;
                            } else if (commandTag == 24) {
                                sendDate = sendDate.substring(0,6)  + value;
                            } else if (commandTag == 25) {
                                sendDate = sendDate.substring(0,6)  + value;
                            } else if (commandTag == 26) {
                                sendDate = sendDate.substring(0,6)  + value;
                            }
                            Log.i(TAG, "----setSyn1111----" + sendDate);
                            msg_channel_tv.setText(name.getText().toString());
                            Constant.channel = name.getText().toString();
                            mHandler.removeCallbacks(sendCmdFail);
                            mHandler.postDelayed(sendCmdFail, interval);

                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("str", "0");
                            message.setData(bundle);
                            message.what = 2;
                            mHandler.sendMessageDelayed(message, 2000);

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.tip_channelValue), Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            }
        });
    }

    private void setPCIDialog(View V) {
        final EditText name = new EditText(getActivity());
        name.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        DialogUtils.createDialog(getActivity(), name, R.string.title_setPCI, android.R.drawable.ic_dialog_info, new DialogUtils.DialogListener() {
            @Override
            public void sure() {
                if (!TextUtils.isEmpty(name.getText())) {
                    int num = Integer.parseInt(name.getText().toString());
                    if (MyApp.type == 0) {
                        if (num < 504) {
                            if (System.currentTimeMillis() - lastClick < interval) {
                                return;
                            }
                            String value = CHexConver.toHex(num);
                            Log.i(TAG, "----setChannel----" + SendCommendHelper.setPCI() + value);
                            commandTag = 12;
                            sendCommand(SendCommendHelper.setPCI() + value);
                            pci_tv.setText(name.getText().toString());
                            Constant.pci = name.getText().toString();
                            mHandler.removeCallbacks(sendCmdFail);
                            lastClick = System.currentTimeMillis();
                            mHandler.postDelayed(sendCmdFail, interval);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.tip_pciValue), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (num > 0 && num < 504) {
                            String value = CHexConver.toHex(num);

                            if (commandTag == 1) {
                                sendDate = sendDate.substring(0,10)  + value;
                            } else if (commandTag == 2) {
                                sendDate = sendDate.substring(0,10) + value;
                            }
                            Log.i(TAG, "----set许明达Channel2222----" + sendDate);
                            pci_tv.setText(name.getText().toString());
                            Constant.pci = name.getText().toString();
                            mHandler.removeCallbacks(sendCmdFail);
                            mHandler.postDelayed(sendCmdFail, interval);
                        }

                        else {
                            Toast.makeText(getActivity(), getString(R.string.tip_pciValue), Toast.LENGTH_SHORT).show();
                        }
                        if (num < 999 && num > 0) {
                            if (System.currentTimeMillis() - lastClick < interval) {
                                return;
                            }
                            String value = CHexConver.toHex(num);

                            if (commandTag == 24) {
                                sendDate = sendDate.substring(0, 10) + value;
                            } else if (commandTag == 25) {
                                sendDate = sendDate.substring(0, 10) + value;
                            }
                            pci_tv.setText(name.getText().toString());
                            Constant.pci = name.getText().toString();
                            mHandler.removeCallbacks(sendCmdFail);
                            mHandler.postDelayed(sendCmdFail, interval);
                        }else{
                            Toast.makeText(getActivity(), getString(R.string.tip_pnValue), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });
    }

    private void showLanguageChooseDialog(View V) {
        if (languageDialog == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_select_lanuage, null);
            TextView select_tdd = (TextView) layout.findViewById(R.id.select_tdd);
            TextView select_fdd = (TextView) layout.findViewById(R.id.select_fdd);
            TextView select_wcd = (TextView) layout.findViewById(R.id.select_wcd);
            TextView select_cdma = (TextView) layout.findViewById(R.id.select_cdma);
            TextView select_tds = (TextView) layout.findViewById(R.id.select_tds);
            TextView select_gsm = (TextView) layout.findViewById(R.id.select_gsm);
            Button select_cancel1 = (Button) layout.findViewById(R.id.select_cancel1);
            languageDialog = new Dialog(getActivity(), R.style.Custom_Dialog_Theme);
            languageDialog.setCanceledOnTouchOutside(false);
            select_tdd.setOnClickListener(this);
            select_fdd.setOnClickListener(this);
            select_wcd.setOnClickListener(this);
            select_cdma.setOnClickListener(this);
            select_tds.setOnClickListener(this);
            select_gsm.setOnClickListener(this);
            select_cancel1.setOnClickListener(this);
            if (MyApp.type == 0) {
                select_wcd.setVisibility(View.GONE);
                select_cdma.setVisibility(View.GONE);
                select_tds.setVisibility(View.GONE);
                select_gsm.setVisibility(View.GONE);
            } else {
                select_wcd.setVisibility(View.VISIBLE);
                select_cdma.setVisibility(View.VISIBLE);
                select_tds.setVisibility(View.VISIBLE);
                select_gsm.setVisibility(View.VISIBLE);
            }
            languageDialog.setContentView(layout);
        }
        languageDialog.show();
        if (synChooseDialog != null) {
            synChooseDialog = null;
        }
    }

    private void showSynChooseDialog(View V) {

      /*  if (synChooseDialog == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout;
            if(TextUtils.equals(Constant.standard, getString(R.string.tdd))){
                layout = inflater.inflate(R.layout.dialog_select_synstate, null);
            }else{
                layout = inflater.inflate(R.layout.dialog_select_synstate2, null);
                syn_b5 = (TextView) layout.findViewById(R.id.syn_b5);
                syn_b8 = (TextView) layout.findViewById(R.id.syn_b8);
                syn_b5.setOnClickListener(this);
                syn_b8.setOnClickListener(this);
            }
            syn_b40 = (TextView) layout.findViewById(R.id.syn_b40);
            syn_b39 = (TextView) layout.findViewById(R.id.syn_b39);
            syn_b38 = (TextView) layout.findViewById(R.id.syn_b38);
            syn_b41 = (TextView) layout.findViewById(R.id.syn_b41);
            Log.i(TAG, "----Constant.standard----"+Constant.standard);
            Log.i(TAG, "----getString(R.string.tdd)----"+getString(R.string.tdd));
//            if(TextUtils.equals(Constant.standard, getString(R.string.tdd))){
//                Log.i(TAG, "----选择的TDD模式----");
//                syn_b40.setText(getString(R.string.syn_b40));
//                syn_b39.setText(getString(R.string.syn_b39));
//                syn_b38.setText(getString(R.string.syn_b38));
//                syn_b41.setText(getString(R.string.syn_b41));
//            }else{
//                syn_b40.setText(getString(R.string.syn_b1_lian));
//                syn_b39.setText(getString(R.string.syn_b1_dian));
//                syn_b38.setText(getString(R.string.syn_b3_lian));
//                syn_b41.setText(getString(R.string.syn_b3_dian));
//            }

            Button selectCancel2 = (Button)layout.findViewById(R.id.selectCancel2);
            synChooseDialog = new Dialog(getActivity(), R.style.Custom_Dialog_Theme);
            synChooseDialog.setCanceledOnTouchOutside(false);
            syn_b40.setOnClickListener(this);
            syn_b39.setOnClickListener(this);
            syn_b38.setOnClickListener(this);
            syn_b41.setOnClickListener(this);
            selectCancel2.setOnClickListener(this);
            synChooseDialog.setContentView(layout);


            final EditText name = new EditText(getActivity());
            name.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            DialogUtils.createDialog(getActivity(), name, R.string.title_setChannel,android.R.drawable.ic_dialog_info, new DialogUtils.DialogListener() {
                @Override
                public void sure() {
                    if(!TextUtils.isEmpty(name.getText())){
                        int num = Integer.parseInt(name.getText().toString());
                        if(num < 42001 && num > 17999){
                            if (System.currentTimeMillis() - lastClick < interval) {
                                return;
                            }
                            String value = CHexConver.toHex(num);
                            Log.i(TAG, "-----setChannel----"+SendCommendHelper.setChannel()+value);
                            commandTag = 11;
                            sendCommand(SendCommendHelper.setChannel()+value);
                            msg_channel_tv.setText(name.getText().toString());
                            Constant.channel = name.getText().toString();
                            mHandler.removeCallbacks(sendCmdFail);
                            lastClick = System.currentTimeMillis();
                            mHandler.postDelayed(sendCmdFail, interval);

                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("str", "0");
                            message.setData(bundle);
                            message.what = 2;
                            mHandler.sendMessageDelayed(message, 2000);
                        }else {
                            Toast.makeText(getActivity(), getString(R.string.tip_channelValue), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }else{
            Log.i(TAG, "----Constant.standard----"+Constant.standard);
            Log.i(TAG, "----getString(R.string.tdd)----"+getString(R.string.tdd));
//            if(TextUtils.equals(Constant.standard, getString(R.string.tdd))){
//                Log.i(TAG, "----选择的TDD模式----");
//                syn_b40.setText(getString(R.string.syn_b40));
//                syn_b39.setText(getString(R.string.syn_b39));
//                syn_b38.setText(getString(R.string.syn_b38));
//                syn_b41.setText(getString(R.string.syn_b41));
//            }else{
//                syn_b40.setText(getString(R.string.syn_b1_lian));
//                syn_b39.setText(getString(R.string.syn_b1_dian));
//                syn_b38.setText(getString(R.string.syn_b3_lian));
//                syn_b41.setText(getString(R.string.syn_b3_dian));
//            }
        }
        synChooseDialog.show();*/


        final EditText name = new EditText(getActivity());
        name.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        DialogUtils.createDialog(getActivity(), name, R.string.title_setSyn, android.R.drawable.ic_dialog_info, new DialogUtils.DialogListener() {
            @Override
            public void sure() {
                if (!TextUtils.isEmpty(name.getText())) {
                    if (MyApp.type == 0) {
                        int num = Integer.parseInt(name.getText().toString());
                        if (num < 65536 && num > -1) {
                            if (System.currentTimeMillis() - lastClick < interval) {
                                return;
                            }
                            String value = CHexConver.toHex(num);
                            Log.i(TAG, "----setSyn----" + SendCommendHelper.setSyn() + value);
                            commandTag = 17;
                            sendCommand(SendCommendHelper.setSyn() + value);
                            syn_sate_tv.setText(name.getText().toString());
                            Constant.synchro = name.getText().toString();
                            mHandler.removeCallbacks(sendCmdFail);
                            lastClick = System.currentTimeMillis();
                            mHandler.postDelayed(sendCmdFail, interval);

//                        Message message = new Message();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("str", "0");
//                        message.setData(bundle);
//                        message.what = 2;
//                        mHandler.sendMessageDelayed(message, 2000);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.tip_synValue), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        int num = Integer.parseInt(name.getText().toString());
                        if (num < 999 && num > 0) {
                            if (System.currentTimeMillis() - lastClick < interval) {
                                return;
                            }
                            String value = CHexConver.toHex(num);

                            if (commandTag == 24) {
                                sendDate = sendDate.substring(0,10) + value;
                            } else if (commandTag == 25) {
                                sendDate = sendDate.substring(0,10) + value;
                            }
                            Log.i(TAG, "----setSyn频点----" + sendDate);
//                           xmd if(commandTag==1){
//                                sendCommand(XHD+value);
//                            }else if(commandTag==2){
//                                sendCommand(XHD+value);
//                            }els
                            syn_sate_tv.setText(name.getText().toString());
                            Constant.synchro = name.getText().toString();
                            mHandler.removeCallbacks(sendCmdFail);
                            lastClick = System.currentTimeMillis();
                            mHandler.postDelayed(sendCmdFail, interval);

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.tip_synValue), Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            }
        });
    }

    private void showSynChooseNewDialog(View V) {
        if (synChooseNewDialog == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_select_newsyn_choose, null);
            TextView near_state1 = (TextView) layout.findViewById(R.id.near_state1);
            TextView yuan_state2 = (TextView) layout.findViewById(R.id.yuan_state2);
//            TextView display_state3 = (TextView) layout.findViewById(R.id.display_state3);
//            TextView display_state4 = (TextView) layout.findViewById(R.id.display_state4);
            Button selectsynCancel = (Button) layout.findViewById(R.id.selectsynCancel);
            synChooseNewDialog = new Dialog(getActivity(), R.style.Custom_Dialog_Theme);
            synChooseNewDialog.setCanceledOnTouchOutside(false);
            near_state1.setOnClickListener(this);
            yuan_state2.setOnClickListener(this);
//            display_state3.setOnClickListener(this);
//            display_state4.setOnClickListener(this);
            selectsynCancel.setOnClickListener(this);
            synChooseNewDialog.setContentView(layout);
        }
        synChooseNewDialog.show();
    }

    private void showDisplayDialog(View V) {
        if (displayDialog == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_select_displaymode, null);
            TextView display_state1 = (TextView) layout.findViewById(R.id.display_state1);
            TextView display_state2 = (TextView) layout.findViewById(R.id.display_state2);
//            TextView display_state3 = (TextView) layout.findViewById(R.id.display_state3);
//            TextView display_state4 = (TextView) layout.findViewById(R.id.display_state4);
            Button selectCancel3 = (Button) layout.findViewById(R.id.selectCancel3);
            displayDialog = new Dialog(getActivity(), R.style.Custom_Dialog_Theme);
            displayDialog.setCanceledOnTouchOutside(false);
            display_state1.setOnClickListener(this);
            display_state2.setOnClickListener(this);
//            display_state3.setOnClickListener(this);
//            display_state4.setOnClickListener(this);
            selectCancel3.setOnClickListener(this);
            displayDialog.setContentView(layout);
        }
        displayDialog.show();
    }


    private void sendCommand(String str) {
        if (MyApp.type == 0) {
            String cmd = CHexConver.str2HexStr(str) + "0D0A";
            if (isConnect()) {
                int i;
                if (cmd.length() <= 0)
                    return; /* Loop/switch isn't completed */
                if (mOutputMode != 0) {
                    Log.i(TAG, "-----mOutputMode != 0-----");
                    byte byte0 = mOutputMode;
                    i = 0;
                    if (1 == byte0) {
                        Log.i(TAG, "-----1 == byte0-----");
                        if (CHexConver.checkHexStr(cmd)) {

                            i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
                        } else {
                            i = 0;
                        }
                    }
                } else {
                    Log.i(TAG, "-----connect_ok---SendData--");
                    i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
                }
                if (i < 0) {
                    Log.i(TAG, "-----SendBytesErr-----");
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.msg_Bluetooth_conn_lost), Toast.LENGTH_SHORT).show();
            }
        } else {
//            Log.e("数据","str:"+str+"    CHexConver:"+CHexConver.hexStringToBytes(str.toUpperCase()).length);
//            for (int i=0;i<CHexConver.hexStringToBytes(str.toUpperCase()).length;i++){
//                Log.e("数据","CHexConver:"+CHexConver.hexStringToBytes(str.toUpperCase())[i]);
//                System.out.printf("%x\n",CHexConver.hexStringToBytes(str.toUpperCase())[i]);//按16进制输
//            }
            String cmd = str;
            if (isConnect()) {
                int i;
                if (cmd.length() <= 0)
                    return; /* Loop/switch isn't completed */
                if (mOutputMode != 0) {
                    Log.i(TAG, "-----mOutputMode != 0-----");
                    byte byte0 = mOutputMode;
                    i = 0;
                    if (1 == byte0) {
                        Log.i(TAG, "-----1 == byte0-----");
                        if (CHexConver.checkHexStr(cmd)) {

                            i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
                            Log.e("数据", "CHexConver:"+i);
                        } else {
                            i = 0;
                        }
                    }
                } else {
                    Log.i(TAG, "-----connect_ok---SendData--");
                    i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
                }
                if (i < 0) {
                    Log.i(TAG, "-----SendBytesErr-----");
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.msg_Bluetooth_conn_lost), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //接收蓝牙数据
        startReceiveData();
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

    Runnable sendCmdFail = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(100);
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
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String str = getString(R.string.msg_connect_ok) + "\r\n";
                    break;
                case 2:
                    String data = msg.getData().getString("str");
                    if(MyApp.type==1){
                        byte[] bBuf = (byte[]) msg.obj;
                        String newdate = bytesToHexString(bBuf);

                        if (isConnect()&&sendDate.length()>30) {
                            if (!TextUtils.isEmpty(newdate)&&newdate.endsWith("21")&&newdate.length()>10){
                                if(newdate.substring(4,10).equalsIgnoreCase(sendDate.substring(4,10))){
                                    Toast.makeText(getActivity(), getString(R.string.SettingSuccess), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        Log.e("蓝牙接收数据","蓝牙接收数据:"+newdate);
                    }else{
                        if (isConnect() && data.length() >= 4 && commandTag > 0) {
//                        test.setText(data);
                            if (commandTag == 11 || commandTag == 12 || commandTag == 17) {
                                if (data.substring(0, 4).equals(cmdTag[commandTag - 1])) {
                                    mHandler.removeCallbacks(sendCmdFail);
                                    Toast.makeText(getActivity(), getString(R.string.SettingSuccess), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (data.length() >= 6) {
                                    if (data.substring(0, 6).equals(cmdTag[commandTag - 1])) {
                                        Toast.makeText(getActivity(), getString(R.string.SettingSuccess), Toast.LENGTH_SHORT).show();
                                        mHandler.removeCallbacks(sendCmdFail);
                                    }
                                }
                            }
                        }
                    }
                    Log.i(TAG, "----version--data: " + data);

                    break;
                case 3:
                    break;
                case 100:
                    if(MyApp.type==0){
                        if (isAdded()) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.SettingFail), Toast.LENGTH_SHORT).show();
                        }
                    }

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
    }
}
