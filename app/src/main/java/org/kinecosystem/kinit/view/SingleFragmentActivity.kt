package org.kinecosystem.kinit.view

import android.os.Bundle
import android.support.v4.app.Fragment
import org.kinecosystem.kinit.R

abstract class SingleFragmentActivity : BaseActivity() {

    protected abstract fun getFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_fragment_layout)
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, getFragment()).commitNow()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commitNowAllowingStateLoss()
    }
}