package io.github.andreasscherbaum.currencyconverter.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.andreasscherbaum.currencyconverter.BuildConfig
import io.github.andreasscherbaum.currencyconverter.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.about_version)) },
                supportingContent = { Text(BuildConfig.VERSION_NAME) }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.about_build)) },
                supportingContent = { Text("${BuildConfig.VERSION_CODE} (${BuildConfig.BUILD_TIMESTAMP})") }
            )

            HorizontalDivider()

            Text(
                text = stringResource(R.string.about_description),
                style = MaterialTheme.typography.bodyMedium
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.about_data_source)) },
                supportingContent = { Text(stringResource(R.string.about_ecb)) }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.about_author)) },
                supportingContent = { Text("Andreas Scherbaum") }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.about_website)) },
                supportingContent = { Text("https://andreas.scherbaum.la/") },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open") },
                modifier = Modifier.clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://andreas.scherbaum.la/")))
                }
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.about_source_code)) },
                supportingContent = { Text("https://github.com/andreasscherbaum/currency-converter") },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open") },
                modifier = Modifier.clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/andreasscherbaum/currency-converter")))
                }
            )
        }
    }
}
