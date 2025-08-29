package com.pixelrabbit.animalshelterfirebase.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.common.AdRequestConfiguration

class AppOpenAdManager(
    private val application: Application,
    private val adUnitId: String
) : Application.ActivityLifecycleCallbacks {

    private val TAG = "AppOpenAdManager"

    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var shouldShowAd = false

    init {
        application.registerActivityLifecycleCallbacks(this)
        // Не показываем ad сразу при старте, только загружаем
        loadAd()
    }

    fun setCurrentActivity(activity: Activity) {
        currentActivity = activity
        // Если уже загружена реклама и нужно показать
        if (shouldShowAd && appOpenAd != null) {
            showAdIfAvailable()
        }
    }

    private fun loadAd() {
        val loader = AppOpenAdLoader(application)
        loader.setAdLoadListener(object : AppOpenAdLoadListener {
            override fun onAdLoaded(ad: AppOpenAd) {
                Log.d(TAG, "AppOpenAd loaded")
                appOpenAd = ad
                // Показываем только если запросили
                if (shouldShowAd && currentActivity != null) {
                    showAdIfAvailable()
                }
            }

            override fun onAdFailedToLoad(error: com.yandex.mobile.ads.common.AdRequestError) {
                Log.e(TAG, "AppOpenAd failed to load: ${error.description}")
                appOpenAd = null
            }
        })

        val config = AdRequestConfiguration.Builder(adUnitId).build()
        loader.loadAd(config)
    }

    fun showAdIfAvailable() {
        val activity = currentActivity ?: run {
            shouldShowAd = true
            return
        }

        appOpenAd?.let { ad ->
            ad.show(activity)
            appOpenAd = null
            shouldShowAd = false
            // Загружаем следующий ad после показа
            loadAd()
        } ?: run {
            shouldShowAd = true
            // Если ad ещё не загружен, он будет показан после загрузки
            loadAd()
        }
    }

    // --- Activity Lifecycle Callbacks ---
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
    override fun onActivityStarted(activity: Activity) { currentActivity = activity }
    override fun onActivityResumed(activity: Activity) { currentActivity = activity }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) currentActivity = null
    }
}
