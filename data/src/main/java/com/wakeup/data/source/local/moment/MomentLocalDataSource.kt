package com.wakeup.data.source.local.moment

import com.wakeup.data.database.entity.GlobeEntity
import com.wakeup.data.database.entity.LocationEntity
import com.wakeup.data.database.entity.MomentEntity
import com.wakeup.data.database.entity.PictureEntity
import kotlinx.coroutines.flow.Flow

interface MomentLocalDataSource {
    fun getMoments(): Flow<List<MomentEntity>>

    fun getLocation(momentId: Int): Flow<LocationEntity>

    fun getPictures(momentId: Int): Flow<List<PictureEntity>>

    fun getGlobes(momentId: Int): Flow<List<GlobeEntity>>
}