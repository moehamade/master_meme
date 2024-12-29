package com.mobilecampus.mastermeme.meme.data.local.use_case

import com.mobilecampus.mastermeme.core.presentation.design_system.AppIcons
import com.mobilecampus.mastermeme.meme.domain.model.MemeItem
import com.mobilecampus.mastermeme.meme.domain.use_case.GetTemplatesUseCase

class GetTemplatesUseCaseImpl(
) : GetTemplatesUseCase {
    override suspend operator fun invoke(): List<MemeItem.Template> {
        val templates: List<MemeItem.Template> by lazy {
            AppIcons.meme.mapIndexed { index, resourceId ->
                MemeItem.Template(
                    imageUri = "meme_template_${(index + 1).toString().padStart(2, '0')}",
                    description = getMemeDescription(index),
                    resourceId = resourceId
                )
            }
        }
        return templates
    }

    private fun getMemeDescription(index: Int): String = when (index) {
        0 -> "Distracted boyfriend looking at another girl while with his girlfriend"
        1 -> "Drake refusing and approving different options in a split panel"
        2 -> "Woman yelling at confused cat at dinner table"
        3 -> "Galaxy brain expanding with increasingly complex ideas"
        4 -> "Two buttons with sweating person choosing between them"
        5 -> "Spider-Man pointing at Spider-Man in identical poses"
        6 -> "Success Kid making a fist in triumph on the beach"
        7 -> "Ancient Aliens guy with wild hair explaining conspiracy theories"
        8 -> "This is fine dog sitting in burning room"
        9 -> "One Does Not Simply walk into Mordor Boromir meme"
        10 -> "Change My Mind guy sitting at table with sign"
        11 -> "Roll Safe guy tapping temple with clever but flawed logic"
        12 -> "Expanding brain showing increasingly absurd ideas"
        13 -> "Surprised Pikachu face reacting to obvious outcome"
        14 -> "Monkey puppet looking away nervously"
        15 -> "Confused math lady with calculations floating around"
        16 -> "Disaster girl smiling in front of burning house"
        17 -> "Tom reading a small book vs a huge scroll of truth"
        18 -> "I see this as an absolute win Hulk celebrating"
        19 -> "Hide the Pain Harold forcing a smile through inner pain"
        20 -> "Buff Doge vs Cheems comparing past and present"
        21 -> "Always Has Been astronauts with gun in space"
        22 -> "Stonks man with rising arrow graph"
        23 -> "Panik Kalm Panik sequence of reactions"
        24 -> "They're the same picture Pam from The Office comparing"
        25 -> "Leonardo DiCaprio laughing with wine glass"
        26 -> "Is this a pigeon guy misidentifying obvious things"
        27 -> "Evil Kermit suggesting bad ideas to regular Kermit"
        28 -> "Think Mark Think from Invincible animation"
        29 -> "Gru's plan presentation with unexpected final panel"
        30 -> "Left Exit 12 Off Ramp car swerving to take exit"
        31 -> "Tuxedo Winnie the Pooh elegant vs regular version"
        32 -> "Call"
        33 -> "Bernie asking for financial support once again"
        34 -> "Anakin and Padme discussing increasingly concerning topics"
        35 -> "Trade Offer TikTok guy making a proposition"
        36 -> "Disappointed but not surprised cricket fan reaction"
        37 -> "X when Y / X when Z comparing different reactions"
        38 -> "Gigachad responding with simple based statement"
        39 -> "Running away balloon representing avoiding responsibilities"
        40 -> "Mother ignoring kid drowning in pool while helping other kid"
        41 -> "Time traveler moves chair / Timeline changes dramatically"
        42 -> "Average fan vs Average enjoyer comparison"
        43 -> "Sir this is a Wendys responding to long rant"
        44 -> "Farmers getting ready to post freshly grown memes"
        45 -> "Finally, inner peace after achieving something simple"
        46 -> "You guys are getting paid? We're the Millers scene"
        47 -> "I missed the part where that's my problem Tobey Maguire"
        48 -> "Look what they need to mimic a fraction of our power"
        49 -> "Never gonna give you up Rick Astley classic"
        50 -> "Squid Games"
        51 -> "Squid Games Old Man"
        else -> "Template ${index + 1}"
    }
}