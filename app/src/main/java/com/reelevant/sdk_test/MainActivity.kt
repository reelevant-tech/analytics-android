package com.reelevant.sdk_test

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.reelevant.analytics_android.ReelevantSDK
import com.reelevant.sdk_test.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sdk: ReelevantSDK

    override fun onCreate(savedInstanceState: Bundle?) {
        val context = this
        this.sdk = ReelevantSDK(
            context,
            "<companyId>",
            "<datasourceId"
        )

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clientIdInput = findViewById<EditText>(R.id.edit_client_id)
        val urlInput = findViewById<EditText>(R.id.edit_url)
        findViewById<Button>(R.id.button_client_id_set).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                sdk.setUser(clientIdInput.text.toString())
            }
        }
        findViewById<Button>(R.id.button_url_set).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                sdk.setCurrentURL(urlInput.text.toString())
            }
        }
    }

    fun onClickPageViewButton (view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            sdk.send(sdk.pageView(emptyMap()))
        }
    }

    fun onClickAddCartButton (view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            sdk.send(sdk.addCart(listOf("id1", "id2"), emptyMap()))
        }
    }

    fun onClickPurchaseButton (view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            sdk.send(sdk.purchase(listOf("id1", "id2"), 23.45, "transId", emptyMap()))
        }
    }

    fun onClickProductPageButton (view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            sdk.send(sdk.productPage("pageId", emptyMap()))
        }
    }

    fun onClickCategoryViewButton (view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            sdk.send(sdk.categoryView("categoryId", emptyMap()))
        }
    }

    fun onClickBrandViewButton (view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            sdk.send(sdk.brandView("brandId", emptyMap()))
        }
    }

    fun onClickProductHoverButton (view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            sdk.send(sdk.productHover("productId", emptyMap()))
        }
    }

    fun onClickCustomButton (view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            sdk.send(sdk.custom("customEvent", emptyMap()))
        }
    }
}
