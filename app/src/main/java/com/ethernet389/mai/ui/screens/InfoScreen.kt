package com.ethernet389.mai.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.ethernet389.mai.R
import com.ethernet389.mai.util.annotatedStringResource

@Composable
fun InfoScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        item { InfoCard(text = annotatedStringResource(R.string.how_to_use), isTitle = true) }
        item { InfoCard(text = annotatedStringResource(R.string.instruction)) }
        item { InfoCard(text = annotatedStringResource(R.string.meaning_of_numbers), isTitle = true) }
        item { InfoCard(text = annotatedStringResource(R.string.description_of_numbers)) }
        item { InfoCard(text = annotatedStringResource(R.string.coherency), isTitle = true) }
        item { InfoCard(text = annotatedStringResource(R.string.description_of_coherent)) }
        item { InfoCard(text = annotatedStringResource(R.string.authors), isTitle = true) }
        item {
            val authors = stringArrayResource(R.array.authors).map { 
                AnnotatedString(text = it)
            }.reduce { acc, s -> AnnotatedString("$acc\n$s") }
            InfoCard(text = authors)
        }
        item { InfoCard(text = annotatedStringResource(R.string.literature), isTitle = true) }
        item {
            val titles = stringArrayResource(R.array.literature_names).map {
                AnnotatedString(text = it)
            }
            val links = stringArrayResource(R.array.hyperlink_literature)
            for (i in titles.indices)
                InfoCard(text = titles[i], hyperlink = links[i])
        }
    }
}

@Composable
private fun InfoCard(
    text: AnnotatedString,
    isTitle: Boolean = false,
    hyperlink: String? = null,
    colors: CardColors = CardDefaults.cardColors(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val linkColor = colorResource(R.color.link)
    val neutralColor = MaterialTheme.colorScheme.onSecondaryContainer
    val visitedLinkColor = colorResource(R.color.visited_link)


    var color by remember {
        mutableStateOf(if (hyperlink != null) linkColor else neutralColor)
    }
    Card(
        modifier = modifier
            .padding(dimensionResource(R.dimen.little_padding))
            .fillMaxWidth(),
        elevation = CardDefaults
            .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        shape = MaterialTheme.shapes.medium,
        colors = colors
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(dimensionResource(R.dimen.large_padding))
        ) {
            Text(
                text = text,
                style = with(MaterialTheme.typography) {
                    if (isTitle) titleLarge else bodyLarge
                },
                color = color,
                textDecoration = if (hyperlink != null) TextDecoration.Underline else null,
                fontWeight = if (isTitle) FontWeight.Bold else null,
                modifier = Modifier.clickable(enabled = hyperlink != null) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(hyperlink)
                    }
                    color = visitedLinkColor
                    context.startActivity(intent)
                }
            )
        }
    }
}