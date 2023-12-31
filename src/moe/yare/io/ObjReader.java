package moe.yare.io;

import moe.yare.render.Model;
import moe.yare.render.Texture;
import moe.yare.render.Triangle;
import moe.yare.math.Vector2f;
import moe.yare.math.Vector3f;
import moe.yare.math.Vector3i;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;

public class ObjReader {

    public static Model readModel(String filepath) {
        LinkedList<Vector3f> vertices = new LinkedList<>();
        LinkedList<Vector2f> uvs = new LinkedList<>();
        LinkedList<Vector3f> normals = new LinkedList<>();
        LinkedList<Triangle> triangles = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            Texture currentTexture = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] values = line.split("\\s+");
                    float x = Float.parseFloat(values[1]);
                    float y = Float.parseFloat(values[2]);
                    float z = Float.parseFloat(values[3]);
                    vertices.add(new Vector3f(x, y, z));
                    continue;
                }

                if (line.startsWith("vt ")) {
                    String[] values = line.split("\\s+");
                    float x = Float.parseFloat(values[1]);
                    float y = Float.parseFloat(values[2]);
                    uvs.add(new Vector2f(x, y));
                    continue;
                }

                if (line.startsWith("vn ")) {
                    String[] values = line.split("\\s+");
                    float x = Float.parseFloat(values[1]);
                    float y = Float.parseFloat(values[2]);
                    float z = Float.parseFloat(values[3]);
                    normals.add(new Vector3f(x, y, z));
                    continue;
                }

                //TODO MTL reading
//                if (line.startsWith("usemtl ")) {
//                    String filename = line.split("\\s+")[1];
//                    try {
//                        currentTexture = TextureReader.loadTexture(Path.of(filepath)
//                                .toAbsolutePath()
//                                .getParent()
//                                .resolve(filename)
//                                .toAbsolutePath()
//                                .toString());
//                    }
//                    catch (Exception ex) {
//                        currentTexture = null;
//                        ex.printStackTrace();
//                    }
//                    continue;
//                }

                if (line.startsWith("f ")) {
                    readTriangle(line, currentTexture, uvs, normals, triangles);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Model(vertices.toArray(new Vector3f[0]), triangles.toArray(new Triangle[0]));
    }

    private static void readTriangle(String line,
                                     Texture currentTexture,
                                     LinkedList<Vector2f> uvs,
                                     LinkedList<Vector3f> normals,
                                     LinkedList<Triangle> triangles) {
        String[] values = line.split("\\s+");

        if (values.length == 5) {
            int[] face = new int[3];
            Vector2f[] triangleUvs = null;
            Vector3f[] triangleNormals = null;
            int k = 0;
            for (int i = 1; k < 3; i++, k++) {
                String[] vertexIndex = values[i].split("/");
                face[k] = Integer.parseInt(vertexIndex[0]) - 1;
                if (vertexIndex.length > 1 && !vertexIndex[1].isBlank()) {
                    if (triangleUvs == null) triangleUvs = new Vector2f[3];
                    triangleUvs[k] = uvs.get(Integer.parseInt(vertexIndex[1]) - 1);
                }
                if (vertexIndex.length > 2 && !vertexIndex[2].isBlank()) {
                    if (triangleNormals == null) triangleNormals = new Vector3f[3];
                    triangleNormals[k] = normals.get(Integer.parseInt(vertexIndex[2]) - 1);
                }
                if (i == 1) i++;
            }

            triangles.add(new Triangle(new Vector3i(face[0], face[1], face[2]), null,
                    triangleNormals, currentTexture, triangleUvs));
        }

        int[] face = new int[3];
        Vector2f[] triangleUvs = null;
        Vector3f[] triangleNormals = null;
        for (int i = 1; i <= 3; i++) {
            String[] vertexIndex = values[i].split("/");
            face[i-1] = Integer.parseInt(vertexIndex[0]) - 1;
            if (vertexIndex.length > 1 && !vertexIndex[1].isBlank()) {
                if (triangleUvs == null) triangleUvs = new Vector2f[3];
                triangleUvs[i-1] = uvs.get(Integer.parseInt(vertexIndex[1]) - 1);
            }
            if (vertexIndex.length > 2 && !vertexIndex[2].isBlank()) {
                if (triangleNormals == null) triangleNormals = new Vector3f[3];
                triangleNormals[i-1] = normals.get(Integer.parseInt(vertexIndex[2]) - 1);
            }
        }

        triangles.add(new Triangle(new Vector3i(face[0], face[1], face[2]), null,
                triangleNormals, currentTexture, triangleUvs));
    }
}
