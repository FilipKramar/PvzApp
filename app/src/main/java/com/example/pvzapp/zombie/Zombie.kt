package com.example.pvzapp.zombie

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Zombie(
    var name: String? = null,
    var picture: String? = null,
    var Ability: String? = null,
    var toughness: String? = null,
    var Description: String? = null,
    var isstarred: Boolean? = null

) : Parcelable {

    constructor() : this("", "", "", "", "",null)
}
