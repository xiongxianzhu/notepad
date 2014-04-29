package com.gditc.notepad;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gditc.notepad.db.MyDbHelper;
import com.gditc.notepad.db.MyDbInfo;
import com.gditc.notepad.util.MyApplication;
import com.gditc.notepad.util.PlayAnimActivity;

public class NoteDetailedActivity extends PlayAnimActivity {

	private Button btn_back = null;
	private Button btn_edit = null;
	private Button btn_delete = null;

	private TextView tv_title = null;
	private TextView tv_content = null;

	private MyDbHelper db = null;	//数据库对象
	private Cursor  cursor = null;	//游标
	private String sql = "";		//sql语句
	
	/* 
	 * 笔记明细
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.note_detailed);
		MyApplication.getInstance().addActivity(this);
		loadingFormation();
		initInfo();
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back02);
		btn_edit = (Button) this.findViewById(R.id.btn_edit02);
		btn_delete = (Button) this.findViewById(R.id.btn_delete02);
		tv_title = (TextView) this.findViewById(R.id.tv_noteTitle);
		tv_content = (TextView) this.findViewById(R.id.tv_noteContent);
	}

	/**
	 * 初始化数据
	 */
	private void initInfo() {
		//实例化数据库对象
		db = MyDbHelper.getInstance(this.getApplicationContext());
		//打开数据库
		db.open();

		//启动获取该Note的Intent
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("data");
		final String title = bundle.getString("title");
		
		sql = "SELECT * FROM tbl_note WHERE note_title='" + title + "'";
		cursor = db.rawQuery(sql, null);
		if (!cursor.moveToNext()) {
			finish();
		}
		
		String tempContent = "";
		sql = "SELECT note_content FROM tbl_note WHERE note_title='" + title + "'";
		cursor = db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			tempContent = cursor.getString(0);
		}
		final String content = tempContent;
		tv_title.setText(title);
		tv_content.setText(content);
		//对"返回"、 "编辑"、 "删除"3个按钮进行点击事件监听
		btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				playAnim();
			}
		});
		btn_edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Bundle b = new Bundle();
				b.putString("title", title);
				b.putString("content", content);
				Intent i = new Intent();
				i.setClass(getApplicationContext(), UpdateNoteActivity.class);
				i.putExtra("data", b);
				startActivity(i);
				playAnim();
			}
		});
		btn_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int tableIndex = 1;
				int reVal = 0;
				reVal = db.delete(MyDbInfo.getTableNames()[tableIndex], "note_title=?",
						new String[]{title});
				if (reVal > 0) {
					Toast.makeText(getApplicationContext(), "删除成功", 
							Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(), "删除失败", 
							Toast.LENGTH_LONG).show();
				}
				finish();
			}
		});
	}
	
	/**
	 * 重写onResume()方法实现刷新数据
	 */
	@Override
	protected void onResume() {
		super.onResume();
		//调用onCreate(), 目的是刷新数据
		//onCreate(null);
		initInfo();
	}
}
