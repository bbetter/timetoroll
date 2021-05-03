package com.owlsoft.shared.viewmodel

import com.owlsoft.shared.utils.asLiveFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

expect open class BaseViewModel() {
    val scope: CoroutineScope

}


