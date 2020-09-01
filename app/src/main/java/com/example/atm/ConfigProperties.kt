package com.example.atm

import android.content.Context
import java.io.InputStream
import java.util.*

class ConfigProperties {
    fun getConfigValue(context: Context, name: String): String {
        val resources = context.resources
        val rawResources: InputStream = resources.openRawResource(R.raw.config)
        val properties = Properties()
        properties.load(rawResources)
        return properties.getProperty(name)
    }
}

