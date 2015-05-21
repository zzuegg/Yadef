package gg.zue.yadef;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.SimpleBatchNode;
import com.jme3.scene.shape.Box;



/**
 * Created by MiZu on 19.05.2015.
 */
public class YadefTest extends SimpleApplication {
    Geometry cube;

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(100f);
        cube = new Geometry("Cube", new Box(4, 4, 4));
        cam.setFrustumFar(10000);

        initDeferred();
        //initRegular();

        addAmbientLight();
        addDirectionalLights();
        addPointLights();
        //addSingleSphere();
        addSphereGrid();

        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap", assetManager.loadTexture("Textures/diffuse.jpg"));
        material.setTexture("NormalMap", assetManager.loadTexture("Textures/normal.jpg"));
        Geometry clone = cube.clone();
        clone.setMaterial(material);
        clone.setQueueBucket(RenderQueue.Bucket.Translucent);
        rootNode.attachChild(clone);
        clone.move(0, 20, 0);
    }

    private void addPointLights() {
        for (int i = 0; i < 100; i++) {
            PointLight pointLight = new PointLight();
            pointLight.setColor(ColorRGBA.randomColor());
            pointLight.setRadius(40);
            pointLight.setPosition(new Vector3f(FastMath.nextRandomFloat() * 12 * 20, 10, FastMath.nextRandomFloat() * 12 * 20));
            rootNode.addLight(pointLight);
        }
    }

    void addSingleSphere() {
        rootNode.attachChild(cube);
    }

    void addSphereGrid() {
        SimpleBatchNode simpleBatchNode = new SimpleBatchNode();
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                Geometry clone = cube.clone();
                /*clone.addControl(new AbstractControl() {
                    float s = FastMath.nextRandomFloat() * 10;
                    float up = 1;

                    @Override
                    protected void controlUpdate(float v) {
                        s += up * (v * 4);
                        if (s > 10) {
                            s = 10 - (s - 10);
                            up = -1;
                        }
                        if (s < 0) {
                            s = s * -1;
                            up = 1;
                        }
                        Vector3f pos = getSpatial().getLocalTranslation();
                        pos.y = s;
                        getSpatial().setLocalTranslation(pos);
                    }

                    @Override
                    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

                    }
                });*/
                clone.setLocalTranslation(x * 12, FastMath.nextRandomFloat() * 10, y * 12);
                simpleBatchNode.attachChild(clone);
            }
        }
        simpleBatchNode.batch();
        rootNode.attachChild(simpleBatchNode);
        simpleBatchNode.updateGeometricState();
    }

    void addAmbientLight() {
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.1f));
        rootNode.addLight(ambientLight);
    }

    void addDirectionalLights() {
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setDirection(new Vector3f(-1, 0.5f, 0).normalize());
        directionalLight.setColor(ColorRGBA.White.mult(0.5f));
        rootNode.addLight(directionalLight);
    }

    void initDeferred() {
        for (SceneProcessor sceneProcessor : viewPort.getProcessors()) {
            viewPort.removeProcessor(sceneProcessor);
        }

        viewPort.addProcessor(new DeferredRenderer(this));
        Material material = new Material(assetManager, "Materials/yadef/Deferred/Deferred.j3md");
        material.setTexture("diffuseTexture", assetManager.loadTexture("Textures/diffuse.jpg"));
        material.setTexture("normalTexture", assetManager.loadTexture("Textures/normal.jpg"));
        cube.setMaterial(material);
    }

    void initRegular() {
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap", assetManager.loadTexture("Textures/diffuse.jpg"));
        material.setTexture("NormalMap", assetManager.loadTexture("Textures/normal.jpg"));
        cube.setMaterial(material);

        cube.getMaterial().setColor("Diffuse", new ColorRGBA(0.5f, 0.5f, 0.5f, 0f));
    }

    public static void main(String[] args) {
        new YadefTest().start();
    }
}
