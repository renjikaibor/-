package com.aicodeassistant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _currentTab = MutableStateFlow(0)
    val currentTab = _currentTab

    fun setCurrentTab(tab: Int) {
        _currentTab.value = tab
    }
}
