package com.example.skyexplorer

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey

@Database(entities = [PhotoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Query("SELECT * FROM photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("SELECT * FROM photos")
    suspend fun getAllPhotosOnce(): List<PhotoEntity>
}

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String,
    val timestamp: Long = System.currentTimeMillis()
)