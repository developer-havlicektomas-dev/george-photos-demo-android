package dev.havlicektomas.photosapp.feature.home.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import dev.havlicektomas.photosapp.core.domain.util.DataError
import dev.havlicektomas.photosapp.core.domain.util.Result
import dev.havlicektomas.photosapp.feature.home.domain.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads photos and populates state`() = runTest {
        val photos = listOf(
            photo("1", tags = listOf("nature", "forest")),
            photo("2", tags = listOf("sunset")),
        )
        val viewModel = HomeViewModel(FakePhotoRemoteDataSource(Result.Success(photos)))

        val state = viewModel.state.value
        assertThat(state.photos).isEqualTo(photos)
        assertThat(state.filteredPhotos).isEqualTo(photos)
        assertThat(state.isLoading).isFalse()
        assertThat(state.isRefreshing).isFalse()
    }

    @Test
    fun `init fetches with empty tags`() = runTest {
        val fake = FakePhotoRemoteDataSource(Result.Success(emptyList()))
        HomeViewModel(fake)

        assertThat(fake.tagsHistory).isEqualTo(listOf(emptyList<String>()))
    }

    @Test
    fun `init derives distinct sorted non-blank tags`() = runTest {
        val photos = listOf(
            photo("1", tags = listOf("forest", "nature", "")),
            photo("2", tags = listOf("nature", "sunset")),
            photo("3", tags = listOf("ocean")),
        )
        val viewModel = HomeViewModel(FakePhotoRemoteDataSource(Result.Success(photos)))

        assertThat(viewModel.state.value.availableTags)
            .containsExactly("forest", "nature", "ocean", "sunset")
    }

    @Test
    fun `init failure sets errorMessage and emits ShowError event`() = runTest {
        val fake = FakePhotoRemoteDataSource(Result.Error(DataError.Network.NO_INTERNET))
        val viewModel = HomeViewModel(fake)

        viewModel.events.test {
            assertThat(awaitItem()).isInstanceOf(HomeEvent.ShowError::class)
        }
        val state = viewModel.state.value
        assertThat(state.errorMessage).isNotNull()
        assertThat(state.isLoading).isFalse()
        assertThat(state.photos).isEmpty()
    }

    @Test
    fun `OnRefresh re-fetches with current selectedTags and surfaces isRefreshing transition`() = runTest {
        val initial = listOf(photo("1"))
        val refreshed = listOf(photo("1"), photo("2"))
        val fake = FakePhotoRemoteDataSource(Result.Success(initial))
        val viewModel = HomeViewModel(fake)
        // First fetch already consumed by init.
        fake.queueResponse(Result.Success(refreshed))
        fake.pauseNextFetch()

        viewModel.state.test {
            val first = awaitItem()
            assertThat(first.isRefreshing).isFalse()
            assertThat(first.photos).isEqualTo(initial)

            viewModel.onAction(HomeAction.OnRefresh)

            val refreshing = awaitItem()
            assertThat(refreshing.isRefreshing).isTrue()

            fake.resumePausedFetch()

            val done = awaitItem()
            assertThat(done.isRefreshing).isFalse()
            assertThat(done.photos).isEqualTo(refreshed)
        }
        assertThat(fake.fetchCount).isEqualTo(2)
        assertThat(fake.lastTags).isEqualTo(emptyList<String>())
    }

    @Test
    fun `OnFilterClick opens sheet and copies selectedTags into draftTags`() = runTest {
        val (viewModel, _) = primedViewModel(
            photos = listOf(photo("1", tags = listOf("nature"))),
            committedSelected = setOf("nature"),
        )

        viewModel.onAction(HomeAction.OnFilterClick)

        val state = viewModel.state.value
        assertThat(state.isFilterSheetOpen).isTrue()
        assertThat(state.draftTags).isEqualTo(setOf("nature"))
    }

    @Test
    fun `OnDraftTagToggle adds and removes tag from draft only`() = runTest {
        val viewModel = HomeViewModel(
            FakePhotoRemoteDataSource(
                Result.Success(listOf(photo("1", tags = listOf("a", "b")))),
            ),
        )
        viewModel.onAction(HomeAction.OnFilterClick)

        viewModel.onAction(HomeAction.OnDraftTagToggle("a"))
        assertThat(viewModel.state.value.draftTags).isEqualTo(setOf("a"))
        assertThat(viewModel.state.value.selectedTags).isEmpty()

        viewModel.onAction(HomeAction.OnDraftTagToggle("b"))
        assertThat(viewModel.state.value.draftTags).isEqualTo(setOf("a", "b"))

        viewModel.onAction(HomeAction.OnDraftTagToggle("a"))
        assertThat(viewModel.state.value.draftTags).isEqualTo(setOf("b"))
        assertThat(viewModel.state.value.selectedTags).isEmpty()
    }

    @Test
    fun `OnTagInputChange updates tagInput`() = runTest {
        val viewModel = HomeViewModel(
            FakePhotoRemoteDataSource(Result.Success(emptyList())),
        )

        viewModel.onAction(HomeAction.OnTagInputChange("kit"))

        assertThat(viewModel.state.value.tagInput).isEqualTo("kit")
    }

    @Test
    fun `OnAddTypedTag adds trimmed input to draftTags and clears tagInput`() = runTest {
        val viewModel = HomeViewModel(
            FakePhotoRemoteDataSource(Result.Success(emptyList())),
        )
        viewModel.onAction(HomeAction.OnFilterClick)
        viewModel.onAction(HomeAction.OnTagInputChange("  kittens  "))

        viewModel.onAction(HomeAction.OnAddTypedTag)

        val state = viewModel.state.value
        assertThat(state.draftTags).isEqualTo(setOf("kittens"))
        assertThat(state.tagInput).isEqualTo("")
    }

    @Test
    fun `OnAddTypedTag ignores blank input`() = runTest {
        val viewModel = HomeViewModel(
            FakePhotoRemoteDataSource(Result.Success(emptyList())),
        )
        viewModel.onAction(HomeAction.OnFilterClick)
        viewModel.onAction(HomeAction.OnTagInputChange("   "))

        viewModel.onAction(HomeAction.OnAddTypedTag)

        val state = viewModel.state.value
        assertThat(state.draftTags).isEmpty()
        assertThat(state.tagInput).isEqualTo("   ")
    }

    @Test
    fun `OnSheetApply commits draft, refetches with selected tags, and closes sheet`() = runTest {
        val matching = photo("1", tags = listOf("nature", "forest"))
        val nonMatching = photo("2", tags = listOf("city"))
        val fake = FakePhotoRemoteDataSource(Result.Success(listOf(matching, nonMatching)))
        // Server response for the "forest"-filtered query.
        fake.queueResponse(Result.Success(listOf(matching)))
        val viewModel = HomeViewModel(fake)
        viewModel.onAction(HomeAction.OnFilterClick)
        viewModel.onAction(HomeAction.OnDraftTagToggle("forest"))

        viewModel.onAction(HomeAction.OnSheetApply)

        val state = viewModel.state.value
        assertThat(state.selectedTags).isEqualTo(setOf("forest"))
        assertThat(state.filteredPhotos).isEqualTo(listOf(matching))
        assertThat(state.isFilterSheetOpen).isFalse()
        assertThat(state.draftTags).isEmpty()
        assertThat(state.tagInput).isEqualTo("")
        assertThat(fake.lastTags).isEqualTo(listOf("forest"))
    }

    @Test
    fun `OnSheetApply forwards typed tag to the data source`() = runTest {
        val fake = FakePhotoRemoteDataSource(Result.Success(emptyList()))
        fake.queueResponse(Result.Success(emptyList()))
        val viewModel = HomeViewModel(fake)
        viewModel.onAction(HomeAction.OnFilterClick)
        viewModel.onAction(HomeAction.OnTagInputChange("kittens"))
        viewModel.onAction(HomeAction.OnAddTypedTag)

        viewModel.onAction(HomeAction.OnSheetApply)

        assertThat(viewModel.state.value.selectedTags).isEqualTo(setOf("kittens"))
        assertThat(fake.lastTags).isEqualTo(listOf("kittens"))
    }

    @Test
    fun `OnSheetDismiss closes sheet, discards draft, and clears tagInput`() = runTest {
        val (viewModel, _) = primedViewModel(
            photos = listOf(photo("1", tags = listOf("nature"))),
            committedSelected = setOf("nature"),
        )
        viewModel.onAction(HomeAction.OnFilterClick)
        viewModel.onAction(HomeAction.OnDraftTagToggle("nature")) // unselects in draft
        viewModel.onAction(HomeAction.OnTagInputChange("typed"))

        viewModel.onAction(HomeAction.OnSheetDismiss)

        val state = viewModel.state.value
        assertThat(state.isFilterSheetOpen).isFalse()
        assertThat(state.draftTags).isEmpty()
        assertThat(state.tagInput).isEqualTo("")
        assertThat(state.selectedTags).isEqualTo(setOf("nature")) // committed untouched
    }

    @Test
    fun `OnSheetClear empties draft and tagInput`() = runTest {
        val (viewModel, _) = primedViewModel(
            photos = listOf(photo("1", tags = listOf("nature", "forest"))),
            committedSelected = setOf("nature", "forest"),
        )
        viewModel.onAction(HomeAction.OnFilterClick)
        viewModel.onAction(HomeAction.OnTagInputChange("typed"))

        viewModel.onAction(HomeAction.OnSheetClear)

        val state = viewModel.state.value
        assertThat(state.draftTags).isEmpty()
        assertThat(state.tagInput).isEqualTo("")
        assertThat(state.isFilterSheetOpen).isTrue()
        assertThat(state.selectedTags).isEqualTo(setOf("nature", "forest"))
    }

    @Test
    fun `OnRemoveActiveFilter removes tag from selected and refetches with the remaining tags`() = runTest {
        val nature = photo("1", tags = listOf("nature"))
        val city = photo("2", tags = listOf("city"))
        val (viewModel, fake) = primedViewModel(
            photos = listOf(nature, city),
            committedSelected = setOf("nature", "city"),
        )
        // Server response after removing "nature".
        fake.queueResponse(Result.Success(listOf(city)))

        viewModel.onAction(HomeAction.OnRemoveActiveFilter("nature"))

        val state = viewModel.state.value
        assertThat(state.selectedTags).isEqualTo(setOf("city"))
        assertThat(state.filteredPhotos).isEqualTo(listOf(city))
        assertThat(fake.lastTags).isEqualTo(listOf("city"))
    }

    @Test
    fun `OnClearAllActiveFilters empties selected and refetches without filters`() = runTest {
        val photos = listOf(
            photo("1", tags = listOf("nature")),
            photo("2", tags = listOf("city")),
        )
        val (viewModel, fake) = primedViewModel(
            photos = photos,
            committedSelected = setOf("nature"),
            committedResult = listOf(photos[0]),
        )
        // Sanity-check the precondition: priming applied the "nature" filter on the server.
        assertThat(viewModel.state.value.filteredPhotos).isEqualTo(listOf(photos[0]))
        // Server response after clearing all filters.
        fake.queueResponse(Result.Success(photos))

        viewModel.onAction(HomeAction.OnClearAllActiveFilters)

        val state = viewModel.state.value
        assertThat(state.selectedTags).isEmpty()
        assertThat(state.filteredPhotos).isEqualTo(photos)
        assertThat(fake.lastTags).isEqualTo(emptyList<String>())
    }

    @Test
    fun `OnPhotoClick emits NavigateToDetail with the same photo`() = runTest {
        val target = photo("42", tags = listOf("nature"))
        val viewModel = HomeViewModel(
            FakePhotoRemoteDataSource(Result.Success(listOf(target))),
        )

        viewModel.events.test {
            viewModel.onAction(HomeAction.OnPhotoClick(target))
            assertThat(awaitItem()).isEqualTo(HomeEvent.NavigateToDetail(target))
        }
    }

    private fun photo(
        id: String,
        title: String = "Photo $id",
        tags: List<String> = emptyList(),
    ) = Photo(
        id = id,
        title = title,
        imageUrl = "https://example.test/$id.jpg",
        tags = tags,
    )

    /**
     * Loads [photos] via init, then commits [committedSelected] through the sheet flow so the VM
     * is in a state where filters are applied — useful as a starting point for action tests.
     * The apply step now triggers a fetch; callers can override [committedResult] when the
     * server-filtered response should differ from [photos].
     */
    private fun primedViewModel(
        photos: List<Photo>,
        committedSelected: Set<String>,
        committedResult: List<Photo> = photos,
    ): Pair<HomeViewModel, FakePhotoRemoteDataSource> {
        val fake = FakePhotoRemoteDataSource(Result.Success(photos))
        if (committedSelected.isNotEmpty()) {
            fake.queueResponse(Result.Success(committedResult))
        }
        val viewModel = HomeViewModel(fake)
        if (committedSelected.isNotEmpty()) {
            viewModel.onAction(HomeAction.OnFilterClick)
            committedSelected.forEach { viewModel.onAction(HomeAction.OnDraftTagToggle(it)) }
            viewModel.onAction(HomeAction.OnSheetApply)
        }
        return viewModel to fake
    }
}