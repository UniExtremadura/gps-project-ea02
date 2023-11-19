package com.example.fruticion.model

import androidx.room.Entity
import androidx.room.ForeignKey
import java.io.Serializable
import java.time.LocalDate

@Entity(
    primaryKeys = ["userId", "fruitId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Fruit::class,
            parentColumns = ["roomId"],
            childColumns = ["fruitId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class DailyIntake(
    var fruitId: Long,
    var userId: Long,
    var additionDate: LocalDate
): Serializable