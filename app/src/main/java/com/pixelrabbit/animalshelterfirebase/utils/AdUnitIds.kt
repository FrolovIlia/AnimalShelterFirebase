package com.pixelrabbit.animalshelterfirebase.utils

import android.content.Context
import com.pixelrabbit.animalshelterfirebase.R

object AdUnitIds {

    fun cardOpen(context: Context): String =
        context.getString(R.string.yandex_card_open_id)

    fun slideShowPortrait(context: Context): String =
        context.getString(R.string.yandex_banner_vertical_id) // заменим на правильный ID

    fun slideShowLandscape(context: Context): String =
        context.getString(R.string.yandex_banner_horizontal_id) // заменим на правильный ID

    fun bannerVertical(context: Context): String =
        context.getString(R.string.yandex_banner_vertical_id)

    fun bannerHorizontal(context: Context): String =
        context.getString(R.string.yandex_banner_horizontal_id)

    fun taskBanner(context: Context): String =
        context.getString(R.string.yandex_task_banner_id)

    fun donateBanner(context: Context): String =
        context.getString(R.string.yandex_donate_banner_id)

    fun interstitial(context: Context): String =
        context.getString(R.string.yandex_interstitial_id)
}
