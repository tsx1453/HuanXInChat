package buct.huanxinchat.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tian on 2018/7/8.
 */

public class SharedPreferenceUtil {

    private static final String SPNAME = "HuanXinChat";
    private static final String LOGINSTATE = "loginState";
    private static final String AUTOBG = "autobg";

    private static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSP(context).edit();
    }

    public static boolean getLoginState(Context context) {
        return getSP(context).getBoolean(LOGINSTATE, false);
    }

    public static void setLoginState(Context context, boolean state) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(LOGINSTATE, state);
        editor.commit();
    }

    public static boolean getChatTimeShow(Context context, String user) {
        return getSP(context).getBoolean(user + "time", false);
    }

    public static void setChatTimeShow(Context context, String user, Boolean isShow) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(user + "time", isShow);
        editor.commit();
    }

    public static String getChatbackGround(Context context, String user) {
        return getSP(context).getString(user + "back", null);
    }

    public static void setBackGround(Context context, String user, String path) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(user + "back", path);
        editor.commit();
    }

    public static boolean getAutoBg(Context context, String user) {
        return getSP(context).getBoolean(user + AUTOBG, false);
    }

    public static void setAutoBg(Context context, String user, Boolean flag) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(user + AUTOBG, flag);
        editor.commit();
    }

}
