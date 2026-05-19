package com.karunadakote.ui.detail

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar          // ← CHANGE 2: Snackbar
import com.karunadakote.R
import com.karunadakote.data.model.ApiResult
import com.karunadakote.data.model.Fort
import com.karunadakote.databinding.BottomSheetFortDetailBinding
import com.karunadakote.viewmodel.MapViewModel

class FortDetailBottomSheet : BottomSheetDialogFragment() {

    companion object {

        const val TAG =
            "FortDetailBottomSheet"

        private const val ARG_FORT_ID =
            "arg_fort_id"

        private const val ARG_FORT_NAME =
            "arg_fort_name"

        private const val ARG_FORT_LAT =
            "arg_fort_lat"

        private const val ARG_FORT_LNG =
            "arg_fort_lng"

        private const val ARG_FORT_DESC =
            "arg_fort_desc"

        private const val ARG_FORT_IMAGE =
            "arg_fort_image"

        private const val ARG_FORT_DYNASTY =
            "arg_fort_dynasty"

        private const val ARG_FORT_YEAR =
            "arg_fort_year"

        private const val ARG_FORT_TYPE =
            "arg_fort_type"

        private const val ARG_FORT_DISTRICT =
            "arg_fort_district"

        private const val ARG_FORT_HIGHLIGHTS =
            "arg_fort_highlights"

        // CHANGE 4: Typing speed — ms per character
        private const val TYPING_SPEED_MS = 12L

        fun newInstance(
            fort: Fort
        ): FortDetailBottomSheet {

            return FortDetailBottomSheet().apply {

                arguments = Bundle().apply {

                    putInt(
                        ARG_FORT_ID,
                        fort.id
                    )

                    putString(
                        ARG_FORT_NAME,
                        fort.name
                    )

                    putDouble(
                        ARG_FORT_LAT,
                        fort.lat
                    )

                    putDouble(
                        ARG_FORT_LNG,
                        fort.lng
                    )

                    putString(
                        ARG_FORT_DESC,
                        fort.description
                    )

                    putString(
                        ARG_FORT_IMAGE,
                        fort.image
                    )

                    putString(
                        ARG_FORT_DYNASTY,
                        fort.dynasty
                    )

                    putString(
                        ARG_FORT_YEAR,
                        fort.yearBuilt
                    )

                    putString(
                        ARG_FORT_TYPE,
                        fort.fortType
                    )

                    putString(
                        ARG_FORT_DISTRICT,
                        fort.districtName
                    )

                    putStringArrayList(
                        ARG_FORT_HIGHLIGHTS,
                        ArrayList(fort.highlights)
                    )
                }
            }
        }
    }

    private var _binding:
            BottomSheetFortDetailBinding? = null

    private val binding
        get() = _binding!!

    private val viewModel:
            MapViewModel by activityViewModels()

    private lateinit var fort: Fort

    private var currentSpeakText:
            String? = null

    // CHANGE 4: Typing animation state
    private val typingHandler = Handler(Looper.getMainLooper())
    private var typingRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            BottomSheetFortDetailBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onStart() {

        super.onStart()

        val bottomSheet = dialog
            ?.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

        bottomSheet?.let {

            val behavior =
                BottomSheetBehavior.from(it)

            behavior.state =
                BottomSheetBehavior.STATE_EXPANDED

            behavior.skipCollapsed = true

            behavior.isDraggable = true

            it.layoutParams.height =
                (Resources.getSystem()
                    .displayMetrics.heightPixels * 0.92)
                    .toInt()
        }
    }

