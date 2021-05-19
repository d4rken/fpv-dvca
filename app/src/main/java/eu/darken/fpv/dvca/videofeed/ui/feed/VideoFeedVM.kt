package eu.darken.fpv.dvca.videofeed.ui.feed

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.darken.fpv.dvca.common.viewmodel.SmartVM
import eu.darken.fpv.dvca.gear.GearManager
import eu.darken.fpv.dvca.gear.goggles.djifpv.FpvGogglesV1
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VideoFeedVM @Inject constructor(
    private val handle: SavedStateHandle,
    private val gearManager: GearManager
) : SmartVM() {

    private val goggleFeed = gearManager.availableGear
        .map {
            it.singleOrNull() as? FpvGogglesV1
        }
        .filterNotNull()
        .onEach {
            Timber.tag(TAG).d("Device available: %s", it.logId)
            if (it.videoFeed.first() == null) {
                Timber.tag(TAG).d("Enabling videofeed for %s", it.logId)
                it.startVideoFeed()
            }
        }

    val feedAvailability = goggleFeed
        .flatMapMerge { it.videoFeed }
        .onEach { Timber.tag(TAG).d("Videofeed: %s", it) }
        .asLiveData2()

}