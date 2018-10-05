package ec.kleber.tesis.tracker.data

import java.util.*

data class ApiSync (
        val timestamp: Date,
        val latitude: Double,
        val longitude: Double,
        val accuracy: Float
)