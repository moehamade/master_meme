package com.mobilecampus.mastermeme.meme.presentation.screens.meme_list.components

import android.net.Uri
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.mobilecampus.mastermeme.R
import com.mobilecampus.mastermeme.meme.domain.model.Meme

@Composable
fun MemeGrid(
    memes: List<Meme>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(10) {
            MemeGridItem(
                memeUrl = memes[it].imageUri,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MemeGridPreview() {
    val context = LocalContext.current
    val imageUri =
        Uri.parse("android.resource://${context.packageName}/${R.drawable.ic_launcher_background}")
    MemeGrid(
        memes = List(10) {
            Meme(
                id = it,
                title = "Meme $it",
                isFavorite = false,
                imageUri = imageUri.toString(),
            )
        },
        modifier = Modifier
    )
}