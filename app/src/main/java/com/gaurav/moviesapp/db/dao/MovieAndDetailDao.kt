package com.gaurav.moviesapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gaurav.moviesapp.models.Movie
import com.gaurav.moviesapp.util.Category

@Dao
interface MovieAndDetailDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovies(vararg movie: Movie):LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: Movie)

    @Update
    fun updateMovie(movie:Movie)

    @Query("SELECT * FROM movie WHERE category=:category LIMIT (:pageNumber*20)")
    fun getMovies(pageNumber: Int,category: Category):LiveData<List<Movie>>

    /*LIST VIEW STATE*/
    @Query("SELECT * FROM movie WHERE title LIKE '%' || :query || '%'  LIMIT (:pageNumber*20) ")
    fun searchListMovie(query: String,pageNumber:Int):LiveData<List<Movie>>

}