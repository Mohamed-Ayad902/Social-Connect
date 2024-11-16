package com.mayad.instagram.android.utils

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mayad.instagram.R
import com.zhpan.indicator.IndicatorView
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import javax.inject.Inject

class ViewPagerUtils @Inject constructor(private val context: Context) :
    ViewPager2.OnPageChangeCallback() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var indicator: IndicatorView

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        indicator.onPageScrolled(
            position,
            positionOffset,
            positionOffsetPixels
        )
    }

    override fun onPageSelected(position: Int) {
        indicator.onPageSelected(position)
    }

    fun setIndicators(
        _viewPager: ViewPager2,
        _indicator: IndicatorView,
        adapter: RecyclerView.Adapter<*>,
        size: Int
    ) {
        viewPager2 = _viewPager
        indicator = _indicator
        viewPager2.adapter = adapter
        indicator.apply {
            setSliderColor(
                ContextCompat.getColor(context, R.color.darker_grey),
                ContextCompat.getColor(context, R.color.black)
            )
            setSliderWidth(25F)
            setSliderHeight(15f)
            setCheckedColor(ContextCompat.getColor(context, R.color.instagram_middleColor))
            setNormalColor(ContextCompat.getColor(context, R.color.darker_grey))
            setSlideMode(IndicatorSlideMode.SMOOTH)
            setIndicatorStyle(IndicatorStyle.CIRCLE)
            setPageSize(size)
            notifyDataChanged()
        }
        subscribeToViewPager()
    }

    private fun subscribeToViewPager() {
        viewPager2.registerOnPageChangeCallback(this)
    }

    fun destroy() {
        if (::viewPager2.isInitialized)
            viewPager2.unregisterOnPageChangeCallback(this)
    }
}