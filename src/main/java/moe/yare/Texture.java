package moe.yare;

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

    //TODO: getTexel
}
