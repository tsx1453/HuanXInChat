package buct.huanxinchat.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.activity.CaptureActivity;
import com.hyphenate.chat.EMClient;

import buct.huanxinchat.Adapter.ContractRecyclerViewAdapter;
import buct.huanxinchat.R;
import buct.huanxinchat.Utils.QRCodeUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tian on 2018/7/8.
 */

public class ContractFragment extends Fragment {
    private static ContractFragment fragment;
    private ContractRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private Button qrCodeShow;
    private Button qrCodeScan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contract,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        recyclerView = view.findViewById(R.id.contract_recycler);
        qrCodeScan = view.findViewById(R.id.qr_scan);
        qrCodeShow = view.findViewById(R.id.qr_code_show);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new ContractRecyclerViewAdapter(this.getContext());
        recyclerView.setAdapter(adapter);
        qrCodeShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(EMClient.getInstance().getCurrentUser(),600,600);
                final Dialog  dialog = new Dialog(ContractFragment.this.getContext(),R.style.Theme_PicDialog);
                ImageView imageView = new ImageView(ContractFragment.this.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(500,500));
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(imageView);
                dialog.setCancelable(true);
                dialog.show();
            }
        });
        qrCodeScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContractFragment.this.getContext(), CaptureActivity.class);
                startActivityForResult(intent, 99);
            }
        });
    }

    public static ContractFragment newInstance(){
        if (fragment==null){
            fragment = new ContractFragment();
        }
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 99) { //RESULT_OK = -1
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Log.d("tsx-mylog", "ContractFragment->onActivityResult: "+scanResult);
        }
        EditText text = new EditText(this.getContext());
        new AlertDialog.Builder(this.getActivity().getApplicationContext())
                .setTitle("是否添加好友?")
                .setMessage("添加"+"为好友")
                .setView(text)
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("否", null)
                .create().show();
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }
}
