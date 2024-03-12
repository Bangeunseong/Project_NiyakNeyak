package com.capstone.project_niyakneyak.data.patient_model

class PatientData {
    //Getter and Setter for patient Info.
    //Field
    private var patientName: String?
    private var patientAge: String?
    private var medsData: MutableList<MedsData>

    //Constructor
    init {
        medsData = ArrayList()
    }
    constructor() {
        patientName = "Guest"
        patientAge = null
    }

    constructor(patientName: String?, patientAge: String?) {
        this.patientName = patientName
        this.patientAge = patientAge
    }

    constructor(patientName: String?, patientAge: String?, medsData: MutableList<MedsData>?) {
        this.patientName = patientName
        this.patientAge = patientAge
        if (medsData == null) this.medsData = ArrayList() else this.medsData = medsData
    }

    //Useful Functions for MedsData Configuration
    fun setMedsData(medsData: MutableList<MedsData>) {
        this.medsData = medsData
    }

    fun getMedsData(): List<MedsData> {
        return medsData
    }

    fun searchMedsData(target: MedsData): Boolean {
        return medsData.contains(target)
    }

    fun addMedsData(data: MedsData) {
        medsData.add(data)
    }

    fun modifyMedsData(target: MedsData, changed: MedsData): Boolean {
        if (!medsData.contains(target)) return false
        val data = medsData[medsData.indexOf(target)]
        data.medsName = changed.medsName
        data.medsDetail = changed.medsDetail
        data.medsStartDate = changed.medsStartDate
        data.medsEndDate = changed.medsEndDate
        data.alarms = changed.alarms
        return true
    }

    fun deleteMedsData(target: MedsData): Boolean {
        if (!medsData.contains(target)) return false
        medsData.remove(target)
        return true
    }
}