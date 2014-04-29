package com.gditc.notepad;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gditc.notepad.util.MyApplication;
import com.gditc.notepad.util.PlayAnimActivity;

@SuppressWarnings("deprecation")
public class SendNoteBySMSActivity extends PlayAnimActivity {
	
	private Button btn_back = null;
	private Button btn_send = null;
	
	private EditText et_telNumber = null;
	private TextView tv_message = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.send_note_by_sms);
		MyApplication.getInstance().addActivity(this);
		loadingFormation();
		initInfo();
	}

	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back03);
		btn_send = (Button) this.findViewById(R.id.btn_send);
		et_telNumber = (EditText) this.findViewById(R.id.et_telNumber);
		tv_message = (TextView) this.findViewById(R.id.tv_message);
	}

	private void initInfo() {
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("data");
		final String title = bundle.getString("title");
		final String content = bundle.getString("content");
		
		final String message = "笔记标题：" + title + "\n笔记内容：\n"
				+ content;
		tv_message.setText(message);
		
		btn_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				playAnim();
			}
		});
		btn_send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String telNum = et_telNumber.getText().toString().trim();
				
				SmsManager manager = SmsManager.getDefault();
				ArrayList<String> texts = manager.divideMessage(message);
				for (String text : texts) {
					manager.sendTextMessage(telNum, null, text, null, null);
				}
				Toast.makeText(getApplicationContext(),
						R.string.send_success, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	/**
	 * 刷新, 这种刷新方法，只有一个Activity实例。
	 */
	protected void refresh() {
		//onCreate(null);
		initInfo();
	}
}
