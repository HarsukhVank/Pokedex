package com.aa.android.pokedex.nav

import kotlinx.serialization.Serializable

sealed class NavDestination {
    @Serializable
    object Main

    @Serializable
    data class Detail(val name: String)
}