# Reelevant Analytics SDK for Android

This Android package could be used to send tracking events to Reelevant datasources

## How to use

You need to have a datasourceId and a companyId to be able to init the SDK and start sending events:

``` Kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate() {
        val sdk = ReelevantSDK(this,"60d2dcae87a5ca0006335a82", "6298afcc7527000300387fdf")
    }
}
```

### Current URL

When a user is browsing a page you should call the `sdk.setCurrentURL` method if you want to be able to filter on it in Reelevant.

# User infos

To identify a user, you should call the `sdk.setUser('<user id>')` method which will store the user id in the device and send it to Reelevant.

# Labels

Each event type allow you to pass additional infos via `labels (Map<String, String>)` on which you'll be able to filter in Reelevant.

``` Kotlin
val event = sdk.addCart(ids: listOf("my-product-id"), mapOf("lang" to "en_US"))
```