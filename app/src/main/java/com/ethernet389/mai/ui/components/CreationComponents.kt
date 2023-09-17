package com.ethernet389.mai.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ethernet389.mai.R


private val delimiters = Regex("""\s*[,;]+\s*""")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreationDialog(
    onDismissRequest: () -> Unit,
    onCreateRequest:(String, List<String>) -> Unit,
    title: String,
    namePlaceholder: String,
    nameTitle: String = stringResource(R.string.name),
    optionsPlaceholder: String,
    optionsLabel: String,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        var name by rememberSaveable { mutableStateOf("") }
        var options by rememberSaveable { mutableStateOf("") }
        var isNameError by rememberSaveable { mutableStateOf(false) }
        var isOptionsError by rememberSaveable { mutableStateOf(false) }

        Card(
            modifier = modifier.width(200.dp),
            elevation = CardDefaults
                .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
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
                    value = name,
                    onValueChange = { newName -> name = newName },
                    placeholder = { Text(text = namePlaceholder) },
                    label = { Text(text = nameTitle) },
                    singleLine = true,
                    isError = isNameError
                )
                TextField(
                    value = options,
                    onValueChange = { newOptions -> options = newOptions },
                    placeholder = { Text(text = optionsPlaceholder) },
                    label = { Text(text = optionsLabel) },
                    isError = isOptionsError
                )
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
                            val listedOptions = options
                                .split(delimiters)
                                .filter { option -> option.isNotBlank() }
                            isOptionsError = listedOptions.isEmpty()
                            isNameError = name.isBlank()
                            if (isNameError || isOptionsError) return@Button
                            onCreateRequest(name, listedOptions)
                        }
                    ) {
                        Text(text = stringResource(R.string.create))
                    }
                }
            }
        }
    }
}