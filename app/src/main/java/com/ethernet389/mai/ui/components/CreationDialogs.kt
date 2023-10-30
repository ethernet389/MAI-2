package com.ethernet389.mai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ethernet389.domain.model.template.Template
import com.ethernet389.mai.R
import com.ethernet389.mai.util.simpleVerticalScrollbar


private val delimiters = Regex("""\s*[,;]+\s*""")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Name(
    placeholder: String,
    title: String,
    value: String,
    onValueChanged: (String) -> Unit,
    isNameError: Boolean = false,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 10.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChanged,
            placeholder = { Text(text = placeholder) },
            label = { Text(text = stringResource(R.string.name)) },
            singleLine = true,
            isError = isNameError
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateCreationDialog(
    onDismissRequest: () -> Unit,
    onCreateRequest: (Template) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        var name by rememberSaveable { mutableStateOf("") }
        var criteriaString by rememberSaveable { mutableStateOf("") }
        var isNameError by remember { mutableStateOf(false) }
        var isOptionsError by remember { mutableStateOf(false) }

        Card(
            modifier = modifier.width(200.dp),
            elevation = CardDefaults
                .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Name(
                    title = stringResource(R.string.template),
                    placeholder = stringResource(R.string.template_name_example),
                    value = name,
                    onValueChanged = { newName -> name = newName },
                    isNameError = isNameError
                )
                TextField(
                    value = criteriaString,
                    onValueChange = { newOptions -> criteriaString = newOptions },
                    placeholder = { Text(text = stringResource(R.string.criteria_example)) },
                    label = { Text(text = stringResource(R.string.criteria)) },
                    isError = isOptionsError
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .padding(5.dp)
                        .height(50.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val criteria = criteriaString
                                .split(delimiters)
                                .filter { option -> option.isNotBlank() }
                            isOptionsError = criteria.isEmpty()
                            isNameError = name.isBlank()
                            if (isNameError || isOptionsError) return@Button
                            onCreateRequest(Template(name = name, criteria = criteria))
                        },
                    ) {
                        Text(text = stringResource(R.string.create))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCreationDialog(
    onDismissRequest: () -> Unit,
    onCreateRequest: (String, Template, List<String>) -> Unit,
    templates: List<Template>,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        var name by rememberSaveable { mutableStateOf("") }
        var alternatives by rememberSaveable { mutableStateOf("") }
        var isNameError by remember { mutableStateOf(false) }
        var isAlternativeError by remember { mutableStateOf(false) }
        var selectedIndex by rememberSaveable { mutableStateOf<Int?>(null) }
        val templateListState = rememberLazyListState()

        Card(
            modifier = modifier.width(200.dp),
            elevation = CardDefaults
                .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Name(
                    title = stringResource(R.string.note),
                    placeholder = stringResource(R.string.note_name_example),
                    value = name,
                    onValueChanged = { newName -> name = newName },
                    isNameError = isNameError
                )
                TextField(
                    value = alternatives,
                    onValueChange = { newAlternatives -> alternatives = newAlternatives },
                    placeholder = { Text(text = stringResource(R.string.alternatives_example)) },
                    label = { Text(text = stringResource(R.string.alternatives)) },
                    isError = isAlternativeError
                )
                Text(
                    text = stringResource(R.string.templates),
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
                LazyColumn(
                    state = templateListState,
                    modifier = Modifier
                        .heightIn(min = 0.dp, max = 200.dp)
                        .simpleVerticalScrollbar(state = templateListState)
                ) {
                    itemsIndexed(items = templates) { index, template ->
                        Column(modifier = Modifier.clickable { selectedIndex = index }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(dimensionResource(R.dimen.medium_padding))
                            ) {
                                RadioButton(
                                    selected = index == selectedIndex,
                                    onClick = null,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(text = template.name, modifier = Modifier.weight(4f))
                            }
                            Divider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.inverseSurface
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .padding(5.dp)
                        .height(50.dp)
                ) {
                    OutlinedButton(onClick = onDismissRequest) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            isNameError = name.isBlank()
                            val splitAlternatives = alternatives
                                .split(delimiters)
                                .filter { it.isNotBlank() }
                            isAlternativeError = splitAlternatives.isEmpty()
                            if (
                                isNameError || selectedIndex == null || isAlternativeError
                            ) return@Button
                            val template = templates[selectedIndex!!]
                            onCreateRequest(name, template, splitAlternatives)
                        }
                    ) {
                        Text(text = stringResource(R.string.create))
                    }
                }
            }
        }
    }
}