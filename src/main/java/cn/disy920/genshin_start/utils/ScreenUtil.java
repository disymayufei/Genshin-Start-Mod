package cn.disy920.genshin_start.utils;

import cn.disy920.genshin_start.access.ImageAccess;
import net.minecraft.client.MinecraftClient;

public class ScreenUtil {

    /**
     * 判断当前屏幕是否有足够的白屏范围
     * @return 是否有足够的白屏范围
     */
    public boolean isWhiteScreen() {
        MemorySafeImage image = ((ImageAccess)(MinecraftClient.getInstance())).getScreenshot();

        if (image == null) {
            return false;
        }

        double area = image.getHeight() * image.getWidth();
        int whitePixelNum = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color imageColor = image.getColor(x, y);

                if (imageColor.getRed() > 200 && imageColor.getGreen() > 200 && imageColor.getBlue() > 200) {
                    whitePixelNum++;
                }
            }
        }

        return whitePixelNum / area > 0.82;
    }
}
