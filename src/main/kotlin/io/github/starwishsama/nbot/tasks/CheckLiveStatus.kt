package io.github.starwishsama.nbot.tasks

import io.github.starwishsama.nbot.BotConstants
import io.github.starwishsama.nbot.BotMain
import io.github.starwishsama.nbot.api.bilibili.DynamicApi
import io.github.starwishsama.nbot.api.bilibili.FakeClientApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

object CheckLiveStatus : Runnable {
    /** 推送过的直播间列表, 避免重复推送 */
    private val pushedList = mutableListOf<Long>()

    override fun run() {
        if (BotConstants.cfg.subList.isNotEmpty() && BotConstants.cfg.pushGroups.isNotEmpty()) {
            BotConstants.cfg.subList.forEach { roomId ->
                val data = runBlocking {
                    FakeClientApi.getLiveRoom(roomId)?.data
                }

                if (data != null) {
                    when (data.liveStatus) {
                        0 -> {
                            /** 如果下播了就删除, 等待下一次开播后做提醒 */
                            if (pushedList.contains(roomId)) {
                                pushedList.remove(roomId)
                            }
                        }
                        1 -> {
                            if (!pushedList.contains(roomId)) {
                                val msg =
                                    "单推助手 > \n${DynamicApi.getUserNameByMid(data.uid)} 开播了!\n标题: ${data.title}\n开播时间: ${data.liveTime}\n传送门: https://live.bilibili.com/${data.roomId}"
                                BotMain.bot.groups.forEach { group ->
                                    runBlocking {
                                        if (BotConstants.cfg.pushGroups.contains(group.id)) {
                                            group.sendMessage(msg)
                                        }
                                        /** 防止消息发送失败 */
                                        delay(1000)
                                    }
                                }
                            }
                            pushedList.add(roomId)
                        }
                    }
                }
            }
        }
    }
}