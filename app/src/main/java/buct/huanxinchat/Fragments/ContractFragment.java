package buct.huanxinchat.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import buct.huanxinchat.Adapter.ContractRecyclerViewAdapter;
import buct.huanxinchat.R;

/**
 * Created by tian on 2018/7/8.
 */

public class ContractFragment extends Fragment {
    private static ContractFragment fragment;
    private ContractRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contract,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        recyclerView = view.findViewById(R.id.contract_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new ContractRecyclerViewAdapter(this.getContext());
        recyclerView.setAdapter(adapter);
    }

    public static ContractFragment newInstance(){
        if (fragment==null){
            fragment = new ContractFragment();
        }
        return fragment;
    }
}
