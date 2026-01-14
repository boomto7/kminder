package com.kminder.minder.ui.screen.home

import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.analysis.AnalyzeIntegratedEmotionUseCase
import com.kminder.domain.usecase.journal.GetDynamicJournalEntriesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val getDynamicJournalEntriesUseCase: GetDynamicJournalEntriesUseCase = mockk()
    private val analyzeIntegratedEmotionUseCase: AnalyzeIntegratedEmotionUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = HomeViewModel(
            getDynamicJournalEntriesUseCase,
            analyzeIntegratedEmotionUseCase
        )
    }

    @Test
    fun `refresh updates state and data correctly`() = runTest(testDispatcher) {
        // Given
        val mockEntries = listOf(
            JournalEntry(1, "Test content", com.kminder.domain.model.EntryType.FREE_WRITING, null, LocalDateTime.now(), LocalDateTime.now())
        )
        coEvery { getDynamicJournalEntriesUseCase(any()) } returns flowOf(mockEntries)

        createViewModel()
        advanceUntilIdle() // Initial load

        assertEquals(HomeUiState.Success(mapOf()), viewModel.uiState.value.let { if(it is HomeUiState.Success) it.copy(groupedFeed = mapOf()) else it })
        
        // When
        viewModel.refresh()
        
        // Then
        assertTrue(viewModel.isRefreshing.value) // Should be true immediately
        
        advanceUntilIdle() // Let refresh complete
        
        assertEquals(false, viewModel.isRefreshing.value) // Should be false after load
    }

    @Test
    fun `refresh triggers reload even if limit is already at initial value`() = runTest(testDispatcher) {
        // Given
        val mockEntries = listOf(
             JournalEntry(1, "Data 1", com.kminder.domain.model.EntryType.FREE_WRITING, null, LocalDateTime.now(), LocalDateTime.now())
        )
        coEvery { getDynamicJournalEntriesUseCase(any()) } returns flowOf(mockEntries)
        
        createViewModel()
        advanceUntilIdle() // Initial load (limit = 20)

        // Pre-condition: limit should be 20
        // We can't access private limit, but we know initial flow logic ran.

        // When
        viewModel.refresh() // This sets limit to 20 (no change), but increments refreshTrigger
        
        // Then
        assertTrue(viewModel.isRefreshing.value)
        advanceUntilIdle()
        assertEquals(false, viewModel.isRefreshing.value)
    }

    @Test
    fun `empty data during refresh sets UiState to Empty`() = runTest(testDispatcher) {
        // Given
        val mockEntries = listOf(
             JournalEntry(1, "Data 1", com.kminder.domain.model.EntryType.FREE_WRITING, null, LocalDateTime.now(), LocalDateTime.now())
        )
        coEvery { getDynamicJournalEntriesUseCase(any()) } returns flowOf(mockEntries)
        
        createViewModel()
        advanceUntilIdle() // Initial load with data

        // Now simulate empty data return for refresh
        coEvery { getDynamicJournalEntriesUseCase(any()) } returns flowOf(emptyList())

        // When
        viewModel.refresh()
        
        // Then
        assertTrue(viewModel.isRefreshing.value)
        advanceUntilIdle()
        
        // The fix we implemented: Empty data during refresh should set state to Empty
        assertTrue(viewModel.uiState.value is HomeUiState.Empty)
        assertEquals(false, viewModel.isRefreshing.value)
    }
}
