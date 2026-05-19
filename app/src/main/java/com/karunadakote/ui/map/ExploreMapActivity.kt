package com.karunadakote.ui.map

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karunadakote.R
import com.karunadakote.data.model.Fort
import com.karunadakote.databinding.ActivityExploreMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class ExploreMapActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityExploreMapBinding

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(
                "osmdroid",
                MODE_PRIVATE
            )
        )

        binding =
            ActivityExploreMapBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        setupMap()
    }

    private fun setupMap() {

        val map =
            binding.mapView

        map.setTileSource(
            TileSourceFactory.MAPNIK
        )

        map.setMultiTouchControls(true)

        map.setBuiltInZoomControls(false)

        val karnataka =
            GeoPoint(
                15.3173,
                75.7139
            )

        // Cinematic Start Zoom
        map.controller.setZoom(5.8)

        // CHANGE 6: Animated camera with 3.5s cinematic pan
        map.controller.animateTo(
            karnataka,
            6.8,
            3500L
        )

        val forts =
            getDemoForts()

        binding.tvExplored.text =
            "${forts.size} Heritage Forts · Karnataka"

        addMarkers(
            forts,
            map
        )

        drawRoutes(
            forts,
            map
        )

        startFloatingCamera(map)
    }

    private fun addMarkers(
        forts: List<Fort>,
        map: MapView
    ) {

        forts.forEachIndexed { index, fort ->

            val marker =
                Marker(map)

            marker.position =
                GeoPoint(
                    fort.lat,
                    fort.lng
                )

            marker.title =
                fort.name

            // CHANGE 5: Branded gold/saffron custom marker
            marker.icon =
                createBrandedMarkerIcon()

            marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )

            marker.alpha = 0.4f

            marker.setOnMarkerClickListener { _, _ ->

                // CHANGE 6: Animated camera to marker on click with 1s duration
                map.controller.animateTo(
                    GeoPoint(
                        fort.lat,
                        fort.lng
                    ),
                    10.5,
                    1000L               // 1-second smooth zoom
                )

                map.postDelayed({

                    openFort(fort)

                }, 1600)

                true
            }

            startPulse(marker)

            map.overlays.add(marker)
        }
    }

    private fun drawRoutes(
        forts: List<Fort>,
        map: MapView
    ) {

        val polyline =
            Polyline()

        polyline.outlinePaint.strokeWidth =
            10f

        polyline.outlinePaint.alpha =
            180

        polyline.outlinePaint.color =
            Color.parseColor(
                "#E0B16A"
            )

        forts.forEach {

            polyline.addPoint(

                GeoPoint(
                    it.lat,
                    it.lng
                )
            )
        }

        map.overlays.add(polyline)
    }

    private fun startPulse(
        marker: Marker
    ) {

        val animator =

            ValueAnimator.ofFloat(
                0.3f,
                1f
            )

        animator.duration = 1400

        animator.repeatMode =
            ValueAnimator.REVERSE

        animator.repeatCount =
            ValueAnimator.INFINITE

        animator.addUpdateListener {

            val alpha =
                it.animatedValue as Float

            marker.alpha = alpha

            // Visible rotation movement
            marker.rotation += 2f
        }

        animator.start()
    }

    private fun startFloatingCamera(
        map: MapView
    ) {

        map.postDelayed(

            object : Runnable {

                private var toggle = false

                override fun run() {

                    val target =

                        if (toggle) {

                            GeoPoint(
                                17.9104,
                                77.5199
                            )

                        } else {

                            GeoPoint(
                                14.2306,
                                76.3980
                            )
                        }

                    toggle = !toggle

                    // CHANGE 6: Consistent animateTo with 1s duration pattern
                    map.controller.animateTo(
                        target,
                        7.8,
                        4500L
                    )

                    map.postDelayed(
                        this,
                        5500
                    )
                }

            },

            2500
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CHANGE 5: Same branded marker as MapActivity — consistent brand identity
    // Gold ring, saffron fill, teardrop tail, 🏰 emoji
    // ─────────────────────────────────────────────────────────────────────────
    private fun createBrandedMarkerIcon(): BitmapDrawable {

        val sizePx = (40 * resources.displayMetrics.density).toInt()
        val tailH = (12 * resources.displayMetrics.density).toInt()
        val totalH = sizePx + tailH

        val bitmap = Bitmap.createBitmap(sizePx, totalH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val cx = sizePx / 2f
        val cy = sizePx / 2f

        // Gold outer ring
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, sizePx / 2f, ringPaint)

        // Saffron inner fill
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#A63B12")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, sizePx / 2f - (3 * resources.displayMetrics.density), fillPaint)

        // Teardrop tail
        val tailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.FILL
        }
        val tailPath = android.graphics.Path().apply {
            val dp = resources.displayMetrics.density
            moveTo(cx - 4 * dp, sizePx.toFloat())
            lineTo(cx + 4 * dp, sizePx.toFloat())
            lineTo(cx, totalH.toFloat())
            close()
        }
        canvas.drawPath(tailPath, tailPaint)

        // Fort icon text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 16 * resources.displayMetrics.density
            textAlign = Paint.Align.CENTER
        }
        val textY = cy - (textPaint.ascent() + textPaint.descent()) / 2f
        canvas.drawText("🏰", cx, textY, textPaint)

        return BitmapDrawable(resources, bitmap)
    }

    private fun openFort(
        fort: Fort
    ) {

        startActivity(

            Intent(
                this,
                MapActivity::class.java
            ).apply {

                putExtra(
                    MapActivity.EXTRA_FORT_ID,
                    fort.id
                )

                putExtra(
                    MapActivity.EXTRA_FORT_NAME,
                    fort.name
                )

                putExtra(
                    MapActivity.EXTRA_FORT_LAT,
                    fort.lat
                )

                putExtra(
                    MapActivity.EXTRA_FORT_LNG,
                    fort.lng
                )

                putExtra(
                    MapActivity.EXTRA_FORT_DESC,
                    fort.description
                )

                putExtra(
                    MapActivity.EXTRA_FORT_IMAGE,
                    fort.image
                )
            }
        )
    }

    private fun getDemoForts(): List<Fort> {
        return try {
            val jsonString = assets.open("forts.json")
                .bufferedReader()
                .use { it.readText() }
            val type = object : TypeToken<List<Fort>>() {}.type
            Gson().fromJson(jsonString, type)
        } catch (e: Exception) {
            // Fallback to core forts if JSON fails
            listOf(
                Fort(1, "Chitradurga Fort", 14.2306, 76.3980, "Historic hill fort", "chitradurga"),
                Fort(2, "Bidar Fort", 17.9104, 77.5199, "Bahmani dynasty fort", "bidar"),
                Fort(3, "Bangalore Fort", 12.9629, 77.5753, "Built by Kempegowda", "bangalore"),
                Fort(4, "Raichur Fort", 16.2076, 77.3463, "Historic fort city", "raichur")
            )
        }
    }

    override fun onResume() {

        super.onResume()

        binding.mapView.onResume()
    }

    override fun onPause() {

        super.onPause()

        binding.mapView.onPause()
    }
}
