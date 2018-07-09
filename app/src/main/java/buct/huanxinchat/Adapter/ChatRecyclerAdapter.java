package buct.huanxinchat.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import buct.huanxinchat.R;
import buct.huanxinchat.Utils.SharedPreferenceUtil;

/**
 * Created by tian on 2018/7/8.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_TXT = 0;
    private final static int TYPE_IMAGE = 1;
    private final static int TYPE_MORE = 2;

    private List<EMMessage> messages;
    private Context mContext;
    private String username;
    private Handler mHadler;
    private EMConversation conversation;
    private RecyclerView recyclerView;

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
            case TYPE_MORE:
                bindMoreViewHolder((MoreViewHolder) holder);
                break;
            case TYPE_TXT:
                bindTxtViewHolder((TxtViewHolder) holder, position);
                break;
            case TYPE_IMAGE:
                bindPicViewHoler((PicViewHoler) holder, position);
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MORE:
                return new MoreViewHolder(new TextView(mContext));
            case TYPE_IMAGE:
                return new PicViewHoler(LayoutInflater.from(mContext).inflate(R.layout.chat_item_pic, parent, false));
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
        if (position == 0) {
            return TYPE_MORE;
        }
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
        TextView timeLeft;
        TextView timeRight;
        View left;
        View right;

        public TxtViewHolder(View itemView) {
            super(itemView);
            headImgRight = itemView.findViewById(R.id.chat_item_head_img_right);
            contextRight = itemView.findViewById(R.id.chat_item_content_right);
            contextRight.setMaxWidth(800);
            left = itemView.findViewById(R.id.chat_item_left);
            headImgLeft = itemView.findViewById(R.id.chat_item_head_img_left);
            contextLeft = itemView.findViewById(R.id.chat_item_content_left);
            contextLeft.setMaxWidth(800);
            right = itemView.findViewById(R.id.chat_item_right);
            timeLeft = itemView.findViewById(R.id.message_time_left);
            timeRight = itemView.findViewById(R.id.message_time_right);
        }
    }

    private void bindTxtViewHolder(TxtViewHolder holder, int position) {
        EMMessage emMessage = messages.get(position);
        boolean flag = TextUtils.equals(emMessage.getTo(), username);
        if (flag) {
            holder.left.setVisibility(View.GONE);
            holder.contextRight.setText(((EMTextMessageBody) emMessage.getBody()).getMessage());
            holder.timeRight.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(emMessage.getMsgTime())));
        } else {
            holder.right.setVisibility(View.GONE);
            holder.contextLeft.setText(((EMTextMessageBody) emMessage.getBody()).getMessage());
            holder.timeLeft.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(emMessage.getMsgTime())));
        }
        if (!SharedPreferenceUtil.getChatTimeShow(mContext, username)) {
            holder.timeLeft.setVisibility(View.GONE);
            holder.timeRight.setVisibility(View.GONE);
        }
    }

    public class PicViewHoler extends RecyclerView.ViewHolder {
        ImageView headImgLeft;
        ImageView headImgRight;
        ImageView contextLeft;
        ImageView contextRight;
        TextView timeLeft;
        TextView timeRight;
        View left;
        View right;

        public PicViewHoler(View itemView) {
            super(itemView);
            headImgRight = itemView.findViewById(R.id.chat_item_head_img_right);
            contextRight = itemView.findViewById(R.id.chat_item_content_right);
            contextRight.setMaxWidth(800);
            left = itemView.findViewById(R.id.chat_item_left);
            headImgLeft = itemView.findViewById(R.id.chat_item_head_img_left);
            contextLeft = itemView.findViewById(R.id.chat_item_content_left);
            contextLeft.setMaxWidth(800);
            right = itemView.findViewById(R.id.chat_item_right);
            timeLeft = itemView.findViewById(R.id.message_time_left);
            timeRight = itemView.findViewById(R.id.message_time_right);
        }
    }

    private void bindPicViewHoler(PicViewHoler holder, int position) {
        EMMessage emMessage = messages.get(position);
        boolean flag = TextUtils.equals(emMessage.getTo(), username);
        EMImageMessageBody body = (EMImageMessageBody) emMessage.getBody();
//        Log.d("tsxmylog", "bindPicViewHoler: " + body.getLocalUrl() + ":" + body.getThumbnailUrl() + ":" + body.getRemoteUrl());
        if (flag) {
            holder.left.setVisibility(View.GONE);
            if (emMessage.getTo().equals(username)) {
//                holder.contextRight.setImageBitmap(loadBitmap(body.getLocalUrl()));
                loadImg(holder.contextRight, body.getLocalUrl());
            } else {
                loadImg(holder.contextRight, body.getRemoteUrl());
            }
//            holder.contextRight.setText(((EMTextMessageBody) emMessage.getBody()).getMessage());
            holder.timeRight.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(emMessage.getMsgTime())));
        } else {
            holder.right.setVisibility(View.GONE);
            if (emMessage.getTo().equals(username)) {
//                holder.contextLeft.setImageBitmap(loadBitmap(body.getLocalUrl()));
                loadImg(holder.contextLeft, body.getLocalUrl());
            } else {
                loadImg(holder.contextLeft, body.getRemoteUrl());
            }
//            holder.contextLeft.setImageBitmap(bitmap);
//            holder.contextLeft.setText(((EMTextMessageBody) emMessage.getBody()).getMessage());
            holder.timeLeft.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(emMessage.getMsgTime())));
        }
        if (!SharedPreferenceUtil.getChatTimeShow(mContext, username)) {
            holder.timeLeft.setVisibility(View.GONE);
            holder.timeRight.setVisibility(View.GONE);
        }
    }

    private class MoreViewHolder extends RecyclerView.ViewHolder {

        TextView loadImg;

        public MoreViewHolder(View itemView) {
            super(itemView);
            loadImg = (TextView) itemView;
        }
    }

    private void bindMoreViewHolder(MoreViewHolder holder) {
        holder.loadImg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
        holder.loadImg.setGravity(Gravity.CENTER);
        holder.loadImg.setText("loading...");
        loadData();
    }

    private void loadImg(ImageView view, String path) {
        Glide.with(mContext)
                .load(path)
                .override(800, 600)
                .placeholder(R.drawable.ic_launcher_background)
                .into(view);
    }

    private Bitmap loadBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scalex = width < 100 ? 1 : width / 800;
        Matrix matrix = new Matrix();
        matrix.setScale(scalex, scalex);
        return Bitmap.createBitmap(bitmap, 0, 0, width < 800 ? width : 800, height, matrix, false);
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                conversation = EMClient.getInstance().chatManager().getConversation(username, null, true);
//                Log.d("lalala", "run: "+(conversation==null));
                final EMMessage message;
                final boolean scroll;
                if (messages.size() == 0) {
                    messages.add(null);
                    scroll = true;
                    message = conversation.getLastMessage();
                    messages.add(1, message);
//                    mHadler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (message == null) {
//                                return;
//                            }
////                        Log.d("tsxmylog", "run: "+conversation.loadMoreMsgFromDB(list.get(0).getMsgId(),10));
//
//                            messages.addAll(0, conversation.loadMoreMsgFromDB(message.getMsgId(), 20));
//                            ChatRecyclerAdapter.this.notifyItemInserted(messages.size());
//                            recyclerView.scrollToPosition(getItemCount() - 1);
//                        }
//                    });
                } else {
                    message = messages.get(1);
                    scroll = false;
                }
                mHadler.post(new Runnable() {
                    @Override
                    public void run() {
                        messages.addAll(1, conversation.loadMoreMsgFromDB(message.getMsgId(), 20));
                        ChatRecyclerAdapter.this.notifyDataSetChanged();
                        if (scroll) {
                            recyclerView.scrollToPosition(getItemCount() - 1);
                        }
                    }
                });
            }
        }).start();
    }

    public void insertMessage(EMMessage message) {
        messages.add(message);
        this.notifyItemInserted(messages.size());
        recyclerView.scrollToPosition(getItemCount() - 1);
        conversation.insertMessage(message);
    }

    public void saveToDB() {
        EMClient.getInstance().chatManager().importMessages(messages);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}
