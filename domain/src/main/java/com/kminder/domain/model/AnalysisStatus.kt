package com.kminder.domain.model

/**
 * 감정 분석 상태
 */
enum class AnalysisStatus {
    PENDING,   // 분석 대기 중 (저장 직후)
    COMPLETED, // 분석 완료
    FAILED,    // 분석 실패 (네트워크 오류 등)
    NONE       // 분석 필요 없음 (혹은 초기 상태)
}
