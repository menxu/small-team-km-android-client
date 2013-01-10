package com.teamkn.activity.dataitem;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.teamkn.R;
import com.teamkn.Logic.HttpApi;
import com.teamkn.base.activity.TeamknBaseActivity;
import com.teamkn.base.task.TeamknAsyncTask;
import com.teamkn.base.utils.BaseUtils;
import com.teamkn.model.DataList;
import com.teamkn.model.MusicInfo;



public class MusicSearchActivity extends TeamknBaseActivity {
	
	private DataList data_list;
	
	private EditText v_query_text;
	private MusicInfo music_info;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_search);
		
		load_UI();
    }
	
	private void load_UI() {
    	v_query_text = (EditText)findViewById(R.id.query_text);
    	
    	Intent intent = getIntent();
    	data_list = (DataList) intent.getSerializableExtra("data_list");
    	Log.d("aaaa", Integer.toString(data_list.server_data_list_id));
        music_info = (MusicInfo) intent.getSerializableExtra("music_info");
        
	}
	
	public void do_search(View view) {
		final String query = v_query_text.getText().toString();
		
		if (BaseUtils.is_wifi_active(this)) {
			new TeamknAsyncTask<Void, Void, ArrayList<MusicInfo>>(this,R.string.now_search) {
				@Override
				public ArrayList<MusicInfo> do_in_background(Void... params) throws Exception {
					try {
						return HttpApi.DataItem.search_music(query);
					
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				@Override
				public void on_success(ArrayList<MusicInfo> music_info_items) {
					Intent intent = new Intent(MusicSearchActivity.this,
							MusicSearchResultActivity.class);
				
					intent.putExtra("music_info_items", music_info_items);
					intent.putExtra("data_list", data_list);
					startActivity(intent);
					finish();
				}
			}.execute();
		}else{
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
		}
	}
	
	
	public void save_data_item(View view) {
		
		if (BaseUtils.is_wifi_active(this)) {
			new TeamknAsyncTask<Void, Void, Void>() {
				@Override
				public Void do_in_background(Void... params) throws Exception {
					try {
						HttpApi.DataItem.create_music(music_info, data_list.server_data_list_id);
					
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
				@Override
				public void on_success(Void result) {
					Intent intent = new Intent(MusicSearchActivity.this, DataItemListActivity.class);
					intent.putExtra("data_list", data_list);
					intent.putExtra("data_list_public", "follow");
					startActivityForResult(intent, 9);
					finish();
				}
			}.execute();
		}else{
			BaseUtils.toast(getResources().getString(R.string.is_wifi_active_msg));
		}
		
		
		
	}
	
}