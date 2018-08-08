package org.kinecosystem.kinit.view.walletCreation

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.R
import org.kinecosystem.kinit.analytics.Analytics
import org.kinecosystem.kinit.databinding.WalletCreationErrorFragmentBinding
import org.kinecosystem.kinit.view.BaseFragment
import javax.inject.Inject

class WalletCreationErrorFragment : BaseFragment() {

    @Inject
    lateinit var analytics: Analytics
    private var actions: WalletCreationUIActions? = null

    companion object {
        val TAG = WalletCreationErrorFragment::class.java.simpleName
        private const val HAS_BACK = "HAS_BACK"

        @JvmStatic
        fun newInstance(hasBack: Boolean): WalletCreationErrorFragment {
            val fragment = WalletCreationErrorFragment()
            val args = Bundle()
            args.putBoolean(HAS_BACK, hasBack)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<WalletCreationErrorFragmentBinding>(inflater, R.layout.wallet_creation_error_fragment, container, false)
        if (activity !is WalletCreationActivity)
            onInvalidData()
        binding.model = (activity as WalletCreationActivity).model
        return binding.root
    }

    private fun onInvalidData() {
        Log.e(TAG, "Invalid data cant start WalletCreationErrorFragment")
        activity?.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        KinitApplication.coreComponent.inject(this)
        super.onCreate(savedInstanceState)
        analytics.protectView(view)
    }
}