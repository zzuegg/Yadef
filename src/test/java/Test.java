import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Sphere;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by MiZu on 24.05.2015.
 */
public class Test {
    public static void main(String[] args) {
        Geometry geometry = new Geometry("PointLight", new Sphere(6, 6, 1));
        Mesh mesh = geometry.getMesh();
        FloatBuffer positionData = (FloatBuffer) mesh.getBuffer(VertexBuffer.Type.Position).getData();
        ShortBuffer indexData = (ShortBuffer) mesh.getBuffer(VertexBuffer.Type.Index).getData();
        Vector3f[] positions = new Vector3f[mesh.getVertexCount()];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = new Vector3f(positionData.get(i * 3), positionData.get(i * 3 + 1), positionData.get(i * 3 + 2));
        }
        int id[] = new int[indexData.limit()];
        for (int i = 0; i < id.length; i++) {
            id[i] = indexData.get(i);
        }
        System.out.println("Vector3f[] positions=new Vector3f[" + positions.length + "];");
        for (int i = 0; i < positions.length; i++) {
            System.out.println("positions[" + i + "]=new Vector3f(" + positions[i].x + "f," + positions[i].y + "f," + positions[i].z + "f);");
        }
        System.out.println("int[] indices=new int[]{");
        for (int i = 0; i < id.length; i++) {
            if (i != 0) {
                System.out.print(",");
            }
            System.out.print(id[i]);
        }
        System.out.println("}");
        System.out.println(id.length);
    }
}
