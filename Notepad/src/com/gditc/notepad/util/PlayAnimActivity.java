package com.gditc.notepad.util;

import java.util.Random;

import com.gditc.notepad.R;

import android.app.Activity;

public class PlayAnimActivity extends Activity {
	
	public PlayAnimActivity() {
		super();
	}
	
	/**
	 * 切换activity时播放动画, 公共代码
	 */
	public void playAnim(){
		Random random = new Random();
		int val = random.nextInt(25);
		switch (val) {
		case 1:
			//淡入淡出效果 
			overridePendingTransition(R.anim.fade, R.anim.hold);
			break;
		case 2:
			overridePendingTransition(R.anim.alpha_rotate,
					R.anim.my_alpha_action);
			break;
		case 3:
			overridePendingTransition(R.anim.alpha_scale_rotate,
					R.anim.my_alpha_action);
			break;
		case 4:
			overridePendingTransition(
					R.anim.alpha_scale_translate_rotate,
					R.anim.my_alpha_action);
			break;
		case 5:
			overridePendingTransition(R.anim.alpha_scale_translate,
					R.anim.my_alpha_action);
			break;
		case 6:
			//中心放大效果

			overridePendingTransition(R.anim.alpha_scale,
					R.anim.my_alpha_action);
			break;
		case 7:
			overridePendingTransition(R.anim.alpha_translate_rotate,
					R.anim.my_alpha_action);
			break;
		case 8:
			overridePendingTransition(R.anim.alpha_translate,
					R.anim.my_alpha_action);
			break;
		case 9:
			overridePendingTransition(R.anim.my_rotate_action,
					R.anim.my_alpha_action);
			break;
		case 10:
			overridePendingTransition(R.anim.my_scale_action,
					R.anim.my_alpha_action);
			break;
		case 11:
			overridePendingTransition(R.anim.my_translate_action,
					R.anim.my_alpha_action);
			break;
		case 12:
			overridePendingTransition(R.anim.myanimation_simple,
					R.anim.my_alpha_action);
			break;
		case 13:
			overridePendingTransition(R.anim.myown_design,
					R.anim.my_alpha_action);
			break;
		case 14:
			overridePendingTransition(R.anim.scale_rotate,
					R.anim.my_alpha_action);
			break;
		case 15:
			overridePendingTransition(R.anim.scale_translate_rotate,
					R.anim.my_alpha_action);
			break;
		case 16:
			overridePendingTransition(R.anim.scale_translate,
					R.anim.my_alpha_action);
			break;
		case 17:
			overridePendingTransition(R.anim.translate_rotate,
					R.anim.my_alpha_action);
			break;
		case 18:
			overridePendingTransition(R.anim.hyperspace_in,
					R.anim.hyperspace_out);
			break;
		case 19:
			overridePendingTransition(R.anim.shake,
					R.anim.my_alpha_action);
			break;
		case 20:
			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_left_out);
			break;
		case 21:
			//从下方移出效果
			overridePendingTransition(R.anim.push_up_in,
					R.anim.push_up_out);
			break;
		case 22:
			overridePendingTransition(R.anim.slide_left,
					R.anim.slide_right);
			break;
		case 23:
			overridePendingTransition(R.anim.slide_top_to_bottom,
					R.anim.my_alpha_action);
			break;
		case 24:
			overridePendingTransition(R.anim.wave_scale,
					R.anim.my_alpha_action);
			break;
		}
	}
}
