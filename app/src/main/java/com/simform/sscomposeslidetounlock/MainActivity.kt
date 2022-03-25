package com.simform.sscomposeslidetounlock

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simform.slidetounlock.SlideToUnlock
import com.simform.slidetounlock.SliderAppearance
import com.simform.slidetounlock.SliderThumb
import com.simform.sscomposeslidetounlock.ui.theme.SSComposeSlideToUnlockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SSComposeSlideToUnlockTheme {
                SwipeToUnlockUI()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SSComposeSlideToUnlockTheme {
        SwipeToUnlockUI()
    }
}

@Composable
fun SwipeToUnlockUI() {
    val context = LocalContext.current
    var cornerRadius by remember {
        mutableStateOf(30f)
    }
    var thumbs by remember {
        mutableStateOf(setOf(SliderThumb.Left))
    }
    var thumbSize by remember {
        mutableStateOf(50f)
    }
    val sliderAppearance by remember(cornerRadius, thumbSize) {
        mutableStateOf(
            SliderAppearance(
                cornerRadius = cornerRadius.dp,
                elevation = 5.dp,
                thumbSize = thumbSize.dp
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(Modifier.align(Alignment.Center)) {
            SlideToUnlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                sliderAppearance = sliderAppearance,
                sliderThumb = thumbs,
                leftThumbIcon = {
                    Icon(
                        imageVector = Icons.Filled.Call,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                },
                rightThumbIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                },
                onSwiped = {
                    Toast.makeText(context, it.name, Toast.LENGTH_LONG).show()
                }
            ) {
                Text(
                    text = "Swipe to call or Unlock",
                    fontFamily = FontFamily.Cursive,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(text = "Thumbs")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = thumbs.contains(SliderThumb.Left),
                    onCheckedChange = {
                        thumbs = if (it) {
                            mutableSetOf(
                                SliderThumb.Left
                            ).plus(thumbs)
                        } else {
                            val temp = thumbs.toMutableSet().apply {
                                remove(SliderThumb.Left)
                            }
                            if (temp.isEmpty()) {
                                setOf(SliderThumb.Left)
                            } else {
                                temp
                            }
                        }
                    }
                )
                Text(
                    text = SliderThumb.Left.name,
                    modifier = Modifier.padding(start = 10.dp, end = 20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )
                Checkbox(
                    checked = thumbs.contains(SliderThumb.Right),
                    onCheckedChange = {
                        thumbs = if (it) {
                            mutableSetOf(
                                SliderThumb.Right
                            ).plus(thumbs)
                        } else {
                            val temp = thumbs.toMutableSet().apply {
                                remove(SliderThumb.Right)
                            }
                            if (temp.isEmpty()) {
                                setOf(SliderThumb.Right)
                            } else {
                                temp
                            }
                        }
                    }
                )
                Text(
                    text = SliderThumb.Right.name,
                    modifier = Modifier.padding(start = 10.dp, end = 20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )
            }

            Text(text = "Thumb Size")
            Slider(
                valueRange = 30f..70f,
                value = thumbSize,
                steps = 10,
                onValueChange = {
                    thumbSize = it
                }
            )
            Text(text = "Corner Radius")
            Slider(
                valueRange = 0f..30f,
                value = cornerRadius,
                steps = 10,
                onValueChange = {
                    cornerRadius = it
                }
            )
        }
    }
}
