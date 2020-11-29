package com.gaurav.moviesapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gaurav.moviesapp.db.dao.MovieAndDetailDao
import com.gaurav.moviesapp.models.Movie

@Database(
    entities = [Movie::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converter::class)
abstract class MovieDB : RoomDatabase() {
    abstract val movieDao: MovieAndDetailDao
}