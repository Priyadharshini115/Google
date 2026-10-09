package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.SavedReviewEntity
import com.example.data.model.SentimentAnalysisResult
import com.example.domain.SampleReview
import com.example.domain.SampleReviewsData
import com.example.domain.SentimentAnalyzerEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SentimentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val reviewDao = db.reviewDao()

    val historyList: StateFlow<List<SavedReviewEntity>> = reviewDao.getAllReviews()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentResult = MutableStateFlow<SentimentAnalysisResult>(
        SentimentAnalyzerEngine.analyzeReview(SampleReviewsData.USER_FEATURED_REVIEW.reviewText)
    )
    val currentResult: StateFlow<SentimentAnalysisResult> = _currentResult.asStateFlow()

    private val _inputText = MutableStateFlow(SampleReviewsData.USER_FEATURED_REVIEW.reviewText)
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _saveFeedback = MutableStateFlow<String?>(null)
    val saveFeedback: StateFlow<String?> = _saveFeedback.asStateFlow()

    init {
        // Automatically save initial featured review to history if empty
        viewModelScope.launch {
            val featuredResult = _currentResult.value
            reviewDao.insertReview(
                SavedReviewEntity(
                    reviewText = featuredResult.reviewText,
                    category = "Footwear & Apparel",
                    classification = featuredResult.overallClassification.label,
                    polarityScore = featuredResult.polarityScore,
                    estimatedRating = featuredResult.estimatedRating,
                    executiveSummary = featuredResult.executiveSummary
                )
            )
        }
    }

    fun setActiveTab(tab: Int) {
        _activeTab.value = tab
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun analyzeInput(category: String = "General") {
        viewModelScope.launch {
            _isAnalyzing.value = true
            // Smooth brief animation delay for analytical realism
            delay(350)
            val result = SentimentAnalyzerEngine.analyzeReview(_inputText.value)
            _currentResult.value = result
            _isAnalyzing.value = false

            // Automatically persist to history
            if (result.reviewText.isNotBlank()) {
                reviewDao.insertReview(
                    SavedReviewEntity(
                        reviewText = result.reviewText,
                        category = category,
                        classification = result.overallClassification.label,
                        polarityScore = result.polarityScore,
                        estimatedRating = result.estimatedRating,
                        executiveSummary = result.executiveSummary
                    )
                )
            }
        }
    }

    fun loadSample(sample: SampleReview) {
        _inputText.value = sample.reviewText
        _currentResult.value = SentimentAnalyzerEngine.analyzeReview(sample.reviewText)
    }

    fun deleteHistoryItem(item: SavedReviewEntity) {
        viewModelScope.launch {
            reviewDao.deleteReview(item)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            reviewDao.clearAllReviews()
        }
    }

    fun showFeedback(message: String) {
        viewModelScope.launch {
            _saveFeedback.value = message
            delay(2000)
            _saveFeedback.value = null
        }
    }
}
