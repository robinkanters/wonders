package com.robinkanters.wonders

import com.github.kittinunf.fuel.httpGet
import com.robinkanters.wonders.SecurityManager.decrypt
import org.json.JSONObject

object WondersApi {
    // (obfuscation optimized away by Kotlin compiler)
    private val ENCRYPTION_KEY = "17"+"68"+"25"+"7"+"09"+"1"+"02"+"3"+"4"+"96"

    private val URL_WIS_FEED = "ht"+"tp"+"s:"+"//m"+"ob"+"il"+"e-s"+"er"+"vi"+"ce"+"s.ef"+"te"+"li"+"ng"+".com"+"/w"+"is/"
    private val URL_SEARCH = "h"+"tt"+"p:/"+"/prd"+"-se"+"a"+"rc"+"h-"+"acs"+".e"+"fte"+"li"+"ng."+"com"+"/2013"+"-01"+"-01/"

    private val QUERY_DEFAULT = "searc"+"h?s"+"ize=10"+"00&q.p"+"ar"+"ser="+"st"+"ruct"+"ure"+"d&q="+"(phr"+"as"+"e%20f"+"ield%"+"3Dl"+"angu"+"ag"+"e%"+"20'e"+"n"+"')"

    private val HEADER_API_VERSION = "X"+"-Ap"+"i-V"+"ers"+"ion"

    @JvmStatic
    fun getWisFeed(callback: (JSONObject) -> Unit) {
        URL_WIS_FEED.createRequest()
                .responseString { _, response, _ ->
                    callback(response.data.parseResponseData())
                }
    }

    @JvmStatic
    fun search(query: String = QUERY_DEFAULT, callback: (JSONObject) -> Unit) {
        "$URL_SEARCH$query".createRequest()
                .responseString { _, response, _ ->
                    callback(String(response.data).toJSONObject())
                }
    }

    private fun String.createRequest() = this.httpGet().header(HEADER_API_VERSION to 4)

    private fun ByteArray.parseResponseData() = String(this).decodeResponse().toJSONObject()

    private fun String.decodeResponse() = decrypt(this, ENCRYPTION_KEY)

    private fun String.toJSONObject() = JSONObject(this)
}
