package buct.huanxinchat.Activitys;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import buct.huanxinchat.Adapter.ChatRecyclerAdapter;
import buct.huanxinchat.R;
import buct.huanxinchat.Utils.MediaManager;
import buct.huanxinchat.Utils.SharedPreferenceUtil;
import buct.huanxinchat.Views.AudioRecoderButton;
import buct.huanxinchat.Views.RecyclerViewWithContextMenu;

public class ChatActivity extends BaseActivity {

    public static String CHAT_EXTRA_USER_ID = "chat_user_id";
    private String chatUserId;
    private EditText editText;
    private Button sendBtn;
    private RecyclerViewWithContextMenu recyclerView;
    private EMConversation conversation;
    private boolean hasContext = false;
    private ChatRecyclerAdapter adapter;
    private Handler mHadler;
    private EMMessageListener listener;
    private View rootView;
    private ImageView backGround;
    private View moreArea;
    private ImageView pictureBtn;
    private ImageView cameraBtn;
    private AudioRecoderButton recoderButton;
    private Button recoderStartBtn;
    private boolean keyboardhasopend = false;
    private boolean hasBackGround = false;
    private int statusHeight = 0;
    private String mImgPath;
    public final static int CHOOSE_PHOTO_FOR_BACKGROUND = 2;
    public final static int CHOOSE_PHOTO_FOR_SEND = 3;
    public final static int CAMERA_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mHadler = new Handler();
        initData(getIntent());
        initView();
        initRecycler();
        initEvent();
        initListener();
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);
        recoderStartBtn = findViewById(R.id.recoder_btn);
        editText = findViewById(R.id.chat_input);
        sendBtn = findViewById(R.id.chat_send_btn);
        recyclerView = findViewById(R.id.chat_recycler);
        rootView = findViewById(R.id.chat_root_view);
        backGround = findViewById(R.id.chat_background);
        moreArea = findViewById(R.id.more_area);
        pictureBtn = findViewById(R.id.more_picture);
        cameraBtn = findViewById(R.id.more_carema);
        recoderButton = findViewById(R.id.record_button);
        if (SharedPreferenceUtil.getAutoBg(this, chatUserId)) {
            loadAutoBg();
            hasBackGround = true;
        } else {
            setBackGround(SharedPreferenceUtil.getChatbackGround(this, chatUserId));
        }
    }

    private void loadAutoBg() {
        hasBackGround = true;
        Glide.with(this)
                .load("http://www.nanbaolongbao.com/background.png")
                .placeholder(null)
                .into(backGround);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        changeMenuTime(menu.getItem(1));
        return true;
    }

    private void changeMenuTime(MenuItem item) {
        if (SharedPreferenceUtil.getChatTimeShow(this, chatUserId)) {
            item.setTitle(R.string.chat_menu_time_show);
        } else {
            item.setTitle(R.string.chat_nemu_time_unshow);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_menu_user:
                Toast.makeText(ChatActivity.this, "emmm...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.chat_menu_back:
                if (hasBackGround) {
                    setBackGround(null);
                    SharedPreferenceUtil.setAutoBg(this, chatUserId, false);
                } else {
                    openAlbum(CHOOSE_PHOTO_FOR_BACKGROUND);
                }
                break;
            case R.id.chat_menu_time:
                SharedPreferenceUtil.setChatTimeShow(ChatActivity.this, chatUserId, !SharedPreferenceUtil.getChatTimeShow(ChatActivity.this, chatUserId));
                adapter.notifyDataSetChanged();
                changeMenuTime(item);
                break;
            case android.R.id.home:
                try {
                    MediaManager.pause();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                break;
            case R.id.chat_menu_auto_bg:
                if (SharedPreferenceUtil.getAutoBg(this, chatUserId)) {
                    item.setTitle(R.string.autobg_off);
                    SharedPreferenceUtil.setAutoBg(this, chatUserId, false);
                    setBackGround(null);
                } else {
                    SharedPreferenceUtil.setAutoBg(this, chatUserId, true);
                    item.setTitle(R.string.autobg_on);
                    loadAutoBg();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initListener() {
        listener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (final EMMessage e : messages) {
                    mHadler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.insertMessage(e);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }
                });
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };
        EMClient.getInstance().chatManager().addMessageListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(listener);
//        adapter.saveToDB();
    }

    private void moreAreaViewHeight(int from, int to) {

        ValueAnimator heightAnimator = ObjectAnimator.ofInt(from, to);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int h = (Integer) animation.getAnimatedValue();
                moreArea.getLayoutParams().height = h;
                moreArea.requestLayout();
            }
        });
        heightAnimator.start();
    }

    private void roateMoreBtn(float from, float to) {
        ValueAnimator animator = ObjectAnimator.ofFloat(sendBtn, "rotation", from, to);
        animator.start();
    }

    private void initEvent() {
        if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasContext) {
                    EMMessage message = EMMessage.createTxtSendMessage(editText.getText().toString(), chatUserId);
                    EMClient.getInstance().chatManager().sendMessage(message);
                    adapter.insertMessage(message);
                    editText.setText("");
                } else {
                    if (moreArea.getHeight() > 0) {
                        hideMoreArea();
                    } else {
                        hideKeyBoard();
                        showMoreArea();
                    }
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.d("tsxmylog", "onTextChanged: "+s);
                if (s.length() == 0) {
                    hasContext = false;
//                    Bitmap addBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_add_circle_black_24dp);
                    sendBtn.setBackground(getDrawable(R.drawable.ic_add_circle_outline_black_24dp));
                } else {
                    hasContext = true;
                    sendBtn.setBackground(getDrawable(R.drawable.ic_send_black_24dp));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.d("tsxmylog", "afterTextChanged: "+s);
            }
        });
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int navigationHeight = 0;
                int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    navigationHeight = getResources().getDimensionPixelSize(resourceId);
                }
                int statusBarHeight = 0;
                resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = getResources().getDimensionPixelSize(resourceId);
                }
                statusHeight = statusBarHeight;
                // display window size for the app layout
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

                // screen height - (user app height + status + nav) ..... if non-zero, then there is a soft keyboard
                int keyboardHeight = rootView.getRootView().getHeight() - (statusBarHeight + navigationHeight + rect.height());
                if (keyboardHeight > 0) {
//                    Log.d("tsxmylog", "onGlobalLayout: open");
                    mHadler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!keyboardhasopend) {
                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                hideMoreArea();
                                keyboardhasopend = true;
                            }
                        }
                    });
                } else {
                    keyboardhasopend = false;
                }
            }
        };
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum(CHOOSE_PHOTO_FOR_SEND);
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        recoderButton.setAudioFinishRecorderListener(new AudioRecoderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                EMMessage message = EMMessage.createVoiceSendMessage(filePath, (int) seconds, chatUserId);
                EMClient.getInstance().chatManager().sendMessage(message);
                adapter.insertMessage(message);
            }
        });
        recoderStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recoderButton.getVisibility() == View.GONE) {
                    recoderButton.setVisibility(View.VISIBLE);
                    hideKeyBoard();
                } else {
                    recoderButton.setVisibility(View.GONE);
                }
            }
        });
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void hideMoreArea() {
        moreAreaViewHeight(moreArea.getHeight(), 0);
        roateMoreBtn(45, 0);
    }

    private void showMoreArea() {
        roateMoreBtn(0, 45);
        moreAreaViewHeight(0, 400);
    }

    private void initRecycler() {
        adapter = new ChatRecyclerAdapter(this, chatUserId);
        adapter.setRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
    }

    private void initData(Intent intent) {
        chatUserId = intent.getStringExtra(CHAT_EXTRA_USER_ID);
    }

    private void openAlbum(int requestId) {
        if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, requestId);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("tsxmylog", "onActivityResult: " + requestCode + ":" + resultCode);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CHOOSE_PHOTO_FOR_BACKGROUND:
                if (data.getData() == null) {
                    return;
                }
                setBackGround(handleImagePath(data.getData()));
                break;
            case CHOOSE_PHOTO_FOR_SEND:
                String path = handleImagePath(data.getData());
                if (path != null) {
                    sendPic(path);
                }

                break;
            case CAMERA_CODE:
