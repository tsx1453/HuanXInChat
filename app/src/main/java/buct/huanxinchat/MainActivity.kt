package buct.huanxinchat

import android.os.Bundle
import android.support.v4.app.Fragment
import buct.huanxinchat.Activitys.BaseActivity
import buct.huanxinchat.Fragments.ContractFragment

class MainActivity : BaseActivity(), MainActivityConstract.View{

    private var mPresenter: MainActivityConstract.Presenter? = null

    override fun setPresenter(presenter: MainActivityConstract.Presenter?) {
        mPresenter = presenter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.frag_container,ContractFragment.newInstance()).commit()
    }


}
