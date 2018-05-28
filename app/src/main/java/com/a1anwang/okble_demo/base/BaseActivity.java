package com.a1anwang.okble_demo.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.a1anwang.okble_demo.APP;
import com.a1anwang.okble_demo.MyUtils;
import com.a1anwang.okble_demo.views.MyProgressDialog;
import com.a1anwang.okble_demo.R;
import com.gyf.barlibrary.ImmersionBar;


/**
 * Created by a1anwang.com on 2017/12/27.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    protected ImmersionBar mImmersionBar;//状态栏颜色框架

    /**
     * 是否全屏
     */
    public boolean fullScreen = false;


    public APP application;

    protected Context mContext;


    private ViewFlipper mContentView;
    protected RelativeLayout mHeadLayout;
    protected ImageView mBtnLeft;
    protected TextView   mHeadLeftText;
    protected ImageView mBtnRight;
    protected TextView mTitleText;
    protected TextView mHeadRightText;
    protected LinearLayout layout_left;
    protected LinearLayout layout_right;
    public String simpleName(){
        return BaseActivity.this.getClass().getSimpleName();
    }

    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (this.getCurrentFocus() != null) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mImmersionBar != null){
            mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fullScreen= isFullScreen();
        setFullScreen(fullScreen);
        super.setContentView(R.layout.activity_base);

        mImmersionBar= ImmersionBar.with(this);
        mContext = this;
        application= (APP) getApplication();


        initHeaderView();
        setHeadVisibility(View.GONE);
        setContentLayout();
        // 以下代码用于去除阴影
        if(Build.VERSION.SDK_INT >= 21) {
            ActionBar actionBar=getSupportActionBar();
            if(actionBar!=null){
                actionBar.setElevation(0);
            }
        }
        beforeInitView();
        initView();
        afterInitView();
        setStatusBarColor(R.color.colorPrimary);
    }


    public void setStatusBarColor(int color){
        boolean showingActionBar=false;
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
           if(actionBar.isShowing()){
               showingActionBar=true;
           }
        }
        if(color==R.color.white){

            mImmersionBar.fitsSystemWindows(true).supportActionBar(showingActionBar).statusBarColor(color).statusBarDarkFont(true).init();
        }else{
            mImmersionBar.fitsSystemWindows(true).supportActionBar(showingActionBar).statusBarColor(color).statusBarDarkFont(false).init();
        }
    }

    protected   void initHeaderView(){

        // 初始化公共头部
        mContentView = (ViewFlipper) findViewById(R.id.layout_container);
        mHeadLayout = (RelativeLayout) findViewById(R.id.layout_head);
        mHeadLeftText= (TextView) findViewById(R.id.tv_left);
        mHeadRightText = (TextView) findViewById(R.id.text_right);
        mBtnLeft = (ImageView) findViewById(R.id.btn_left);
        mBtnRight = (ImageView) findViewById(R.id.btn_right);
        mTitleText = (TextView) findViewById(R.id.tv_title);

        layout_left=findViewById(R.id.layout_left);
        layout_right=findViewById(R.id.layout_right);
    }





    /**
     * 是否全屏
     */
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen=fullScreen;

        if (fullScreen) {
            //注释掉的部分是之前继承Activity时候设置全屏的代码
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {

        }
    }
    public abstract boolean isFullScreen();
    /**
     * 设置布局文件
     */
    public abstract void setContentLayout();

    /**
     * 在实例化控件之前的逻辑操作
     */
    public abstract void beforeInitView();

    /**
     * 实例化控件
     */
    public abstract void initView();



    /**
     * 实例化控件之后的操作
     */
    public abstract void afterInitView();
    /**
     * onClick方法的封装
     */
    public abstract void onClickEvent(View v);


    @Override
    public void onClick(View v) {
        if(!MyUtils.isFastClick()){
            onClickEvent(v);
        }
    }
    /**
     * 获得屏幕的宽度
     */
    public int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    /**
     * 获得屏幕的高度
     */
    public int getScreenHeigh() {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenHeigh = dm.heightPixels;
        return screenHeigh;
    }


    @Override
    public void setContentView(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        mContentView.addView(view, lp);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }


    /**
     * 设置头部是否可见
     *
     * @param visibility
     */
    public void setHeadVisibility(int visibility) {
        mHeadLayout.setVisibility(visibility);
    }


    /**
     * 点击左按钮
     */
    public void onHeadLeftClick(View v) {
        finish();
    }

    /**
     * 点击右按钮
     */
    public void onHeadRightClick(View v) {

    }

    public void setHeadRightVisibility(int visibility) {
        mHeadLayout.setVisibility(visibility);
    }


    public ImageView getHeadRightButton() {
        return mBtnRight;
    }

    public void setRightText(String text){
        mHeadRightText.setText(text);
    }
    public void setRightText(int resid){
        mHeadRightText.setText(resid);
    }
    public void setLeftText(String text){
        mHeadLeftText.setText(text);
    }
    public void setLeftText(int resid){
        mHeadLeftText.setText(resid);
    }


    /**
     * 设置左边是否可见
     *
     * @param visibility
     */
    public void setHeadLeftButtonVisibility(int visibility) {
        mBtnLeft.setVisibility(visibility);
    }

    /**
     * 设置右边是否可见
     *
     * @param visibility
     */
    public void setHeadRightButtonVisibility(int visibility) {
        mBtnRight.setVisibility(visibility);
    }

    /**
     * 设置标题
     */
    public void setTitle(int titleId) {
        setTitle(getString(titleId), false);
    }

    /**
     * 设置标题
     */
    public void setTitle(int titleId, boolean hideLeftIcon) {
        setTitle(getString(titleId), hideLeftIcon);
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        setTitle(title, false);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title, boolean hideLeftIcon) {
        mTitleText.setText(title);
        if (hideLeftIcon) {
             mBtnLeft.setVisibility(View.GONE);
        } else {
            mBtnLeft.setVisibility(View.VISIBLE);
        }
    }



    // ----------Activity跳转----------//
    public void startActivityClearOther(Class<?> targetClass) {
        Intent intent = new Intent(this, targetClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
    public void startActivityClearOther(Class<?> targetClass, Bundle bundle) {
        Intent intent = new Intent(this, targetClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startActivity(Class<?> targetClass) {
        Intent intent = new Intent(this, targetClass);
        startActivity(intent);
    }
    public void startActivity(Class<?> targetClass, Bundle bundle) {
        Intent intent = new Intent(this, targetClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }



    MyProgressDialog progressDialog;
    public void showProgressDialog(String title,String content) {

        if(progressDialog==null){
            progressDialog = new MyProgressDialog(mContext,title,content);
        }

        progressDialog.show();
    }


    public void dismissProgressDialog() {
        if(progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }
    }

    /**
     * 回到桌面,相当于按下home键
     */
    public void toHome(){
        Intent home=new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }



    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        ViseLog.e(" ------onTouchEvent------");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.getCurrentFocus() != null) {
                if (this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }






}
