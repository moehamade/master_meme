package com.mobilecampus.mastermeme.meme.data.local.use_case

import android.content.Context
import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons
import com.mobilecampus.mastermeme.meme.domain.data_source.MemeDataSource
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase
import java.util.UUID

// Update your GetTemplatesUseCase to include the resource ID
class GetTemplatesUseCaseImpl(
    private val context: Context
) : GetTemplatesUseCase {
    override suspend operator fun invoke(): List<MemeItem.Template> {
        return (1..49).map { index ->
            val number = index.toString().padStart(2, '0')
            val resourceName = "meme_template_$number"
            val resourceId = context.resources.getIdentifier(
                resourceName,
                "drawable",
                context.packageName
            )

            MemeItem.Template(
                id = UUID.randomUUID().toString(),
                imageUri = resourceName,
                description = "Template $index",
                resourceId = resourceId  // Store the actual resource ID
            )
        }
    }
}