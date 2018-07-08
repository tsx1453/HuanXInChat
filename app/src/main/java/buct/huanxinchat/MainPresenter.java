package buct.huanxinchat;

import android.content.Context;

/**
 * Created by tian on 2018/7/8.
 */

public class MainPresenter implements MainActivityConstract.Presenter {

    private MainActivityConstract.View mVIew;
    private Context mContext;

    public MainPresenter(MainActivityConstract.View mVIew, Context mContext) {
        this.mVIew = mVIew;
        this.mContext = mContext;
        mVIew.setPresenter(this);
    }
}
