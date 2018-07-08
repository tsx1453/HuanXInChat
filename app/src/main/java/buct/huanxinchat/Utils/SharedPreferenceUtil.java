package buct.huanxinchat.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tian on 2018/7/8.
 */

public class SharedPreferenceUtil {

    private static String SPNAME = "HuanXinChat";
    private static String LOGINSTATE = "loginState";

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
}
