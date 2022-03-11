package com.example.pokedexapp.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.example.pokedexapp.R

@Composable
fun PokemonText(
    text: String,
    modifier: Modifier,
    fontSize: Float = 84F
){
    val customTypefaceHollow = ResourcesCompat.getFont(LocalContext.current, R.font.pokemon_hollow)
    val customTypefaceFilled = ResourcesCompat.getFont(LocalContext.current, R.font.pokemon_solid)

    val textPaintStroke = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        textSize = fontSize
        color = Color(0,103,180).hashCode()
        strokeWidth = 24f
        strokeMiter= 20f
        strokeJoin = android.graphics.Paint.Join.ROUND
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = customTypefaceHollow
    }

    val textPaint = Paint().asFrameworkPaint().apply {
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
        style = android.graphics.Paint.Style.FILL
        textSize = fontSize
        color = Color(255, 203, 8).hashCode()
        typeface = customTypefaceFilled
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .height(50.dp)
                .background(Color.White)
                .align(Alignment.Center)
            ,
            onDraw = {
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        text,
                        0f,
                        100f,
                        textPaintStroke
                    )
                    it.nativeCanvas.drawText(
                        text,
                        0f,
                        100f,
                        textPaint
                    )
                }
            }
        )
    }
}