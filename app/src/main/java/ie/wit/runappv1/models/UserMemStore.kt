package ie.wit.runappv1.models
import timber.log.Timber.i

class UserMemStore : UserStore {

    val users = ArrayList<UserModel>()

    override fun findAll(): List<UserModel> {
        return users
    }

    override fun findOne(email : String ): UserModel? {
        var foundUser: UserModel? = users.find { p -> p.email == email }
        return foundUser
    }

    override fun create(user: UserModel) {
        users.add(user)
        logAll()
    }

    override fun update(user: UserModel) {
        var foundUser: UserModel? = users.find { p -> p.email == user.email }
//        if (foundRace != null) {
//            foundRace.title = race.title
//            foundRace.description = race.description
//            foundRace.raceDate = race.raceDate
//            foundRace.raceDistance = race.raceDistance
//            logAll()
//        }
    }

     fun logAll() {
       users.forEach { i("$it") }
    }
}