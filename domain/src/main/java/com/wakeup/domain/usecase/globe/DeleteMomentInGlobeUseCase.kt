package com.wakeup.domain.usecase.globe

import com.wakeup.domain.model.Globe
import com.wakeup.domain.model.Moment
import com.wakeup.domain.repository.GlobeRepository
import com.wakeup.domain.repository.RelationRepository
import javax.inject.Inject

class DeleteMomentInGlobeUseCase @Inject constructor(
    private val relationRepository: RelationRepository,
    private val globeRepository: GlobeRepository,
) {
    suspend operator fun invoke(moment: Moment, globe: Globe) {
        relationRepository.deleteMomentGlobeXRef(moment.id, globe.id)

        val firstMoment = globeRepository.getFirstMomentByGlobe(globe.id)

        if (firstMoment == null || firstMoment.pictures.isEmpty()) {
            globeRepository.updateGlobe(globe.copy(thumbnail = null))
        } else {
            globeRepository.updateGlobe(globe.copy(thumbnail = firstMoment.pictures.first()))
        }
    }
}