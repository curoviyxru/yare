package moe.yare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObjParser {

    public static Model getModelFromFile(String filePath) throws IOException {
        List<Vector3f> vertices = parseVertices(filePath);
        List<Vector3i> faces = parseFaces(filePath);
        return new Model(vertices.toArray(new Vector3f[vertices.size()]), faces.toArray(new Vector3i[faces.size()]));
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

    private static List<Vector3i> parseFaces(String filePath) throws IOException {
        List<Vector3i> faces = new ArrayList<>();
        Path path = Paths.get(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("f ")) {
                    String[] values = line.split("\\s+");
                    int[] face = new int[3];
                    for (int i = 1; i <= 3; i++) {
                        String[] vertexIndex = values[i].split("/");
                        int vertexIndexInt = Integer.parseInt(vertexIndex[0]) - 1;
                        int uvIndexInt = Integer.parseInt(vertexIndex[1]) - 1;
                        int normalIndexInt = Integer.parseInt(vertexIndex[2]) - 1;
                        face[i-1] = vertexIndexInt;
                    }
                    faces.add(new Vector3i(face[0], face[1], face[2]));
                }
            }
        }

        return faces;
    }
}
