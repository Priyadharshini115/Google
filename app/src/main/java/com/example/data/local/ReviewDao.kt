package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SavedReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM saved_reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<SavedReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: SavedReviewEntity): Long

    @Delete
    suspend fun deleteReview(review: SavedReviewEntity)

    @Query("DELETE FROM saved_reviews")
    suspend fun clearAllReviews()
}
