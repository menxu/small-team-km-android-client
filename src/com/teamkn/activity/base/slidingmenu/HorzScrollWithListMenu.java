package com.teamkn.activity.base.slidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;

import com.teamkn.R;
import com.teamkn.activity.base.slidingmenu.MyHorizontalScrollView.SizeCallback;

/**
 * This demo uses a custom HorizontalScrollView that ignores touch events, and therefore does NOT allow manual scrolling.
 * 
 * The only scrolling allowed is scrolling in code triggered by the menu button.
 * 
 * When the button is pressed, both the menu and the app will scroll. So the menu isn't revealed from beneath the app, it
 * adjoins the app and moves with the app.
 */
public class HorzScrollWithListMenu extends Activity implements OnGestureListener{
    MyHorizontalScrollView scrollView;
    View menu;
    View app;
    ImageView btnSlide;
//    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(this);
        scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.horz_scroll_with_list_menu, null);
        setContentView(scrollView);

        menu = inflater.inflate(R.layout.horz_scroll_menu, null);
        app = inflater.inflate(R.layout.horz_scroll_app, null);
        ViewGroup tabBar = (ViewGroup) app.findViewById(R.id.tabBar);

        ListView listView = (ListView) app.findViewById(R.id.list);
        ViewUtils.initListView(this, listView, "Item ", 30, android.R.layout.simple_list_item_1);

        listView = (ListView) menu.findViewById(R.id.list);
        ViewUtils.initListView(this, listView, "Menu ", 30, android.R.layout.simple_list_item_1);

        btnSlide = (ImageView) tabBar.findViewById(R.id.BtnSlide);
        btnSlide.setOnClickListener(new ClickListenerForScrolling(scrollView, menu));

        final View[] children = new View[] { menu, app };

        // Scroll to app (view[1]) when layout finished.
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnSlide));
    }

    /**
     * Helper for examples with a HSV that should be scrolled by a menu View's width.
     */
    public static boolean menuOut = false;
    
    public static class ClickListenerForScrolling implements OnClickListener{
        HorizontalScrollView scrollView;
        View menu;
        /**
         * Menu must NOT be out/shown to start with.
         */
        public ClickListenerForScrolling(HorizontalScrollView scrollView, View menu) {
            super();
            this.scrollView = scrollView;
            this.menu = menu;
        }

        @Override
        public void onClick(View v) {
            menu.getContext();

            int menuWidth = menu.getMeasuredWidth();

            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);

            if (!menuOut) {
                // Scroll to 0 to reveal menu
                int left = 0;
                scrollView.smoothScrollTo(left, 0);
            } else {
                // Scroll to menuWidth so menu isn't on screen.
                int left = menuWidth;
                scrollView.smoothScrollTo(left, 0);
            }
            menuOut = !menuOut;
        }

    }
    
    public static class MyOnGestureListener {
    	public static void flag_show_menu(HorizontalScrollView scrollView, View menu){
    		menu.getContext();
            int menuWidth = menu.getMeasuredWidth();
            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);
            if (!menuOut) {
                // Scroll to 0 to reveal menu
                int left = 0;
                scrollView.smoothScrollTo(left, 0);
            } else {
                // Scroll to menuWidth so menu isn't on screen.
                int left = menuWidth;
                scrollView.smoothScrollTo(left, 0);
            }
            menuOut = !menuOut;
    	}
    	
    	static int ji_left_no = 0;
    	static int ji_left_is = 0;
    	
    	public static void flag_show_menu_move(final HorizontalScrollView scrollView, final View menu){
    		menu.getContext();
    		
            final int menuWidth = menu.getMeasuredWidth();
            ji_left_no = menuWidth;
            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);
            
            if (menuOut) {   
            	new Thread(){
            		public void run() {
            			try {	
	               			int left = 0; 
	               			
	                        boolean isrun = true;
	               			while(isrun){
	               				ji_left_no -=1;
	               				menu.post(new Runnable() {
	            					@Override
	            					public void run() {
	            						scrollView.smoothScrollTo(ji_left_no, 0);
	            					}
	            				}) ;
	               				
	               				Thread.sleep(1);
	               				if(ji_left_no<=left){
	               					isrun = false;
	               					ji_left_no = 0;
//	               					menu.setFocusable(false);
	               					menuOut = !menuOut;
	               				}
	               			}	
	           			} catch (InterruptedException e) {
	           				e.printStackTrace();
	           			}
            		};
            	}.start();
                
                
            } else {
            	
            	new Thread(){
            		public void run() {
            			try {	
	              			int left = menuWidth; 
	              			
	                        boolean isrun = true;
	              			while(isrun){
	              				ji_left_is +=1;
	              				menu.post(new Runnable() {					
	            					@Override
	            					public void run() {	
	            						scrollView.smoothScrollTo(ji_left_is, 0);
	            					}
	            				}); 
	              				
	              				Thread.sleep(1);
	              				if(ji_left_is>=left){
	              					isrun = false;
	              					 
	              					ji_left_is = 0;
//	              					menu.setFocusable(true);
	              					menuOut = !menuOut;
	              				}
	              			}	
	          			} catch (InterruptedException e) {
	          				e.printStackTrace();
	          			}
            		};
            	}.start();
            	
            	
            }  
           
    	}
    }

    /**
     * Helper that remembers the width of the 'slide' button, so that the 'slide' button remains in view, even when the menu is
     * showing.
     */
    public static class SizeCallbackForMenu implements SizeCallback {
        int btnWidth;
        View btnSlide;

        public SizeCallbackForMenu(View btnSlide) {
            super();
            this.btnSlide = btnSlide;
        }

        @Override
        public void onGlobalLayout() {
            btnWidth = btnSlide.getMeasuredWidth();
            System.out.println("btnWidth=" + btnWidth);
        }

        @Override
        public void getViewSize(int idx, int w, int h, int[] dims) {
            dims[0] = w;
            dims[1] = h;
            final int menuIdx = 0;
            if (idx == menuIdx) {
                dims[0] = w - btnWidth;
            }
        }
    }

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}
