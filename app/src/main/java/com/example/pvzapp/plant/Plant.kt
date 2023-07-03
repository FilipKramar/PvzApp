package com.example.pvzapp.plant

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Plant(
    var name: String? = null,
    var cost: String? = null,
    var terrain: String? = null,
    var damage: String? = null,
    var recharge: String? = null,
    var picture: String? = null,
    var Ability: String? = null,
    var Description: String? = null,
    var isstarred: Boolean? = null
) : Parcelable {
    constructor() : this("", "", "", "",null)
}
