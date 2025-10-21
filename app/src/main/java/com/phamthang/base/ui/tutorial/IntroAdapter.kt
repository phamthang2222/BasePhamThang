package com.phamthang.base.ui.tutorial

import android.app.Activity
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amazic.library.ads.admob.Admob

import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.phamthang.base.R
import com.phamthang.base.databinding.ItemGuideBinding
import com.phamthang.base.databinding.LayoutItemAdsIntroFullBinding
import java.util.Locale

class IntroAdapter(
    private val context: Activity,
    private val mHelpIntroModel: ArrayList<IntroModel>,
    private val onCLickExit: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var nativeAd: NativeAd? = null
    private var nativeAd2: NativeAd? = null
    private var nativeAd1: NativeAd? = null
    private var nativeAd12: NativeAd? = null

    companion object {
        private const val VIEW_TYPE_ADS = 1
        private const val VIEW_TYPE_ADS1 = 2
        private const val VIEW_TYPE_INTRO = 0
        private const val ADS_ = "ads"
        private const val ADS_1 = "ads1"
    }

    override fun getItemViewType(position: Int): Int {
        return when (mHelpIntroModel[position].title) {
            ADS_ -> VIEW_TYPE_ADS
            ADS_1 -> VIEW_TYPE_ADS1
            else -> VIEW_TYPE_INTRO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADS -> {
                val binding = LayoutItemAdsIntroFullBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AdsViewHolder(binding)
            }

            VIEW_TYPE_ADS1 -> {
                val binding = LayoutItemAdsIntroFullBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                Ads1ViewHolder(binding)
            }

            else -> {
                val binding =
                    ItemGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                IntroAdapterHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder.itemViewType) {
            VIEW_TYPE_ADS -> {
                val adsHolder = holder as AdsViewHolder
                adsHolder.bind()
            }

            VIEW_TYPE_ADS1 -> {
                val adsHolder = holder as Ads1ViewHolder
                adsHolder.bind()
            }

            else -> {
                val helpIntroModelModel = mHelpIntroModel[position]
                (holder as IntroAdapterHolder).bind(helpIntroModelModel)
            }
        }
    }


    override fun getItemCount(): Int {
        return mHelpIntroModel.size
    }

    inner class IntroAdapterHolder(val binding: ItemGuideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: IntroModel) {
            data.img.let { binding.imgGuide.setImageResource(it) }
            binding.tvTitle.text = data.title
            binding.tvContent.text = data.content
        }
    }

    inner class AdsViewHolder(val binding: LayoutItemAdsIntroFullBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            showNativeFullScreen(
                nativeAd,
                R.layout.layout_item_ads_intro_full,
                R.layout.layout_item_ads_intro_full,
                binding.frAds,
                context
            )
            if(nativeAd2 != null){
                Handler(context.mainLooper).postDelayed({
                    showNativeFullScreen(
                        nativeAd2,
                        R.layout.layout_item_ads_intro_full,
                        R.layout.layout_item_ads_intro_full,
                        binding.frAds,
                        context
                    )
                }, 700)

            }
            binding.imgExit.setOnClickListener {
                onCLickExit()
            }
        }
    }

    inner class Ads1ViewHolder(val binding: LayoutItemAdsIntroFullBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            showNativeFullScreen(
                nativeAd1,
                R.layout.layout_item_ads_intro_full,
                R.layout.layout_item_ads_intro_full,
                binding.frAds,
                context
            )
            if(nativeAd12 != null){
                Handler(context.mainLooper).postDelayed({
                    showNativeFullScreen(
                        nativeAd12,
                        R.layout.layout_item_ads_intro_full,
                        R.layout.layout_item_ads_intro_full,
                        binding.frAds,
                        context
                    )
                }, 700)
            }
            binding.imgExit.setOnClickListener {
                onCLickExit()
            }
        }
    }

    fun setNativeAds(nativeAd: NativeAd, position: Int) {
        this.nativeAd = nativeAd
        notifyItemChanged(position)
    }

    fun setNativeAds2(nativeAd: NativeAd, position: Int) {
        this.nativeAd2 = nativeAd
        notifyItemChanged(position)
    }

    fun setNativeAds1(nativeAd: NativeAd, position: Int) {
        this.nativeAd1 = nativeAd
        notifyItemChanged(position)
    }

    fun setNativeAds12(nativeAd: NativeAd, position: Int) {
        this.nativeAd12 = nativeAd
        notifyItemChanged(position)
    }

    private fun showNativeFullScreen(
        nativeAd: NativeAd?,
        layoutNative: Int,
        layoutNativeMeta: Int,
        frAds: ViewGroup,
        context: Activity
    ) {
        val shimmer =
            LayoutInflater.from(context).inflate(R.layout.shimmer_native_full_screen, null)
        frAds.removeAllViews()
        frAds.addView(shimmer)
        if (nativeAd != null) {
            val adView: NativeAdView
            var mediationAdapterClassName: String? = ""
            if (nativeAd.responseInfo != null) {
                mediationAdapterClassName = nativeAd.responseInfo?.mediationAdapterClassName
            }
            if (mediationAdapterClassName != null && mediationAdapterClassName.lowercase(Locale.getDefault())
                    .contains("facebook")
            ) {
                adView =
                    context.layoutInflater.inflate(layoutNativeMeta, frAds, false) as NativeAdView
            } else {
                adView = context.layoutInflater.inflate(layoutNative, frAds, false) as NativeAdView
            }
//            frAds.removeAllViews()
            frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeAd, adView)
        }
    }
}
