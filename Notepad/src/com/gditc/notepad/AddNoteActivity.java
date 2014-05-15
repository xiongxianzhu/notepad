package com.gditc.notepad;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gditc.notepad.db.MyDbHelper;
import com.gditc.notepad.db.MyDbInfo;
import com.gditc.notepad.util.MyApplication;
import com.gditc.notepad.util.PlayAnimActivity;

public class AddNoteActivity extends PlayAnimActivity {

	private List<String> list = null;
	private ArrayAdapter<String> adapter = null;
	private Button btn_back = null;
	private Button btn_save = null;

	private EditText et_noteTitle = null;
	private Spinner spinner_noteType = null;
	private EditText et_noteContent = null;

	private MyDbHelper db = null;	//数据库对象
	private Cursor  cursor = null;	//游标
	private String sql = "";		//sql语句

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_update_note);
		MyApplication.getInstance().addActivity(this);
		loadingFormation();
		initInfo();

	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back01);
		btn_save = (Button) this.findViewById(R.id.btn_save01);
		et_noteTitle = (EditText) this.findViewById(R.id.et_noteTitle);
		spinner_noteType = (Spinner) this.findViewById(R.id.spinner_noteType);
		et_noteContent = (EditText) this.findViewById(R.id.et_noteContent);
	}

	/**
	 * 初始化数据
	 */
	private void initInfo() {
		//实例化数据库对象
		db = MyDbHelper.getInstance(this.getApplicationContext());
		//打开数据库
		db.open();

		list = new ArrayList<String>();
		sql = "SELECT  type_name FROM tbl_type";
		cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()){
			list.add(cursor.getString(0));
		}

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_noteType.setAdapter(adapter);

		btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				playAnim();
			}
		});
		btn_save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//处理点击"保 存"操作
				int tableIndex = 1;	//MyDbInfo中的TableNames[]中的索引
				String[] fieldNames = new String[3];
				String[] values = null;
				for (int i = 1; i < MyDbInfo.getFieldNames()[tableIndex].length; i++) {
					fieldNames[i-1] = MyDbInfo.getFieldNames()[tableIndex][i];
				}
				String noteTitle = "";
				noteTitle = et_noteTitle.getText().toString().trim();
				String noteType = spinner_noteType.getSelectedItem().toString();
				String noteContent = "";
				noteContent = et_noteContent.getText().toString().trim();
				
				sql = "SELECT * FROM tbl_note WHERE note_title='" + noteTitle+ "'";
				cursor = db.rawQuery(sql, null);
				if (cursor.moveToNext()) {
					Toast.makeText(getApplicationContext(), "该笔记标题已存在，请另命名一个响亮的标题",
							Toast.LENGTH_LONG).show();
				}else if (noteTitle.equals("") || noteContent.equals("")) {
					Toast.makeText(getApplicationContext(), "笔记标题或笔记内容不能为空",
							Toast.LENGTH_LONG).show();
				}else{
					values = new String[]{
							noteTitle, 
							noteType, 
							noteContent
					};

					long reVal = 0;
					reVal = db.insert(MyDbInfo.getTableNames()[tableIndex],
							fieldNames, values);

					if(reVal > 0){
						sql = "SELECT count(*) FROM tbl_note where note_type='"
								+ noteType + "'";
						cursor = db.rawQuery(sql, null);
						int noteNum = 0;
						if(cursor.moveToNext()){
							noteNum = cursor.getInt(0);
						}
						String[] updateFields = new String[1];
						String[] updateValues = null;
						tableIndex = 0;
						for (int i = 2; i < MyDbInfo.getFieldNames()[tableIndex].length; i++) {
							updateFields[i-2] = MyDbInfo.getFieldNames()[tableIndex][i];
						}
						updateValues = new String[]{
								String.valueOf(noteNum)
						};
						db.update(MyDbInfo.getTableNames()[tableIndex], updateFields,
								updateValues, "type_name=?", new String[]{noteType});

						Bundle bundle = new Bundle();
						bundle.putString("category", noteType);
						Intent intent = new Intent();
						intent.setClass(getApplicationContext(),
								NotesActivity.class);
						intent.putExtra("data", bundle);
						startActivity(intent);
						finish();
						playAnim();
						
						Toast.makeText(getApplicationContext(), "保存成功",
								Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(getApplicationContext(), "保存失败",
								Toast.LENGTH_LONG).show();
					}
					refresh();
				}
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
