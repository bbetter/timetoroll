package com.owlsoft.shared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

actual open class BaseViewModel actual constructor() : ViewModel() {
    actual val scope: CoroutineScope
        get() = viewModelScope

}