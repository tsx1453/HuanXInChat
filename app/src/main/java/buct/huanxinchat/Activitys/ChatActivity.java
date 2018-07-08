package buct.huanxinchat.Activitys;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.adapter.EMAConversation;

import buct.huanxinchat.Adapter.ChatRecyclerAdapter;
import buct.huanxinchat.R;

public class ChatActivity extends BaseActivity {

    public static String CHAT_EXTRA_USER_ID = "chat_user_id";
    private String chatUserId;
    private EditText editText;
    private Button sendBtn;
    private RecyclerView recyclerView;
    private EMConversation conversation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initData(getIntent());
        initView();
        initRecycler();
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);
        editText = findViewById(R.id.chat_input);
        sendBtn = findViewById(R.id.chat_send_btn);
        recyclerView = findViewById(R.id.chat_recycler);
    }

    private void initRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ChatRecyclerAdapter(this, chatUserId));
    }

    private void initData(Intent intent) {
        chatUserId = intent.getStringExtra(CHAT_EXTRA_USER_ID);
    }
}
