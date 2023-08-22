package cn.disy920.genshin_start.utils;

import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MemorySafeImage {
    private final int width;
    private final int height;
    private final List<List<Color>> pixel;

    public MemorySafeImage(@NotNull NativeImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();

        pixel = new ArrayList<>(height);
        for (int y = 0; y < height; y++) {
            List<Color> linePixel = new ArrayList<>(width);
            for (int x = 0; x < width; x++) {
                linePixel.add(new Color(image.getColor(x, y)));
            }

            pixel.add(linePixel);
        }
    }

    public Color getColor(int x, int y) {
        return pixel.get(y).get(x);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
