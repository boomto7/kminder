package com.kminder.minder.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.repository.JournalRepository
import com.kminder.minder.util.MockDataInitializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.delay

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val journalRepository: JournalRepository,
    private val mockDataInitializer: MockDataInitializer
) : ViewModel() {

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    init {
        checkAndInitializeData()
    }

    private fun checkAndInitializeData() {
        viewModelScope.launch {
            try {
                // DB가 비어있는지 확인
                val entries = journalRepository.getAllEntries().first()
                if (entries.isEmpty()) {
                    // 비어있으면 목업 데이터 주입
                    mockDataInitializer.initialize()
                }
                
                // 최소 지연 시간 (로고 보여주기 위함)을 여기서 처리할 수도 있지만, 
                // UI 애니메이션과 별개로 데이터 준비 완료 신호만 보냄.
                // UI에서 애니메이션 + 데이터 준비 완료 두 가지 조건을 모두 만족할 때 이동.
                _isCompleted.value = true
            } catch (e: Exception) {
                // 에러 발생 시에도 진행은 시킴 (빈 상태로)
                e.printStackTrace()
                _isCompleted.value = true
            }
        }
    }
}
