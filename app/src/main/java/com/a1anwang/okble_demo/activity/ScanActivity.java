package com.a1anwang.okble_demo.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.a1anwang.okble.client.scan.BLEScanResult;
import com.a1anwang.okble.client.scan.DeviceScanCallBack;
import com.a1anwang.okble.client.scan.OKBLEScanManager;
import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble_demo.R;
import com.a1anwang.okble_demo.base.BaseActivity;
import com.a1anwang.okble_demo.common.MyLinkedHashMap;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    OKBLEScanManager scanManager;


    RefreshLayout refreshLayout;

    ListView listview;




    MyLinkedHashMap<String,BLEScanResult> scanedResults;


    private HashMap<String,Boolean> openflags;


    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_scan);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanManager.stopScan();
    }

    boolean isFirstResume=true;
    @Override
    protected void onResume() {
        super.onResume();
        if(!isFirstResume){
            scanManager.startScan();
        }
        isFirstResume=false;

    }

    @Override
    public void beforeInitView() {
        scanedResults=new MyLinkedHashMap<>();
        openflags=new HashMap<>();
        scanManager=new OKBLEScanManager(this);
        scanManager.setScanCallBack(scanCallBack);
    }

    @Override
    public void initView() {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        setHeadVisibility(View.VISIBLE);
        setTitle("Scan peripheral");
        refreshLayout=findViewById(R.id.refreshLayout);
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                LogUtils.e("onRefresh ");
                scanedResults.clear();
                openflags.clear();
                adapter.notifyDataSetChanged();
                scanManager.startScan();

            }
        });

        listview=findViewById(R.id.listview);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void onClickEvent(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        scanManager.stopScan();
        BLEScanResult scanResult=scanedResults.get(position);
        Intent intent=new Intent(this,DeviceDetailActivity.class);
        intent.putExtra(DeviceDetailActivity.EXTRA_BLEScanResult,scanResult);
        startActivity(intent);

    }


    DeviceScanCallBack scanCallBack=new DeviceScanCallBack() {
        @Override
        public void onBLEDeviceScan(BLEScanResult device, int rssi) {
            LogUtils.e(" scan:"+device.toString());
            int value[]= scanedResults.put(device.getMacAddress(),device);

            if(value[1]==1){
                //这是新增数据,
                adapter.notifyDataSetChanged();
            }else{
                //这是重复数据,刷新rssi
                int index=value[0];
                updatePosition(index);
            }
        }

        @Override
        public void onFailed(int code) {
            switch (code){
                case DeviceScanCallBack.SCAN_FAILED_BLE_NOT_SUPPORT:
                    Toast.makeText(mContext,"该设备不支持BLE",Toast.LENGTH_SHORT).show();
                    refreshLayout.finishRefresh(false);
                    break;
                case DeviceScanCallBack.SCAN_FAILED_BLUETOOTH_DISABLE:
                    Toast.makeText(mContext,"请打开手机蓝牙",Toast.LENGTH_SHORT).show();
                    refreshLayout.finishRefresh(false);
                    break;
                case DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE:
                    Toast.makeText(mContext,"请授予位置权限以扫描周围的蓝牙设备",Toast.LENGTH_SHORT).show();
                    refreshLayout.finishRefresh(false);
                    break;
                case DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE_FOREVER:
                    refreshLayout.finishRefresh(false);
                    Toast.makeText(mContext,"位置权限被您永久拒绝,请在设置里授予位置权限以扫描周围的蓝牙设备",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onStartSuccess() {
            refreshLayout.finishRefresh();
        }
    };

    private void updatePosition(int posi) {
        int visibleFirstPosi = listview.getFirstVisiblePosition();
        int visibleLastPosi = listview.getLastVisiblePosition();
        if (posi >= visibleFirstPosi && posi <= visibleLastPosi) {
            View view = listview.getChildAt(posi - visibleFirstPosi);
            ViewHolder holder = (ViewHolder) view.getTag();

            holder.tv_rssi.setText(scanedResults.get(posi).getRssi()+"");
        }
    }

    private BaseAdapter adapter=new BaseAdapter() {
        @Override
        public int getCount() {
            return scanedResults==null?0:scanedResults.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewholder;
            if (convertView == null) {
                viewholder = new ViewHolder();
                convertView =getLayoutInflater().inflate(R.layout.item_scanresult, null);
                convertView.setTag(viewholder);

                viewholder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
                viewholder.tv_mac= (TextView) convertView.findViewById(R.id.tv_mac);
                viewholder.tv_rssi= (TextView) convertView.findViewById(R.id.tv_rssi);

                viewholder.layout_expand=convertView.findViewById(R.id.layout_expand);
                viewholder.tv_raw=convertView.findViewById(R.id.tv_raw);
                viewholder.tv_service_uuid=convertView.findViewById(R.id.tv_service_uuid);
                viewholder.tv_company_id=convertView.findViewById(R.id.tv_company_id);
                viewholder.tv_company_value=convertView.findViewById(R.id.tv_company_value);
                viewholder.tv_service_data=convertView.findViewById(R.id.tv_service_data);
                viewholder.tv_localname=convertView.findViewById(R.id.tv_localname);

                viewholder.btn_expand=convertView.findViewById(R.id.btn_expand);

                viewholder.btn_expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position=(Integer) v.getTag();
                        BLEScanResult bleScanResult=scanedResults.get(position);
                        if(openflags.containsKey(bleScanResult.getMacAddress())){
                           Boolean flag= openflags.get(bleScanResult.getMacAddress());
                           openflags.put(bleScanResult.getMacAddress(),Boolean.valueOf(!flag.booleanValue()));
                        }else{
                            openflags.put(bleScanResult.getMacAddress(),Boolean.valueOf(true));
                        }
                        adapter.notifyDataSetChanged();

                    }
                });
            } else {
                viewholder = (ViewHolder) convertView.getTag();
            }
            viewholder.btn_expand.setTag(Integer.valueOf(position));
            BLEScanResult bleScanResult= scanedResults.get(position);

            boolean opened;
            if(openflags.containsKey(bleScanResult.getMacAddress())){
                opened=openflags.get(bleScanResult.getMacAddress()).booleanValue();
            }else{
                opened=false;

            }

            if(opened){
                viewholder.btn_expand.setRotation(180);
                viewholder.layout_expand.setVisibility(View.VISIBLE);
                viewholder.tv_raw.setText("0x"+OKBLEDataUtils.BytesToHexString(bleScanResult.getAdvertisingData()) );
                ((View)viewholder.tv_raw.getParent()).setVisibility(View.VISIBLE);


                List<String> serviceuuids= bleScanResult.getServiceUuids();
                if(serviceuuids!=null &&serviceuuids.size()>0){
                    viewholder.tv_service_uuid.setText(formatServiceUUids(serviceuuids));
                    ((View)viewholder.tv_service_uuid.getParent()).setVisibility(View.VISIBLE);
                }else{
                    ((View)viewholder.tv_service_uuid.getParent()).setVisibility(View.GONE);
                }
                SparseArray<byte[]> array=bleScanResult.getManufacturerSpecificData();
                if(array!=null&&array.size()>0){
                    viewholder.tv_company_id.setText("0x"+OKBLEDataUtils.intToHexStr(array.keyAt(0)));
                    viewholder.tv_company_value.setText("0x"+OKBLEDataUtils.BytesToHexString(array.valueAt(0)));
                    ((View)viewholder.tv_company_id.getParent().getParent()).setVisibility(View.VISIBLE);
                }else{
                    ((View)viewholder.tv_company_id.getParent().getParent()).setVisibility(View.GONE);
                }
                Map<String,byte[]> servicesDatas= bleScanResult.getServiceData();
                if(servicesDatas!=null&&servicesDatas.size()>0){
                    viewholder.tv_service_data.setText(formatServiceData(servicesDatas));
                    ((View)viewholder.tv_service_data.getParent()).setVisibility(View.VISIBLE);
                }else{
                    ((View)viewholder.tv_service_data.getParent()).setVisibility(View.GONE);
                }

                String completeLocalName=bleScanResult.getCompleteLocalName();
                if(completeLocalName!=null){
                    viewholder.tv_localname.setText(completeLocalName);
                    ((View)viewholder.tv_localname.getParent()).setVisibility(View.VISIBLE);
                }else{
                    ((View)viewholder.tv_localname.getParent()).setVisibility(View.GONE);
                }

            }else{
                viewholder.btn_expand.setRotation(0);
                viewholder.layout_expand.setVisibility(View.GONE);
            }

            String name=bleScanResult.getBluetoothDevice().getName();
            viewholder.tv_name.setText(name==null?"[null]":name);
            viewholder.tv_mac.setText(bleScanResult.getMacAddress());
            viewholder.tv_rssi.setText(bleScanResult.getRssi()+"");


            return convertView;
        }
    };
    class ViewHolder {
        TextView tv_name;
        TextView tv_mac;
        TextView tv_rssi;
        ImageView btn_expand;

        TextView tv_raw;
        TextView tv_service_uuid;
        TextView tv_company_id;
        TextView tv_company_value;
        TextView tv_service_data;
        TextView tv_localname;
        View layout_expand;
    }
    private void expandPosition(int position) {
        BLEScanResult bleScanResult=scanedResults.get(position);
        View childView = listview.getChildAt(position - listview.getFirstVisiblePosition());
        View layout_expand=childView.findViewById(R.id.layout_expand);
        ImageView btn_expand =childView.findViewById(R.id.btn_expand);
        if(btn_expand.getRotation()==0){
            //未展开, 开始展开
            btn_expand.setRotation(180);
            layout_expand.setVisibility(View.VISIBLE);

            TextView tv_raw=childView.findViewById(R.id.tv_raw);
            tv_raw.setText(OKBLEDataUtils.BytesToHexString(bleScanResult.getAdvertisingData()) );
            ((View)tv_raw.getParent()).setVisibility(View.VISIBLE);

            TextView tv_service_uuid=childView.findViewById(R.id.tv_service_uuid);
            List<String> serviceuuids= bleScanResult.getServiceUuids();
            if(serviceuuids!=null &&serviceuuids.size()>0){
                tv_service_uuid.setText(formatServiceUUids(serviceuuids));
                ((View)tv_service_uuid.getParent()).setVisibility(View.VISIBLE);
            }else{
                ((View)tv_service_uuid.getParent()).setVisibility(View.GONE);
            }

        }else{
            //已展开,收起
            btn_expand.setRotation(0);
            layout_expand.setVisibility(View.GONE);
        }
    }


    private String formatServiceUUids(List<String> serviceuuids){
        String str="";
        for (String uuid:serviceuuids){
            str+="0x"+uuid+",";
        }
        return str.substring(0,str.length()-1);
    }


    private String formatServiceData(Map<String,byte[]> servicesDatas){
        String str="";
        for (String key: servicesDatas.keySet()){
            str+="uuid:0x"+key+" data:0x"+OKBLEDataUtils.BytesToHexString(servicesDatas.get(key))+"\n";
        }

        return str.substring(0,str.length()-1);
    }
}