//                Log.d("tsxmylog", "onActivityResult: " + handleImagePath(imageUri));
                sendPic(mImgPath);
                break;
        }
        hideMoreArea();
    }

    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
            mImgPath = image.getAbsolutePath();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setBackGround(String path) {
        if (path == null) {
            backGround.setImageDrawable(null);
            hasBackGround = false;
            SharedPreferenceUtil.setBackGround(this, chatUserId, null);
        } else {
            hasBackGround = true;
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = getWindowManager();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int oriHeight, oriWidth;
            oriHeight = bitmap.getHeight();
            oriWidth = bitmap.getWidth();
            int screenW, screenH;
            screenH = displayMetrics.heightPixels;
            screenW = displayMetrics.widthPixels;
            float oriF = oriHeight / oriWidth;
            float f = screenH / screenW;
            int scaleX = 0, scaleY = 0, width = 0, height = 0;
            if (oriF > f) {
                width = oriWidth;
                height = (int) (width * f);
                scaleY = (oriHeight - height) / 2;
            } else {
                height = oriHeight;
                width = (int) (height / f);
                scaleX = (oriWidth - width) / 2;
            }
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap, scaleX, scaleY, width, height, null, false);
            backGround.setImageBitmap(bitmap1);
            SharedPreferenceUtil.setBackGround(this, chatUserId, path);
        }
    }

    private String handleImagePath(Uri uri) {
        String path = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getImagePath(contentUri, null);
            }
        }
        return path;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You have the permission,please try it again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void sendPic(String path) {
        EMMessage message = EMMessage.createImageSendMessage(path, false, chatUserId);
        EMClient.getInstance().chatManager().sendMessage(message);
        adapter.insertMessage(message);
    }

    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "buct.huanxinchat.android.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, CAMERA_CODE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        MediaManager.pause();
        super.onBackPressed();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(1, 1, 1, "复制");
        menu.add(1, 2, 1, "删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        RecyclerViewWithContextMenu.RecyclerViewContextInfo info = (RecyclerViewWithContextMenu.RecyclerViewContextInfo) item.getMenuInfo();
        if (info != null && info.getPosition() != -1) {
            switch (item.getItemId()) {
                case 1:
                    adapter.copy(info.getPosition());
                    break;
                case 2:
                    adapter.delete(info.getPosition());
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }



}
