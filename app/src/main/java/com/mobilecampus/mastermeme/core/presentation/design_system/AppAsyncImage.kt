package com.mobilecampus.mastermeme.core.presentation.design_system

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mobilecampus.mastermeme.R

/**
 * A composable that displays a locally stored image with rounded corners.
 * This component handles:
 * - Loading images from local storage
 * - Displaying a placeholder while loading
 * - Error handling if image fails to load
 * - Applying rounded corners using clip modifier
 */
@Composable
fun AppAsyncImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    cornerRadius: Dp = 8.dp
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
        error = painterResource(id = R.drawable.ic_launcher_foreground),
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
    )
}
class SampleImageUrlProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "",
        "content://media/external/images/media/32",
        "file:///data/user/0/com.example.app/cache/image_manager_disk_cache/84e7e69530b75233.0"
    )
}
@Preview
@Composable
fun AppAsyncImagePreview(
    @PreviewParameter(SampleImageUrlProvider::class) imageUrl: String
) {
    AppAsyncImage(imageUrl = imageUrl)
}