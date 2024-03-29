package com.capstone.project_niyakneyak.data.medication_model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.util.Date

@IgnoreExtraProperties
data class MedicineData(
    var prodName: String? = null,
    var mfName: String? = null,
    var standard: String? = null,
    var totalAmt: String? = null,
    var classType: String? = null,
    var packageShp: String? = null,
    var typeMasterCode: String? = null,
    var valDate: String? = null,
    var medType: String? = null,
    var mtrCode: String? = null,
    var defCode: String? = null,
    var prodCodeRev: String? = null,
    var normCode: String? = null,
    var etc: String? = null,
    var cancelDate: String? = null,
    var transferDate: String? = null,
    var transferEndDate: String? = null,
    var serialNumExc: Boolean? = null,
    var serialNumExcCause: String? = null,
    var atcCode: String? = null,
    var spcManMed: String? = null,
    var medSpecEquip: String? = null) {

    fun convertDate(date: String): Date? {
        return SimpleDateFormat("yyyy-MM-dd").parse(date)
    }
}