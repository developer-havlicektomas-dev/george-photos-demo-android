package dev.havlicektomas.photosapp.core.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlin.reflect.typeOf
import kotlinx.serialization.json.Json

val StringListNavType: NavType<List<String>> = object : NavType<List<String>>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): List<String> =
        bundle.getString(key)?.let { Json.decodeFromString<List<String>>(it) }.orEmpty()

    override fun parseValue(value: String): List<String> =
        Json.decodeFromString(Uri.decode(value))

    override fun serializeAsValue(value: List<String>): String =
        Uri.encode(Json.encodeToString(value))

    override fun put(bundle: Bundle, key: String, value: List<String>) {
        bundle.putString(key, Json.encodeToString(value))
    }
}

val DetailRouteTypeMap: Map<kotlin.reflect.KType, NavType<*>> = mapOf(
    typeOf<List<String>>() to StringListNavType,
)
