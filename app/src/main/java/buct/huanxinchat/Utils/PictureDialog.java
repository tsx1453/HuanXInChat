package buct.huanxinchat.Utils;


import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import buct.huanxinchat.R;

public class PictureDialog {

    private Context mContext;
    private Dialog dialog;
    private ImageView image;

    public PictureDialog(Context mContext) {
        this.mContext = mContext;
    }

    public void show(String path) {
        dialog = new Dialog(mContext, R.style.Theme_AppCompat_DayNight_Dialog);
        image = new ImageView(mContext);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(metrics.widthPixels,metrics.heightPixels);
        image.setLayoutParams(layoutParams);
        Glide.with(mContext)
                .load(path)
                .override(metrics.widthPixels,metrics.heightPixels)
                .into(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(image);
        dialog.getWindow().setWindowAnimations(R.style.Theme_diaLogAnimator);
        dialog.show();
    }
}
