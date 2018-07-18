package buct.huanxinchat

//import buct.huanxinchat.Adapter.ContractRecyclerViewAdapter
//import buct.huanxinchat.Fragments.ContractFragment
import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import buct.huanxinchat.Activitys.BaseActivity
import buct.huanxinchat.Adapter.ContractRecyclerViewAdapter
import buct.huanxinchat.Utils.QRCodeUtil
import com.bumptech.glide.Glide
import com.google.zxing.activity.CaptureActivity
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.fragment_contract.*

class MainActivity : BaseActivity(), MainActivityConstract.View {

    //    private var adapter: ContractRecyclerViewAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var qrCodeShow: Button? = null
    private var qrCodeScan: Button? = null
    private var mPresenter: MainActivityConstract.Presenter? = null

    override fun setPresenter(presenter: MainActivityConstract.Presenter?) {
        mPresenter = presenter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_contract)
        title = "VenusChat" + "    |    " + EMClient.getInstance().currentUser
        initView()
    }

    private fun initView() {
        recyclerView = findViewById<RecyclerView>(R.id.contract_recycler)
        qrCodeScan = findViewById<Button>(R.id.qr_scan)
        qrCodeShow = findViewById<Button>(R.id.qr_code_show)
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))
        recyclerView!!.adapter = ContractRecyclerViewAdapter(this)
        recyclerView!!.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_falldown)
        qrCodeShow!!.setOnClickListener(View.OnClickListener {
            val bitmap = QRCodeUtil.createQRCodeBitmap(EMClient.getInstance().currentUser, 600, 600)
            val dialog = Dialog(this, R.style.Theme_PicDialog)
            val imageView = ImageView(this)
            imageView.layoutParams = ViewGroup.LayoutParams(500, 500)
            imageView.setImageBitmap(bitmap)
            imageView.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setContentView(imageView)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setOnDismissListener{
                initView()
            }
            dialog.show()
        })
        qrCodeScan!!.setOnClickListener(View.OnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                run { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1) }
            val intent = Intent(this, CaptureActivity::class.java)
            startActivityForResult(intent, 99)
        })
        logout.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("注意！")
                    .setMessage("是否需要登出？")
                    .setNegativeButton("是") { dialog: DialogInterface?, which: Int ->
                        EMClient.getInstance().logout(true, object : EMCallBack {
                            override fun onSuccess() {
                                sendBroadcast(Intent("logout_huanxin"))
                                finish()
                            }

                            override fun onError(code: Int, error: String) {

                            }

                            override fun onProgress(progress: Int, status: String) {

                            }
                        })
                    }
                    .create().show()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Log.d("tsx", requestCode.toString() + ":" + resultCode.toString())
        if (resultCode == 161) { //RESULT_OK = -1
            val bundle = data!!.getExtras()
            val scanResult = bundle!!.getString("qr_scan_result")
//            Log.d("tsx-mylog", "ContractFragment->onActivityResult: " + scanResult)
            val text = EditText(this)
            if (scanResult.equals(EMClient.getInstance().currentUser)) {
                return
            }
            AlertDialog.Builder(this)
                    .setTitle("是否添加好友?")
                    .setMessage("添加" + scanResult + "为好友")
                    .setView(text)
                    .setNegativeButton("是") { dialogInterface, i ->
                        Thread(Runnable {
                            EMClient.getInstance().contactManager().addContact(scanResult, text.text.toString());
                        }).start()
                        initView()
                    }
                    .setPositiveButton("否", null)
                    .create().show()
        }
    }
}
