package moe.yare;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObjParser {

    public static Model getModelFromFile(String filePath) throws IOException {
        List<Vector3f> vertices = parseVertices(filePath);
        List<Vector3f> normals = parseNormals(filePath);
        List<Vector2f> uvs = parseUVs(filePath);
        List<Triangle> tris = parseTris(filePath, uvs, normals);
        return new Model(vertices.toArray(new Vector3f[vertices.size()]), tris.toArray(new Triangle[tris.size()]));
    }

    private static List<Vector3f> parseVertices(String filePath) throws IOException {
        List<Vector3f> vertices = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] values = line.split("\\s+");
                    float x = Float.parseFloat(values[1]);
                    float y = Float.parseFloat(values[2]);
                    float z = Float.parseFloat(values[3]);
                    vertices.add(new Vector3f(x, y, z));
                }
            }
        }

        return vertices;
    }

    private static List<Vector2f> parseUVs(String filePath) throws IOException {
        List<Vector2f> UVs = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("vt ")) {
                    String[] values = line.split("\\s+");
                    float x = Float.parseFloat(values[1]);
                    float y = Float.parseFloat(values[2]);
                    UVs.add(new Vector2f(x, y));
                }
            }
        }

        return UVs;
    }

    private static List<Vector3f> parseNormals(String filePath) throws IOException {
        List<Vector3f> normals = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("vn ")) {
                    String[] values = line.split("\\s+");
                    float x = Float.parseFloat(values[1]);
                    float y = Float.parseFloat(values[2]);
                    float z = Float.parseFloat(values[3]);
                    normals.add(new Vector3f(x, y, z));
                }
            }
        }
        return normals;
    }

    private static List<Triangle> parseTris(String filePath, List<Vector2f> parsedUVs, List<Vector3f> parsedNormals) throws IOException {
        List<Triangle> triangles = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            Texture currentTexture = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("usemtl ")) {
                    String filename = line.split("\\s+")[1];
                    try {
                        currentTexture = parseTexturePNG(filename);
                    }
                    catch (IOException ex) {
                        System.err.println("Failed to load texture: " + filename);
                    }

                }
                if (line.startsWith("f ")) {

                    String[] values = line.split("\\s+");

                    if (values.length == 5) { //If there is 4 vertices in a face, we triangyliryem them
                        int[] face = new int[3];
                        Vector3f[] normals = new Vector3f[3];
                        Vector2f[] uvs = new Vector2f[3];
                        int k = 0;
                        for (int i = 1; k < 3; i++, k++) {
                            String[] vertexIndex = values[i].split("/");
                            int vertexIndexInt = Integer.parseInt(vertexIndex[0]) - 1;
                            int uvIndexInt = Integer.parseInt(vertexIndex[1]) - 1;
                            int normalIndexInt = Integer.parseInt(vertexIndex[2]) - 1;
                            face[k] = vertexIndexInt;
                            normals[k] = parsedNormals.get(normalIndexInt);
                            uvs[k] = parsedUVs.get(uvIndexInt);
                            if (i == 1) i++;
                        }

                        Vector3i vIndexes = new Vector3i(face[0], face[1], face[2]);
                        Triangle secondTri = new Triangle(vIndexes, new Color(255, 255, 255), normals);
                        secondTri.setTexture(currentTexture);
                        triangles.add(secondTri);
                    }
                    int[] face = new int[3];
                    Vector3f[] normals = new Vector3f[3];
                    Vector2f[] uvs = new Vector2f[3];
                    for (int i = 1; i <= 3; i++) {
                        String[] vertexIndex = values[i].split("/");
                        int vertexIndexInt = Integer.parseInt(vertexIndex[0]) - 1;
                        int uvIndexInt = Integer.parseInt(vertexIndex[1]) - 1;
                        int normalIndexInt = Integer.parseInt(vertexIndex[2]) - 1;
                        face[i-1] = vertexIndexInt;
                        normals[i-1] = parsedNormals.get(normalIndexInt);
                        uvs[i-1] = parsedUVs.get(uvIndexInt);
                    }

                    Vector3i vIndexes = new Vector3i(face[0], face[1], face[2]);
                    Triangle firstTri = new Triangle(vIndexes, new Color(255, 255, 255), normals);
                    firstTri.setTexture(currentTexture);
                    triangles.add(firstTri);
                }
            }
        }

        return triangles;
    }

    private static Texture parseTexturePNG(String filename) throws IOException {
        BufferedImage image = ImageIO.read(new File(filename));

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                pixels[y * width + x] = pixel;
            }
        }
        return new Texture(width, height, pixels);
    }
}
