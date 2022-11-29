package com.wakeup.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.wakeup.data.database.entity.MomentGlobeXRef
import com.wakeup.data.database.entity.MomentPictureXRef

@Dao
interface RefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMomentPictureRefs(momentPictures: List<MomentPictureXRef>)

    @Insert
    suspend fun saveMomentGlobeRef(momentGlobe: MomentGlobeXRef)
}