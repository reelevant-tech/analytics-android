package com.reelevant.analytics_android

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SetUserUnitTest {
    private lateinit var appContext: Context
    private lateinit var sdk: ReelevantSDK
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun initialize () {
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk()

        mockkConstructor(Handler::class)
        every { anyConstructed<Handler>().post(any()) } answers { true }

        appContext = mockk<Context>()
        sdk = spyk(ReelevantSDK(
            appContext,
            "dummy-company-id",
            "dummy-data-source-id"
        ))
        coEvery { sdk.send(any()) } returns Unit


        sharedPreferences = mockk<SharedPreferences>()
        editor = mockk<SharedPreferences.Editor>()
        every { appContext.getSharedPreferences(any(), any()) } returns (sharedPreferences)
        every { sharedPreferences.getString(ReelevantAnalytics.USER_ID, null) } returns ("oldUserId")
        every { sharedPreferences.edit() } returns (editor)
        every { editor.putString(any(), any()) } returns (editor)
        every { editor.apply() } returns (Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldSetUser() = runTest {
        val user = "newUserId"

        sdk.setUser(user)

        verify(exactly = 1) { editor.putString(ReelevantAnalytics.USER_ID, user) }
        verify(exactly = 1) { editor.apply() }
        coVerify(exactly = 1) { sdk.send(withArg {
            assertEquals("identify", it.name)
            assertEquals(emptyMap<String, Any>(), it.payload)
        }) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun shouldNotSetUserWhenUserIsTheSame() = runTest {
        val user = "oldUserId"

        sdk.setUser(user)

        verify(exactly = 0) { editor.putString(ReelevantAnalytics.USER_ID, user) }
        verify(exactly = 0) { editor.apply() }
        coVerify(exactly = 0) { sdk.send(withArg {
            assertEquals("identify", it.name)
            assertEquals(emptyMap<String, Any>(), it.payload)
        }) }
    }
}
