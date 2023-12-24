package com.candra.dicodingstoryapplication.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.candra.dicodingstoryapplication.model.StoryModel

@Dao
interface DaoStory {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryModel)

    @Query("SELECT * FROM story_table")
    fun getAllStory(): PagingSource<Int, StoryModel>

    @Query("DELETE FROM story_table")
    suspend fun deleteAll()
}