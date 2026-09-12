package com.aicodeassistant.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicodeassistant.domain.model.AIModel
import com.aicodeassistant.domain.model.ModelType
import com.aicodeassistant.domain.model.Permission
import com.aicodeassistant.domain.repository.ModelRepository
import com.aicodeassistant.domain.repository.PermissionRepository
import com.aicodeassistant.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val modelRepository: ModelRepository,
    private val permissionRepository: PermissionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedSection = MutableStateFlow(SettingsSection.Models)
    val selectedSection = _selectedSection.asStateFlow()

    private val _showAddModelDialog = MutableStateFlow(false)
    val showAddModelDialog = _showAddModelDialog.asStateFlow()

    private val _editingModel = MutableStateFlow<AIModel?>(null)
    val editingModel = _editingModel.asStateFlow()

    private val _defaultModelId = MutableStateFlow<Long?>(null)
    val defaultModelId = _defaultModelId.asStateFlow()

    // Models
    val builtinModels = modelRepository.observeModelsByType(ModelType.BUILTIN)
    val customModels = modelRepository.observeModelsByType(ModelType.CUSTOM)

    // Permissions
    val permissions = permissionRepository.observeAllPermissions()

    // Search API Keys
    private val _serperApiKey = MutableStateFlow("")
    val serperApiKey = _serperApiKey.asStateFlow()

    private val _bingApiKey = MutableStateFlow("")
    val bingApiKey = _bingApiKey.asStateFlow()

    init {
        loadDefaultModel()
        loadSearchApiKeys()
    }

    private fun loadDefaultModel() {
        viewModelScope.launch {
            modelRepository.getDefaultModel()?.let { model ->
                _defaultModelId.value = model.id
            }
        }
    }

    private fun loadSearchApiKeys() {
        viewModelScope.launch {
            _serperApiKey.value = settingsRepository.getString("serper_api_key", "") ?: ""
            _bingApiKey.value = settingsRepository.getString("bing_api_key", "") ?: ""
        }
    }

    fun setDefaultModel(model: AIModel) {
        viewModelScope.launch {
            modelRepository.setDefaultModel(model.id)
            _defaultModelId.value = model.id
        }
    }

    fun editModel(model: AIModel) {
        _editingModel.value = model
        _showAddModelDialog.value = true
    }

    fun addModel(model: AIModel) {
        viewModelScope.launch {
            modelRepository.addModel(model)
        }
    }

    fun updateModel(model: AIModel) {
        viewModelScope.launch {
            modelRepository.updateModel(model)
        }
    }

    fun deleteModel(model: AIModel) {
        viewModelScope.launch {
            modelRepository.deleteModel(model.id)
        }
    }

    fun requestPermission(permission: Permission) {
        viewModelScope.launch {
            permissionRepository.requestPermission(permission.permissionName)
        }
    }

    fun saveSearchApiKeys() {
        viewModelScope.launch {
            settingsRepository.putString("serper_api_key", _serperApiKey.value)
            settingsRepository.putString("bing_api_key", _bingApiKey.value)
        }
    }

    fun clearCache() {
        // TODO: Implement cache clearing
    }

    fun exportData() {
        // TODO: Implement data export
    }

    fun importData() {
        // TODO: Implement data import
    }
}

enum class SettingsSection {
    Models, Permissions, SearchApi, Data, About
}
