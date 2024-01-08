package moe.yare.render;

import java.util.Arrays;

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
        u = 1.0f - u;
        v = 1.0f - v;

        int y = (int) max(v * height - 1, 0);
        int x = (int) max(u * width - 1, 0);

        int color = rgb[y * width + x];
        return new Color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
    }

    public void emptyTexture(int width, int height) {
        this.width = width;
        this.height = height;
        this.rgb = new int[width * height];
    }

    public void fill(Color color) {
        Arrays.fill(rgb, color.rgb());
    }
}
