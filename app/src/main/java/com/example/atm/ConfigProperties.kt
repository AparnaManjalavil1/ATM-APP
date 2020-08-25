package com.example.atm

import android.content.Context
import android.content.res.Resources
import java.io.InputStream
import java.util.*

class ConfigProperties {
   fun getConfigValue(context: Context, name:String):String{
       val resources:Resources=context.resources
        val rawResources:InputStream=resources.openRawResource(R.raw.config)
       val properties=Properties()
        properties.load(rawResources)
      return properties.getProperty(name)
    }
}