package ie.wit.runappv1.models

data class UnifiedModel (
    var users: MutableList<UserModel>? = null,
    var races: MutableList<RaceModel>? = null
)