package com.abner.manager.util

import com.abner.manager.model.ReturnData
import com.alibaba.fastjson.JSON
import java.lang.reflect.Type

object JsonUtils {
    /**
     * Business is successful
     */
    @JvmStatic
    fun successfulJson(data: Any?): String {
        val returnData = ReturnData()
        returnData.isSuccess = true
        returnData.errorCode = 200
        returnData.data = data
        return JSON.toJSONString(returnData)
    }

    /**
     * Business is failed
     */
    @JvmStatic
    fun failedJson(code: Int, message: String?): String {
        val returnData = ReturnData()
        returnData.isSuccess = false
        returnData.errorCode = code
        returnData.errorMsg = message
        return JSON.toJSONString(returnData)
    }

    /**
     * Converter object to json string
     */
    fun toJsonString(data: Any?): String {
        return JSON.toJSONString(data)
    }

    /**
     * Parse json to object
     */
    @JvmStatic
    fun <T> parseJson(json: String?, type: Type?): T {
        return JSON.parseObject(json, type)
    }
}