package org.kinecosystem.kinit.view

import android.os.Bundle
import android.support.v4.app.Fragment
import org.kinecosystem.kinit.R

abstract class BaseSingleFragmentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        init()
        setContentView(R.layout.single_fragment_layout)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, getFragment()).commitNow()
    }

    fun replaceFragment(fragment: Fragment, tag: String? = null) {
        val stackFragment = supportFragmentManager.findFragmentByTag(tag)
        if (stackFragment != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, stackFragment, tag).commitAllowingStateLoss()
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag).commitAllowingStateLoss()
        }
    }

    abstract fun getFragment(): Fragment
    abstract fun inject()
    abstract fun init()
}