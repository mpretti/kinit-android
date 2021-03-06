package org.kinecosystem.kinit.view.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.kinecosystem.kinit.KinitApplication
import org.kinecosystem.kinit.R
import org.kinecosystem.kinit.databinding.CouponRowLayoutBinding
import org.kinecosystem.kinit.model.spend.Coupon
import org.kinecosystem.kinit.network.ServicesProvider
import org.kinecosystem.kinit.viewmodel.balance.CouponViewModel
import javax.inject.Inject


class CouponsListAdapter(val context: Context)
    : RecyclerView.Adapter<CouponsListAdapter.ViewHolder>() {

    @Inject
    lateinit var servicesProvider: ServicesProvider

    private val updateCouponsCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            coupons = servicesProvider.walletService.coupons.get()
            notifyDataSetChanged()
        }
    }

    lateinit var parent: RecyclerView
    private var coupons: List<Coupon>
    private var currentExpandedPosition = -1
    private var previousExpandedPosition = -1

    init {
        KinitApplication.coreComponent.inject(this)
        coupons = servicesProvider.walletService.coupons.get()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        servicesProvider.walletService.coupons.addOnPropertyChangedCallback(updateCouponsCallback)
        super.onAttachedToRecyclerView(recyclerView)
        this.parent = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        servicesProvider.walletService.coupons.removeOnPropertyChangedCallback(updateCouponsCallback)
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponsListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding: CouponRowLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.coupon_row_layout, parent,
            false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return servicesProvider.walletService.coupons.get().size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coupon = coupons[position]
        val isExpanded: Boolean = position == currentExpandedPosition
        if (isExpanded) previousExpandedPosition = position
        holder.bind(coupon, parent, isExpanded, position)
    }

    fun expandCollapse(isExpanded: Boolean, position: Int) {
        currentExpandedPosition = if (isExpanded) -1 else position
        notifyItemChanged(previousExpandedPosition)
        notifyItemChanged(position)
    }

    class ViewHolder(val binding: CouponRowLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var model: CouponViewModel

        fun bind(coupon: Coupon, parent: RecyclerView, expanded: Boolean, position: Int) {
            this.model = CouponViewModel(coupon, position, parent, expanded)
            binding.model = model
            binding.executePendingBindings()
        }
    }
}