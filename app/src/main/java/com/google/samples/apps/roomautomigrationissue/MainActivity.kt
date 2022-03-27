package com.google.samples.apps.roomautomigrationissue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int?,
    val hairColor: String?
)

@Database(
    entities = [User::class],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
    ],
    exportSchema = true,
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

/**
 * DAO for [User] access
 */
@Dao
interface UserDao {
    @Query(value = "SELECT * FROM users")
    fun getAll(): List<User>

    @Insert
    fun insert(user: User)
}


class MainActivity : AppCompatActivity() {

    private val dbScope = CoroutineScope(Job() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        dbScope.launch {
            val db = Room.databaseBuilder(
                applicationContext,
                UserDatabase::class.java, "database-name"
            ).build()

            val userDao = db.userDao()

            // insert a user
            userDao.insert(User(name = "Don Turner", age = 24, eyeColor = "green"))

            // get the user
            val firstUser = userDao.getAll().first()
            Log.d("MainActivity", "$firstUser")

        }

        setContentView(R.layout.activity_main)
    }
}