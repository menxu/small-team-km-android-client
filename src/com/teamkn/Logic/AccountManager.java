package com.teamkn.Logic;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import com.teamkn.base.http.CookieHelper;
import com.teamkn.base.http.TeamknHttpRequest.AuthenticateException;
import com.teamkn.model.AccountUser;
import com.teamkn.model.database.AccountUserDBHelper;

public class AccountManager {
    public static void login(String cookies, String info) throws Exception {
        AccountUser account_user = new AccountUser(cookies, info);

        if (AccountUserDBHelper.save(account_user)) {
            switch_account(account_user);
        } else {
            throw new AuthenticateException();
        }
    }
    
    public static void switch_account(AccountUser account_user) {
        TeamknPreferences.set_current_user_id(account_user.user_id);
    }

    public static CookieStore get_cookie_store() {
    	String cookies_string = current_user().cookies;
//    	BasicCookieStore cookie_store = new BasicCookieStore();
    	BasicCookieStore cookie_store = (BasicCookieStore) CookieHelper.parse_string_to_cookie_store(cookies_string);
       
//        try {
//            if (!BaseUtils.is_str_blank(cookies_string)) {
//                JSONArray json_arr = new JSONArray(cookies_string);
//                for (int i = 0; i < json_arr.length(); i++) {
//                    JSONObject json = (JSONObject) json_arr.get(i);
//                    String name = (String) json.get("name");
//                    String value = (String) json.get("value");
//                    String domain = (String) json.get("domain");
//                    String path = (String) json.get("path");
//                    BasicClientCookie cookie = new BasicClientCookie(name, value);
//                    cookie.setDomain(domain);
//                    cookie.setPath(path);
//                    cookie_store.addCookie(cookie);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        
        return cookie_store;
    }

    public static AccountUser current_user() {
        int user_id = TeamknPreferences.current_user_id();
        return AccountUserDBHelper.find(user_id);
    }
    public static boolean is_logged_in() {
        return !current_user().is_nil();
    }

}
