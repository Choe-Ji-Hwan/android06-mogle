package com.wakeup.presentation.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.wakeup.domain.usecase.DeleteMomentUseCase
import com.wakeup.domain.usecase.moment.GetMomentUseCase
import com.wakeup.presentation.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MomentDetailViewModel @Inject constructor(
    private val getMomentUseCase: GetMomentUseCase,
    private val deleteMomentUseCase: DeleteMomentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // TODO State 저장할 필요 고민

    private val momentId = savedStateHandle.get<Long>("momentId") ?: -1

    val moment = getMomentUseCase(momentId).map { moment ->
        moment.toPresentation()
    }.asLiveData()

    fun deleteMoment(momentId: Long) {
        viewModelScope.launch {
            deleteMomentUseCase(momentId)
        }
    }
}