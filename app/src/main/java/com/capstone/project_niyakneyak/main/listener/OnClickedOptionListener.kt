package com.capstone.project_niyakneyak.main.listener

import org.json.JSONObject

interface OnClickedOptionListener {
    fun onOptionClicked(option: String, jsonObject: JSONObject?)
}