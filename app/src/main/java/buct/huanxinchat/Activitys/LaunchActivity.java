package buct.huanxinchat.Activitys;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

import buct.huanxinchat.R;

public class LaunchActivity extends AppCompatActivity {
    private Button button;
    private ImageView imageView;
    private boolean launch = false;
    private Timer timer;
    private int timeCount = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        button = findViewById(R.id.launch_close);
        imageView = findViewById(R.id.bg);
        timer = new Timer();
        button.setText("5s|点击跳过");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setText(String.valueOf(timeCount--) + "s|点击跳过");
                    }
                });
            }
        }, 1000, 1000);
        Glide.with(this)
                .load("http://www.nanbaolongbao.com/advertisement1.jpg")
                .placeholder(R.drawable.advertisement)
                .error(R.drawable.advertisement)
                .skipMemoryCache(true)
                .into(imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launch();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                launch();
            }
        }, 5000);
    }

    private void launch() {
        if (!launch) {
            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
            launch = true;
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
