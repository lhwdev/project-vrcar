package com.lhwdev.vrcar

import com.lhwdev.json.ConfigValue
import com.lhwdev.json.JsonConfig
import com.lhwdev.json.serialization.JsonAdapter


val sJsonAdapter = JsonAdapter(JsonConfig(noQuote = ConfigValue.enabled))
