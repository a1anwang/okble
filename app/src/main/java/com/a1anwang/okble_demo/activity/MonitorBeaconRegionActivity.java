package com.a1anwang.okble_demo.activity;

import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.a1anwang.okble.beacon.OKBLEBeacon;
import com.a1anwang.okble.beacon.OKBLEBeaconManager;
import com.a1anwang.okble.beacon.OKBLEBeaconRegion;
import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble_demo.R;
import com.a1anwang.okble_demo.base.BaseActivity;
import com.a1anwang.okble_demo.views.CompleteUUIDInputPopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a1anwang.com on 2018/8/27.
 */

public class MonitorBeaconRegionActivity extends BaseActivity implements OKBLEBeaconManager.OKBLEBeaconRegionListener {
    ListView listView;

    List<OKBLEBeaconRegion> monitoredRegion=new ArrayList<>();

    Map<String,BeaconRegionState> monitoredRegionState=new HashMap<>();

    OKBLEBeaconManager beaconManager;

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_monitor_region);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.stopScan();
    }

    @Override
    public void beforeInitView() {
        beaconManager=new OKBLEBeaconManager(this);
        beaconManager.setRegionListener(this);
    }
    @Override
    public void onEnterBeaconRegion(OKBLEBeaconRegion beaconRegion) {
        BeaconRegionState state=monitoredRegionState.get(beaconRegion.getIdentifier());
        if(state!=null){
            state.isIn=true;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onExitBeaconRegion(OKBLEBeaconRegion beaconRegion) {
        BeaconRegionState state=monitoredRegionState.get(beaconRegion.getIdentifier());
        if(state!=null){
            state.isIn=false;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRangeBeaconsInRegion(List<OKBLEBeacon> beacons) {

    }

    @Override
    public void initView() {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        setHeadVisibility(View.VISIBLE);
        setTitle("Monitor BeaconRegion");
        findViewById(R.id.layout_uuid).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);

        listView=findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }

    @Override
    public void afterInitView() {
        beaconManager.requestLocationPermission();
    }

    @Override
    public void onClickEvent(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                EditText edit_uuid= findViewById(R.id.edit_uuid);
                String uuid=edit_uuid.getText().toString();
                if(!OKBLEDataUtils.isValidUUID(uuid)){
                    return;
                }
                EditText edit_major= findViewById(R.id.edit_major);
                String majorStr=edit_major.getText().toString();

                EditText edit_minor= findViewById(R.id.edit_minor);
                String minorStr=edit_minor.getText().toString();

                int major=-1;
                int minor=-1;
                if(!TextUtils.isEmpty(majorStr)&&TextUtils.isDigitsOnly(majorStr)){
                    major=Integer.valueOf(majorStr);
                }
                if(!TextUtils.isEmpty(minorStr)&&TextUtils.isDigitsOnly(minorStr)){
                    minor=Integer.valueOf(minorStr);
                }

                OKBLEBeaconRegion okbleBeaconRegion;
                if((major>=0&&major<=65535)&&(minor>=0&&minor<=65535)){
                     okbleBeaconRegion=OKBLEBeaconRegion.getInstance(uuid,major,minor);
                }else {
                    if(major>=0&&major<=65535){
                        okbleBeaconRegion=OKBLEBeaconRegion.getInstance(uuid,major);
                    }else{
                        okbleBeaconRegion=OKBLEBeaconRegion.getInstance(uuid);
                    }
                }

                if(monitoredRegionState.containsKey(okbleBeaconRegion.getIdentifier())){
                    return;//已存在，return
                }

                BeaconRegionState beaconRegionState=new BeaconRegionState();
                beaconRegionState.beaconIdentifier=okbleBeaconRegion.getIdentifier();
                monitoredRegionState.put(beaconRegionState.beaconIdentifier,beaconRegionState);
                monitoredRegion.add(okbleBeaconRegion);
                adapter.notifyDataSetChanged();
                beaconManager.startMonitoringForRegion(okbleBeaconRegion);

                break;
            case R.id.layout_uuid:
                showHexInput();
                break;
        }
    }

    private void showHexInput(){
        CompleteUUIDInputPopupWindow popupWindow=new CompleteUUIDInputPopupWindow(this);
        popupWindow.showAt(findViewById(R.id.rootview), Gravity.BOTTOM,0,0);
        popupWindow.setInputListener(new CompleteUUIDInputPopupWindow.InputListener() {
            @Override
            public void onInputComplete(String value) {
                EditText edit_uuid= findViewById(R.id.edit_uuid);
                edit_uuid.setText(value);
            }
        });
    }

    BaseAdapter adapter=new BaseAdapter() {
        @Override
        public int getCount() {
            return monitoredRegion.size();
        }

        @Override
        public Object getItem(int position) {
            return monitoredRegion.get(position);
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
                convertView = getLayoutInflater().inflate(R.layout.list_item_region, null);
                convertView.setTag(viewholder);

                viewholder.tv_uuid = (TextView) convertView.findViewById(R.id.tv_uuid);
                viewholder.tv_major = (TextView) convertView.findViewById(R.id.tv_major);
                viewholder.tv_minor = (TextView) convertView.findViewById(R.id.tv_minor);
                viewholder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);

            }else{
                viewholder = (ViewHolder) convertView.getTag();
            }
            OKBLEBeaconRegion okbleBeaconRegion=monitoredRegion.get(position);
            viewholder.tv_uuid.setText(okbleBeaconRegion.getUuid());
            viewholder.tv_major.setText(okbleBeaconRegion.getMajor()<0? "null":""+okbleBeaconRegion.getMajor());
            viewholder.tv_minor.setText(okbleBeaconRegion.getMinor()<0? "null":""+okbleBeaconRegion.getMinor());

            BeaconRegionState state=monitoredRegionState.get(okbleBeaconRegion.getIdentifier());
            if(state.isIn){
                viewholder.tv_state.setText("In");
                viewholder.tv_state.setTextColor(getResources().getColor(R.color.green));
            }else{
                viewholder.tv_state.setText("Out");
                viewholder.tv_state.setTextColor(getResources().getColor(R.color.red));

            }

            return convertView;
        }
    };



    class ViewHolder {
        TextView tv_uuid;
        TextView tv_major;
        TextView tv_minor;
        TextView tv_state;



    }

    private   class  BeaconRegionState{
        String beaconIdentifier;
        boolean isIn;
    }
}
