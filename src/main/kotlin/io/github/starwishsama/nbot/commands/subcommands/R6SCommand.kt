package io.github.starwishsama.nbot.commands.subcommands

import cn.hutool.core.util.StrUtil
import io.github.starwishsama.nbot.commands.CommandProps
import io.github.starwishsama.nbot.commands.interfaces.UniversalCommand
import io.github.starwishsama.nbot.enums.UserLevel
import io.github.starwishsama.nbot.objects.BotUser
import io.github.starwishsama.nbot.util.BotUtils.getLocalMessage
import io.github.starwishsama.nbot.util.BotUtils.isLegitId
import io.github.starwishsama.nbot.util.BotUtils.isNoCoolDown
import io.github.starwishsama.nbot.util.BotUtils.sendLocalMessage
import io.github.starwishsama.nbot.util.R6SUtils.getR6SInfo
import net.mamoe.mirai.message.ContactMessage
import net.mamoe.mirai.message.GroupMessage
import net.mamoe.mirai.message.data.*

class R6SCommand : UniversalCommand {
    override suspend fun execute(message: ContactMessage, args: List<String>, user: BotUser): MessageChain {
        if (args.isNotEmpty() && isNoCoolDown(message.sender.id, 25) && message is GroupMessage) {
            when (args[1].toLowerCase()) {
                "info", "查询" -> {
                    return if (user.r6sAccount != null && args.size == 2){
                        message.reply(sendLocalMessage("msg.bot-prefix", "查询中..."))
                        val result = getR6SInfo(user.r6sAccount!!)
                        message.sender.at().plus("\n" + result.toMessage())
                    } else if (args.size == 3 && args[2].isNotEmpty() && isLegitId(args[2])){
                        message.reply(sendLocalMessage("msg.bot-prefix", "查询中..."))
                        val result = getR6SInfo(args[2])
                        message.sender.at().plus("\n" + result.toMessage())
                    } else {
                        ("${getLocalMessage("msg.bot-prefix")} /r6 查询 [ID] 或者 /r6 绑定 [id]\n" +
                                "绑定彩虹六号账号 无需输入ID快捷查询游戏数据").toMessage().asMessageChain()
                    }
                }
                "bind", "绑定" ->
                    if (StrUtil.isNotEmpty(args[2]) && args.size == 3) {
                        if (isLegitId(args[1])) {
                            if (BotUser.isUserExist(message.sender.id)) {
                                val botUser1 = BotUser.getUser(message.sender.id)
                                if (botUser1 != null) {
                                    botUser1.r6sAccount = args[2]
                                    return (getLocalMessage("msg.bot-prefix") + "绑定成功!").toMessage().asMessageChain()
                                }
                            } else return (getLocalMessage("msg.bot-prefix") + "使用 /qd 签到自动注册机器人系统").toMessage().asMessageChain()
                        } else return (getLocalMessage("msg.bot-prefix") + "ID 格式有误!").toMessage().asMessageChain()
                    }
                else -> return (getLocalMessage("msg.bot-prefix") + "/r6s info [Uplay账号名]").toMessage().asMessageChain()
            }
        }
        return EmptyMessageChain
    }

    override fun getProps(): CommandProps = CommandProps("r6", arrayListOf("r6s", "彩六"), "nbot.commands.r6s", UserLevel.USER)
    override fun getHelp(): String = """
        ======= 命令帮助 =======
        /r6 info [Uplay账号名] 查询战绩
        /r6 bind [Uplay账号名] 绑定账号
        /r6 info 查询战绩 (需要绑定账号)
    """.trimIndent()
}