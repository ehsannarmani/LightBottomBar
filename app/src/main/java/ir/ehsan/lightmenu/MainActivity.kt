package ir.ehsan.lightmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ir.ehsan.lightmenu.ui.theme.LightMenuTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LightMenuTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    Scaffold(bottomBar = {
//                        BottomBar()
                    }) { paddings ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = paddings.calculateTopPadding()
                                ), contentAlignment = Alignment.Center
                        ) {
                            BottomBar()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar() {
    val configuration = LocalConfiguration.current
    val items = remember {
        mutableStateListOf(
            Item(
                icon = Icons.Rounded.Home,
                color = Color(0xFFFF4747),
            ),
            Item(
                icon = Icons.Rounded.ShoppingCart,
                color = Color(0xFFFFAC47),
            ),
            Item(
                icon = Icons.Rounded.Person,
                color = Color(0xFF47A0FF),
            ),
            Item(
                icon = Icons.Rounded.Search,
                color = Color(0xFFB247FF),
            ),
            Item(
                icon = Icons.Rounded.Call,
                color = Color(0xFF47FFA0),
            ),
        )
    }
    val indicatorWidth = (configuration.screenWidthDp/items.count())/2
    val selectedIndex = remember {
        mutableStateOf(0)
    }
    val indicatorOffset by animateIntOffsetAsState(
        targetValue = IntOffset(
            items[selectedIndex.value].offset.x.toInt() + (items[selectedIndex.value].size.width / 4) - (items.count()*2)+(-2),
            15
        ), animationSpec = tween(400)
    )
    val indicatorColor by animateColorAsState(
        targetValue = items[selectedIndex.value].color,
        animationSpec = tween(500)
    )
    val infiniteTransition = rememberInfiniteTransition()
    val indicatorFlashingColor  by infiniteTransition.animateFloat(
        initialValue = .7f,
        targetValue = .6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )
    val switching = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(switching.value) {
        if (switching.value) {
            delay(250)
            switching.value = false
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(10.dp))
            .clip(
                RoundedCornerShape(10.dp)
            )
            .background(Color(0xff212121))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .onGloballyPositioned {
                            val offset = it.positionInParent()
                            items[index] =
                                items[index].copy(
                                    offset = offset,
                                    size = it.size
                                )
                        }
                        .weight((1.0 / items.count()).toFloat())
                        .clickable(
                            indication = null,
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            onClick = {
                                switching.value = true
                                selectedIndex.value = index
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .offset {
                    indicatorOffset
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        2.dp,
                        CircleShape,
                        ambientColor = indicatorColor,
                        spotColor = indicatorColor
                    )
                    .height(3.dp)
                    .width(indicatorWidth.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
            AnimatedVisibility(
                visible = !switching.value,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .drawBehind {
                            val path = Path()
                            path.moveTo(100f, 0f)
                            path.lineTo(38f, 0f)
                            path.lineTo(-3f, 135f)
                            path.lineTo(135f, 135f)
                            path.close()
                            drawPath(
                                path = path,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        indicatorColor.copy(
                                            alpha = indicatorFlashingColor - .2f
                                        ),
                                        indicatorColor.copy(
                                            alpha = indicatorFlashingColor-.4f
                                        ),
                                        Color.Transparent
                                    )
                                ),
                            )
//
                        }
                )
            }
        }
    }
}

data class Item(
    val icon: ImageVector,
    val color: Color,
    val offset: Offset = Offset(0f, 0f),
    val size: IntSize = IntSize(0, 0)
)
