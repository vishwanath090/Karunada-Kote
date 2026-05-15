package com.karunadakote.ui.fortlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.karunadakote.data.model.Fort
import com.karunadakote.databinding.ActivityFortListBinding
import com.karunadakote.ui.map.MapActivity
import com.karunadakote.viewmodel.FortListViewModel
import kotlin.math.abs
import com.karunadakote.ui.profile.ProfileActivity
import com.karunadakote.data.local.SessionManager
import com.karunadakote.ui.map.ExploreMapActivity
class FortListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFortListBinding

    private val viewModel: FortListViewModel by viewModels()

    private lateinit var adapter: FortAdapter

    private var fullFortList: List<Fort> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityFortListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        // Welcome text on the inline TextView
        val session = SessionManager(this)
        binding.tvWelcome.text = "Welcome, ${session.getUserName()}"

        binding.toolbar.setOnClickListener {
            startActivity(Intent(this, ExploreMapActivity::class.java))
        }

        // Avatar initial + click
        binding.tvProfileInitial.text = session.getUserName().first().uppercase()
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setupRecyclerView()

        setupSearchLogic()

        observeViewModel()
    }

    private fun setupSearchLogic() {

        binding.searchView.setOnQueryTextListener(

            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(
                    query: String?
                ): Boolean {

                    binding.searchView.clearFocus()

                    return true
                }

                override fun onQueryTextChange(
                    newText: String?
                ): Boolean {

                    filterList(newText)

                    return true
                }
            }
        )
    }

    private fun filterList(query: String?) {

        val visitedIds =
            viewModel.visitedIds.value ?: emptySet()

        val filtered =

            if (query.isNullOrBlank()) {

                fullFortList

            } else {

                fullFortList.filter {

                    it.name.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }

        adapter.updateData(
            filtered,
            visitedIds
        )

        updateUiState(
            filtered,
            query
        )
    }

    private fun updateUiState(
        filteredList: List<Fort>,
        query: String?
    ) {

        if (filteredList.isEmpty()) {

            binding.recyclerView.visibility =
                View.GONE

            binding.emptyStateContainer.visibility =
                View.VISIBLE

            if (
                query.isNullOrBlank()
                && fullFortList.isEmpty()
            ) {

                binding.tvStateTitle.text =
                    "No Forts Available"

                binding.tvStateSubtitle.text =
                    "Check your connection or try again later."

            } else {

                binding.tvStateTitle.text =
                    "No Forts Found"

                binding.tvStateSubtitle.text =
                    "We couldn't find anything matching \"$query\""
            }

        } else {

            binding.recyclerView.visibility =
                View.VISIBLE

            binding.emptyStateContainer.visibility =
                View.GONE
        }
    }

    private fun setupRecyclerView() {

        adapter = FortAdapter(

            forts = emptyList(),

            visitedIds = emptySet(),

            onFortClick = { fort, imageView ->

                openMap(
                    fort,
                    imageView
                )
            }
        )

        binding.recyclerView.apply {

            layoutManager =
                LinearLayoutManager(
                    this@FortListActivity
                )

            adapter =
                this@FortListActivity.adapter

            setHasFixedSize(true)

            clipToPadding = false

            val topPaddingPx = (240 * resources.displayMetrics.density).toInt()
            setPadding(0, topPaddingPx, 0, (220 * resources.displayMetrics.density).toInt())

            overScrollMode =
                View.OVER_SCROLL_NEVER

            addOnScrollListener(

                object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(
                        recyclerView: RecyclerView,
                        dx: Int,
                        dy: Int
                    ) {

                        super.onScrolled(
                            recyclerView,
                            dx,
                            dy
                        )

                        applyCarouselEffect()
                    }
                }
            )

            post {

                applyCarouselEffect()
            }

            // Cinematic Auto Scroll

            postDelayed({

                smoothScrollBy(
                    0,
                    120
                )

            }, 450)

            alpha = 0f

            animate()
                .alpha(1f)
                .setDuration(700)
                .start()
        }
    }

    private fun applyCarouselEffect() {

        val recyclerView =
            binding.recyclerView

        val centerY =
            recyclerView.height / 2f

        var closestRatio = 1f

        for (i in 0 until recyclerView.childCount) {

            val child =
                recyclerView.getChildAt(i)

            val childCenterY =
                (child.top + child.bottom) / 2f

            val distance =
                abs(centerY - childCenterY)

            val ratio =
                distance / centerY

            if (ratio < closestRatio) {

                closestRatio = ratio
            }

            val smoothRatio =
                ratio.coerceIn(0f, 1f)

            val scale =
                1f - (smoothRatio * 0.08f)

            val alpha =
                1f - (smoothRatio * 0.28f)

            child.scaleX =
                scale.coerceAtLeast(0.90f)

            child.scaleY =
                scale.coerceAtLeast(0.90f)

            child.alpha =
                alpha.coerceAtLeast(0.58f)

            child.translationZ =
                scale * 35
            child.rotationX =
                smoothRatio * 2.5f

            child.translationY =
                smoothRatio * 18
        }

        // Floating Glass Header Effect

        binding.toolbar.alpha =
            (1f - (closestRatio * 0.15f))
                .coerceIn(0.75f, 1f)

        binding.searchView.alpha =
            (1f - (closestRatio * 0.10f))
                .coerceIn(0.82f, 1f)
    }

    private fun observeViewModel() {

        viewModel.isLoading.observe(this) { isLoading ->

            if (isLoading) {

                binding.shimmerViewContainer.startShimmer()

                binding.shimmerViewContainer.visibility =
                    View.VISIBLE

                binding.recyclerView.visibility =
                    View.GONE

                binding.emptyStateContainer.visibility =
                    View.GONE

            } else {

                binding.shimmerViewContainer.stopShimmer()

                binding.shimmerViewContainer.visibility =
                    View.GONE
            }
        }

        viewModel.forts.observe(this) { forts ->

            fullFortList = forts

            applyCurrentFilter()
        }

        viewModel.visitedIds.observe(this) {

            applyCurrentFilter()
        }

        viewModel.error.observe(this) { errorMsg ->

            errorMsg?.let {

                Snackbar.make(
                    binding.root,
                    it,
                    Snackbar.LENGTH_LONG
                ).show()

                if (fullFortList.isEmpty()) {

                    updateUiState(
                        emptyList(),
                        null
                    )
                }
            }
        }
    }

    private fun applyCurrentFilter() {

        filterList(
            binding.searchView.query.toString()
        )
    }

    override fun onResume() {
        super.onResume()

        viewModel.refreshVisitedIds()
    }

    private fun openMap(
        fort: Fort,
        imageView: View
    ) {

        val intent = Intent(
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

        val options =

            ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                    this,
                    imageView,
                    "fortImage"
                )

        startActivity(
            intent,
            options.toBundle()
        )
    }

}