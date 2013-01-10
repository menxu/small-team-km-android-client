package com.teamkn.activity.dataitem;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.activity.qrcode.QRCodeCameraActivity;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.MusicInfo;
import com.teamkn.model.Product;
import com.teamkn.model.QRCodeResult;

public class CreateDataItemActivity extends TeamknBaseActivity{
	static class RequestCode {
		static String KIND = DataItem.Kind.TEXT;
		static int TYPE = RequestCode.CREATE;
	    final static int CREATE = 1; 
	    final static int UPDATE = 2;
	    final static int QRCODE = 3;
	    final static int MUSIC  = 4;
	}
	TextView show_page_title;
	TextView data_list_title_tv;
	ImageView click_data_item_save_iv;
	EditText create_data_item_title_et,create_data_item_content_et;
	
	LinearLayout include_product;
	LinearLayout include_music_info;
	
	String data_list_public;
	DataList dataList;
	DataItem dataItem;
	
	QRCodeResult qrcode_result;
	Product product;
	MusicInfo music_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_item_create);
		Intent intent = getIntent();
		dataList = (DataList) intent.getSerializableExtra("data_list");
		dataItem = (DataItem) intent.getSerializableExtra("data_item");
		data_list_public = intent.getStringExtra("data_list_public");
		qrcode_result = (QRCodeResult)intent.getExtras().getSerializable("code_result");
		
		music_info = (MusicInfo)intent.getExtras().getSerializable("music_info");
		
		set_type();
		load_UI();
		if(RequestCode.TYPE == RequestCode.QRCODE){
			click_data_item_save_iv(show_page_title);
		}
	}
	private void set_type() {
		boolean qrcode = (qrcode_result != null);
		boolean update = dataItem !=null && !qrcode;
		boolean music = music_info !=null ;
//		boolean create = !(qrcode || update);
		if(qrcode){
			RequestCode.TYPE = RequestCode.QRCODE;
			RequestCode.KIND = DataItem.Kind.PRODUCT;
			return;
		}
		if(update) {
			RequestCode.TYPE = RequestCode.UPDATE;
			return;
		}
		if(music){
			RequestCode.TYPE = RequestCode.MUSIC;
			RequestCode.KIND = DataItem.Kind.MUSIC;
			return;
		}
	}
	private void load_UI() {
		show_page_title = (TextView)findViewById(R.id.show_page_title);
		data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
		create_data_item_title_et = (EditText)findViewById(R.id.create_data_item_title_et);
		create_data_item_content_et = (EditText)findViewById(R.id.create_data_item_content_et);
		include_product = (LinearLayout)findViewById(R.id.include_product);
		include_music_info = (LinearLayout)findViewById(R.id.include_music_info);
		set_page_title();
	}
	private void set_page_title() {
		data_list_title_tv.setText(dataList.title);
		if(RequestCode.TYPE == RequestCode.UPDATE){
			if(dataItem!=null){
				create_data_item_title_et.setText(dataItem.title);
				create_data_item_content_et.setText(dataItem.content);
			}
			show_page_title.setText("编辑条目");
			return;
		}
		if(RequestCode.TYPE == RequestCode.QRCODE){
			if(qrcode_result.farmat == 64){
				create_data_item_content_et.setText(qrcode_result.code);
			}
			show_page_title.setText("创建条目");	
			return;
		}
		if(RequestCode.TYPE == RequestCode.MUSIC){
			create_data_item_title_et.setText(music_info.music_title);
			show_page_title.setText("创建条目");
			show_music_info();
			return;
		}
		show_page_title.setText("创建条目");	
		include_product.setVisibility(View.GONE);
		include_music_info.setVisibility(View.GONE);
	}
	private void show_music_info(){
		if(music_info == null ){
			return;
		}
		RequestCode.TYPE = RequestCode.CREATE;
		
		TextView music_info_album = (TextView)findViewById(R.id.music_info_album);
		TextView music_info_author_name = (TextView)findViewById(R.id.music_info_author_name);
		ImageView music_info_cover_src = (ImageView)findViewById(R.id.music_info_cover_src);
		
		music_info_album.setText(music_info.album_title);
		music_info_author_name.setText(music_info.author_name);
		ImageCache.load_cached_image(music_info.cover_src, music_info_cover_src);
		
		include_music_info.setVisibility(View.VISIBLE);
	}
	public void click_data_item_save_iv(View view){
		final String title_str = create_data_item_title_et.getText().toString();
		final String content_str = create_data_item_content_et.getText().toString();
		
		boolean qrcode_is_str_blank = (product!=null) && BaseUtils.is_str_blank(title_str);
		boolean music_is_str_blank = (product!=null) && BaseUtils.is_str_blank(title_str);
		
		boolean create_is_str_blank = (RequestCode.TYPE == RequestCode.CREATE ) && (BaseUtils.is_str_blank(title_str) || BaseUtils.is_str_blank(content_str) );
		
		
		if(qrcode_is_str_blank || music_is_str_blank){
			 BaseUtils.toast("标题不可以为空");
			 return;
		}
		if(create_is_str_blank && product==null && music_info==null){
			 BaseUtils.toast("标题和内容不可以为空");
			 return;
		}
     	if(!BaseUtils.is_wifi_active(this)){
     		BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
     		return;
     	}

		new TeamknAsyncTask<Void, Void, String>(this,"正在处理") {

			@Override
			public String do_in_background(Void... params)
					throws Exception {
				String back =null;	
				switch (RequestCode.TYPE) {
				case RequestCode.CREATE:
					// # TODO 临时支持 增加 MusicInfo 的构造函数，所以传个 null
					DataItem dataitem =new DataItem(-1, title_str, content_str, null, RequestCode.KIND, dataList.server_data_list_id, null, -1,null,null);
					if(product!=null && product.code!=null){
						dataitem.setProduct(product);
					}
					if(music_info!=null && music_info.music_title!=null){
						dataitem.setMusic_info(music_info);
					}
					back = HttpApi.DataItem.create(dataitem); 
					break;
				case RequestCode.UPDATE:
					dataItem.setTitle(title_str);
					dataItem.setContent(content_str);	
					back = HttpApi.DataItem.update(dataItem); 
					break;
				case RequestCode.QRCODE:
					List<Product> qrcode_data = HttpApi.get_qrcode_search(qrcode_result.code);
					if(qrcode_data.size() > 0){
						product = qrcode_data.get(0);
						break;
					}
					back = "亲，好不意思，没有搜索到";
					return back;
				default:
					break;
				}
				return back;
			}
			@Override
			public void on_success(String result) {
				if(RequestCode.TYPE == RequestCode.QRCODE){
					show_product();
					if(!BaseUtils.is_str_blank(result)){
						BaseUtils.toast(result);
					}
					return;
				}
				if(BaseUtils.is_str_blank(result)){
		         	finish();
		         	BaseUtils.toast(getResources().getString(R.string.save_succeed_show));
				}else{
					BaseUtils.toast(result);
				}
			}
		}.execute();
	}
	private void show_product() {
		if(product == null ){
			return;
		}
		RequestCode.TYPE = RequestCode.CREATE;
		
		TextView data_item_name = (TextView)findViewById(R.id.product_name);
		TextView data_item_kind = (TextView)findViewById(R.id.product_kind);
		TextView data_item_unit = (TextView)findViewById(R.id.product_unit);
		TextView data_item_origin = (TextView)findViewById(R.id.product_origin);
		TextView data_item_vendor = (TextView)findViewById(R.id.product_vendor);
		
		data_item_name.setText(product.name);
		data_item_kind.setText(product.kind);
		data_item_unit.setText(product.unit);
		data_item_origin.setText(product.origin);
		data_item_vendor.setText(product.vendor);
		
		include_product.setVisibility(View.VISIBLE);
	}
	public void click_qrcode_btn(View view){
		Intent intent = new Intent(CreateDataItemActivity.this,QRCodeCameraActivity.class);
		QRCodeResult qrcode_result  =  new QRCodeResult(CreateDataItemActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("qrcode_result", qrcode_result);
		bundle.putSerializable("data_item", dataItem);
		bundle.putSerializable("data_list", dataList);
		bundle.putString("data_list_public", data_list_public);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	// 显示音乐搜索页面
	public void click_music_btn(View view){
		Intent intent = new Intent(CreateDataItemActivity.this,MusicSearchActivity.class);
		intent.putExtra("data_list", dataList);
		startActivity(intent);
	}
}
