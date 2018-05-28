package com.a1anwang.okble_demo.views;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by a1anwang.com on 2018/5/16.
 */

public class MyProgressDialog {
    private ProgressDialog progressDialog;

    public MyProgressDialog(Context context,String title, String content){

        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        if(title!=null){
            progressDialog.setTitle(title);
        }
        if(content!=null){
            progressDialog.setMessage(content);
        }


    }


    public void show(){
        progressDialog.show();
    }

    public boolean isShowing(){
        return progressDialog.isShowing();
    }
    public void dismiss(){
        progressDialog.dismiss();
    }
}
