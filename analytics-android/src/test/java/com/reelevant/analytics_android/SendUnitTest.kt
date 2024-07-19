package com.reelevant.analytics_android

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SendUnitTest {
    private lateinit var appContext: Context
    private lateinit var sdk: ReelevantSDK
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun initialize () {
        mockkObject(ReelevantLogger.Companion)
        every { ReelevantLogger.debug(any()) } returns Unit
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk()

        mockkConstructor(Handler::class)
        every { anyConstructed<Handler>().post(any()) } answers { true }

        appContext = mockk<Context>()
        sdk = spyk(ReelevantSDK(
            appContext,
            "dummy-company-id",
            "dummy-data-source-id"
        ), recordPrivateCalls = true)
        every { sdk["randomIdentifier"]() } returns "1234567890123456789012345"
        every { sdk["getCurrentTimeMillis"]() } returns 123456789876543
        every { sdk["sendToNetwork"](any<JSONObject>()) } returns Unit

        sharedPreferences = mockk<SharedPreferences>()
        editor = mockk<SharedPreferences.Editor>()
        every { appContext.getSharedPreferences(any(), any()) } returns (sharedPreferences)
        every { sharedPreferences.getString(ReelevantAnalytics.USER_ID, null) } returns ("UserId")
        every { sharedPreferences.getString(ReelevantAnalytics.TEMPORARY_USER_ID, null) } returns ("TmpUserId")
        every { sharedPreferences.edit() } returns (editor)
        every { editor.putString(any(), any()) } returns (editor)
        every { editor.apply() } returns (Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldSendEvent () = runTest {
        val expected = "{\"eventId\":\"1234567890123456789012345\",\"clientId\":\"UserId\",\"data\":{},\"v\":1,\"tmpId\":\"TmpUserId\",\"name\":\"page_view\",\"key\":\"dummy-company-id\",\"url\":\"unknown\",\"timestamp\":123456789876543}"
        val event = sdk.pageView(emptyMap())

        sdk.send(event)

        verify(exactly = 1) { sdk["sendToNetwork"](
            withArg<JSONObject> {
                assertEquals(expected, it.toString())
            }
        ) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldSendEventWithGoogleAdvertisingIdWhenNoSharedPreferences () = runTest {
        val expected = "{\"eventId\":\"1234567890123456789012345\",\"clientId\":\"UserId\",\"data\":{},\"v\":1,\"tmpId\":\"googleID\",\"name\":\"page_view\",\"key\":\"dummy-company-id\",\"url\":\"unknown\",\"timestamp\":123456789876543}"
        val event = sdk.pageView(emptyMap())

        every { sharedPreferences.getString(ReelevantAnalytics.TEMPORARY_USER_ID, null) } returns null
        every { sdk["getGooglePlayServicesAdvertisingID"]() } returns "googleID"

        sdk.send(event)

        verify(exactly = 1) { sdk["sendToNetwork"](
            withArg<JSONObject> {
                assertEquals(expected, it.toString())
            }
        ) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldSendEventWithAmazonAdvertisingIdWhenNoSharedPreferences () = runTest {
        val expected = "{\"eventId\":\"1234567890123456789012345\",\"clientId\":\"UserId\",\"data\":{},\"v\":1,\"tmpId\":\"amazonID\",\"name\":\"page_view\",\"key\":\"dummy-company-id\",\"url\":\"unknown\",\"timestamp\":123456789876543}"
        val event = sdk.pageView(emptyMap())

        every { sharedPreferences.getString(ReelevantAnalytics.TEMPORARY_USER_ID, null) } returns null
        // coEvery { sdk["getGooglePlayServicesAdvertisingID"]() }.throws(Error("Should not appear"))
        every { sdk["getAmazonFireAdvertisingID"](any<Context>()) } returns "amazonID"

        sdk.send(event)

        verify(exactly = 1) { sdk["sendToNetwork"](
            withArg<JSONObject> {
                assertEquals(expected, it.toString())
            }
        ) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldSendEventWithRandomIdWhenNoSharedPreferences () = runTest {
        val expected = "{\"eventId\":\"1234567890123456789012345\",\"clientId\":\"UserId\",\"data\":{},\"v\":1,\"tmpId\":\"1234567890123456789012345\",\"name\":\"page_view\",\"key\":\"dummy-company-id\",\"url\":\"unknown\",\"timestamp\":123456789876543}"
        val event = sdk.pageView(emptyMap())

        every { sharedPreferences.getString(ReelevantAnalytics.TEMPORARY_USER_ID, null) } returns null

        sdk.send(event)

        verify(exactly = 1) { sdk["sendToNetwork"](
            withArg<JSONObject> {
                assertEquals(expected, it.toString())
            }
        ) }
    }
}
