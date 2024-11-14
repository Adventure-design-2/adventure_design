// SVGImageComposable.kt
package com.example.myadventure.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myadventure.ui.views.SVGImageView

@Composable
fun SVGImage(
    modifier: Modifier = Modifier,
    resourceId: Int
) {
    AndroidView(
        factory = { context ->
            SVGImageView(context).apply {
                setSVGResource(resourceId)
            }
        },
        modifier = modifier
    )
}
