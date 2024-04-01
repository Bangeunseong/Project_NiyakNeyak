package com.capstone.project_niyakneyak.main.etc

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.lang.StringBuilder

open class OpenApiFunctions {

    // Get Medicine Information
    fun getPrdtMtrDetails(prdtName: String?, pageNo: Int?, numOfRows: Int?): JSONObject? {
        if(prdtName == null) return null

        // Setting URL String
        val urlString = buildUrlString(0, null, prdtName, pageNo, numOfRows)

        // Returns JSONObject
        return returnJSONObjectByString(urlString)
    }
    // Get Dangerous Medicines for Elderly people
    fun getElderlyAttentionPrdtList(itemSeq: String?, prdtName: String?, pageNo: Int?, numOfRows: Int?): JSONObject?{
        if(itemSeq == null && prdtName == null) return null

        // Setting URL String
        val urlString = buildUrlString(1, itemSeq, prdtName, pageNo, numOfRows)

        // Returns JSONObject
        return returnJSONObjectByString(urlString)
    }
    // Get Usage Joint Medicines list in certain medicine
    fun getUsageJointPrdtList(itemSeq: String?, prdtName: String?, pageNo: Int?, numOfRows: Int?): JSONObject?{
        if(itemSeq == null && prdtName == null) return null

        // Setting URL String
        val urlString = buildUrlString(2, itemSeq, prdtName, pageNo, numOfRows)

        // Returns JSONObject
        return returnJSONObjectByString(urlString)
    }

    private fun buildUrlString(type: Int, itemSeq: String?, prdtName: String?, pageNo: Int?, numOfRows: Int?): String{
        val builder = StringBuilder()
        when(type){
            0 -> {
                builder.append(PRODUCT_INFO_BASE_URL + PRODUCT_MATERIAL_DETAILS)
                builder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=$OPEN_API_ENCODED_KEY")
                if(pageNo != null)
                    builder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("$pageNo", "UTF-8"))
                else builder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"))
                if(numOfRows != null)
                    builder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("$numOfRows", "UTF-8"))
                else builder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"))
                builder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"))
                builder.append("&" + URLEncoder.encode("Prduct", "UTF-8") + "=" + URLEncoder.encode(prdtName, "UTF-8"))
            }
            1 -> {
                builder.append(DUR_PRODUCT_LIST_BASE_URL + DUR_ELDERLY_ATTENTION_PRODUCT_LIST)
                builder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=$OPEN_API_ENCODED_KEY")
                if(itemSeq != null)
                    builder.append("&" + URLEncoder.encode("itemSeq", "UTF-8") + "=" + URLEncoder.encode(itemSeq, "UTF-8"))
                if(pageNo != null)
                    builder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("$pageNo", "UTF-8"))
                else builder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"))
                if(numOfRows != null)
                    builder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("$numOfRows", "UTF-8"))
                else builder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"))
                builder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"))
                if(prdtName != null)
                    builder.append("&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode(prdtName, "UTF-8"))
            }
            2 -> {
                builder.append(DUR_PRODUCT_LIST_BASE_URL + DUR_USAGE_JOINT_TABOO_LIST)
                builder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=$OPEN_API_ENCODED_KEY")
                if(pageNo != null)
                    builder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("$pageNo", "UTF-8"))
                else builder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"))
                if(numOfRows != null)
                    builder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("$numOfRows", "UTF-8"))
                else builder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"))
                builder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"))
                if(prdtName != null)
                    builder.append("&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode(prdtName, "UTF-8"))
                if(itemSeq != null)
                    builder.append("&" + URLEncoder.encode("itemSeq", "UTF-8") + "=" + URLEncoder.encode(itemSeq, "UTF-8"))
            }
        }
        return builder.toString()
    }
    private fun returnJSONObjectByString(urlString: String): JSONObject{
        // Setting Input Stream Reader
        val url = URL(urlString)
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        urlConnection.setRequestProperty("content-type","application/json;charset=utf-8")

        // Setting BufferedReader by getting InputStreamReader from urlConnection
        val reader: BufferedReader = if(urlConnection.responseCode in 200..300){
            BufferedReader(InputStreamReader(urlConnection.inputStream, "UTF-8"))
        } else BufferedReader(InputStreamReader(urlConnection.errorStream, "UTF-8"))

        // Get jsonStrings
        val jsonString = StringBuilder()
        while(true){
            val jsonLine = reader.readLine() ?: break
            jsonString.append(jsonLine)
        }

        // Close BufferedReader and URLConnection
        reader.close()
        urlConnection.disconnect()

        return JSONObject(jsonString.toString())
    }

    companion object{
        // API ENCODED KEY
        private const val OPEN_API_ENCODED_KEY="YiIyyqvg3A9seDQtZNIp8vr9Izzrva%2B4IcDZNhDoj5vcz1%2BIJLR2g014rKYPaJ%2B89YlIOjsJrutz1ApeSiMdcA%3D%3D"

        // REPRESENT URL
        private const val PRODUCT_INFO_BASE_URL = "https://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService05"
        private const val PRODUCT_MATERIAL_DETAILS = "/getDrugPrdtMcpnDtlInq04"
        private const val DUR_PRODUCT_LIST_BASE_URL = "http://apis.data.go.kr/1471000/DURPrdlstInfoService03"
        private const val DUR_ELDERLY_ATTENTION_PRODUCT_LIST = "/getOdsnAtentInfoList03"
        private const val DUR_USAGE_JOINT_TABOO_LIST = "/getUsjntTabooInfoList03"
    }
}