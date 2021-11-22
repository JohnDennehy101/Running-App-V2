package ie.wit.runappv2

import android.content.Context
import ie.wit.runappv2.models.Location
import ie.wit.runappv2.models.RaceJSONMemStore
import ie.wit.runappv2.models.RaceJSONStore
import ie.wit.runappv2.models.RaceModel

import org.junit.Test

import org.junit.Assert.*
import org.junit.After

import org.junit.Before
import org.mockito.Mockito.mock



class RaceJSONMemStoreUnitTest {
    var testRace1 = RaceModel(title="Test1", description="Description 1", raceDistance="5km", raceDate="12/12/2021", image="testimage.png", location=Location(1.0, 1.0, 15f))
    var testRace2 = RaceModel(title="Test2", description="Description 2", raceDistance="8km", raceDate="13/12/2021", image="testimage.png", location=Location(2.0, 2.0, 15f))
    var testRace3 = RaceModel(title="Test3", description="Description 3", raceDistance="10km", raceDate="14/12/2021", image="testimage.png", location=Location(3.0, 3.0, 15f))
    private val races = mutableListOf<RaceModel>(testRace1, testRace2, testRace3)
    lateinit var raceJsonStore: RaceJSONStore
    var context = mock(Context::class.java)

    @Before
    fun init() {
        raceJsonStore = RaceJSONMemStore(context, true)
        for (race in races) {
            raceJsonStore.create(race, true)
        }
    }

    @After
    fun teardown() {
        for (race in races) {
            raceJsonStore.delete(race, true)
        }
    }
    @Test fun `Successful creation of 3 race records`() {
        val allRaces = raceJsonStore.findAll()
        val race1 = allRaces.find { it.description == testRace1.description }
        val race2 = allRaces.find {it.title == testRace2.title}
        val race3 = allRaces.find {it.location == testRace3.location}
        assertNotNull(race1)
        assertNotNull(race2)
        assertNotNull(race3)
        assertEquals(race1?.title,testRace1.title)
        assertEquals(race2?.title, testRace2.title)
        assertEquals(race3?.title, testRace3.title)
    }
    @Test fun `Invalid Race Id returns null`() {
        val nonExistentRaceRecord = raceJsonStore.findOne(4)
        assertNull(nonExistentRaceRecord)
    }
    @Test fun `Valid Race Id returns race record`() {
        val allRaces = raceJsonStore.findAll()
        val findRaceOne = allRaces.find {it.id == allRaces[0].id}
        assertNotNull(findRaceOne)
        assertEquals(findRaceOne?.title, testRace1.title)
    }
    @Test fun `Successfully updating race description`() {
        val newDescription = "New Description"
        var allRaces = raceJsonStore.findAll()
        val race1 = allRaces.find { it.description == testRace1.description }!!

        race1.description = newDescription

        raceJsonStore.update(race1, true)
        allRaces = raceJsonStore.findAll()
        val updatedRace1 = allRaces.find {it.description == newDescription}!!
        assertEquals(race1.title, updatedRace1.title)
    }
    @Test fun `Successfully updating race title`() {
        val newTitle = "New Title"
        var allRaces = raceJsonStore.findAll()
        val race1 = allRaces.find { it.title == testRace1.title }!!

        race1.title = newTitle

        raceJsonStore.update(race1, true)
        allRaces = raceJsonStore.findAll()
        val updatedRace1 = allRaces.find {it.title == newTitle}!!
        assertEquals(updatedRace1.title, newTitle)
    }
    @Test fun `Successfully updating race date`() {
        val newDate = "31/12/2021"
        var allRaces = raceJsonStore.findAll()
        val race1 = allRaces.find { it.raceDate == testRace1.raceDate }!!
        race1.raceDate = newDate

        raceJsonStore.update(race1, true)
        allRaces = raceJsonStore.findAll()
        val updatedRace1 = allRaces.find {it.raceDate == newDate}!!
        assertEquals(updatedRace1.raceDate, newDate)
    }
    @Test fun `Successfully updating race distance`() {
        val newDistance = "1km"
        var allRaces = raceJsonStore.findAll()
        val race1 = allRaces.find { it.raceDistance == testRace1.raceDistance }!!

        race1.raceDistance = newDistance

        raceJsonStore.update(race1, true)
        allRaces = raceJsonStore.findAll()
        val updatedRace1 = allRaces.find {it.raceDistance == newDistance}!!
        assertEquals(updatedRace1.raceDistance, newDistance)
    }
    @Test fun `Successfully updating race image path`() {
        val newImagePath = "newTestImage.png"
        var allRaces = raceJsonStore.findAll()
        val race1 = allRaces.find { it.image == testRace1.image }!!

        race1.image = newImagePath

        raceJsonStore.update(race1, true)
        allRaces = raceJsonStore.findAll()
        val updatedRace1 = allRaces.find {it.image == newImagePath}!!
        assertEquals(updatedRace1.image, newImagePath)
    }
    @Test fun `Successfully updating race location`() {
        val newLocation = Location(10.0,10.0,10F)
        var allRaces = raceJsonStore.findAll()
        val race1 = allRaces.find { it.location == testRace1.location }!!

        race1.location = newLocation

        raceJsonStore.update(race1, true)
        allRaces = raceJsonStore.findAll()
        val updatedRace1 = allRaces.find {it.location == newLocation}!!
        assertEquals(updatedRace1.location, newLocation)
    }
    @Test fun `Successfully deleting race record`() {
        raceJsonStore.delete(testRace1, true)
        val allRacesAfterDelete = raceJsonStore.findAll()
        val searchForRaceOneAfterDelete = allRacesAfterDelete.find {it.title == testRace1.title}

        assertNull(searchForRaceOneAfterDelete)
    }
}