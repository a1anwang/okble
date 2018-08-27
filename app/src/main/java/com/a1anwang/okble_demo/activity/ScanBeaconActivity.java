package com.a1anwang.okble_demo.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.a1anwang.okble.beacon.OKBLEBeacon;
import com.a1anwang.okble.beacon.OKBLEBeaconManager;
import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble_demo.R;
import com.a1anwang.okble_demo.base.BaseActivity;
import com.a1anwang.okble_demo.common.MyLinkedHashMap;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class ScanBeaconActivity extends BaseActivity {

    OKBLEBeaconManager scanManager;


    RefreshLayout refreshLayout;

    ListView listview;

    MyLinkedHashMap<String,OKBLEBeacon> scanedResults;

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
            scanManager.startScanBeacon();
        }
        isFirstResume=false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanManager.stopScan();
    }

    @Override
    public void beforeInitView() {
        scanedResults=new MyLinkedHashMap<>();

        scanManager=new OKBLEBeaconManager(this);
        scanManager.setBeaconScanCallback(scanCallBack);
    }

    @Override
    public void initView() {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        setHeadVisibility(View.VISIBLE);
        setTitle("Scan iBeacon");
        refreshLayout=findViewById(R.id.refreshLayout);
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                LogUtils.e("onRefresh ");
                scanedResults.clear();
                refreshLayout.finishRefresh();
                adapter.notifyDataSetChanged();
                scanManager.startScanBeacon();

            }
        });

        listview=findViewById(R.id.listview);
        listview.setAdapter(adapter);

    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void onClickEvent(View v) {

    }

    OKBLEBeaconManager.OKBLEBeaconScanCallback scanCallBack=new OKBLEBeaconManager.OKBLEBeaconScanCallback() {
        @Override
        public void onScanBeacon(OKBLEBeacon beacon) {
            scanedResults.put(beacon.getIdentifier(),beacon);
            adapter.notifyDataSetChanged();
        }
    };

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

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.list_item_beacon, null);
                // 得到控件对象
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                holder.tv_uuid = (TextView) convertView
                        .findViewById(R.id.tv_uuid);
                holder.tv_major = (TextView) convertView
                        .findViewById(R.id.tv_major);
                holder.tv_minor = (TextView) convertView
                        .findViewById(R.id.tv_minor);

                holder.tv_rssi = (TextView) convertView
                        .findViewById(R.id.tv_rssi);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OKBLEBeacon beacon = scanedResults.get(position);

            holder.tv_name.setText(beacon.getName()==null?"[null]":beacon.getName());
            holder.tv_uuid.setText(beacon.getUuid());
            holder.tv_major.setText("" + beacon.getMajor());
            holder.tv_minor.setText("" + beacon.getMinor());
            holder.tv_rssi.setText("" + beacon.getRssi());
            return convertView;
        }
    };
    class ViewHolder {
        TextView tv_name;
        TextView tv_uuid;
        TextView tv_major;
        TextView tv_minor;

        TextView tv_rssi;
    }

}
