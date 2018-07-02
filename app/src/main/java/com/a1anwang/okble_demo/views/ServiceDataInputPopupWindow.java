package com.a1anwang.okble_demo.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble_demo.R;
import com.example.zhouwei.library.CustomPopWindow;

import java.util.UUID;

/**
 * Created by a1anwang.com on 2018/5/25.
 */

public class ServiceDataInputPopupWindow implements View.OnClickListener {
    CustomPopWindow popWindow;
    private Context mContext;

    boolean isShowing;

    EditText tv_uuid;
    EditText tv_data;

    Button btn_random;

    View layout_uuid;
    View layout_data;


    boolean inUUID=true;

    private InputListener inputListener;

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public ServiceDataInputPopupWindow(Context context){
        mContext=context;
        int screenWidht=context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight=context.getResources().getDisplayMetrics().heightPixels;
        View view= LayoutInflater.from(context).inflate(R.layout.layout_service_data_input,null);

        popWindow = new CustomPopWindow.PopupWindowBuilder(context)
                .setView(view)//显示的布局，还可以通过设置一个View
                .size(screenWidht, (int) (screenHeight*0.6)) //设置显示的大小，不设置就默认包裹内容
                .setFocusable(true)//是否获取焦点，默认为ture
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .enableOutsideTouchableDissmiss(false)
                .create();//创建PopupWindow


        tv_uuid=view.findViewById(R.id.tv_uuid);
        tv_data=view.findViewById(R.id.tv_data);
        tv_uuid.setOnClickListener(this);
        tv_data.setOnClickListener(this);

        layout_uuid=   view.findViewById(R.id.layout_uuid);
        layout_data=   view.findViewById(R.id.layout_data);

        layout_uuid.setOnClickListener(this);
        layout_data.setOnClickListener(this);



        view.findViewById(R.id.btn_0).setOnClickListener(this);
        view.findViewById(R.id.btn_1).setOnClickListener(this);
        view.findViewById(R.id.btn_2).setOnClickListener(this);
        view.findViewById(R.id.btn_3).setOnClickListener(this);
        view.findViewById(R.id.btn_4).setOnClickListener(this);
        view.findViewById(R.id.btn_5).setOnClickListener(this);
        view.findViewById(R.id.btn_6).setOnClickListener(this);
        view.findViewById(R.id.btn_7).setOnClickListener(this);
        view.findViewById(R.id.btn_8).setOnClickListener(this);
        view.findViewById(R.id.btn_9).setOnClickListener(this);
        view.findViewById(R.id.btn_a).setOnClickListener(this);
        view.findViewById(R.id.btn_b).setOnClickListener(this);
        view.findViewById(R.id.btn_c).setOnClickListener(this);
        view.findViewById(R.id.btn_d).setOnClickListener(this);
        view.findViewById(R.id.btn_e).setOnClickListener(this);
        view.findViewById(R.id.btn_f).setOnClickListener(this);
        view.findViewById(R.id.btn_x).setOnClickListener(this);
        view.findViewById(R.id.btn_done).setOnClickListener(this);
        view.findViewById(R.id.btn_back).setOnClickListener(this);
        view.findViewById(R.id.btn_line).setOnClickListener(this);
        btn_random= view.findViewById(R.id.btn_random);
        btn_random.setOnClickListener(this);


    }

    public void showAt(View parent,int gravity,int x,int y){
        popWindow.showAtLocation(parent,gravity,x,y);
        isShowing=true;
    }

