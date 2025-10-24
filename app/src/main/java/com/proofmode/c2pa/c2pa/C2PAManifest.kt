package com.proofmode.c2pa.c2pa

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class C2PAManifest(
    @SerialName("claim_generator") val claimGenerator: String,
    val format: String,
    val title: String? = null,
    val assertions: MutableList<Assertion> = mutableListOf(),
    val ingredients: MutableList<Ingredient> = mutableListOf(),
    val thumbnail: String? = null
)

@Serializable
data class Assertion(
    val label: String,
    val data: JsonElement
)

@Serializable
data class Ingredient(
    val title: String,
    val format: String? = null,
    val relationship: String? = null
)

class ManifestBuilder(
    private val claimGenerator: String,
    private val format: String
) {
    private var title: String? = null
    private val assertions = mutableListOf<Assertion>()
    private val ingredients = mutableListOf<Ingredient>()
    private var thumbnail: String? = null

    fun setTitle(title: String) = apply {
        this.title = title
    }

    fun addAssertion(label: String, data: JsonObject) = apply {
        assertions += Assertion(label, data)
    }

    fun addAction(
        action: String,
        softwareAgent: String,
        whenIso: String
    ) = apply {
        val data = buildJsonObject {
            putJsonArray("actions") {
                addJsonObject {
                    put("action", action)
                    put("when", whenIso)
                    put("softwareAgent", softwareAgent)
                }
            }
        }
        addAssertion("c2pa.actions", data)
    }

    fun addAuthorInfo(author: String, description: String? = null) = apply {
        val data = buildJsonObject {
            put("author", author)
            description?.let { put("description", it) }
        }
        addAssertion("stds.schema-org.CreativeWork", data)
    }

    fun addIngredient(title: String, format: String? = null, relationship: String? = null) = apply {
        ingredients += Ingredient(title, format, relationship)
    }

    fun setThumbnail(base64: String) = apply {
        thumbnail = base64
    }

    fun addLocationInfo(lat: Double, lon: Double, name: String? = null) = apply {
        val data = buildJsonObject {
            name?.let { put("name", it) }
            putJsonObject("geo") {
                put("@type", "GeoCoordinates")
                put("latitude", lat)
                put("longitude", lon)
            }
        }
        addAssertion("stds.schema-org.Place", data)
    }


    fun build(): C2PAManifest = C2PAManifest(
        claimGenerator = claimGenerator,
        format = format,
        title = title,
        assertions = assertions,
        ingredients = ingredients,
        thumbnail = thumbnail
    )

    fun toJson(pretty: Boolean = true): String {
        val json = Json { prettyPrint = pretty }
        return json.encodeToString(build())
    }
}
