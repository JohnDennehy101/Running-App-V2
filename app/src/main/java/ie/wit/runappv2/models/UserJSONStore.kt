package ie.wit.runappv2.models

interface UserJSONStore {
    fun findAll(): List<UserModel>
    fun create(user: UserModel, test: Boolean = false)
    fun update(user: UserModel, test: Boolean = false)
    fun findOne(email: String) : UserModel?
}