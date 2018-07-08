package buct.huanxinchat

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import buct.huanxinchat.Activitys.BaseActivity
import buct.huanxinchat.Fragments.ChatListItemFragment
import buct.huanxinchat.Fragments.ContractFragment
import buct.huanxinchat.Fragments.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainActivityConstract.View, ChatListItemFragment.OnListFragmentInteractionListener {

    private val mainAdapter: MainViewPagerAdapter? = MainViewPagerAdapter(this.supportFragmentManager)
    private var mPresenter: MainActivityConstract.Presenter? = null

    override fun setPresenter(presenter: MainActivityConstract.Presenter?) {
        mPresenter = presenter
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                main_activity_view_pager.setCurrentItem(0,true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                main_activity_view_pager.setCurrentItem(1,true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun initView() {
        val list:ArrayList<Fragment> = ArrayList(3)
        list.add(ChatListItemFragment.newInstance(1))
        list.add(ContractFragment.newInstance())
        mainAdapter!!.setFragments(list)
        main_activity_view_pager.adapter = mainAdapter
    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {

    }
}
