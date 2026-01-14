package com.kminder.minder.ui.screen.statistics

import com.kminder.domain.model.ChartPeriod
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionKeyword
import com.kminder.domain.model.EmotionStatistics
import com.kminder.domain.usecase.statistics.GetEmotionStatisticsUseCase
import com.kminder.domain.usecase.statistics.GetKeywordNetworkDataUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {

    private lateinit var viewModel: StatisticsViewModel
    private val getEmotionStatisticsUseCase: GetEmotionStatisticsUseCase = mockk()
    private val getKeywordNetworkDataUseCase: GetKeywordNetworkDataUseCase = mockk()
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mocks
        coEvery { getEmotionStatisticsUseCase(any(), any(), any()) } returns emptyList()
        coEvery { getKeywordNetworkDataUseCase(any(), any()) } returns emptyList()
        
        viewModel = StatisticsViewModel(
            getEmotionStatisticsUseCase,
            getKeywordNetworkDataUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads statistics for current week`() = runTest(testDispatcher) {
        // Given
        val mockStats = listOf(
            EmotionStatistics(LocalDate.now(), EmotionAnalysis(), 1)
        )
        coEvery { getEmotionStatisticsUseCase(ChartPeriod.WEEK, any(), any()) } returns mockStats
        
        // When
        viewModel.loadStatistics() // Explicit call for test, though init block calls it too
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines
        
        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.totalEntryCount)
        assertEquals(ChartPeriod.WEEK, state.selectedPeriod)
        
        coVerify { getEmotionStatisticsUseCase(ChartPeriod.WEEK, any(), any()) }
    }

    @Test
    fun `setPeriod updates period and reloads statistics`() = runTest(testDispatcher) {
        // When
        viewModel.setPeriod(ChartPeriod.MONTH)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(ChartPeriod.MONTH, viewModel.uiState.value.selectedPeriod)
        coVerify { getEmotionStatisticsUseCase(ChartPeriod.MONTH, any(), any()) }
    }

    @Test
    fun `moveDate updates anchorDate and reloads statistics`() = runTest(testDispatcher) {
        // Given
        val initialDate = viewModel.uiState.value.anchorDate
        
        // When
        viewModel.moveDate(-1) // Previous week
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val newDate = viewModel.uiState.value.anchorDate
        assertTrue(newDate.isBefore(initialDate))
        // Verify reload happen
        coVerify(exactly = 2) { getEmotionStatisticsUseCase(any(), any(), any()) } // Init + Move
    }
}
