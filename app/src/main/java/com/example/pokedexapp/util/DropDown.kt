package com.example.pokedexapp.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController

@Composable
fun DropDown(
    text: String,
    initiallyOpened: Boolean = false,
    options: List<String>,
    sortByState: MutableLiveData<String>,
    modifier: Modifier
) {
    var isOpen by remember {
        mutableStateOf(initiallyOpened)
    }

    val alpha = animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300
        )
    )

    val rotateX = animateFloatAsState(
        targetValue = if (isOpen) 0f else -90f,
        animationSpec = tween(
            durationMillis = 300
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
    ) {

        Column {

            Row {

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isOpen = !isOpen
                        }
                        .shadow(10.dp, RoundedCornerShape(10.dp))
                        .background(Color(255, 203, 8))
                        .padding(12.dp, 8.dp, 12.dp, 0.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = text,
                            fontWeight = FontWeight.Medium,
                            color = Color(0, 103, 180),
                            fontSize = 20.sp,
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Open or close the dropdown",
                            tint = Color(0, 103, 180),
                            modifier = Modifier
                                .scale(1f, if (isOpen) -1f else 1f)
                        )

                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

            }

            if (isOpen) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.5f, 0f)
                            rotationX = rotateX.value
                        }
                        .alpha(alpha.value)
                        .fillMaxWidth()
                        .shadow(10.dp, RoundedCornerShape(10.dp))
                        .background(Color(255, 203, 8))
                        .padding(12.dp, 8.dp, 12.dp, 12.dp)
                ) {
                    options.forEach {
                        Text(
                            text = it,
                            fontSize = 18.sp,
                            color = Color(0, 103, 180),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .clickable {
                                    sortByState.postValue(it)
                                    isOpen = false
                                }
                        )
                    }
                }
            }
        }
    }
}