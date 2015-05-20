package gg.zue.jmedeferred;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.SimpleBatchNode;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 * Created by MiZu on 20.05.2015.
 */
public class BatchNodeTest extends SimpleApplication {
    Spatial cube;
    BatchNode simpleBatchNode;
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(100f);
        cube = assetManager.loadModel("Models/brokenCube.j3o");
        for (int i = 0; i < cube.getNumControls(); i++) {
            cube.removeControl(cube.getControl(0));
        }
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        cube.setMaterial(material);
        material.setColor("Diffuse", new ColorRGBA(0.5f, 0.5f, 0.5f, 0f));

        addSphereGrid();

        simpleBatchNode.updateGeometricState();

        addPointLights();
        addAmbientLight();
    }

    void addAmbientLight() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.1f));
        rootNode.addLight(ambientLight);
    }

    private void addPointLights() {
        for (int i = 0; i < 10; i++) {
            PointLight pointLight = new PointLight();
            pointLight.setColor(ColorRGBA.randomColor());
            pointLight.setRadius(40);
            pointLight.setPosition(new Vector3f(FastMath.nextRandomFloat() * 12 * 20, 10, FastMath.nextRandomFloat() * 12 * 20));
            rootNode.addLight(pointLight);
        }
    }

    void addSphereGrid() {
        simpleBatchNode = new SimpleBatchNode();
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                Spatial clone = cube.clone();
                clone.setLocalTranslation(x * 12, 0, y * 12);
                simpleBatchNode.attachChild(clone);
            }
        }
        rootNode.attachChild(simpleBatchNode);
        simpleBatchNode.batch();

    }

    public static void main(String[] args) {
        new BatchNodeTest().start();
    }
}
