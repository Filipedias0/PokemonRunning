package com.example.pokedexapp.data.remote.responses

import com.google.gson.annotations.SerializedName

data class GenerationIv(
    @SerializedName("fdiamond-pearl")
    val diamondPearl: DiamondPearl,
    @SerializedName("heartgold-soulsilver")
    val heartgoldSoulsilver: HeartgoldSoulsilver,
    val platinum: Platinum
)