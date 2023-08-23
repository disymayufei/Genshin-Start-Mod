package cn.disy920.genshin_start.mixin;

import cn.disy920.genshin_start.utils.GenshinUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinChat {

    @Inject(at = @At("HEAD"), method = "sendChatMessage")
    private void injectOnChatMessage(String message, CallbackInfo ci) {
        if (GenshinUtil.containGenshinStartCommand(message)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;

            if (player != null) {
                GenshinUtil.PROCESS_STATUS status = GenshinUtil.startGenshin();

                if (status == GenshinUtil.PROCESS_STATUS.STARTED) {
                    player.sendMessage(new LiteralText("原神已运行！").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)), true);
                }
                else if (status == GenshinUtil.PROCESS_STATUS.NOT_EXISTS) {
                    player.sendMessage(new LiteralText("您尚未安装原神！").setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true)), true);
                }
                else if (status == GenshinUtil.PROCESS_STATUS.FAILED_WITH_EXCEPTION) {
                    player.sendMessage(new LiteralText("原神启动出错！").setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true)), true);
                }
                else {
                    player.sendMessage(new LiteralText("原神启动中...").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true)), true);
                }
            }
        }
    }
}
