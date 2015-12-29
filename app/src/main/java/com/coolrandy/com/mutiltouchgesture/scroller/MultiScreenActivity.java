package com.coolrandy.com.mutiltouchgesture.scroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coolrandy.com.mutiltouchgesture.R;


//带有可以切换屏的Activity
public class MultiScreenActivity extends Activity implements OnClickListener {

	private Button bt_scrollLeft;
	private Button bt_scrollRight;
	private MultiViewGroup mulTiViewGroup  ;

	public static int screenWidth  ;  // 屏幕宽度
	public static int scrrenHeight ;  //屏幕高度

	private int curscreen = 0;   // 当前位于第几屏幕  ，共3个"屏幕"， 3个LinearLayout

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//获得屏幕分辨率大小
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		screenWidth = metric.widthPixels ;
		scrrenHeight = metric.heightPixels;
		System.out.println("screenWidth * scrrenHeight --->" + screenWidth + " * " +scrrenHeight);

		setContentView(R.layout.multiview);

		//获取自定义视图的空间引用
		mulTiViewGroup = (MultiViewGroup)findViewById(R.id.mymultiViewGroup);

		bt_scrollLeft = (Button) findViewById(R.id.bt_scrollLeft);
		bt_scrollRight = (Button) findViewById(R.id.bt_scrollRight);

		bt_scrollLeft.setOnClickListener(this);
		bt_scrollRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
			case R.id.bt_scrollLeft:
				if(curscreen > 0) {  //防止屏幕越界
					curscreen -- ;
					Toast.makeText(MultiScreenActivity.this, "第" +(curscreen+1) + "屏", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(MultiScreenActivity.this, "当前已是第一屏",Toast.LENGTH_SHORT).show();
				mulTiViewGroup.scrollTo(curscreen * screenWidth , 0);
				break;
			case R.id.bt_scrollRight:
				if (curscreen < 2 ){ //防止屏幕越界
					curscreen ++ ;
					Toast.makeText(MultiScreenActivity.this, "第" + (curscreen+1) + "屏", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(MultiScreenActivity.this, "当前已是最后一屏",Toast.LENGTH_SHORT).show();
				mulTiViewGroup.scrollTo(curscreen * screenWidth, 0);
				break;
		}
	}

}
