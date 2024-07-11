package com.reelevant.analytics_android

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method
import kotlin.coroutines.Continuation


class FailQueueUnitTest {
    private lateinit var appContext: Context
    private lateinit var sdk: ReelevantSDK
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun initialize () {
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk()

        mockkConstructor(Handler::class)
        every { anyConstructed<Handler>().post(any()) } returns true

        appContext = mockk<Context>()
        sdk = spyk(ReelevantSDK(
            appContext,
            "dummy-company-id",
            "dummy-data-source-id"
        ), recordPrivateCalls = true)
        every { sdk["sendToNetwork"](any<JSONObject>()) } returns Unit
        every { sdk["getCurrentTimeMillis"]() } returns 123456789876543

        sharedPreferences = mockk<SharedPreferences>()
        editor = mockk<SharedPreferences.Editor>()
        every { appContext.getSharedPreferences(any(), any()) } returns (sharedPreferences)
        every { sharedPreferences.getString(ReelevantAnalytics.FAIL_QUEUE, any()) } returns "[]"
        every { sharedPreferences.edit() } returns (editor)
        every { editor.putString(any(), any()) } returns (editor)
        every { editor.apply() } returns (Unit)
        every { editor.remove(any()) } returns (editor)
    }

    private suspend fun invokeSuspendPrivateSDKMethod(methodName: String) {
        val privateStringField: Method = ReelevantSDK::class.java.getDeclaredMethod(methodName, Continuation::class.java)
        privateStringField.isAccessible = true
        suspendCoroutineUninterceptedOrReturn<Unit> {
                continuation -> privateStringField.invoke(sdk, continuation)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldNotCallSendToNetworkWhenNoFailedEventInQueue()= runTest {
        invokeSuspendPrivateSDKMethod("handleFailQueue")
        verify(exactly = 0) { sdk["sendToNetwork"](any<JSONObject>()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldCallSendToNetworkWithOneEventInQueue()= runTest {
        val expected = "[{\"eventId\":\"1234567890123456789012345\",\"clientId\":\"UserId\",\"data\":{},\"v\":1,\"tmpId\":\"TmpUserId\",\"name\":\"page_view\",\"key\":\"60d2dcae87a5ca0006335a82\",\"url\":\"unknown\",\"timestamp\":123456789876543}]"
        every { sharedPreferences.getString(ReelevantAnalytics.FAIL_QUEUE, any()) } returns expected
        invokeSuspendPrivateSDKMethod("handleFailQueue")
        verify(exactly = 1) { sdk["sendToNetwork"](any<JSONObject>()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldCallSendToNetworkWithTwoEventInQueue()= runTest {
        val expected = "[{\"eventId\":\"1234567890123456789012345\",\"clientId\":\"UserId\",\"data\":{},\"v\":1,\"tmpId\":\"TmpUserId\",\"name\":\"page_view\",\"key\":\"60d2dcae87a5ca0006335a82\",\"url\":\"unknown\",\"timestamp\":123456789876543},{\"eventId\":\"1234567890123456789012345\",\"clientId\":\"UserId\",\"data\":{},\"v\":1,\"tmpId\":\"TmpUserId\",\"name\":\"page_view\",\"key\":\"60d2dcae87a5ca0006335a82\",\"url\":\"unknown\",\"timestamp\":123456789876543}]"
        every { sharedPreferences.getString(ReelevantAnalytics.FAIL_QUEUE, any()) } returns expected
        invokeSuspendPrivateSDKMethod("handleFailQueue")
        verify(exactly = 2) { sdk["sendToNetwork"](any<JSONObject>()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldNotCallSendToNetworkWithEventIsTooOld()= runTest {
        val expected = "[{\"eventId\":\"1234567890123456789012345\",\"clientId\":\"UserId\",\"data\":{},\"v\":1,\"tmpId\":\"TmpUserId\",\"name\":\"page_view\",\"key\":\"60d2dcae87a5ca0006335a82\",\"url\":\"unknown\",\"timestamp\":1876543},{\"eventId\":\"1234567890123456789012345\",\"clientId\":\"UserId\",\"data\":{},\"v\":1,\"tmpId\":\"TmpUserId\",\"name\":\"page_view\",\"key\":\"60d2dcae87a5ca0006335a82\",\"url\":\"unknown\",\"timestamp\":123456789876543}]"
        every { sharedPreferences.getString(ReelevantAnalytics.FAIL_QUEUE, any()) } returns expected
        invokeSuspendPrivateSDKMethod("handleFailQueue")
        verify(exactly = 1) { sdk["sendToNetwork"](any<JSONObject>()) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldClearQueue()= runTest {
        invokeSuspendPrivateSDKMethod("handleFailQueue")
        verify(exactly = 1) { editor.remove(ReelevantAnalytics.FAIL_QUEUE) }
        verify(exactly = 0) { sdk["sendToNetwork"](any<JSONObject>()) }
    }
}
