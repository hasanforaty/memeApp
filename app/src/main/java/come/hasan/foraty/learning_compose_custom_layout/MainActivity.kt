package come.hasan.foraty.learning_compose_custom_layout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import come.hasan.foraty.learning_compose_custom_layout.ui.theme.LearningComposeCustomLayoutTheme
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LearningComposeCustomLayoutTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name",
        Modifier
            .firstBaseLineTo(25.dp)
            .leftPadding(25.dp))
}

fun Modifier.leftPadding(
    leftPadding:Dp
) = this.then(
    layout{measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val width = placeable.width + leftPadding.roundToPx()
        layout(width = width,placeable.height){
            placeable.placeRelative(x = leftPadding.roundToPx(),y = 0)
        }
    }
)

fun Modifier.firstBaseLineTo(
    firstBaseLineTo: Dp
) = this.then(
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints = constraints)
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)

        val firstBaseLine = placeable[FirstBaseline]

        val placeableY = firstBaseLineTo.roundToPx() - firstBaseLine
        val height = placeable.height + placeableY
        layout(placeable.width, height) {
            placeable.placeRelative(0, placeableY)
        }

    }
)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LearningComposeCustomLayoutTheme {
        MyOwnColumn {
            repeat(100) {
                Greeting(name = "hello")
            }
        }
    }
}


@Composable
fun MyOwnColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurable, constraints ->
        val placable = measurable.map {
            it.measure(constraints)
        }
        var yPosition = 0
        layout(constraints.maxWidth, constraints.maxHeight) {
            placable.forEach {
                it.placeRelative(0, yPosition)
                yPosition += it.height
            }
        }
    }
}

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows:Int = 3,
    content: @Composable () -> Unit
){
    Layout(content = content,modifier = modifier){ measures , constrains ->
        val rowsHeight = IntArray(rows)
        val rowsWidth = IntArray(rows)
        val placeables = measures.mapIndexed{index, measurable ->
            val placeable = measurable.measure(constrains)
            val row = index % rows
            rowsWidth[row] += placeable.width
            rowsHeight[row] = max(rowsHeight[row],placeable.height)
            placeable
        }
        val width = rowsWidth.maxOrNull()
                ?.coerceIn(constrains.minWidth.rangeTo(constrains.maxWidth))?:constrains.minWidth
        val height = rowsHeight.sumOf { it }
            .coerceIn(constrains.minHeight.rangeTo(constrains.maxHeight))

        val rowsY = IntArray(rows){ 0 }
        for(i in 1 until rows){
            rowsY[i] = rowsY[i-1]+rowsHeight[i-1]
        }

        layout(width = width,height = height){
            val rowsX = IntArray(rows){ 0 }

            placeables.forEachIndexed{ index, placeable ->
                val row = index% rows
                placeable.placeRelative(x = rowsX[row], y = rowsY[row])
                rowsX[row]+=placeable.width
            }

        }
    }
}

val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)

@Preview
@Composable
fun PrevGrid(){
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        StaggeredGrid(rows = 5) {
            for (topic in topics){
                Greeting(name = topic)
            }
        }
    }

}