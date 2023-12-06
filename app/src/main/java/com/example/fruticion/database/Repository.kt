package com.example.fruticion.database

import androidx.lifecycle.LiveData
import com.example.fruticion.view.activity.LoginActivity
import com.example.fruticion.api.APIError
import com.example.fruticion.api.FruitMapper
import com.example.fruticion.api.FruticionAPI
import com.example.fruticion.model.DailyIntake
import com.example.fruticion.model.Favourite
import com.example.fruticion.model.Fruit
import com.example.fruticion.model.User
import com.example.fruticion.model.WeeklyIntake
import java.time.LocalDate
import java.time.LocalTime
import android.util.Log

// Repository.k
class Repository (private val api: FruticionAPI, private val db: FruticionDatabase) {

    val favFruitsInList = LoginActivity.currentUserId?.let { userId ->
        Log.i("Valor id usuario antes de obtener los favs", "$userId")
        db.favouriteDao().getAllLDFavFruitsByUser(userId)
    }

    val dailyFruitsInList = LoginActivity.currentUserId?.let { userId ->
        Log.i("Valor id usuario antes de obtener los favs", "$userId")
        db.dailyIntakeDao().getAllLDDailyFruitsByUser(userId)
    }

    val weeklyFruitsInList = LoginActivity.currentUserId?.let { userId ->
        Log.i("Valor id usuario antes de obtener los favs", "$userId")
        db.weeklyIntakeDao().getAllLDWeeklyFruitsByUser(userId)
    }


    //Este metodo es para SearchFragment. Obtiene las frutas de la API, las mapea, las inserta en Room y las devuelve para meterlas en el Adapter del RecyclerView.
    suspend fun getFruits(): List<Fruit> {
        try {
            //llamada a la API
            val serializedFruits = api.getAllFruits()
            //se mapean las frutas de la API
            val readyFruitList = FruitMapper.mapFromSerializedFruitList(serializedFruits)
            //se aÃ±aden las frutas a la BD
            db.fruitDao().addFruitList(readyFruitList)

            //se devuelven todas las frutas de la BD
            return db.fruitDao().getAll()

        } catch (cause: Throwable) {
            throw APIError("Unable to fetch data from API", cause)
        }
    }


    // FavouriteFragment
    fun getAllFavFruits(): LiveData<List<Fruit>> {
        return db.favouriteDao().getAllLDFavFruitsByUser(LoginActivity.currentUserId!!)
    }

    suspend fun getAllFavFruitsList(): List<Fruit> {
        return db.favouriteDao().getAllFavFruitsByUser(LoginActivity.currentUserId!!)
    }

    suspend fun getAllDailyFruitsList(): List<Fruit> {
        return db.dailyIntakeDao().getAllDailyFruitsByUser(LoginActivity.currentUserId!!)
    }

    suspend fun getAllWeeklyFruitsList(): List<Fruit> {
        return db.weeklyIntakeDao().getAllWeeklyFruitsByUser(LoginActivity.currentUserId!!)
    }

    suspend fun getFavFruitByUser(fruitId: Long): Fruit{
        return db.favouriteDao().getFavFruitByUser(LoginActivity.currentUserId!!, fruitId)
    }

    suspend fun addFavFruit(fruitId: Long) {
        db.favouriteDao().addFavFruit(Favourite(LoginActivity.currentUserId!!, fruitId))
    }

    suspend fun deleteFavFruit(fruitId: Long) {
        db.favouriteDao().deleteFavById(LoginActivity.currentUserId!!, fruitId)
    }

    //Register activity y Login Activity
    suspend fun insertUser(user: User) {
        db.userDao().insertUser(user)
    }

    suspend fun updateUser(newName: String, newPassword: String){
        db.userDao().updateUser(
            User(
                LoginActivity.currentUserId,
                newName,
                newPassword
            ))
    }

    suspend fun checkUserByUsername(username: String): Boolean {
        return db.userDao().getUserByUsername(username) == null
    }

    suspend fun getUserByUsername(username: String): User {
        return db.userDao().getUserByUsername(username)
    }

    suspend fun getUserById(): User{
        return db.userDao().getUserById(LoginActivity.currentUserId)
    }

    suspend fun deleteUserById(){
        db.userDao().deleteUserById(LoginActivity.currentUserId!!)
    }

    suspend fun insertDailyFruit(fruitId: Long){
        db.dailyIntakeDao().insertDailyFruit(
            DailyIntake(
                fruitId,
                LoginActivity.currentUserId!!,
                LocalDate.now(),
                LocalTime.now()
            )
        )
    }

    suspend fun deleteDailyFruits(fechaSistema: LocalDate) {
        db.dailyIntakeDao().deleteDailyfruits(LoginActivity.currentUserId!!, fechaSistema)
    }

    suspend fun insertWeeklyFruit(fruitId: Long){
        db.weeklyIntakeDao().insertWeeklyFruit(
            WeeklyIntake(
                fruitId,
                LoginActivity.currentUserId!!,
                LocalDate.now(),
                LocalTime.now()
            )
        )
    }

    suspend fun deleteWeeklyFruits() {
        db.weeklyIntakeDao().deleteWeeklyfruits(LoginActivity.currentUserId!!)
    }

    suspend fun getOneWeeklyFruit(): WeeklyIntake {
        return db.weeklyIntakeDao().getOneWeeklyFruit(LoginActivity.currentUserId!!)
    }

    //DetailFragment
    suspend fun getFruitById(fruitId: Long): Fruit {
        return db.fruitDao().getFruitById(fruitId)
    }

    suspend fun checkFruitIsFav(fruitId: Long): Boolean {
        return db.favouriteDao().getFavFruitByUser(LoginActivity.currentUserId!!, fruitId) == null
    }

    //Instancia del patron Repository (no tocar)
    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(api: FruticionAPI, db: FruticionDatabase): Repository {
            return instance ?: synchronized(this) {
                instance ?: Repository(api, db).also { instance = it }
            }
        }
    }
}