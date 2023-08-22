package cn.disy920.genshin_start.mixin;

import cn.disy920.genshin_start.access.ImageAccess;
import cn.disy920.genshin_start.utils.MemorySafeImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements ImageAccess {
    @Unique
    private MemorySafeImage screenshot = null;

    @Unique
    private long lastShotTime = System.currentTimeMillis();

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/TickDurationMonitor;create(Ljava/lang/String;)Lnet/minecraft/util/TickDurationMonitor;", shift = At.Shift.BEFORE))
    private void injectRun(CallbackInfo ci) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime > 500) {
            lastShotTime = currentTime;

            Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
            if (framebuffer != null) {
                NativeImage nativeScreenshot = ScreenshotRecorder.takeScreenshot(framebuffer);
                screenshot = new MemorySafeImage(nativeScreenshot);
                nativeScreenshot.close();
            }
        }
    }

    @Override
    public MemorySafeImage getScreenshot() {
        return screenshot;
    }
}
