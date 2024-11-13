// SVGImageView.kt
package com.example.myadventure.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Picture
import android.util.AttributeSet
import android.view.View
import com.caverock.androidsvg.SVG

class SVGImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var svgPicture: Picture? = null

    fun setSVGResource(resourceId: Int) {
        val svg = SVG.getFromResource(context, resourceId)
        svgPicture = svg.renderToPicture()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        svgPicture?.let {
            canvas.drawPicture(it)
        }
    }
}
