package com.gditc.notepad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gditc.notepad.db.MyDbHelper;
import com.gditc.notepad.db.MyDbInfo;
import com.gditc.notepad.util.MyApplication;
import com.gditc.notepad.util.PlayAnimActivity;

public class NotesActivity extends PlayAnimActivity {

	private Button btn_mainInterface = null;
	private Button btn_addnote = null;
	private Button btn_search = null;
	private Button btn_addType = null;

	private TextView tv_category = null;
	private TextView tv_num = null;
	private ListView lv02 = null;

	private List<Map<String, Object>> list = null;
	private Map<String, Object> map = null;

	private MyDbHelper db = null;	//数据库对象
	private Cursor  cursor = null;	//游标
	private String sql = "";		//sql语句

	private String value = "0";		//新建分类时， 该分类笔记的初始数量

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.notes);
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

		tv_category = (TextView) this.findViewById(R.id.category);
		tv_num = (TextView) this.findViewById(R.id.num);
		lv02 = (ListView) this.findViewById(R.id.lv02);
	}

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("deprecation")
	private void initInfo() {
		//实例化数据库对象
		db = MyDbHelper.getInstance(this.getApplicationContext());
		//打开数据库
		db.open();

		//为ListView类型的lv02注册上下文菜单
		registerForContextMenu(lv02);

		//启动获取该Note的Intent
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("data");

		String category = bundle.getString("category");
		int num = 0;
		sql = "SELECT COUNT(*) FROM tbl_note WHERE note_type='" + category + "'";
		cursor = db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			num = cursor.getInt(0);
		}

		tv_category.setText(category);
		tv_num.setText(String.valueOf(num));

		//创建数据源
		list = new ArrayList<Map<String, Object>>();	//涉及知识：多态、泛型

		sql = "SELECT * FROM tbl_note WHERE note_type='" + category + "'";
		cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			map = new HashMap<String, Object>();
			map.put("title", cursor.getString(1));
			map.put("content", cursor.getString(3));
			list.add(map);
		}
		String[] from = {"title", "content"};		//创建from数组
		int[] to = {R.id.tvTitle, R.id.tvContent};	//创建to数组
		//实例化一个适配器
		SimpleAdapter sa = new SimpleAdapter(this, list, R.layout.listview02, from, to);
		lv02.setAdapter(sa);	//设置适配器
		//列表选项单击监听事件
		lv02.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				map = new HashMap<String, Object>();
				map = list.get(position);
				Bundle bundle = new Bundle();	//创建一个Bundle对象
				String title = map.get("title").toString();
				//String content = map.get("content").toString();
				bundle.putString("title", title);
				//bundle.putString("content", content);
				//创建一个Intent
				Intent intent = new Intent(getApplicationContext(),
						NoteDetailedActivity.class);
				intent.putExtra("data", bundle);
				//启动intent对应的Activity
				startActivity(intent);
				playAnim();
			}

		});

		btn_mainInterface.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//主界面
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				playAnim();
			}
		});
		btn_addnote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//处理新增笔记操作
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), AddNoteActivity.class);
				startActivity(intent);
				playAnim();
			}
		});
		btn_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//处理查询操作
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), SearchNotesActivity.class);
				startActivity(intent);
				playAnim();
			}
		});
		btn_addType.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//处理创建分类操作， 弹出创建分类对话框
				Bundle args = new Bundle();
				//调用 onCreateDialog(int id, Bundle args)方法
				NotesActivity.this.showDialog(1, args);
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
		map = new HashMap<String, Object>();
		map = list.get(position);
		Bundle bundle = new Bundle();	//创建一个Bundle对象
		String title = map.get("title").toString();
		String content = map.get("content").toString();
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
		map = new HashMap<String, Object>();
		map = list.get(position);
		Bundle bundle = new Bundle();	//创建一个Bundle对象
		String title = map.get("title").toString();
		String content = map.get("content").toString();
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
		map = new HashMap<String, Object>();
		map = list.get(position);
		String title = map.get("title").toString();
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
		map = new HashMap<String, Object>();
		map = list.get(position);
		Bundle bundle = new Bundle();	//创建一个Bundle对象
		String title = map.get("title").toString();
		String content = map.get("content").toString();
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
		//调用onCreate(), 目的是刷新数据
		//onCreate(null);
		initInfo();
	}
}
