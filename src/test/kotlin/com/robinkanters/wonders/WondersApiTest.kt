package com.robinkanters.wonders

import com.github.kittinunf.fuel.Fuel
import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WondersApiTest {
    @Before
    fun setUp() {
        Fuel.testMode()
    }

    @Test
    fun testSearchRequest() {
        var json: JSONObject? = null
        WondersApi.search {
            json = it
        }

        json!!.let {
            assertTrue(it.has("status"))
            assertTrue(it.has("hits"))
            assertTrue(it.getJSONObject("hits").getInt("found") > 0)
        }
    }

    @Test
    fun testWisFeed() {
        var json: JSONObject? = null
        WondersApi.getWisFeed {
            json = it
        }

        json!!.let {
            assertTrue(it.has("Version"))
            assertTrue(it.has("TimeStamp"))
            assertTrue(it.has("OpeningHours"))
            assertTrue(it.has("AttractionInfo"))
            assertTrue(it.getJSONArray("AttractionInfo").length() > 0)
            assertTrue(it.getJSONArray("AttractionInfo").getJSONObject(0).getString("Id").isNotEmpty())
        }
    }
}
