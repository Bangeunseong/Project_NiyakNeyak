package com.capstone.project_niyakneyak.main.etc

class AlarmDataView {
    var isSunday = false
    var isMonday = false
    var isTuesday = false
    var isWednesday = false
    var isThursday = false
    var isFriday = false
    var isSaturday = false

    constructor()
    constructor(
        sunday: Boolean,
        monday: Boolean,
        tuesday: Boolean,
        wednesday: Boolean,
        thursday: Boolean,
        friday: Boolean,
        saturday: Boolean
    ) {
        isSunday = sunday
        isMonday = monday
        isTuesday = tuesday
        isWednesday = wednesday
        isThursday = thursday
        isFriday = friday
        isSaturday = saturday
    }

    val displayData: String
        get() {
            val builder = StringBuilder()
            for (i in 0..6) {
                when (i) {
                    0 -> {
                        if (isSunday) builder.append("Sun")
                    }

                    1 -> {
                        if (isMonday) {
                            if (!builder.toString().isEmpty()) builder.append(",")
                            builder.append("Mon")
                        }
                    }

                    2 -> {
                        if (isTuesday) {
                            if (!builder.toString().isEmpty()) builder.append(",")
                            builder.append("Tue")
                        }
                    }

                    3 -> {
                        if (isWednesday) {
                            if (!builder.toString().isEmpty()) builder.append(",")
                            builder.append("Wed")
                        }
                    }

                    4 -> {
                        if (isThursday) {
                            if (!builder.toString().isEmpty()) builder.append(",")
                            builder.append("Thu")
                        }
                    }

                    5 -> {
                        if (isFriday) {
                            if (!builder.toString().isEmpty()) builder.append(",")
                            builder.append("Fri")
                        }
                    }

                    6 -> {
                        if (isSaturday) {
                            if (!builder.toString().isEmpty()) builder.append(",")
                            builder.append("Sat")
                        }
                    }
                }
            }
            return builder.toString()
        }
}