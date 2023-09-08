package com.ethernet389.mai.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ethernet389.mai.R

@Composable
fun InfoScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        item { InfoCard(text = stringResource(R.string.how_to_use), isTitle = true) }
        item { InfoCard(text = stringResource(R.string.instruction)) }
        item { InfoCard(text = stringResource(R.string.how_to_create), isTitle = true) }
        item { InfoCard(text = stringResource(R.string.how_to_fill)) }
        item { InfoCard(text = stringResource(R.string.literature), isTitle = true) }
        item {
            val titles = stringArrayResource(R.array.literature_names)
            val links = stringArrayResource(R.array.hyperlink_literature)
            for (i in titles.indices)
                InfoCard(text = titles[i], hyperlink = links[i])
        }
        item { InfoCard(text = stringResource(R.string.how_to_use), isTitle = true) }
        item { InfoCard(text = stringResource(R.string.instruction)) }
        item { InfoCard(text = stringResource(R.string.how_to_create), isTitle = true) }
        item { InfoCard(text = stringResource(R.string.how_to_fill)) }
        item { InfoCard(text = stringResource(R.string.literature), isTitle = true) }
        item {
            val titles = stringArrayResource(R.array.literature_names)
            val links = stringArrayResource(R.array.hyperlink_literature)
            for (i in titles.indices)
                InfoCard(text = titles[i], hyperlink = links[i])
        }
    }
}

@Composable
fun InfoCard(
    text: String,
    isTitle: Boolean = false,
    hyperlink: String? = null,
    colors: CardColors = CardDefaults.cardColors(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        modifier = modifier.padding(15.dp),
        elevation = CardDefaults
            .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        shape = MaterialTheme.shapes.medium,
        colors = colors
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = with(MaterialTheme.typography) {
                    if (isTitle) titleLarge else bodyMedium
                },
                fontWeight = if (isTitle) FontWeight.Bold else null,
                modifier = Modifier.clickable(enabled = hyperlink != null) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(hyperlink)
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Preview
@Composable
fun InfoCardPreviewTitle() {
    InfoCard(text = "title", isTitle = true)
}

@Preview
@Composable
fun InfoCardPreviewNoTitle() {
    InfoCard(text = "body text", isTitle = false)
}