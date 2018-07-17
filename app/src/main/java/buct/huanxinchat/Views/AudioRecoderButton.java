package buct.huanxinchat.Views;


import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.IOException;

import buct.huanxinchat.R;
import buct.huanxinchat.Utils.AudioManager;
import buct.huanxinchat.Utils.RecordingDialog;

public class AudioRecoderButton extends AppCompatButton implements AudioManager.AudioStateListener {

    //手指滑动 距离
    private static final int DISTANCE_Y_CANCEL = 50;
    //状态
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    //当前状态
    private int mCurState = STATE_NORMAL;
    //已经开始录音
    private boolean isRecording = false;

    private RecordingDialog mDialogManager;
    private AudioManager mAudioManager;

    private float mTime;
    //是否触发onlongclick
    private boolean mReady;

    public AudioRecoderButton(Context context) {
        this(context, null);
    }

    public AudioRecoderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new RecordingDialog(getContext());
        String dir = Environment.getExternalStorageDirectory() + "/recorder_audios";
        File file = new File(dir);
        if (!file.exists()){
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mAudioManager = new AudioManager(dir);
        mAudioManager.setOnAudioStateListener(this);
        //按钮长按 准备录音 包括start
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener{
        //时长  和 文件
        void onFinish(float seconds,String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener (AudioFinishRecorderListener listener){
        mListener = listener;
    }
    //获取音量大小的Runnable
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGED = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED :
                    mDialogManager.showRecordingDialog();
                    isRecording = true;

                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED :
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));

                    break;
                case MSG_DIALOG_DIMISS :
                    mDialogManager.dimissDialog();
                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //TODO
                isRecording = true;
                changeState(STATE_RECORDING);

                break;
            case MotionEvent.ACTION_MOVE:

                if (isRecording) {


                    //根据想x,y的坐标，判断是否想要取消
                    if (wantToCancel(x, y)) {

                        changeState(STATE_WANT_TO_CANCEL);
                    } else {

                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //如果longclick 没触发
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                //触发了onlongclick 没准备好，但是已经prepared 已经start
                //所以消除文件夹
                if(!isRecording||mTime<0.6f){
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                }else if(mCurState==STATE_RECORDING){//正常录制结束

                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    if (mListener != null) {
                        mListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
                    }

                }else if (mCurState == STATE_RECORDING) {

                    mDialogManager.dimissDialog();
                    //release
                    //callbacktoAct
                } else if (mCurState == STATE_WANT_TO_CANCEL) {
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                    //cancel
                }

                reset();

                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态 标志位
     */
    private void reset() {
        isRecording = false;
        mReady = false;
        changeState(STATE_NORMAL);
        mTime = 0;

    }

    private boolean wantToCancel(int x, int y) {
        //如果左右滑出 button
        if (x < 0 || x > getWidth()) {
            return true;
        }
        //如果上下滑出 button  加上我们自定义的距离
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    //改变状态
    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
//                    setBackgroundResource(R.drawable.record_btn_back);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
//                    setBackgroundResource(R.drawable.recorn_btn_back_press);
                    setText(R.string.str_recorder_recording);

                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
//                    setBackgroundResource(R.drawable.ic_add_circle_black_24dp);
                    setText(R.string.str_recorder_want_cancel);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }


}