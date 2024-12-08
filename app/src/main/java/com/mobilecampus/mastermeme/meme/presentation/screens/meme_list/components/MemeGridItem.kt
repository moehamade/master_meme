package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components

import android.net.Uri
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.core.presentation.design_system.AppAsyncImage

@Composable
fun MemeGridItem(
    memeUrl: String,
    modifier: Modifier = Modifier
) {
    AppAsyncImage(
        imageUrl = memeUrl,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Makes the item square
        contentDescription = "Meme image"
    )
}

@Preview(showBackground = true)
@Composable
fun MemeGridItemPreview() {
    val context = LocalContext.current
    val imageUri =
        Uri.parse("android.resource://${context.packageName}/${R.drawable.ic_launcher_foreground}")

    MemeGridItem(
        memeUrl = imageUri.toString(),
        modifier = Modifier
    )
}