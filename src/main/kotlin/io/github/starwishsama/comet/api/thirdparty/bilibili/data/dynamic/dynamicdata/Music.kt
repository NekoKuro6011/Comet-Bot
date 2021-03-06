package io.github.starwishsama.comet.api.thirdparty.bilibili.data.dynamic.dynamicdata

import com.google.gson.annotations.SerializedName
import io.github.starwishsama.comet.api.thirdparty.bilibili.data.dynamic.DynamicData
import io.github.starwishsama.comet.objects.wrapper.MessageWrapper

data class Music(var id: Long,
                 @SerializedName("cover")
                 var coverURL: String?,
                 @SerializedName("intro")
                 var dynamic: String) : DynamicData {
    override suspend fun getContact(): MessageWrapper {
        val wrapped = MessageWrapper("发布了音乐 $dynamic\n")

        coverURL.let {
            if (it != null) {
                try {
                    wrapped.plusImageUrl(it)
                } catch (e: UnsupportedOperationException) {
                    return@let
                }
            }
        }

        return wrapped
    }
}