package com.a1anwang.okble_demo.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.a1anwang.okble_demo.R;
import com.example.zhouwei.library.CustomPopWindow;

import me.grantland.widget.AutofitTextView;

/**
 * Created by a1anwang.com on 2018/5/25.
 */

public class HexInputPopupWindow implements View.OnClickListener {
    CustomPopWindow popWindow;

    boolean isShowing;

    AutofitTextView tv_value;

    private InputListener inputListener;

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public HexInputPopupWindow(Context context){
        int screenWidht=context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight=context.getResources().getDisplayMetrics().heightPixels;
        View view= LayoutInflater.from(context).inflate(R.layout.layout_hexinput,null);

        popWindow = new CustomPopWindow.PopupWindowBuilder(context)
                .setView(view)//显示的布局，还可以通过设置一个View
                .size(screenWidht, (int) (screenHeight*0.6)) //设置显示的大小，不设置就默认包裹内容
                .setFocusable(true)//是否获取焦点，默认为ture
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .enableOutsideTouchableDissmiss(false)
                .create();//创建PopupWindow


        tv_value=view.findViewById(R.id.tv_value);
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
            case R.id.btn_0:
                tv_value.append("0");
                break;
            case R.id.btn_1:
                tv_value.append("1");
                break;
            case R.id.btn_2:
                tv_value.append("2");
                break;
            case R.id.btn_3:
                tv_value.append("3");
                break;
            case R.id.btn_4:
                tv_value.append("4");
                break;
            case R.id.btn_5:
                tv_value.append("5");
                break;
            case R.id.btn_6:
                tv_value.append("6");
                break;
            case R.id.btn_7:
                tv_value.append("7");
                break;
            case R.id.btn_8:
                tv_value.append("8");
                break;
            case R.id.btn_9:
                tv_value.append("9");
                break;
            case R.id.btn_a:
                tv_value.append("A");
                break;
            case R.id.btn_b:
                tv_value.append("B");
                break;
            case R.id.btn_c:
                tv_value.append("C");
                break;
            case R.id.btn_d:
                tv_value.append("D");
                break;
            case R.id.btn_e:
                tv_value.append("E");
                break;
            case R.id.btn_f:
                tv_value.append("F");
                break;
            case R.id.btn_x:
                String nowStr=tv_value.getText().toString();
                if(nowStr.length()>0){
                    tv_value.setText(nowStr.substring(0,nowStr.length()-1));
                }
                break;
            case R.id.btn_done:
                if(inputListener!=null){
                    inputListener.onInputComplete(tv_value.getText().toString());
                }
                tv_value.setText("");
                popWindow.dissmiss();

                break;
            case R.id.btn_back:
                tv_value.setText("");
                popWindow.dissmiss();
                break;
        }
    }


    public interface InputListener{
        void onInputComplete(String value);
    }
}
