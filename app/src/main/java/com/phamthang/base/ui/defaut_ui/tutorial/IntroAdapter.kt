package com.phamthang.base.ui.defaut_ui.tutorial

import android.app.Activity
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.callback.NativeCallback
import com.google.android.gms.ads.LoadAdError

import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.phamthang.base.R
import com.phamthang.base.databinding.ItemGuideBinding
import com.phamthang.base.databinding.LayoutItemAdsIntroFullBinding
import com.phamthang.base.extention.loadImage
import com.phamthang.base.extention.tap
import com.phamthang.base.utils.Constants
import java.util.Locale

class IntroAdapter(
    private val context: Activity,
    private val mHelpIntroModel: ArrayList<IntroModel>,
    private val onCLickExit: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_ADS = 0
    private val TYPE_ITEM = 1
    private var nativeAdFullScreen: NativeAd? = null
    private var nativeAdFullScreen2: NativeAd? = null
    private var nativeAdFullScreen1: NativeAd? = null
    private var nativeAdFullScreen12: NativeAd? = null

    init {
        loadNativeIntroFull(Constants.RemoteKeys.native_intro_full, Constants.RemoteKeys.native_intro_full)
        loadNativeIntroFull(Constants.RemoteKeys.native_intro_full_2, Constants.RemoteKeys.native_intro_full_2)
        loadNativeIntroFull1(Constants.RemoteKeys.native_intro_full1, Constants.RemoteKeys.native_intro_full1)
        loadNativeIntroFull1(Constants.RemoteKeys.native_intro_full1_2, Constants.RemoteKeys.native_intro_full1_2)
    }

    private fun loadNativeIntroFull(adsKey: String, remoteKey: String) {
        if (Admob.getInstance().checkCondition(context, remoteKey)) {
            Admob.getInstance().loadNativeAds(context, AdmobApi.getInstance().getListIDByName(adsKey), object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    if (adsKey == Constants.RemoteKeys.native_intro_full) {
                        setNativeAdFullScreen(nativeAd)
                        TutorialActivity.nativeAdFullScreen = nativeAd
                    } else if (adsKey == Constants.RemoteKeys.native_intro_full_2) {
                        setNativeAdFullScreen2(nativeAd)
                        TutorialActivity.nativeAdFullScreen2 = nativeAd
                    }
                    notifyNativeAdFullScreen()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                    super.onAdFailedToLoad(loadAdError)
                    setNativeAdFullScreen(null)
                    setNativeAdFullScreen2(null)
                }
            }, remoteKey)
        }
    }

    private fun loadNativeIntroFull1(adsKey: String, remoteKey: String) {
        if (Admob.getInstance().checkCondition(context, remoteKey)) {
            Admob.getInstance().loadNativeAds(context, AdmobApi.getInstance().getListIDByName(adsKey), object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    if (adsKey == Constants.RemoteKeys.native_intro_full1) {
                        setNativeAdFullScreen1(nativeAd)
                        TutorialActivity.nativeAdFullScreen1 = nativeAd
                    } else if (adsKey == Constants.RemoteKeys.native_intro_full1_2) {
                        setNativeAdFullScreen12(nativeAd)
                        TutorialActivity.nativeAdFullScreen12 = nativeAd
                    }
                    notifyNativeAdFullScreen1()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                    super.onAdFailedToLoad(loadAdError)
                    setNativeAdFullScreen1(null)
                    setNativeAdFullScreen12(null)
                }
            }, remoteKey)
        }
    }

    fun setNativeAdFullScreen(nativeAd: NativeAd?) {
        this.nativeAdFullScreen = nativeAd
    }

    fun setNativeAdFullScreen2(nativeAd: NativeAd?) {
        this.nativeAdFullScreen2 = nativeAd
    }

    fun notifyNativeAdFullScreen() {
        notifyItemChanged(1)
    }

    fun setNativeAdFullScreen1(nativeAd: NativeAd?) {
        this.nativeAdFullScreen1 = nativeAd
    }

    fun setNativeAdFullScreen12(nativeAd: NativeAd?) {
        this.nativeAdFullScreen12 = nativeAd
    }

    fun notifyNativeAdFullScreen1() {
        mHelpIntroModel.forEachIndexed { index, introModel ->
            if (introModel.content == "ads1") {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) IntroAdapterHolder(ItemGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else NativeFullAdapterHolder(LayoutItemAdsIntroFullBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
        val helpIntroModelModel = mHelpIntroModel[i]
        if (holder is IntroAdapterHolder) {
            holder.bind(helpIntroModelModel)
        } else if (holder is NativeFullAdapterHolder) {
            holder.bind(i)
        }
    }

    override fun getItemCount(): Int {
        return mHelpIntroModel.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mHelpIntroModel[position].position == 99) {
            TYPE_ADS
        } else {
            TYPE_ITEM
        }
    }

    inner class IntroAdapterHolder(val binding: ItemGuideBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: IntroModel) {
            data.img.let { binding.imgGuide.loadImage(it) }
            binding.tvTitle.text = data.title
            binding.tvContent.text = data.content
        }
    }

    inner class NativeFullAdapterHolder(private var binding: LayoutItemAdsIntroFullBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            if (mHelpIntroModel[position].content == "ads") {
                showNativeFullScreen(
                    nativeAdFullScreen,
                    R.layout.layout_item_ads_intro_full,
                    R.layout.layout_item_ads_intro_full,
                    binding.frAds,
                    context,
                    Constants.RemoteKeys.native_intro_full
                )
                Handler(context.mainLooper).postDelayed({
                    showNativeFullScreen(
                        nativeAdFullScreen2,
                        R.layout.layout_item_ads_intro_full,
                        R.layout.layout_item_ads_intro_full,
                        binding.frAds, context,
                        Constants.RemoteKeys.native_intro_full_2
                    )
                }, 700)
            } else if (mHelpIntroModel[position].content == "ads1") {
                showNativeFullScreen(
                    nativeAdFullScreen1,
                    R.layout.layout_item_ads_intro_full,
                    R.layout.layout_item_ads_intro_full,
                    binding.frAds,
                    context,
                    Constants.RemoteKeys.native_intro_full1
                )
                Handler(context.mainLooper).postDelayed({
                    showNativeFullScreen(
                        nativeAdFullScreen12,
                        R.layout.layout_item_ads_intro_full,
                        R.layout.layout_item_ads_intro_full,
                        binding.frAds,
                        context,
                        Constants.RemoteKeys.native_intro_full1_2
                    )
                }, 700)

            }
            binding.imgExit.tap {
                onCLickExit()
            }
        }
    }

    private fun showNativeFullScreen(nativeAd: NativeAd?, layoutNative: Int, layoutNativeMeta: Int, frAds: ViewGroup, context: Activity, remoteKey: String) {
        if (Admob.getInstance().checkCondition(context, remoteKey) && nativeAd != null) {
            val shimmer = LayoutInflater.from(context).inflate(R.layout.shimmer_native_full_screen, null)
            frAds.removeAllViews()
            frAds.addView(shimmer)
            val adView: NativeAdView
            var mediationAdapterClassName: String? = ""
            if (nativeAd.responseInfo != null) {
                mediationAdapterClassName = nativeAd.responseInfo?.mediationAdapterClassName
            }
            adView = if (mediationAdapterClassName != null && mediationAdapterClassName.lowercase(Locale.getDefault()).contains("facebook")
            ) {
                context.layoutInflater.inflate(layoutNativeMeta, frAds, false) as NativeAdView
            } else {
                context.layoutInflater.inflate(layoutNative, frAds, false) as NativeAdView
            }
//            frAds.removeAllViews()
            frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeAd, adView)
        }
    }
}
