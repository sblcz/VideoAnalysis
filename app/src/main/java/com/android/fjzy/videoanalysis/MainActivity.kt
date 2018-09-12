package com.android.fjzy.videoanalysis

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var noe = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewpager.addOnPageChangeListener(mOnPageChangeListener)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        viewpager.setAdapter(ViewPagerAdapter(getSupportFragmentManager()))

        val PERMISSIONS = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")
        //检测是否有写的权限
        val permission = ContextCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE")
        if (permission != PackageManager.PERMISSION_GRANTED) {
            //没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_one -> {
                viewpager.setCurrentItem(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_two -> {
                viewpager.setCurrentItem(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_three -> {
                viewpager.setCurrentItem(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {

        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            otherMainFun.sendAnalysisSign(position)
            navigation.getMenu().getItem(position).setChecked(true);
        }
    }

    inner class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragments = arrayOf(Fragment_One(), Fragment_One(), Fragment_Two())
        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return 3
        }
    }

    object otherMainFun {
        private var mhandler = Handler()
        fun setmhandler(getmhandler: Handler) {
            mhandler = getmhandler
        }

        fun sendAnalysisSign(sendobj: Int) {
            val message = Message()
            message.what = 0
            message.obj = sendobj
            mhandler.sendMessage(message)
        }
    }

}