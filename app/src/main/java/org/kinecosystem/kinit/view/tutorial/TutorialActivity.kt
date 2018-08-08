package org.kinecosystem.kinit.view.tutorial

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.text.Html
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.tutorial_activity.*
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.R
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.analytics.Events
import org.kinecosystem.kinit.repository.UserRepository
import org.kinecosystem.kinit.view.BaseActivity
import org.kinecosystem.kinit.view.MainActivity
import org.kinecosystem.kinit.view.phoneVerify.PhoneVerifyActivity
import javax.inject.Inject

class TutorialActivity : BaseActivity() {
    @Inject
    lateinit var analytics: Analytics
    @Inject
    lateinit var userRepository: UserRepository
    private val pages = arrayOfNulls<View>(NUM_ITEMS)
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        KinitApplication.coreComponent.inject(this)
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        setContentView(R.layout.tutorial_activity)
        initTosText()
        initPagesIndicator()
        view_pager.adapter = TutorialPagerAdapter(supportFragmentManager)
        view_pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                clearPagesIndicator()
                currentPage = position
                pages[position]?.isSelected = true
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
            override fun onPageScrollStateChanged(state: Int) = Unit
        })
        start_app.setOnClickListener { onStartClicked() }
    }

    private fun initTosText() {
        if (TextUtils.isEmpty(userRepository.tos)) {
            tos_text.text = ""
        } else {
            tos_text.text = Html.fromHtml(resources.getString(R.string.tos))
            tos_text.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(userRepository.tos)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        analytics.logEvent(Events.Analytics.ViewOnboardingPage(currentPage))
    }

    private fun onStartClicked() {
        analytics.logEvent(Events.Analytics.ClickStartButtonOnOnboardingPage(currentPage))
        if (userRepository.isPhoneVerificationEnabled) {
            startActivity(PhoneVerifyActivity.getIntent(this, true))
        } else {
            startActivity(MainActivity.getIntent(this))
        }
        finish()
    }

    private fun initPagesIndicator() {
        pages[0] = page0
        pages[1] = page1
        pages[2] = page2
        pages[currentPage]?.isSelected = true
    }

    private fun clearPagesIndicator() {
        for (page in pages) {
            page?.isSelected = false
        }
    }

    private class TutorialPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        override fun getCount(): Int {
            return NUM_ITEMS
        }

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> TutorialWelcomeFragment.newInstance()
                1 -> TutorialEarnFragment.newInstance()
                2 -> TutorialEnjoyFragment.newInstance()
                else -> null
            }
        }
    }

    companion object {
        const val NUM_ITEMS = 3

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, TutorialActivity::class.java)
        }
    }

}
