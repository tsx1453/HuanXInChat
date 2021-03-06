package buct.huanxinchat.Views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;

/**
 * Created by tian on 2018/7/15.
 */

public class RecyclerViewWithContextMenu extends RecyclerView {
    private final static String TAG = "RVWCM";

    private RecyclerViewContextInfo mContextInfo = new RecyclerViewContextInfo();

    public RecyclerViewWithContextMenu(Context context) {
        super(context);
    }

    public RecyclerViewWithContextMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithContextMenu(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean showContextMenuForChild(View originalView, float x, float y) {
        LinearLayoutManager layoutManager = (LinearLayoutManager)getLayoutManager();
        if(layoutManager != null) {
            int position = layoutManager.getPosition(originalView);
//            Log.d(TAG,"showContextMenuForChild position = " + position);
            mContextInfo.mPosition = position;
        }

        return super.showContextMenuForChild(originalView, x, y);
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        return mContextInfo;
    }

    public static class RecyclerViewContextInfo implements ContextMenu.ContextMenuInfo {
        private int mPosition = -1;

        public int getPosition() {
            return mPosition;
        }
    }
}
