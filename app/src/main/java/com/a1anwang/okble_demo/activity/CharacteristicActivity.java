package com.a1anwang.okble_demo.activity;

import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.a1anwang.okble.client.core.OKBLEOperation;
import com.a1anwang.okble.client.core.OKBLEDeviceListener;
import com.a1anwang.okble.common.OKBLECharacteristicModel;
import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble_demo.R;
import com.a1anwang.okble_demo.base.BaseActivity;
import com.a1anwang.okble_demo.views.HexInputPopupWindow;

import java.sql.Timestamp;

/**
 * Created by a1anwang.com on 2018/5/23.
 */

public class CharacteristicActivity extends BaseActivity implements OKBLEDeviceListener {
    public  static  final  String EXTRA_Characteristic=CharacteristicActivity.class.getName()+".EXTRA_Characteristic";

    OKBLECharacteristicModel characteristicModel;

    Button btn_read,btn_write,btn_notify,btn_indicate;

    Button btn_clear;

    TextView tv_log;

    HexInputPopupWindow inputPopupWindow;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(application.okbleDevice!=null){
            application.okbleDevice.removeDeviceListener(this);
        }
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_characteristic);
    }

    @Override
    public void beforeInitView() {
        characteristicModel=getIntent().getParcelableExtra(EXTRA_Characteristic);
    }

    @Override
    public void initView() {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        setHeadVisibility(View.VISIBLE);
        tv_log=findViewById(R.id.tv_log);
        btn_clear=findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);

        btn_read=findViewById(R.id.btn_read);
        btn_write=findViewById(R.id.btn_write);
        btn_notify=findViewById(R.id.btn_notify);
        btn_indicate=findViewById(R.id.btn_indicate);

        btn_read.setOnClickListener(this);
        btn_write.setOnClickListener(this);
        btn_notify.setOnClickListener(this);
        btn_indicate.setOnClickListener(this);


        if(characteristicModel!=null){
            setTitle(characteristicModel.getUuid());
            if(characteristicModel.isCanRead()){
                btn_read.setVisibility(View.VISIBLE);
            }else{
                btn_read.setVisibility(View.GONE);
            }
            if(characteristicModel.isCanWrite()||characteristicModel.isCanWriteNoResponse()){
                btn_write.setVisibility(View.VISIBLE);
            }else{
                btn_write.setVisibility(View.GONE);
            }
            if(characteristicModel.isCanNotify()){
                btn_notify.setVisibility(View.VISIBLE);
                if(application.okbleDevice.isNotifyEnabled(characteristicModel.getUuid())){
                    btn_notify.setText("Notification enabled");
                    btn_notify.setEnabled(false);
                }else{
                    btn_notify.setText("Notification disabled");
                }

            }else{
                btn_notify.setVisibility(View.GONE);
            }
            if(characteristicModel.isCanIndicate()){
                btn_indicate.setVisibility(View.VISIBLE);
                if(application.okbleDevice.isIndicateEnabled(characteristicModel.getUuid())){
                    btn_indicate.setText("Indication enabled");
                    btn_indicate.setEnabled(false);
                }else{
                    btn_indicate.setText("Indication disabled");
                }

            }else{
                btn_indicate.setVisibility(View.GONE);
            }

            application.okbleDevice.addDeviceListener(this);
        }

    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void onClickEvent(View v) {
        switch (v.getId()){
            case R.id.btn_read:
                  application.okbleDevice.addReadOperation(characteristicModel.getUuid(), new OKBLEOperation.ReadOperationListener() {
                      @Override
                      public void onReadValue(final byte[] value) {
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  addLog("onReadValue:"+ OKBLEDataUtils.BytesToHexString(value)+" ("+new String(value)+")");
                              }
                          });
                      }

                      @Override
                      public void onFail(int code, final String errMsg) {
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  addLog("read onFail:"+ errMsg);
                              }
                          });
                      }

                      @Override
                      public void onExecuteSuccess(OKBLEOperation.OperationType type) {

                      }
                  });
                break;
            case R.id.btn_write:
                if(inputPopupWindow==null){
                    inputPopupWindow=new HexInputPopupWindow(this);
                    inputPopupWindow.setInputListener(new HexInputPopupWindow.InputListener() {
                        @Override
                        public void onInputComplete(final String value) {
                            if(value.length()>0){
                                application.okbleDevice.addWriteOperation(characteristicModel.getUuid(),value,new OKBLEOperation.WriteOperationListener() {
                                    @Override
                                    public void onWriteValue(final byte[] byteValue) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                addLog(" onWriteValue:"+value);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFail(int code, final String errMsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                addLog("write onFail:"+errMsg);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onExecuteSuccess(OKBLEOperation.OperationType type) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                addLog("write value execute success value length:"+OKBLEDataUtils.hexStringToBytes(value).length+" value:"+value);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
                inputPopupWindow.showAt(findViewById(R.id.rootview), Gravity.BOTTOM,0,0);
                break;
            case R.id.btn_notify:
            case R.id.btn_indicate:
                final OKBLEOperation.OperationType[] operationType = new OKBLEOperation.OperationType[1];
                application.okbleDevice.addNotifyOrIndicateOperation(characteristicModel.getUuid(), true, new OKBLEOperation.NotifyOrIndicateOperationListener() {
                    @Override
                    public void onNotifyOrIndicateComplete() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addLog("onNotifyOrIndicateComplete");
                                if(operationType[0]== OKBLEOperation.OperationType.OperationType_Enable_Indicate){
                                    btn_indicate.setText("Indication enabled");
                                    btn_indicate.setEnabled(false);
                                }else if(operationType[0]== OKBLEOperation.OperationType.OperationType_Enable_Notify){
                                    btn_notify.setText("Notification enabled");
                                    btn_notify.setEnabled(false);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFail(int code, final String errMsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addLog("NotifyOrIndicate onFail:"+ errMsg);
                            }
                        });
                    }

                    @Override
                    public void onExecuteSuccess(OKBLEOperation.OperationType type) {
                        operationType[0] =type;
                    }
                });
                break;
            case R.id.btn_clear:
                tv_log.setText("");
                break;

        }
    }

    public void addLog(String log){
        tv_log.append("\n"+formatTimeYMDHMSF(System.currentTimeMillis())+" "+log);
    }


    private  String formatTimeYMDHMSF(long time) {

        Timestamp ts = new Timestamp(time);
        String timeStr=ts.toString();
        String[] array= timeStr.split(" ");
        if(array.length==2){
            return array[1];
        }else{
            return array[0];
        }
    }

    @Override
    public void onConnected(String deviceTAG) {

    }

    @Override
    public void onDisconnected(String deviceTAG) {

    }

    @Override
    public void onReadBattery(String deviceTAG, int battery) {

    }

    @Override
    public void onReceivedValue(String deviceTAG, final String uuid, final byte[] value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addLog("onReceivedValue UUID:"+uuid+" value:"+ OKBLEDataUtils.BytesToHexString(value));
            }
        });
    }

    @Override
    public void onWriteValue(String deviceTAG, String uuid, byte[] value, boolean success) {

    }

    @Override
    public void onReadValue(String deviceTAG, String uuid, byte[] value, boolean success) {

    }

    @Override
    public void onNotifyOrIndicateComplete(String deviceTAG, String uuid, boolean enable, boolean success) {

    }
}