    private fun animateContent() {

        binding.tvDescription.alpha = 0f
        binding.layoutAiCard.alpha = 0f
        binding.btnAction.alpha = 0f
        binding.layoutMetaRow.alpha = 0f
        binding.chipGroupHighlights.alpha = 0f

        binding.layoutMetaRow.translationY = 30f
        binding.chipGroupHighlights.translationY = 40f
        binding.tvDescription.translationY = 50f
        binding.layoutAiCard.translationY = 80f
        binding.btnAction.translationY = 100f

        binding.layoutMetaRow.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .start()

        binding.chipGroupHighlights.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(80)
            .setDuration(450)
            .start()

        binding.tvDescription.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(160)
            .setDuration(500)
            .start()

        binding.layoutAiCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(240)
            .setDuration(550)
            .start()

        binding.btnAction.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(320)
            .setDuration(600)
            .start()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        val args = requireArguments()

        fort = Fort(

            id = args.getInt(ARG_FORT_ID),

            name = args.getString(ARG_FORT_NAME, ""),

            lat = args.getDouble(ARG_FORT_LAT),

            lng = args.getDouble(ARG_FORT_LNG),

            description = args.getString(ARG_FORT_DESC, ""),

            image = args.getString(ARG_FORT_IMAGE, ""),

            dynasty = args.getString(ARG_FORT_DYNASTY, ""),

            yearBuilt = args.getString(ARG_FORT_YEAR, ""),

            fortType = args.getString(ARG_FORT_TYPE, ""),

            districtName = args.getString(ARG_FORT_DISTRICT, ""),

            highlights = args.getStringArrayList(ARG_FORT_HIGHLIGHTS) ?: emptyList()
        )

        binding.root.alpha = 0f

        binding.root.animate()
            .alpha(1f)
            .setDuration(250)
            .start()

        bindFortData()
        animateContent()

        setupButtons()

        observeAiDescription()
    }

    private fun bindFortData() {

        binding.tvFortName.text = fort.name

        binding.tvDescription.text = fort.description

        currentSpeakText = fort.description

        // District label on hero image
        if (fort.districtName.isNotEmpty()) {
            binding.tvDistrictName.text = "📍 ${fort.districtName} District"
            binding.tvDistrictName.visibility = View.VISIBLE
        } else {
            binding.tvDistrictName.visibility = View.GONE
        }

        // Dynasty / Year / Type meta row
        binding.tvDynasty.text = fort.dynasty.ifEmpty { "—" }
        binding.tvYearBuilt.text = fort.yearBuilt.ifEmpty { "—" }
        binding.tvFortType.text = fort.fortType.ifEmpty { "—" }

        // Highlights chips
        binding.chipGroupHighlights.removeAllViews()
        fort.highlights.forEach { highlight ->
            val chip = Chip(requireContext()).apply {
                text = highlight
                isCheckable = false
                isClickable = false
                setChipBackgroundColorResource(android.R.color.transparent)
                setTextColor(Color.WHITE)
                chipStrokeWidth = 1.5f
                setChipStrokeColorResource(android.R.color.white)
                textSize = 12f
            }
            binding.chipGroupHighlights.addView(chip)
        }

        val isVisited =
            viewModel.visitedIds.value
                ?.contains(fort.id) == true

        binding.tvVisitedStatus.visibility =
            if (isVisited) {
                View.VISIBLE
            } else {
                View.GONE
            }

        val imageResId =
            requireContext().resources
                .getIdentifier(
                    fort.image,
                    "drawable",
                    requireContext().packageName
                )

        if (imageResId != 0) {
            binding.ivFortHero.setImageResource(imageResId)
        } else {
            binding.ivFortHero.setImageResource(R.drawable.ic_fort)
        }
    }

