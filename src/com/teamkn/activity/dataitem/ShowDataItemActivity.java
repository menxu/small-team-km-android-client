package com.teamkn.activity.dataitem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamkn.R;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.cache.image.ImageCache;
import com.teamkn.model.DataItem;
import com.teamkn.model.DataList;
import com.teamkn.model.Product;

public class ShowDataItemActivity extends TeamknBaseActivity{
	private DataList data_list;
	private DataItem data_item;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_data_item);
		
		Intent intent = getIntent();
        data_list = (DataList) intent.getSerializableExtra("data_list");
        data_item = (DataItem) intent.getSerializableExtra("data_item");
        Product product = (Product)intent.getSerializableExtra("product");
        
        TextView data_list_title_tv = (TextView)findViewById(R.id.data_list_title_tv);
        data_list_title_tv.setText(data_list.title);
        
        TextView show_data_item_page_title = (TextView)findViewById(R.id.show_data_item_page_title);
        if( product !=null ){
        	show_data_item_page_title.setText("显示商品信息页面");
        	show_product(product);
        }else{
        	show_data_item_page_title.setText("显示音乐页面");
        	show_music_info();
        }
	}
	private void show_music_info() {
		LinearLayout show_music_info_ll = (LinearLayout)findViewById(R.id.show_music_info_ll);
		show_music_info_ll.setVisibility(View.VISIBLE);
		
		TextView music_title = (TextView)findViewById(R.id.music_title);
		TextView music_info_album = (TextView)findViewById(R.id.music_info_album);
		TextView music_info_author_name = (TextView)findViewById(R.id.music_info_author_name);
		ImageView music_info_cover_src = (ImageView)findViewById(R.id.music_info_cover_src);
    	
		music_title.setText(data_item.music_info.music_title);
    	music_info_album.setText(data_item.music_info.album_title);
    	music_info_author_name.setText(data_item.music_info.author_name);
    	
    	ImageCache.load_cached_image(data_item.music_info.cover_src, music_info_cover_src);
    	
	}
	private void show_product(Product product) {
		LinearLayout show_product_ll = (LinearLayout)findViewById(R.id.show_product_ll);
		show_product_ll.setVisibility(View.VISIBLE);
		
		TextView data_item_title = (TextView)findViewById(R.id.product_title);
		TextView data_item_name = (TextView)findViewById(R.id.product_name);
		TextView data_item_kind = (TextView)findViewById(R.id.product_kind);
		TextView data_item_unit = (TextView)findViewById(R.id.product_unit);
		TextView data_item_origin = (TextView)findViewById(R.id.product_origin);
		TextView data_item_vendor = (TextView)findViewById(R.id.product_vendor);
		
		data_item_title.setText(data_item.title);
		data_item_name.setText(product.name);
		data_item_kind.setText(product.kind);
		data_item_unit.setText(product.unit);
		data_item_origin.setText(product.origin);
		data_item_vendor.setText(product.vendor);
	}
}
