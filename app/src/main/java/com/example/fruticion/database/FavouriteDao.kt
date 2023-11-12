package com.example.fruticion.database

import Favourite
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fruticion.model.Fruit

@Dao
interface FavouriteDao {

    @Query("SELECT * FROM Favourite JOIN Fruit ON Favourite.fruitId = Fruit.roomId WHERE Favourite.userId=':userId'")
    suspend fun getAllFavFruitsByUser(userId: Long): List<Fruit>

    @Insert
    suspend fun addFavFruit(favourite: Favourite)

    @Query("DELETE FROM Favourite WHERE userId = :userId AND fruitId = :fruitId")
    suspend fun deleteFavById(userId: Long, fruitId: Long)
}