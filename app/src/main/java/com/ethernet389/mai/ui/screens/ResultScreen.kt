package com.ethernet389.mai.ui.screens

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ethernet389.domain.model.note.Note
import com.ethernet389.mai.R
import com.ethernet389.mai.mai.FinalWeights
import com.ethernet389.mai.matrix_extensions.MaiCoefficients
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.google.accompanist.drawablepainter.rememberDrawablePainter

private fun createQrCode(text: String): Drawable {
    val options = createQrVectorOptions {

        padding = .05f

        background {
            color = QrVectorColor.Solid(Color.White.toArgb())
        }
    }
    val data = QrData.Text(text)
    return QrCodeDrawable(data = data, options = options, charset = Charsets.UTF_8)
}

@Composable
fun ResultScreen(
    note: Note,
    finalWeights: FinalWeights,
    crsOfEachAlternativesMatrices: List<Double>,
    crOfCriteriaMatrix: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val ratingBody = finalWeights
        .finalRelativeWeights
        .mapIndexed { i, coefficient -> note.alternatives[i] to coefficient }
        .sortedByDescending { it.second }
        .mapIndexed { i, pair -> "${i + 1}. ${pair.first} (${pair.second.toFloat()})" }
        .reduce { acc, s -> "$acc\n$s" }
    var shareBody = stringResource(R.string.rating) +
            "\n" +
            ratingBody +
            "\n\n" +
            "${stringResource(R.string.coherence_of_criteria)}: ${isConsistent(d = crOfCriteriaMatrix).first}" +
            "\n\n" +
            stringResource(R.string.coherence_of_alternatives_by_criterion) +
            "\n" +
            "=====" +
            "\n"
    note.template.criteria.forEachIndexed { i, s ->
        shareBody += "$s: ${isConsistent(crsOfEachAlternativesMatrices[i]).first}\n"
    }
    val qrCodeDrawable = createQrCode(shareBody)

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            ItemCard(modifier = Modifier.fillMaxWidth(),
                content = {
                    Text(
                        text = stringResource(R.string.rating),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = ratingBody)
                }
            )
        }
        item {
            ItemCard(
                modifier = modifier.fillMaxWidth(),
                content = {
                    Text(
                        text = stringResource(R.string.coherence_of_criteria),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    CrStatus(cr = crOfCriteriaMatrix)

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.large_padding)))

                    Text(
                        text = stringResource(R.string.coherence_of_alternatives_by_criterion),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Column {
                        val colorOfDivider = MaterialTheme.colorScheme.onTertiaryContainer
                        val thicknessOfDivider = 1.dp
                        Divider(thickness = thicknessOfDivider, color = colorOfDivider)
                        note.template.criteria.forEachIndexed { i, criterion ->
                            Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                                Text(
                                    text = criterion,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                                Divider(
                                    color = colorOfDivider,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(thicknessOfDivider)
                                )
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    CrStatus(cr = crsOfEachAlternativesMatrices[i])
                                }
                            }
                            Divider(thickness = thicknessOfDivider, color = colorOfDivider)
                        }
                    }
                }
            )
        }
        item {
            ItemCard(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
                    ) {
                        Text(
                            text = stringResource(R.string.share),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                val shareIntent = Intent()
                                    .apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareBody)
                                        type = "text/plain"
                                    }
                                context.startActivity(shareIntent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(shareBody))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentPaste,
                                contentDescription = null,
                                modifier = Modifier.fillMaxHeight()
                            )
                        }
                    }

                    Image(
                        painter = rememberDrawablePainter(drawable = qrCodeDrawable),
                        contentDescription = null,
                        modifier = Modifier.sizeIn(
                            minWidth = 200.dp, minHeight = 200.dp,
                            maxWidth = 300.dp, maxHeight = 300.dp
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun CrStatus(
    cr: Double,
    modifier: Modifier = Modifier
) {
    val (text, isCoherent) = isConsistent(cr)
    val (color, icon) = when(isCoherent) {
        true -> colorResource(R.color.is_not_inverse_color) to Icons.Filled.Check
        false -> colorResource(R.color.is_inverse_color) to Icons.Filled.Close
    }
    Row(modifier = modifier) {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Center
        )
        Icon(
            imageVector = icon,
            tint = color,
            contentDescription = null
        )
    }
}

@Composable
@ReadOnlyComposable
private fun isConsistent(d: Double): Pair<String, Boolean> {
    return when (d <= MaiCoefficients.MAX_OF_CR) {
        true -> stringResource(R.string.coherent) to true
        false -> stringResource(R.string.non_coherent) to false
    }
}

@Composable
private fun ItemCard(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults
            .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        modifier = modifier.padding(dimensionResource(R.dimen.little_padding)),
        shape = MaterialTheme.shapes.medium
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            content = { content() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.medium_padding))
        )
    }
}