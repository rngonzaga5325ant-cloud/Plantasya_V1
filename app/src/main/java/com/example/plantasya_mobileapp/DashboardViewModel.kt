package com.example.plantasya_mobileapp

import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {
    val plantTasks = mapOf(
        "Fiddle-leaf Fig" to listOf(
            "Water once a week",
            "Wipe leaves for dust",
            "Check for indirect sunlight",
            "Rotate the pot for even growth",
            "Mist leaves for humidity",
            "Check for root rot"
        ),
        "Parlor Palm" to listOf(
            "Keep soil slightly moist",
            "Mist leaves daily",
            "Avoid direct sun",
            "Fertilize once a month (spring/summer)",
            "Trim brown leaf tips",
            "Repot every 2-3 years"
        ),
        "Philodendron" to listOf(
            "Water when top inch is dry",
            "Provide moderate light",
            "Clean leaves with damp cloth",
            "Provide support for climbing",
            "Check for yellowing leaves",
            "Pinch stems for bushier growth"
        ),
        "Pothos" to listOf(
            "Water every 1-2 weeks",
            "Trim long vines",
            "Low light is okay",
            "Check for variegated leaf health",
            "Propagate from cuttings",
            "Clean pot from mineral buildup"
        ),
        "ZZ Plant" to listOf(
            "Water once a month",
            "Dust the leaves",
            "Thrives in low light",
            "Keep away from cold drafts",
            "Check for bulbous root health",
            "Fertilize twice a year"
        )
    )

    // Store task states to retain them when switching plants or fragments
    val taskStates = mutableMapOf<String, MutableList<Boolean>>()

    fun getTaskStatesForPlant(plantName: String): MutableList<Boolean> {
        return taskStates.getOrPut(plantName) {
            MutableList(plantTasks[plantName]?.size ?: 0) { false }
        }
    }

    fun updateTaskState(plantName: String, position: Int, isChecked: Boolean) {
        taskStates[plantName]?.set(position, isChecked)
    }
}
