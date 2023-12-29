package moe.yare;

import static java.lang.Math.max;

public class Texture {

    private int width;
    private int height;
    private int[] rgb;

    public Texture(int width, int height, int[] rgb) {
        this.width = width;
        this.height = height;
        this.rgb = rgb;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getRGB() {
        return rgb;
    }

    public Color getTexel(float u, float v) {
        int color = rgb[(int) max(v * height - 1, 0) * width + (int) max(u * width - 1, 0)];

        return new Color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
    }
}
