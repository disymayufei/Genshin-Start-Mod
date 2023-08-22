package cn.disy920.genshin_start.utils;

public class Color {

    private final int red;
    private final int green;
    private final int blue;

    public Color(int color) {
        red = (color >> 16) & 0xFF;
        green = (color >> 8) & 0xFF;
        blue = color & 0xFF;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}
