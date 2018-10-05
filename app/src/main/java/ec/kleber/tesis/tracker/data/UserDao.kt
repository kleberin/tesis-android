package ec.kleber.tesis.tracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Query("SELECT * FROM User WHERE email = :email")
    fun findByEmail(email: String): User?

    @Insert
    fun insertAll(vararg users: User)
}