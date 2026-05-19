package com.karunadakote.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.karunadakote.audio.AmbientSoundManager
import com.karunadakote.data.model.Fort
import com.karunadakote.databinding.ActivityMapBinding
import com.karunadakote.ui.detail.FortDetailBottomSheet
import com.karunadakote.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_FORT_ID =
            "extra_fort_id"

        const val EXTRA_FORT_NAME =
            "extra_fort_name"

        const val EXTRA_FORT_LAT =
            "extra_fort_lat"

        const val EXTRA_FORT_LNG =
            "extra_fort_lng"

        const val EXTRA_FORT_DESC =
            "extra_fort_desc"

        const val EXTRA_FORT_IMAGE =
            "extra_fort_image"

        const val EXTRA_FORT_DYNASTY =
            "extra_fort_dynasty"

        const val EXTRA_FORT_YEAR =
            "extra_fort_year"

        const val EXTRA_FORT_TYPE =
            "extra_fort_type"

        const val EXTRA_FORT_DISTRICT =
            "extra_fort_district"

        const val EXTRA_FORT_HIGHLIGHTS =
            "extra_fort_highlights"
    }

    private lateinit var binding:
            ActivityMapBinding

    private val viewModel:
            MapViewModel by viewModels()

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
            ActivityMapBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        setSupportActionBar(
            binding.toolbar
        )

        supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title =

            intent.getStringExtra(
                EXTRA_FORT_NAME
            ) ?: "Karnataka Forts"

        setupMap()
    }

    override fun onSupportNavigateUp():
            Boolean {

        onBackPressedDispatcher
            .onBackPressed()

        return true
    }

    private fun setupMap() {

        val map =
            binding.mapView

        map.setTileSource(
            TileSourceFactory.MAPNIK
        )

        map.setMultiTouchControls(true)

        val targetLat =

            intent.getDoubleExtra(
                EXTRA_FORT_LAT,
                15.3173
            )

        val targetLng =

            intent.getDoubleExtra(
                EXTRA_FORT_LNG,
                75.7139
            )

        val targetName =

            intent.getStringExtra(
                EXTRA_FORT_NAME
            ) ?: "Fort"

        val targetDesc =

            intent.getStringExtra(
                EXTRA_FORT_DESC
            ) ?: ""

        val targetImage =

            intent.getStringExtra(
                EXTRA_FORT_IMAGE
            ) ?: ""

        val targetDynasty =

            intent.getStringExtra(
                EXTRA_FORT_DYNASTY
            ) ?: ""

        val targetYear =

            intent.getStringExtra(
                EXTRA_FORT_YEAR
            ) ?: ""

        val targetFortType =

            intent.getStringExtra(
                EXTRA_FORT_TYPE
            ) ?: ""

        val targetDistrict =

            intent.getStringExtra(
                EXTRA_FORT_DISTRICT
            ) ?: ""

        val targetHighlights =

            intent.getStringArrayListExtra(
                EXTRA_FORT_HIGHLIGHTS
            ) ?: arrayListOf()

        val targetFortId =

            intent.getIntExtra(
                EXTRA_FORT_ID,
                -1
            )

        val fortLocation =
            GeoPoint(
                targetLat,
                targetLng
            )

        // ─────────────────────────────────────────────────────────────────
        // CHANGE 6: Cinematic animated camera — zoom in from overview level
        // ─────────────────────────────────────────────────────────────────
        map.controller.setZoom(6.0)

        map.controller.setCenter(
            GeoPoint(
                fortLocation.latitude - 1.5,
                fortLocation.longitude
            )
        )

        map.postDelayed({

            map.controller.animateTo(
                fortLocation,
                13.5,
                1800L                   // 1.8s smooth cinematic zoom-in
            )

        }, 350)

        val fort = Fort(

            id = targetFortId,

            name = targetName,

            lat = targetLat,

            lng = targetLng,

            description = targetDesc,

            image = targetImage,

            dynasty = targetDynasty,

            yearBuilt = targetYear,

            fortType = targetFortType,

            districtName = targetDistrict,

            highlights = targetHighlights
        )

        // ─────────────────────────────────────────────────────────────────
        // CHANGE 5: Custom branded marker — gold pin with fort icon
        // ─────────────────────────────────────────────────────────────────
        val marker =
            Marker(map)

        marker.position =
            fortLocation

        marker.title =
            targetName

        marker.icon =
            createBrandedMarkerIcon()

        marker.setAnchor(
            Marker.ANCHOR_CENTER,
            Marker.ANCHOR_BOTTOM
        )

        marker.setOnMarkerClickListener { _, _ ->

            binding.mapView.postDelayed({

                showFortDetail(fort)

            }, 450)

            true
        }

        map.overlays.add(marker)

        // Marker Fade-In after camera settles
        marker.alpha = 0f

        map.postDelayed({

            marker.alpha = 1f

            map.invalidate()

        }, 700)

        // Auto Open Detail AFTER zoom settles
        map.postDelayed({

            showFortDetail(fort)

        }, 2300)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CHANGE 5: Branded marker — saffron circle with gold ring + 🏰 emoji label
    // Drawn programmatically so no extra drawable file is needed
    // ─────────────────────────────────────────────────────────────────────────
    private fun createBrandedMarkerIcon(): BitmapDrawable {

        val sizePx = (48 * resources.displayMetrics.density).toInt()
        val tailH = (14 * resources.displayMetrics.density).toInt()
        val totalH = sizePx + tailH

        val bitmap = Bitmap.createBitmap(sizePx, totalH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Outer gold ring
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D4AF37")         // heritage gold
            style = Paint.Style.FILL
        }
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        canvas.drawCircle(cx, cy, sizePx / 2f, ringPaint)

        // Inner saffron circle
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#A63B12")         // heritage saffron
            style = Paint.Style.FILL
        }
        canvas.drawCircle(cx, cy, sizePx / 2f - (3 * resources.displayMetrics.density), fillPaint)

        // Teardrop tail
        val tailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.FILL
        }
        val tailPath = android.graphics.Path().apply {
            moveTo(cx - 5 * resources.displayMetrics.density, sizePx.toFloat())
            lineTo(cx + 5 * resources.displayMetrics.density, sizePx.toFloat())
            lineTo(cx, totalH.toFloat())
            close()
        }
        canvas.drawPath(tailPath, tailPaint)

        // Fort emoji / icon text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 20 * resources.displayMetrics.density
            textAlign = Paint.Align.CENTER
        }
        // Vertical centre of the circle
        val textY = cy - (textPaint.ascent() + textPaint.descent()) / 2f
        canvas.drawText("🏰", cx, textY, textPaint)

        return BitmapDrawable(resources, bitmap)
    }

    private fun showFortDetail(
        fort: Fort
    ) {

        val existingSheet =

            supportFragmentManager
                .findFragmentByTag(
                    FortDetailBottomSheet.TAG
                )

        if (existingSheet != null) return

        val sheet =

            FortDetailBottomSheet
                .newInstance(fort)

        sheet.show(
            supportFragmentManager,
            FortDetailBottomSheet.TAG
        )
    }

    override fun onResume() {

        super.onResume()

        AmbientSoundManager.start(this)

        binding.mapView.onResume()
    }

    override fun onPause() {

        super.onPause()

        AmbientSoundManager.stop()

        binding.mapView.onPause()
    }
}
