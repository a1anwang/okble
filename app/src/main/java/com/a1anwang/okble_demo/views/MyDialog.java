package com.a1anwang.okble_demo.views;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.a1anwang.okble_demo.R;


public class MyDialog extends Dialog {

	View view;
	Context context;
	TextView tv_title, tv_content;
	TextView text_sub_content;
	Button btn_left, btn_right;
 
	View line_vertical;

	float denisty;




	public MyDialog(Context context) {
		super(context, R.style.MyDialog);
		setCancelable(false);
		this.context = context;
		view = LayoutInflater.from(context).inflate(R.layout.layout_mydialog, null);
		 
		tv_title = (TextView) view.findViewById(R.id.text_title);
		tv_content = (TextView) view.findViewById(R.id.text_content);
		btn_left = (Button) view.findViewById(R.id.btn_mydialog_left);
		btn_right = (Button) view.findViewById(R.id.btn_mydialog_right);
		line_vertical = view.findViewById(R.id.line_vertical);
		denisty =context.getResources().getDisplayMetrics().density;
		
		btn_left.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyDialog.this.cancel();
			}
		});
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

			if (btn_right!=null){
				if (btn_right!=null&&btn_right.getText() == null || btn_right.getText().equals("")) {
					btn_right.setVisibility(View.GONE);
					line_vertical.setVisibility(View.GONE);
				}
			}
		this.setContentView(view);
	}

	@Override
	public void setTitle(CharSequence title) {
		tv_title.setText(title);
	}

	/**
	 * setTitle
	 */
	@Override
	public void setTitle(int titleId) {
		tv_title.setText(titleId);

	}

	/**
	 * 
	 * @param content
	 *            dddd
	 */
	public void setContent(CharSequence content) {
		tv_content.setText(content);
	}

	/**
	 * 
	 * @param contentId
	 */
	public void setContent(int contentId) {
		tv_content.setText(contentId);
	}

	/**
	 * 
	 * @param text
	 */
	public void setLeftButtonText(CharSequence text) {
		btn_left.setText(text);
	}
	public void setLeftButtonText(int textid) {
		btn_left.setText(textid);
	}
	/**
	 * 
	 * @param text
	 */
	public void setRightButtonText(CharSequence text) {
		btn_right.setText(text);
	}
	public void setRightButtonText(int textid) {
		btn_right.setText(textid);
	}
	/**
	 * 
	 * @return
	 */
	public Button getLeftButton() {
		return btn_left;
	}

	/**
	 * 
	 * @return
	 */
	public Button getRightButton() {
		return btn_right;
	}

	public TextView getText_sub_content() {
		return text_sub_content;
	}
}
