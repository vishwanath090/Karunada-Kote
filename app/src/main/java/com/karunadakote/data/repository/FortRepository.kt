package com.karunadakote.data.repository
import android.util.Log
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karunadakote.BuildConfig
import com.karunadakote.data.local.VisitedFortsPreferences
import com.karunadakote.data.model.ApiResult
import com.karunadakote.data.model.Fort
import com.karunadakote.network.GeminiContent
import com.karunadakote.network.GeminiPart
import com.karunadakote.network.GeminiRequest
import com.karunadakote.network.RetrofitClient
import com.karunadakote.network.extractText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FortRepository(
    private val context: Context
) {

    private val visitedPrefs =
        VisitedFortsPreferences(context)

    private val geminiService =
        RetrofitClient.geminiApiService

    suspend fun loadForts(): List<Fort> =
        withContext(Dispatchers.IO) {

            val jsonString =
                context.assets.open("forts.json")
                    .bufferedReader()
                    .use { it.readText() }

            val type =
                object : TypeToken<List<Fort>>() {}.type

            Gson().fromJson(jsonString, type)
        }

    fun markFortVisited(fortId: Int) {

        visitedPrefs.markVisited(fortId)
    }

    fun isFortVisited(fortId: Int): Boolean {

        return visitedPrefs.isVisited(fortId)
    }

    fun getVisitedFortIds(): Set<Int> {

        return visitedPrefs.getVisitedIds()
    }

    suspend fun generateAiDescription(
        fortName: String
    ): ApiResult<String> =
        withContext(Dispatchers.IO) {

            try {

                val prompt = """
                    You are a passionate historian and storyteller specializing in Karnataka's royal heritage.
                    Write a vivid, engaging 4-5 sentence description of $fortName for a heritage travel app.
                    Cover: who built it and when, its most dramatic historical moment or battle, 
                    its architectural style or unique features, and why a visitor must experience it today.
                    Write in second person ("You'll find..."), use evocative language, and end with an inspiring sentence.
                    Do not use bullet points or headers — write flowing prose only.
                """.trimIndent()

                val request =
                    GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(
                                    GeminiPart(text = prompt)
                                )
                            )
                        )
                    )
                Log.d(
                    "GEMINI_RUNTIME_KEY",
                    BuildConfig.GEMINI_API_KEY
                )
                val response =
                    geminiService.generateContent(
                        apiKey = BuildConfig.GEMINI_API_KEY,
                        request = request
                    )

                if (response.isSuccessful) {

                    val text =
                        response.body()?.extractText()

                    if (text != null) {
                        ApiResult.Success(text)
                    } else {
                        ApiResult.Error("Empty AI response")
                    }

                } else {

                    ApiResult.Error(
                        response.errorBody()?.string()
                            ?: "API Error ${response.code()}"
                    )
                }

            } catch (e: Exception) {

                ApiResult.Error(
                    e.localizedMessage ?: "Unable to load AI summary for $fortName. Please check your connection and try again."
                )
            }
        }
}