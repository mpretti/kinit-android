package org.kinecosystem.kinit.view

import android.os.Bundle
import android.support.v4.app.Fragment

import org.kinecosystem.kinit.R

abstract class SingleFragmentActivity : BaseActivity() {

    protected abstract fun getFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_fragment_layout)
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, getFragment()).commitNowAllowingStateLoss()
    }

    fun replaceFragment(fragment: Fragment, withSlideAnimation: Boolean = false) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (withSlideAnimation) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)
        }
        fragmentTransaction.replace(R.id.fragment_container, fragment).commitNowAllowingStateLoss()
    }
}