    private fun setupButtons() {

        val alreadyVisited = viewModel.visitedIds.value?.contains(fort.id) == true
        if (alreadyVisited) {
            binding.btnMarkVisited.text = "✓ Visited!"
            binding.btnMarkVisited.isEnabled = false
            binding.btnMarkVisited.alpha = 0.7f
        }

        binding.btnMarkVisited
            .setOnClickListener {
                viewModel.selectFort(fort)
                binding.tvVisitedStatus.visibility = View.VISIBLE
                binding.btnMarkVisited.text = "✓ Visited!"
                binding.btnMarkVisited.isEnabled = false
                binding.btnMarkVisited.alpha = 0.7f
            }

        binding.btnPlayAudio
            .setOnClickListener {

                binding.btnPlayAudio.text =
                    getString(com.karunadakote.R.string.narrating)

                binding.btnStopAudio.visibility =
                    View.VISIBLE

                val text =
                    currentSpeakText
                        ?: fort.description

                viewModel.speakText(text)
            }

        binding.btnStopAudio
            .setOnClickListener {

                viewModel.stopSpeaking()

                binding.btnPlayAudio.text =
                    "Listen"

                binding.btnStopAudio.visibility =
                    View.GONE
            }

        binding.btnAction
            .setOnClickListener {

                dismiss()
            }

        binding.btnGenerateAi
            .setOnClickListener {

                viewModel.generateAiDescription(
                    fort.name
                )
            }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CHANGE 4: Typing animation — reveals AI text character by character
    // ─────────────────────────────────────────────────────────────────────────
    private fun startTypingAnimation(fullText: String) {

        // Cancel any in-progress animation
        typingRunnable?.let { typingHandler.removeCallbacks(it) }

        binding.tvAiDescription.text = ""

        var index = 0

        typingRunnable = object : Runnable {
            override fun run() {
                if (_binding == null) return          // fragment detached guard
                if (index <= fullText.length) {
                    binding.tvAiDescription.text = fullText.substring(0, index)
                    index++
                    typingHandler.postDelayed(this, TYPING_SPEED_MS)
                } else {
                    // Animation complete — update speak text to final AI response
                    currentSpeakText = fullText
                }
            }
        }

        typingHandler.post(typingRunnable!!)
    }

    private fun observeAiDescription() {

        viewModel.aiDescription.observe(
            viewLifecycleOwner
        ) { result ->

            when (result) {

                is ApiResult.Loading -> {

                    binding.progressAi.visibility =
                        View.VISIBLE

                    binding.tvAiDescription.visibility =
                        View.GONE

                    binding.tvAiLabel.visibility =
                        View.GONE

                    // CHANGE 3: Gemini label — hide while loading
                    binding.tvGeminiPowered.visibility =
                        View.GONE

                    binding.btnGenerateAi.visibility =
                        View.GONE
                }

                is ApiResult.Success -> {

                    binding.progressAi.visibility =
                        View.GONE

                    // CHANGE 3: "✨ Powered by Gemini AI" label
                    binding.tvAiLabel.visibility =
                        View.VISIBLE

                    binding.tvGeminiPowered.visibility =
                        View.VISIBLE

                    binding.tvAiDescription.visibility =
                        View.VISIBLE

                    binding.tvAiDescription.alpha = 1f

                    binding.btnGenerateAi.visibility =
                        View.GONE

                    // CHANGE 4: Start typing animation instead of instant set
                    startTypingAnimation(result.data)
                }

                is ApiResult.Error -> {

                    // Cancel any running typing animation
                    typingRunnable?.let { typingHandler.removeCallbacks(it) }

                    binding.progressAi.visibility =
                        View.GONE

                    binding.tvAiLabel.visibility =
                        View.VISIBLE

                    binding.tvGeminiPowered.visibility =
                        View.GONE

                    binding.tvAiDescription.visibility =
                        View.VISIBLE

                    val fallbackText = fort.description.takeIf { it.isNotBlank() }
                        ?: "AI summary unavailable. Please try again."
                    binding.tvAiDescription.text = fallbackText
                    binding.tvAiDescription.alpha = 0.85f

                    binding.btnGenerateAi.visibility =
                        View.VISIBLE

                    binding.btnGenerateAi.text =
                        "✦ Try AI Summary Again"

                    // CHANGE 2: Snackbar for error — much more visible than silent fallback
                    Snackbar.make(
                        binding.root,
                        "AI unavailable — showing fort description",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                null -> {

                    typingRunnable?.let { typingHandler.removeCallbacks(it) }

                    binding.tvAiLabel.visibility =
                        View.GONE

                    binding.tvGeminiPowered.visibility =
                        View.GONE

                    binding.tvAiDescription.visibility =
                        View.GONE

                    binding.btnGenerateAi.visibility =
                        View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()

        // CHANGE 4: Clean up typing handler to prevent memory leaks
        typingRunnable?.let { typingHandler.removeCallbacks(it) }
        typingRunnable = null

        _binding = null
    }
}
