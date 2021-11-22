package ie.wit.runappv2.models

data class UnifiedModel (
    var users: MutableList<UserModel>? = null,
    var races: MutableList<RaceModel>? = null
)