package com.mayad.instagram.android.utils

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mayad.instagram.R
import javax.inject.Inject

class ViewPagerIndicatorUtils @Inject constructor(private val context: Context) :
    ViewPager2.OnPageChangeCallback() {
    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorView: LinearLayout

    override fun onPageSelected(position: Int) {
        setCurrentIndicator(position)
    }

    fun setupOnBoardingIndicators(
        viewPager: ViewPager2,
        adapter: FragmentStateAdapter,
        linearLayout: LinearLayout,
        listSize: Int
    ) {
        this.viewPager = viewPager
        this.indicatorView = linearLayout
        this.viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(this)
        setupIndicators(listSize)
        setCurrentIndicator(0)
    }

    private fun setupIndicators(listSize: Int) {
        val indicators = arrayOfNulls<ImageView>(listSize)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(context)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            indicatorView.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorView.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorView[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

    fun getCurrentPosition(): Int {
        return viewPager.currentItem
    }

    fun destroy() {
        viewPager.unregisterOnPageChangeCallback(this)
    }
}