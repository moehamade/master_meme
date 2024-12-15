package com.mobilecampus.mastermeme.meme.data.local.use_case

import android.content.Context
import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase

class GetTemplatesUseCaseImpl(
    private val context: Context
) : GetTemplatesUseCase {
    override suspend operator fun invoke(): List<MemeItem.Template> {
        val templates: List<MemeItem.Template> by lazy {
            AppIcons.meme.mapIndexed { index, resourceId ->
                MemeItem.Template(
                    imageUri = "meme_template_${(index + 1).toString().padStart(2, '0')}",
                    description = "Template ${index + 1}",
                    resourceId = resourceId
                )
            }
        }
        return templates
    }
}