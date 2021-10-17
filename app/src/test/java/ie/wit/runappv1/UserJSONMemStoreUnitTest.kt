package ie.wit.runappv1

import android.content.Context
import at.favre.lib.crypto.bcrypt.BCrypt
import ie.wit.runappv1.models.*

import org.junit.Test

import org.junit.Assert.*
import org.junit.After

import org.junit.Before
import org.mockito.Mockito.mock



class UserJSONMemStoreUnitTest {
    var testUser1 = UserModel(userName="testUser1", email="testUser1@test.com")
    var testUser2 = UserModel(userName="testUser2", email="testUser2@test.com")
    var testUser3 = UserModel(userName="testUser3", email="testUser3@test.com")
    private val users = mutableListOf<UserModel>(testUser1, testUser2, testUser3)
    lateinit var userJsonStore: UserJSONMemStore
    var context = mock(Context::class.java)

    @Before
    fun init() {
        userJsonStore = UserJSONMemStore(context, true)
        for (user in users) {
            val passHash = BCrypt.withDefaults().hashToString(12, "a".toCharArray())
            user.passwordHash = passHash.toCharArray()
            userJsonStore.create(user, true)
        }
    }

    @After
    fun teardown() {
//        for (user in users) {
//            userJsonStore.delete(user, true)
//        }
    }
    @Test fun `Successful creation of 3 user records`() {
        val allUsers = userJsonStore.findAll()
        val user1 = allUsers.find { it.userName == testUser1.userName }
        val user2 = allUsers.find {it.userName == testUser2.userName}
        val user3 = allUsers.find {it.userName == testUser3.userName}
        assertNotNull(user1)
        assertNotNull(user2)
        assertNotNull(user3)
        assertEquals(user1?.userName,testUser1.userName)
        assertEquals(user2?.userName, testUser2.userName)
        assertEquals(user3?.userName, testUser3.userName)
    }

    @Test fun `Email for non-existent record returns null`() {
        val nonExistentUserRecord = userJsonStore.findOne("notexisting@gmail.com")
        assertNull(nonExistentUserRecord)
    }
    @Test fun `Valid User email returns user record`() {
        val validEmailReturnsRecord = userJsonStore.findOne("testUser1@test.com")
        assertNotNull(validEmailReturnsRecord)
        assertEquals(validEmailReturnsRecord?.email, testUser1.email)
    }
    @Test fun `Find All Returns All User Records`() {
        val allUsers = userJsonStore.findAll()
        assertEquals(allUsers.size, users.size)
    }

}