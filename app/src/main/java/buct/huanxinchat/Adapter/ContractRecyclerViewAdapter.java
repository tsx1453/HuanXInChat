package buct.huanxinchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

import buct.huanxinchat.Activitys.ChatActivity;
import buct.huanxinchat.Items.ContractItem;
import buct.huanxinchat.R;

/**
 * Created by tian on 2018/7/8.
 */

public class ContractRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = "lalala";
    private List<ContractItem> mList;
    private Context mContext;
    private Handler mHandler;

    public ContractRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
        mList = new ArrayList<>();
        mHandler = new Handler();
        getData();
    }

    public void getData() {
        EMClient.getInstance().contactManager().aysncGetAllContactsFromServer(new EMValueCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> value) {
                Log.d(TAG, "onSuccess: "+value.size());
                for (String i:value){
                    mList.add(new ContractItem(i));
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ContractRecyclerViewAdapter.this.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                Log.d(TAG, "onError: "+errorMsg);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContractItem item = mList.get(position);
        ContractViewHolder holder1 = (ContractViewHolder) holder;
        holder1.userId.setText(item.getUserId());
        holder1.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra(ChatActivity.CHAT_EXTRA_USER_ID,item.getUserId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContractViewHolder(LayoutInflater.from(mContext).inflate(R.layout.contract_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ContractViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView userId;
        TextView userNickName;
        View view;

        public ContractViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = itemView.findViewById(R.id.user_img);
            userId = itemView.findViewById(R.id.user_id);
//            userNickName = itemView.findViewById(R.id.user_nick_name);
        }
    }
}
