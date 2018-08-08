package org.kinecosystem.kinit.view.walletCreation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.R
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.view.BaseFragment
import javax.inject.Inject

class WalletCreationFragment : BaseFragment() {
    @Inject
    lateinit var analytics: Analytics
    private var actions: WalletCreationActions? = null

    companion object {
        val TAG = WalletCreationFragment::class.java.simpleName

        @JvmStatic
        fun newInstance(): WalletCreationFragment {
            return WalletCreationFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.creating_wallet_fragment, container, false)
        if (activity is WalletCreationActions) {
            actions = activity as WalletCreationActions?
        } else {
            Log.e(TAG, "activity must implements WalletCreationActivity")
            activity?.finish()
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        KinitApplication.coreComponent.inject(this)
        super.onCreate(savedInstanceState)
        analytics.protectView(view)
    }
}
