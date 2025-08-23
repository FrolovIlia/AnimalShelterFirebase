package com.pixelrabbit.animalshelterfirebase.utils

import android.app.Activity
import android.util.Log
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener

//На случай если решу интегрировать межстраничную рекламу
class AdManager(private val activity: Activity) {

    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdLoader: InterstitialAdLoader? = null

    fun loadAndShowAd(adUnitId: String, onAdClosed: () -> Unit) {
        interstitialAdLoader = InterstitialAdLoader(activity).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    interstitialAd?.setAdEventListener(object : InterstitialAdEventListener {
                        override fun onAdShown() {}
                        override fun onAdDismissed() = onAdClosed()
                        override fun onAdClicked() {}
                        override fun onAdFailedToShow(adError: AdError) {
                            Log.e("AdManager", "Ad failed to show: ${adError.description}")
                            onAdClosed()
                        }
                        override fun onAdImpression(impressionData: ImpressionData?) {}
                    })
                    interstitialAd?.show(activity)
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    onAdClosed()
                }
            })
        }

        // Создаём конфигурацию с нужным adUnitId
        val config = AdRequestConfiguration.Builder(adUnitId).build()
        interstitialAdLoader?.loadAd(config)
    }
}
