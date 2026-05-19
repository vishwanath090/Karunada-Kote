package com.karunadakote.ui.fortlist

import coil.load
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar          // ← CHANGE 2: Snackbar import
import com.karunadakote.R
import com.karunadakote.data.model.Fort
import com.karunadakote.databinding.ItemFortBinding

class FortAdapter(
    private var forts: List<Fort>,
    private var visitedIds: Set<Int>,
    private val onFortClick: (Fort, View) -> Unit
) : RecyclerView.Adapter<FortAdapter.FortViewHolder>() {

    inner class FortViewHolder(
        private val binding: ItemFortBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fort: Fort) {

            // ─────────────────────────────────────────────
            // Text Content
            // ─────────────────────────────────────────────

            binding.tvFortName.text = fort.name

            binding.tvFortDescription.text =
                fort.description.ifBlank {
                    "Explore Karnataka heritage"
                }

            binding.tvDynastyLabel.text =
                fort.dynasty.takeIf { it.isNotBlank() }
                    ?: "Historic Karnataka"

            binding.tvYearLabel.text =
                fort.yearBuilt.takeIf { it.isNotBlank() }
                    ?: "Ancient Era"

            // ─────────────────────────────────────────────
            // Fort Type Badge
            // ─────────────────────────────────────────────

            if (fort.fortType.isNotBlank()) {
                binding.tvFortTypeBadge.text = fort.fortType
                binding.tvFortTypeBadge.visibility = View.VISIBLE
            } else {
                binding.tvFortTypeBadge.visibility = View.GONE
            }

            // ─────────────────────────────────────────────
            // District Chip
            // ─────────────────────────────────────────────

            if (fort.districtName.isNotBlank()) {
                binding.tvDistrictChip.text =
                    "📍 ${fort.districtName}"

                binding.tvDistrictChip.visibility =
                    View.VISIBLE
            } else {
                binding.tvDistrictChip.visibility =
                    View.GONE
            }

            // ─────────────────────────────────────────────
            // Visited Badge
            // ─────────────────────────────────────────────

            binding.tvVisitedBadge.visibility =
                if (visitedIds.contains(fort.id)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            // ─────────────────────────────────────────────
            // Hero Image
            // ─────────────────────────────────────────────

            val imageResId =
                binding.root.context.resources.getIdentifier(
                    fort.image,
                    "drawable",
                    binding.root.context.packageName
                )

            binding.ivFortBanner.load(
                if (imageResId != 0) {
                    imageResId
                } else {
                    R.drawable.ic_fort
                }
            ) {

                crossfade(true)

                crossfade(250)

                allowHardware(true)

                placeholder(R.drawable.ic_fort)

                error(R.drawable.ic_fort)
            }

            // ─────────────────────────────────────────────
            // Entrance Animation
            // ─────────────────────────────────────────────

            if (binding.root.alpha < 1f) {

                binding.root.alpha = 0f
                binding.root.translationY = 60f

                binding.root.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(450)
                    .setStartDelay(
                        (bindingAdapterPosition * 35L)
                            .coerceAtMost(250L)
                    )
                    .start()
            }

            // ─────────────────────────────────────────────
            // Card Click Animation
            // ─────────────────────────────────────────────

            binding.root.setOnClickListener {

                binding.root.animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(100)
                    .withEndAction {

                        binding.root.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(180)
                            .start()

                        onFortClick(
                            fort,
                            binding.ivFortBanner
                        )
                    }
                    .start()
            }

            // ─────────────────────────────────────────────
            // Route Button Click
            // CHANGE 2: Replaced Toast with Snackbar
            // ─────────────────────────────────────────────

            binding.btnRoute.setOnClickListener {

                openGoogleMapsRoute(
                    rootView = binding.root,
                    context = binding.root.context,
                    latitude = fort.lat,
                    longitude = fort.lng,
                    fortName = fort.name
                )
            }
        }
    }

    // ─────────────────────────────────────────────────
    // Create ViewHolder
    // ─────────────────────────────────────────────────

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FortViewHolder {

        val binding = ItemFortBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FortViewHolder(binding)
    }

    // ─────────────────────────────────────────────────
    // Bind ViewHolder
    // ─────────────────────────────────────────────────

    override fun onBindViewHolder(
        holder: FortViewHolder,
        position: Int
    ) {
        holder.bind(forts[position])
    }

    override fun getItemCount() = forts.size

    // ─────────────────────────────────────────────────
    // DiffUtil Update
    // ─────────────────────────────────────────────────

    fun updateData(
        newForts: List<Fort>,
        newVisitedIds: Set<Int>
    ) {

        val diff = DiffUtil.calculateDiff(
            object : DiffUtil.Callback() {

                override fun getOldListSize() =
                    forts.size

                override fun getNewListSize() =
                    newForts.size

                override fun areItemsTheSame(
                    oldPos: Int,
                    newPos: Int
                ): Boolean {

                    return forts[oldPos].id ==
                            newForts[newPos].id
                }

                override fun areContentsTheSame(
                    oldPos: Int,
                    newPos: Int
                ): Boolean {

                    return forts[oldPos] ==
                            newForts[newPos] &&

                            visitedIds.contains(
                                forts[oldPos].id
                            ) ==

                            newVisitedIds.contains(
                                newForts[newPos].id
                            )
                }
            }
        )

        forts = newForts
        visitedIds = newVisitedIds

        diff.dispatchUpdatesTo(this)
    }

    // ─────────────────────────────────────────────────
    // Google Maps Route
    // CHANGE 2: rootView param added for Snackbar anchor
    // ─────────────────────────────────────────────────

    private fun openGoogleMapsRoute(
        rootView: View,
        context: Context,
        latitude: Double,
        longitude: Double,
        fortName: String
    ) {

        try {

            val uri = Uri.parse(
                "google.navigation:q=$latitude,$longitude"
            )

            val intent = Intent(
                Intent.ACTION_VIEW,
                uri
            )

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            context.startActivity(intent)

        } catch (e: Exception) {

            try {

                // Browser fallback
                val webUri = Uri.parse(
                    "https://www.google.com/maps/dir/?api=1" +
                            "&destination=$latitude,$longitude"
                )

                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    webUri
                )

                webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                context.startActivity(webIntent)

            } catch (ex: Exception) {

                // CHANGE 2: Snackbar replaces Toast — stays visible, looks polished
                Snackbar.make(
                    rootView,
                    "Unable to open Maps for $fortName",
                    Snackbar.LENGTH_SHORT
                ).show()

                ex.printStackTrace()
            }

            e.printStackTrace()
        }
    }

}
