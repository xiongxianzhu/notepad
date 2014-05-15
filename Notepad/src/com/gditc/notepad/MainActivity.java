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
import android.view.KeyEvent;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gditc.notepad.db.MyDbHelper;
import com.gditc.notepad.db.MyDbInfo;
import com.gditc.notepad.util.MyApplication;
import com.gditc.notepad.util.PlayAnimActivity;

public class MainActivity extends PlayAnimActivity {

	private Button btn_mainInterface = null;
	private Button btn_addnote = null;
	private Button btn_search = null;
	private Button btn_addType = null;

	private TextView tvNum_allnotes = null;

	private long firstTime = 0; 

	private ListView lv01 = null;
	private List<Map<String, Object>> list = null;
	private Map<String, Object> map = null;
	private SimpleAdapter sa = null;

	private MyDbHelper db = null;	//数据库对象
	private Cursor  cursor = null;	//游标
	private String sql = "";		//sql语句

	private String value = "0";		//新建分类时， 该分类笔记的初始数量

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		MyApplication.getInstance().addActivity(this);
		loadingFormation();
		initInfo();
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		lv01 = (ListView) this.findViewById(R.id.lv01);
		btn_mainInterface = (Button) this.findViewById(R.id.btn_mainInterface);
		btn_addnote = (Button) this.findViewById(R.id.btn_addnote);
		btn_search = (Button) this.findViewById(R.id.btn_search);
		btn_addType = (Button) this.findViewById(R.id.btn_addType);
		tvNum_allnotes = (TextView) this.findViewById(R.id.tvNum_allnotes);
	}

	/**
	 * 初始化信息
	 */
	private void initInfo() {
		//实例化数据库对象
		db = MyDbHelper.getInstance(this.getApplicationContext());
		//打开数据库
		db.open();

		//设置拖动之后是否再次显示背景，也就是说设为true后，拖动listview，就不会显示背景图片了
		lv01.setAlwaysDrawnWithCacheEnabled(false); 

		//查询是否存在"默认分类", 且"默认分类"不能被删除
		String defaultNoteType = "默认分类";
		sql = "SELECT * FROM tbl_type WHERE type_name='" + defaultNoteType + "'";
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() == 0) {
			//插入"默认分类"
			int tableIndex = 0;
			String[] fieldNames = new String[2];
			String[] values = null;
			for (int i = 1; i < MyDbInfo.getFieldNames()[tableIndex].length; i++) {
				fieldNames[i-1] = MyDbInfo.getFieldNames()[tableIndex][i];
			}
			values = new String[]{
					defaultNoteType,
					value
			};
			long reVal = 0;
			reVal = db.insert(MyDbInfo.getTableNames()[tableIndex], fieldNames, values);
			if(!(reVal > 0)){
				refresh();
			}
		}

		sql = "SELECT * FROM tbl_type";
		cursor = db.rawQuery(sql, null);
		//为ListView类型的lv01注册上下文菜单
		registerForContextMenu(lv01);
		//创建数据源
		list = new ArrayList<Map<String, Object>>();	//涉及知识：多态、泛型
		while (cursor.moveToNext()) {	//加载所有分类
			map = new HashMap<String, Object>();
			map.put("image", R.drawable.img30);
			map.put("category", cursor.getString(1));
			sql = "SELECT COUNT(*) FROM tbl_note WHERE note_type='" + cursor.getString(1) +"'";
			Cursor c = db.rawQuery(sql, null);
			if (c.moveToNext()) {
				map.put("num", c.getInt(0));
			}else{
				map.put("num", cursor.getInt(2));
			}
			c.close();
			list.add(map);
		}
		//获取所有笔记的数量总和
		sql = "SELECT count(*) FROM tbl_note";
		cursor = db.rawQuery(sql, null);
		if(cursor.moveToNext()){
			tvNum_allnotes.setText(String.valueOf(cursor.getInt(0)));
		}

		//创建from数组
		String[] from = {"image", "category", "num"};
		//创建to数组
		int[] to = {R.id.iv01, R.id.tvCategory, R.id.tvNum};

		sa = new SimpleAdapter(getApplicationContext(), list, R.layout.listview01, from, to);
		lv01.setAdapter(sa);

		lv01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/**
			 * 匿名内部类
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				map = new HashMap<String, Object>();
				map = list.get(position);
				String category = map.get("category").toString();
				//创建一个Bundle对象
				Bundle bundle = new Bundle();
				bundle.putString("category", category);
				//创建一个Intent
				Intent intent = new Intent(MainActivity.this, NotesActivity.class);
				intent.putExtra("data", bundle);
				//启动intent对应的Activity
				startActivity(intent);
				playAnim();
			}
		});
		btn_mainInterface.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//暂时不必处理
			}
		});
		btn_addnote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//处理新增笔记操作
				sql = "SELECT COUNT(*) FROM tbl_type";
				cursor = db.rawQuery(sql, null);
				if (cursor.getCount() == 0) {
					Toast.makeText(getApplicationContext(), "笔记分类不存在， 请先创建分类",
							Toast.LENGTH_LONG).show();
				}else{
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, AddNoteActivity.class);
					startActivity(intent);
					playAnim();		//播放动画
				}
			}
		});
		btn_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//处理查询操作
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SearchNotesActivity.class);
				startActivity(intent);
				playAnim();
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
		inflator.inflate(R.menu.context01, menu);
	}

	//上下文菜单中菜单项被单击时触发该方法
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.mainInterface_update:
			editNoteType(menuInfo.position);
			break;

		case R.id.mainInterface_delete:
			deleteNoteType(menuInfo.position);
			break;
		}
		//return super.onContextItemSelected(item);
		return true;
	}

	/**
	 * 编辑笔记类别名称
	 * @param position
	 */
	private void editNoteType(int position) {
		map = new HashMap<String, Object>();
		/**
		 * ListView点击的时候会有一个position, 可以通过position来获取你listView绑定的数据源中的数据的 
		 * 如果是List<Object>data  可以直接data.get(position).getXxx();来获取Object的属性
		 * 如果是数组 就可以直接a[position];
		 */
		map = list.get(position);
		final String category = map.get("category").toString();

		//装载/res/layout/edit_type.xml界面布局
		final TableRow editNoteType = (TableRow) this.getLayoutInflater()
				.inflate(R.layout.edit_type, null);

		//EditText et = (EditText) this.findViewById(R.id.edit_noteType); //空指针异常
		EditText et = (EditText) editNoteType.findViewById(R.id.edit_noteType);
		et.setText(category);

		//代码风格一:
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
		// 设置对话框的图标
		.setIcon(R.drawable.coffee)
		// 设置对话框的标题
		.setTitle("消 息")
		// 设置对话框显示的View对象
		.setView(editNoteType)
		// 为对话框设置一个"提 示"按钮
		.setPositiveButton("提 交", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//执行提交处理
				String type_name = ((EditText) editNoteType.findViewById(R.id.edit_noteType))
						.getText().toString().trim();
				sql = "SELECT * FROM tbl_type WHERE type_name='" + type_name + "'";
				cursor = db.rawQuery(sql, null);
				if (category.equals("默认分类")) {
					Toast.makeText(getApplicationContext(), "默认分类不能被编辑",
							Toast.LENGTH_LONG).show();
				}else if (cursor.moveToNext()) {
					Toast.makeText(getApplicationContext(), "笔记类别名称不能同名",
							Toast.LENGTH_LONG).show();
				}else if(type_name.equals("") || type_name == null) {
					Toast.makeText(getApplicationContext(), "分类名称不能为空",
							Toast.LENGTH_LONG).show();
				}else{
					int tableIndex = 0;
					String[] updateFieldNames = new String[1];
					String[] updateValues = null;
					updateFieldNames[0] = MyDbInfo.getFieldNames()[tableIndex][1];
					updateValues = new String[]{
							type_name
					};
					int reVal = 0;
					reVal = db.update(MyDbInfo.getTableNames()[tableIndex], updateFieldNames,
							updateValues, "type_name=?", new String[]{category});
					if(reVal > 0){
						tableIndex = 1;
						updateFieldNames = new String[1];
						updateValues = null;
						updateFieldNames[0] = MyDbInfo.getFieldNames()[tableIndex][2];
						updateValues = new String[]{
								type_name
						};
						db.update(MyDbInfo.getTableNames()[tableIndex], updateFieldNames,
								updateValues, "note_type=?", new String[]{category});
						Toast.makeText(getApplicationContext(), "编辑成功", 
								Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(getApplicationContext(), "编辑失败", 
								Toast.LENGTH_LONG).show();
					}
				}
				refresh();
			}
		})
		// 为对话框设置一个"取 消"按钮
		.setNegativeButton("取 消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//执行取消操作

			}
		});
		//创建并显示对话框
		builder.create().show();
	}
	//删除该笔记类别及其下所有子笔记
	private void deleteNoteType(int position) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.warning);
		builder.setTitle("提 示");
		map = list.get(position);
		final String category = map.get("category").toString();
		if (category.equals("默认分类")) {
			builder.setMessage("确定要清空该分类下的所有笔记吗？");
			builder.setPositiveButton("确 定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					//确定操作处理
					int tableIndex = 1;
					int reVal = 0;
					reVal = db.delete(MyDbInfo.getTableNames()[tableIndex],
							"note_type=?", new String[]{category});
					if (reVal > 0) {
						tableIndex = 0;
						reVal = 0;
						String[] updateFieldNames = new String[1];
						String[] updateValues = null;
						updateFieldNames[0] = MyDbInfo.getFieldNames()[tableIndex][2];
						updateValues = new String[]{
								String.valueOf(0)
						};
						reVal = db.update(MyDbInfo.getTableNames()[tableIndex], updateFieldNames,
								updateValues, "type_name=?", new String[]{category});
						if(reVal > 0){
							Toast.makeText(getApplicationContext(), "清空成功", 
									Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), "清空失败", 
									Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "清空失败", 
								Toast.LENGTH_LONG).show();
					}
					refresh();
				}
			});
		}else{
			builder.setMessage("确定要删除该分类吗？\n分类下的笔记将同时被删除");
			builder.setPositiveButton("确 定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					//确定操作处理
					if (category.trim().equals("默认分类")) {
						Toast.makeText(getApplicationContext(), "默认分类不能被删除", 
								Toast.LENGTH_LONG).show();
					}else{
						int tableIndex = 0;
						int reVal = 0;
						reVal = db.delete(MyDbInfo.getTableNames()[tableIndex], "type_name=?",
								new String[]{category});
						if (reVal > 0) {
							tableIndex = 1;
							reVal = 0;
							reVal = db.delete(MyDbInfo.getTableNames()[tableIndex],
									"note_type=?", new String[]{category});
							if (reVal > 0) {
								Toast.makeText(getApplicationContext(), "删除成功", 
										Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "删除失败", 
									Toast.LENGTH_LONG).show();
						}
					}

					refresh();
				}
			});
		}
		builder.setNegativeButton("取 消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//取消操作处理
			}
		});
		builder.create();	//创建对话框
		builder.show();		//显示对话框
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
				//过滤 sql 语句字符串中的注入脚本的处理, 如对非法字符" ' "作禁止输入处理
				String tempStr = type_name;
				type_name = MyApplication.getInstance().FilteSQLStr(type_name);
				if(tempStr.equals(type_name)) {
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
				}else{
					Toast.makeText(getApplicationContext(), "输入了非法字符\" ' \", 请重试",
							Toast.LENGTH_LONG).show();
				}
				
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
		onCreate(null);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//event.getRepeatCount() 这是重复次数。
		//点后退键的时候，为了防止点得过快，触发两次后退事件，故做此设置。
		//建议保留这个判断，增强程序健壮性。
		/*if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//按下的如果是BACK，同时没有重复 , do something...
			dialog();
		}
		 */
		switch(keyCode) {  
		case KeyEvent.KEYCODE_BACK:  
			long secondTime = System.currentTimeMillis();   
			if (secondTime - firstTime > 2000) {   //如果两次按键时间间隔大于2秒，则不退出  
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();   
				firstTime = secondTime;//更新firstTime  
				return true;   
			} else {
				//两次按键小于2秒时，退出应用
				cursor.close();	//关闭游标
				db.close();	//关闭数据库对象
				//完全退出程序
				MyApplication.getInstance().exit();
				//dialog(); 	//弹出退出程序对话框
			}   
			break;  
		}
		return false;
	}

	/**
	 * 连按两次返回键以退出程序时弹出的对话框
	 */
	/*private void dialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("你要离开我了吗？");
		builder.setIcon(R.drawable.offline);
		builder.setTitle("提 示");
		builder.setPositiveButton("确 定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//确定操作处理
				MainActivity.this.finish();		//结束该Activity
				cursor.close();	//关闭游标
				db.close();		//关闭数据库对象
				//完全退出程序
				MyApplication.getInstance().exit();
			}
		});
		builder.setNegativeButton("取 消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//取消操作处理
			}
		});
		builder.create();	//创建对话框
		builder.show();		//显示对话框
	}*/

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
