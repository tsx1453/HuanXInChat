package buct.huanxinchat.Adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.adapter.message.EMATextMessageBody;

import java.util.ArrayList;
import java.util.List;

import buct.huanxinchat.R;

/**
 * Created by tian on 2018/7/8.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_TXT = 0;
    private final static int TYPE_IMAGE = 1;

    private List<EMMessage> messages;
    private Context mContext;
    private String username;
    private Handler mHadler;
    private EMConversation conversation;

    public ChatRecyclerAdapter(Context mContext, String username) {
        this.mContext = mContext;
        this.username = username;
        mHadler = new Handler();
        messages = new ArrayList<>();
        loadData();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_TXT:
                bindTxtViewHolder((TxtViewHolder) holder, position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TXT:
            default:
                return new TxtViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_text, parent, false));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (messages.get(position).getType()) {
            case TXT:
                return TYPE_TXT;
            case IMAGE:
                return TYPE_IMAGE;
        }
        return TYPE_TXT;
    }

    public class TxtViewHolder extends RecyclerView.ViewHolder {
        ImageView headImgLeft;
        ImageView headImgRight;
        TextView contextLeft;
        TextView contextRight;
        View left;
        View right;

        public TxtViewHolder(View itemView) {
            super(itemView);
            headImgRight = itemView.findViewById(R.id.chat_item_head_img_right);
            contextRight = itemView.findViewById(R.id.chat_item_content_right);
            left = itemView.findViewById(R.id.chat_item_left);
            headImgLeft = itemView.findViewById(R.id.chat_item_head_img_left);
            contextLeft = itemView.findViewById(R.id.chat_item_content_left);
            right = itemView.findViewById(R.id.chat_item_right);
        }
    }

    private void bindTxtViewHolder(TxtViewHolder holder, int position) {
        EMMessage emMessage = messages.get(position);
        boolean flag = TextUtils.equals(emMessage.getTo(), username);
        if (flag) {
            holder.left.setVisibility(View.GONE);
            holder.contextRight.setText(((EMTextMessageBody) emMessage.getBody()).getMessage());
        } else {
            holder.right.setVisibility(View.GONE);
            holder.contextLeft.setText(((EMTextMessageBody) emMessage.getBody()).getMessage());
        }
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                conversation = EMClient.getInstance().chatManager().getConversation(username,null,true);
//                Log.d("lalala", "run: "+(conversation==null));
                final List<EMMessage> list = conversation.getAllMessages();
                mHadler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null){
                            return;
                        }
                        messages.addAll(list);
                        ChatRecyclerAdapter.this.notifyItemInserted(messages.size());
                    }
                });
            }
        }).start();
    }
}