    public void dismiss(){
        if(popWindow!=null){
            popWindow.dissmiss();
        }
        isShowing=false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_uuid:
                inUUID=true;
                layout_uuid.setBackgroundResource(R.drawable.bg_edit_focus);
                layout_data.setBackground(null);
                btn_random.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_data:
                inUUID=false;
                layout_data.setBackgroundResource(R.drawable.bg_edit_focus);
                layout_uuid.setBackground(null);
                btn_random.setVisibility(View.GONE);
                break;
            case R.id.btn_0:
                if(inUUID){
                    tv_uuid.append("0");
                }else{
                    tv_data.append("0");
                }
                break;
            case R.id.btn_1:
                if(inUUID){
                    tv_uuid.append("1");
                }else{
                    tv_data.append("1");
                }
                break;
            case R.id.btn_2:
                if(inUUID){
                    tv_uuid.append("2");
                }else{
                    tv_data.append("2");
                }
                break;
            case R.id.btn_3:
                if(inUUID){
                    tv_uuid.append("3");
                }else{
                    tv_data.append("3");
                }
                break;
            case R.id.btn_4:
                if(inUUID){
                    tv_uuid.append("4");
                }else{
                    tv_data.append("4");
                }
                break;
            case R.id.btn_5:
                if(inUUID){
                    tv_uuid.append("5");
                }else{
                    tv_data.append("5");
                }
                break;
            case R.id.btn_6:
                if(inUUID){
                    tv_uuid.append("6");
                }else{
                    tv_data.append("6");
                }
                break;
            case R.id.btn_7:
                if(inUUID){
                    tv_uuid.append("7");
                }else{
                    tv_data.append("7");
                }
                break;
            case R.id.btn_8:
                if(inUUID){
                    tv_uuid.append("8");
                }else{
                    tv_data.append("8");
                }
                break;
            case R.id.btn_9:
                if(inUUID){
                    tv_uuid.append("9");
                }else{
                    tv_data.append("9");
                }
                break;
            case R.id.btn_a:
                if(inUUID){
                    tv_uuid.append("A");
                }else{
                    tv_data.append("A");
                }
                break;
            case R.id.btn_b:
                if(inUUID){
                    tv_uuid.append("B");
                }else{
                    tv_data.append("B");
                }
                break;
            case R.id.btn_c:
                if(inUUID){
                    tv_uuid.append("C");
                }else{
                    tv_data.append("C");
                }
                break;
            case R.id.btn_d:
                if(inUUID){
                    tv_uuid.append("D");
                }else{
                    tv_data.append("D");
                }
                break;
            case R.id.btn_e:
                if(inUUID){
                    tv_uuid.append("E");
                }else{
                    tv_data.append("E");
                }
                break;
            case R.id.btn_f:
                if(inUUID){
                    tv_uuid.append("F");
                }else{
                    tv_data.append("F");
                }
                break;
            case R.id.btn_line:
                if(inUUID){
                    tv_uuid.append("-");
                }else{
                    tv_data.append("-");
                }
                break;
            case R.id.btn_x:
                String nowStr;
                if(inUUID){
                    nowStr =tv_uuid.getText().toString();
                    if(nowStr.length()>0){
                        tv_uuid.setText(nowStr.substring(0,nowStr.length()-1));
                    }
                }else{
                    nowStr =tv_data.getText().toString();
                    if(nowStr.length()>0){
                        tv_data.setText(nowStr.substring(0,nowStr.length()-1));
                    }
                }
                break;
            case R.id.btn_done:
                String uuid=tv_uuid.getText().toString();
                if(!OKBLEDataUtils.isValidShortUUID(uuid) &&!OKBLEDataUtils.isValidUUID(uuid)){
                    Toast.makeText(mContext," uuid is not valid",Toast.LENGTH_SHORT).show();
                    return;
                }
                String value=tv_data.getText().toString();
                if(TextUtils.isEmpty(value)){
                    Toast.makeText(mContext," data is not valid",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(inputListener!=null){
                    inputListener.onInputComplete(uuid,value);
                }
                tv_uuid.setText("");
                tv_data.setText("");
                popWindow.dissmiss();

                break;
            case R.id.btn_back:
                tv_uuid.setText("");
                tv_data.setText("");
                popWindow.dissmiss();
                break;
            case R.id.btn_random:
                tv_uuid.setText(UUID.randomUUID().toString().toUpperCase());
                break;
        }
    }


    public interface InputListener{
        void onInputComplete(String uuid,String value);
    }
}
