package com.gditc.notepad;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.gditc.notepad.db.MyDbHelper;
import com.gditc.notepad.db.MyDbInfo;
import com.gditc.notepad.util.MyApplication;
import com.gditc.notepad.util.PlayAnimActivity;

public class SearchNotesActivity extends PlayAnimActivity {

	private Button btn_mainInterface = null;
	private Button btn_addnote = null;
	private Button btn_search = null;
	private Button btn_addType = null;
	private Button btn_searchNotes = null;

	private EditText et_search = null;
	private ListView lv03 = null;

	private List<String> list = null;
	private ArrayAdapter<String> aa = null;

	private MyDbHelper db = null;	//数据库对象
	private Cursor  cursor = null;	//游标
	private String sql = "";		//sql语句

	private String value = "0";		//新建分类时， 该分类笔记的初始数量
	String keywords = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search_notes);
		MyApplication.getInstance().addActivity(this);
		loadingFormation();
		initInfo();
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_mainInterface = (Button) this.findViewById(R.id.btn_mainInterface);
		btn_addnote = (Button) this.findViewById(R.id.btn_addnote);
		btn_search = (Button) this.findViewById(R.id.btn_search);
		btn_addType = (Button) this.findViewById(R.id.btn_addType);
		btn_searchNotes = (Button) this.findViewById(R.id.btn_searchNotes);
		lv03 = (ListView) this.findViewById(R.id.lv03);
		et_search = (EditText) this.findViewById(R.id.et_search);
	}

	/**
	 * 初始化数据
	 */
	private void initInfo() {
		//实例化数据库对象
		db = MyDbHelper.getInstance(this.getApplicationContext());
		//打开数据库
		db.open();

		//为ListView类型的lv01注册上下文菜单
		registerForContextMenu(lv03);

		//初次进入该activity时， 显示所有笔记标题
		if (keywords.equals("")) {
			sql = "SELECT note_title FROM tbl_note";
			cursor = db.rawQuery(sql, null);
			list = new ArrayList<String>();
			while (cursor.moveToNext()) {
				list.add(cursor.getString(0));
			}
		}

		btn_searchNotes.setOnClickListener(new View.OnClickListener() {

			/**
			 * 模糊查询 , keywords = "%关%键%词%"
			 */
			@Override
			public void onClick(View v) {
				keywords = et_search.getText().toString().trim();
				String[] str = keywords.split("");
				keywords = "";
				for (int i = 0; i < str.length; i++) {
					keywords += str[i] + "%";
				}
				sql = "SELECT note_title FROM tbl_note WHERE note_title LIKE '"
						+ keywords + "'";
				cursor = db.rawQuery(sql, null);
				list = new ArrayList<String>();
				while (cursor.moveToNext()) {
					list.add(cursor.getString(0));
				}
				initInfo();	//再次刷新列表
			}
		});

		aa = new ArrayAdapter<String>(this, R.layout.listview03, R.id.tvNotes, list);
		lv03.setAdapter(aa);
		lv03.setTextFilterEnabled(true);
		lv03.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String title = list.get(position);
				String content = "";
				sql = "SELECT note_content FROM tbl_note WHERE note_title='" + title + "'";
				cursor = db.rawQuery(sql, null);
				if (cursor.moveToNext()) {
					content = cursor.getString(0);
				}
				Bundle bundle = new Bundle();
				bundle.putString("title", title);
				bundle.putString("content", content);
				Intent intent = new Intent(getApplicationContext(),
						NoteDetailedActivity.class);
				intent.putExtra("data", bundle);
				startActivity(intent);
				playAnim();
			}
		});
		btn_mainInterface.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//主界面
				Intent intent = new Intent();
				intent.setClass(SearchNotesActivity.this, MainActivity.class);
				SearchNotesActivity.this.startActivity(intent);
				playAnim();
			}
		});
		btn_addnote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//处理新增笔记操作
				Intent intent = new Intent();
				intent.setClass(SearchNotesActivity.this, AddNoteActivity.class);
				startActivity(intent);
				playAnim();
			}
		});
		btn_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//处理查询操作
				//refresh();
			}
		});
		btn_addType.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				//处理创建分类操作， 弹出创建分类对话框
				Bundle args = new Bundle();
				//调用 onCreateDialog(int id, Bundle args) 方法
				showDialog(1, args);
			}
		});

	}

	//创建上下文菜单时触发该方法
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflator = new MenuInflater(this);
		//装填R.menu.context对应的菜单， 并添加到menu中
		inflator.inflate(R.menu.context02, menu);
	}

	//上下文菜单中菜单项被单击时触发该方法
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.note_look:
			lookNote(menuInfo.position);
			break;

		case R.id.note_edit:
			editNote(menuInfo.position);
			break;

		case R.id.note_delete:
			deleteNote(menuInfo.position);
			break;

		case R.id.note_sendSMS:
			sendNoteBySMS(menuInfo.position);
			break;
		}
		//return super.onContextItemSelected(item);
		return true;
	}

	/**
	 * 查看该笔记
	 * @param position
	 */
	private void lookNote(int position) {

		Bundle bundle = new Bundle();	//创建一个Bundle对象
		String title = list.get(position);
		sql = "SELECT note_content FROM tbl_note WHERE note_title='"
				+ title + "'";
		cursor = db.rawQuery(sql, null);
		String content = "";
		if (cursor.moveToNext()) {
			content = cursor.getString(0);
		}
		bundle.putString("title", title);
		bundle.putString("content", content);
		//创建一个Intent
		Intent intent = new Intent(getApplicationContext(),
				NoteDetailedActivity.class);
		intent.putExtra("data", bundle);
		//启动intent对应的Activity
		startActivity(intent);
		playAnim();
	}

	/**
	 * 编辑该笔记
	 * @param position
	 */
	private void editNote(int position) {

		Bundle bundle = new Bundle();	//创建一个Bundle对象
		String title = list.get(position);
		sql = "SELECT note_content FROM tbl_note WHERE note_title='"
				+ title + "'";
		cursor = db.rawQuery(sql, null);
		String content = "";
		if (cursor.moveToNext()) {
			content = cursor.getString(0);
		}
		bundle.putString("title", title);
		bundle.putString("content", content);
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), UpdateNoteActivity.class);
		intent.putExtra("data", bundle);
		startActivity(intent);
		playAnim();
	}

	/**
	 * 删除该笔记
	 * @param position
	 */
	private void deleteNote(int position) {
		String title = list.get(position);
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
		refresh();
	}

	/**
	 * 以短信方式发送该笔记
	 * @param position
	 */
	private void sendNoteBySMS(int position) {
		Bundle bundle = new Bundle();	//创建一个Bundle对象
		String title = list.get(position);
		sql = "SELECT note_content FROM tbl_note WHERE note_title='"
				+ title + "'";
		cursor = db.rawQuery(sql, null);
		String content = "";
		if (cursor.moveToNext()) {
			content = cursor.getString(0);
		}
		bundle.putString("title", title);
		bundle.putString("content", content);
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), SendNoteBySMSActivity.class);
		intent.putExtra("data", bundle);
		startActivity(intent);
		playAnim();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog.Builder builder = new Builder(this);
		//设置对话框的图标
		builder.setIcon(R.drawable.addtype);
		//设置对话框的标题
		builder.setTitle("创建分类");
		//装载/res/layout/add_type.xml 界面布局
		final LinearLayout addTypeView = (LinearLayout) this.getLayoutInflater()
				.inflate(R.layout.add_type, null);

		builder.setView(addTypeView);	//设置对话框显示的View对象
		builder.setPositiveButton("提 交", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//提交操作处理
				String type_name = ((EditText) addTypeView.findViewById(R.id.noteType))
						.getText().toString().trim();
				sql = "SELECT * FROM tbl_type WHERE type_name='" + type_name + "'";
				cursor = db.rawQuery(sql, null);
				if (cursor.moveToNext()) {
					Toast.makeText(getApplicationContext(), "创建失败！该分类已存在",
							Toast.LENGTH_LONG).show();
				}else if(type_name.equals("") || type_name == null) {
					Toast.makeText(getApplicationContext(), "分类名称不能为空",
							Toast.LENGTH_LONG).show();
				}else{
					int tableIndex = 0;
					String[] fieldNames = new String[2];
					String[] values = null;
					for (int i = 1; i < MyDbInfo.getFieldNames()[tableIndex].length; i++) {
						fieldNames[i-1] = MyDbInfo.getFieldNames()[tableIndex][i];
					}
					values = new String[]{
							type_name,
							value
					};
					long reVal = 0;
					reVal = db.insert(MyDbInfo.getTableNames()[tableIndex], fieldNames, values);
					if(reVal > 0){
						Toast.makeText(getApplicationContext(), "创建分类成功",
								Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(getApplicationContext(), "创建分类失败",
								Toast.LENGTH_LONG).show();
					}
				}
				refresh();
			}
		});
		builder.setNegativeButton("取 消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//取消操作处理

			}
		});
		builder.create();		//创建对话框
		builder.show();			//显示对话框
		return super.onCreateDialog(id, args);
	}

	/**
	 * 刷新, 这种刷新方法，只有一个Activity实例。
	 */
	public void refresh() {
		//onCreate(null);
		initInfo();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		initInfo();
		//调用onCreate(), 目的是刷新数据
		//onCreate(null);	
	}
}
