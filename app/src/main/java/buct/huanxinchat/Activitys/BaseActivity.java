package buct.huanxinchat.Activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by tian on 2018/7/8.
 */

public class BaseActivity extends AppCompatActivity {

    private BroadcastReceiver forceOffLineReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setMessage("强制下线");
            builder.setCancelable(false);
            builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent1 = new Intent(context,LoginActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                }
            });
            builder.setPositiveButton("重新登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
    }

    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(forceOffLineReceiver,new IntentFilter(""));
    }
}
