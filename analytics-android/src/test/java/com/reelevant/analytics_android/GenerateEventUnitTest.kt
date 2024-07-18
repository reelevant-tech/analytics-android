package com.reelevant.analytics_android

import android.content.Context
import android.os.Handler
import android.os.Looper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.spyk

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class GenerateEventInstrumentedTest {
  private lateinit var appContext: Context
  private lateinit var sdk: ReelevantSDK
  @Before
  fun initialize() {
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
  }

  @Test
  fun shouldGetPageViewEvent() {
    val event = this.sdk.pageView(mapOf("label" to "value"))
    assertEquals("page_view", event.name)
    assertEquals(mapOf("label" to "value"), event.payload)
  }

  @Test
  fun shouldGetAddCartEvent() {
    val ids = listOf("productId1", "productId2")
    val event = this.sdk.addCart(ids, mapOf("label" to "value"))
    assertEquals("add_cart", event.name)
    assertEquals(mapOf("ids" to ids, "label" to "value"), event.payload)
  }

  @Test
  fun shouldGetPurchaseEvent() {
    val ids = listOf("productId1", "productId2")
    val event = this.sdk.purchase(ids, 46.6, "transaction1", mapOf("label" to "value"))
    assertEquals("purchase", event.name)
    assertEquals(mapOf(
      "ids" to ids,
      "value" to 46.6,
      "transId" to "transaction1",
      "label" to "value"
    ), event.payload)
  }

  @Test
  fun shouldGetProductPageEvent() {
    val event = this.sdk.productPage("productId1", mapOf("label" to "value"))
    assertEquals("product_page", event.name)
    assertEquals(mapOf(
      "id" to "productId1",
      "label" to "value"
    ), event.payload)
  }

  @Test
  fun shouldGetCategoryViewEvent() {
    val event = this.sdk.categoryView("category1", mapOf("label" to "value"))
    assertEquals("category_view", event.name)
    assertEquals(mapOf(
      "id" to "category1",
      "label" to "value"
    ), event.payload)
  }

  @Test
  fun shouldGetBrandViewEvent() {
    val event = this.sdk.brandView("brand1", mapOf("label" to "value"))
    assertEquals("brand_view", event.name)
    assertEquals(mapOf(
      "id" to "brand1",
      "label" to "value"
    ), event.payload)
  }

  @Test
  fun shouldGetProductHoverEvent() {
    val event = this.sdk.productHover("productId1", mapOf("label" to "value"))
    assertEquals("product_hover", event.name)
    assertEquals(mapOf(
      "id" to "productId1",
      "label" to "value"
    ), event.payload)
  }

  @Test
  fun shouldGetCustomEvent() {
    val event = this.sdk.custom("custom_event_1", mapOf("label" to "value"))
    assertEquals("custom_event_1", event.name)
    assertEquals(mapOf(
      "label" to "value"
    ), event.payload)
  }
}
