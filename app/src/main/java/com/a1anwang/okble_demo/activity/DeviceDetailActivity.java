package com.a1anwang.okble_demo.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.a1anwang.okble.client.core.OKBLEDeviceListener;
import com.a1anwang.okble.common.OKBLECharacteristicModel;
import com.a1anwang.okble.client.core.OKBLEDevice;
import com.a1anwang.okble.client.core.OKBLEDeviceImp;
import com.a1anwang.okble.common.OKBLEServiceModel;
import com.a1anwang.okble.client.scan.BLEScanResult;
import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble_demo.R;
import com.a1anwang.okble_demo.base.BaseActivity;
import com.a1anwang.okble_demo.views.MyDialog;

import java.util.List;

/**
 * Created by a1anwang.com on 2018/5/22.
 */

public class DeviceDetailActivity extends BaseActivity implements OKBLEDeviceListener, ExpandableListView.OnChildClickListener {
    public  final  String TAG="DeviceDetailActivity";
    public  static  final String EXTRA_BLEScanResult=DeviceDetailActivity.class.getName()+".EXTRA_BLEScanResult";

    BLEScanResult bleScanResult;

    OKBLEDevice okbleDevice;


    private ExpandableListView expandlistview;

    List<OKBLEServiceModel> serviceModels;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(okbleDevice!=null){
            okbleDevice.remove();
            okbleDevice=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(okbleDevice!=null){
            updateDeviceStatus();
        }
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_device_detail);
    }

    @Override
    public void beforeInitView() {
        bleScanResult=getIntent().getParcelableExtra(EXTRA_BLEScanResult);
    }

    @Override
    public void initView() {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        setHeadVisibility(View.VISIBLE);
        expandlistview=findViewById(R.id.expandlistview);
        expandlistview.setAdapter(adapter);
        expandlistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;//可以禁止点击group收起
            }
        });
        expandlistview.setOnChildClickListener(this);
    }

    @Override
    public void afterInitView() {
        if(bleScanResult!=null){
            okbleDevice=new OKBLEDeviceImp(mContext,bleScanResult);
            okbleDevice.addDeviceListener(this);
            okbleDevice.connect(true);

            setTitle(bleScanResult.getMacAddress());
            updateDeviceStatus();
        }
    }

    @Override
    public void onClickEvent(View v) {

    }

    @Override
    public void onHeadRightClick(View v) {
        if(okbleDevice.getDeviceStatus()== OKBLEDeviceImp.DeviceStatus.DEVICE_STATUS_CONNECTED){
            final MyDialog dialog=new MyDialog(this);
            dialog.setTitle("");
            dialog.setContent(getString(R.string.caution_want_to_disconnect));
            dialog.getLeftButton().setText(getString(R.string.yes));
            dialog.getRightButton().setText(getString(R.string.no));
            dialog.getLeftButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    okbleDevice.disConnect(false);
                }
            });
            dialog.getRightButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }else if(okbleDevice.getDeviceStatus()== OKBLEDeviceImp.DeviceStatus.DEVICE_STATUS_DISCONNECTED){
            okbleDevice.connect(true);
            updateDeviceStatus();
        }
    }

    private void updateDeviceStatus(){
        switch (okbleDevice.getDeviceStatus()){
            case DEVICE_STATUS_CONNECTED:
                setRightText("Connected");
                break;
            case DEVICE_STATUS_CONNECTING:
                setRightText("Connecting");
                break;
            case DEVICE_STATUS_DISCONNECTED:
                setRightText("Disconnected");
                break;
        }
    }
    @Override
    public void onConnected(String deviceTAG) {
        LogUtils.e(TAG," onConnected:"+deviceTAG);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDeviceStatus();
                serviceModels=okbleDevice.getServiceModels();
                adapter.notifyDataSetChanged();
            }
        });

    }
    @Override
    public void onDisconnected(String deviceTAG) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDeviceStatus();
            }
        });
    }
    @Override
    public void onReadBattery(String deviceTAG, int battery) {

    }

    @Override
    public void onReceivedValue(String deviceTAG, String uuid, byte[] value) {

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

    BaseExpandableListAdapter adapter=new BaseExpandableListAdapter() {
        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            //默认全部展开
            for(int i = 0; i <getGroupCount(); i++){
                expandlistview.expandGroup(i);
            }
        }

        @Override
        public int getGroupCount() {
            return serviceModels==null?0:serviceModels.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            OKBLEServiceModel serviceModel= serviceModels.get(groupPosition);

            return serviceModel.getCharacteristicModels()==null?0:serviceModel.getCharacteristicModels().size();
        }

        @Override
        public OKBLEServiceModel getGroup(int groupPosition) {
            OKBLEServiceModel serviceModel= serviceModels.get(groupPosition);
            return serviceModel;
        }

        @Override
        public OKBLECharacteristicModel getChild(int groupPosition, int childPosition) {
            OKBLEServiceModel serviceModel= serviceModels.get(groupPosition);
            return serviceModel.getCharacteristicModels().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder groupViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_servicemodel, parent, false);
                groupViewHolder = new GroupViewHolder();
                groupViewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                groupViewHolder.tv_desc= (TextView) convertView.findViewById(R.id.tv_desc);
                convertView.setTag(groupViewHolder);
            } else {
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }
            OKBLEServiceModel serviceModel= getGroup(groupPosition);
            groupViewHolder.tv_title.setText(serviceModel.getUuid());
            groupViewHolder.tv_desc.setText(serviceModel.getDesc());
            return convertView;

        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder childViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_characteristicmodel, parent, false);
                childViewHolder = new ChildViewHolder();
                childViewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                childViewHolder.tv_desc= (TextView) convertView.findViewById(R.id.tv_desc);
                childViewHolder.tv_read= (TextView) convertView.findViewById(R.id.tv_read);
                childViewHolder.tv_write= (TextView) convertView.findViewById(R.id.tv_write);
                childViewHolder.tv_write_no_response= (TextView) convertView.findViewById(R.id.tv_write_no_response);
                childViewHolder.tv_notify= (TextView) convertView.findViewById(R.id.tv_notify);
                childViewHolder.tv_indicate= (TextView) convertView.findViewById(R.id.tv_indicate);


                convertView.setTag(childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }
            OKBLECharacteristicModel characteristicModel=getChild(groupPosition,childPosition);
            childViewHolder.tv_title.setText(characteristicModel.getUuid());
            childViewHolder.tv_desc.setText(characteristicModel.getDesc());
            childViewHolder.tv_read.setVisibility(characteristicModel.isCanRead()?View.VISIBLE:View.GONE);
            childViewHolder.tv_write.setVisibility(characteristicModel.isCanWrite()?View.VISIBLE:View.GONE);
            childViewHolder.tv_write_no_response.setVisibility(characteristicModel.isCanWriteNoResponse()?View.VISIBLE:View.GONE);
            childViewHolder.tv_notify.setVisibility(characteristicModel.isCanNotify()?View.VISIBLE:View.GONE);
            childViewHolder.tv_indicate.setVisibility(characteristicModel.isCanIndicate()?View.VISIBLE:View.GONE);

            if(isLastChild){
                convertView.setBackgroundResource(R.drawable.shape_bottom);
            }else{
                convertView.setBackgroundResource(R.drawable.shape_center);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
        class GroupViewHolder {
            TextView tv_title;
            TextView tv_desc;
        }
        class ChildViewHolder {
            TextView tv_title;
            TextView tv_desc;

            TextView tv_read;
            TextView tv_write;
            TextView tv_write_no_response;
            TextView tv_notify;
            TextView tv_indicate;
        }
    };

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        OKBLECharacteristicModel characteristicModel= (OKBLECharacteristicModel) adapter.getChild(groupPosition,childPosition);
        application.okbleDevice=okbleDevice;
        Intent intent=new Intent(this,CharacteristicActivity.class);
        intent.putExtra(CharacteristicActivity.EXTRA_Characteristic,characteristicModel);
        startActivity(intent);
        return false;
    }
}
