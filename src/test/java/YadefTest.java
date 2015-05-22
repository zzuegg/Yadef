import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.SimpleBatchNode;
import com.jme3.scene.shape.Box;
import gg.zue.yadef.DeferredRenderer;


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

        //addAmbientLight();
        //addDirectionalLights();
        //addPointLights(100);
        addSpotLights(10);
        //addFPSFLashLight();

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

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        moveFlashLight();
    }


    private SpotLight flashLight;

    private void addFPSFLashLight() {
        flashLight = new SpotLight();
        flashLight.setSpotInnerAngle(6 * FastMath.DEG_TO_RAD);
        flashLight.setSpotOuterAngle(10 * FastMath.DEG_TO_RAD);
        flashLight.setColor(ColorRGBA.White);
        flashLight.setSpotRange(300f);
        rootNode.addLight(flashLight);
    }

    private void moveFlashLight() {
        if (flashLight != null) {
            flashLight.setDirection(cam.getDirection());
            flashLight.setPosition(cam.getLocation().add(flashLight.getDirection().mult(5)));
        }
    }

    private void addSpotLights(int count) {
        for (int i = 0; i < count; i++) {
            SpotLight pointLight = new SpotLight();
            pointLight.setColor(ColorRGBA.randomColor().mult(2));
            pointLight.setDirection(new Vector3f(FastMath.nextRandomFloat() * -1, FastMath.nextRandomFloat() * -1, FastMath.nextRandomFloat() * -1).normalize());
            pointLight.setPosition(new Vector3f(FastMath.nextRandomFloat() * 12 * 20, 50, FastMath.nextRandomFloat() * 12 * 20));
            float v = FastMath.abs(FastMath.nextRandomFloat() * 20) + 4;
            System.out.println(v);
            v = 20;
            float v1 = 15;//Math.max(v - 4, 1;
            pointLight.setSpotOuterAngle(v * FastMath.DEG_TO_RAD);
            pointLight.setSpotInnerAngle(v1 * FastMath.DEG_TO_RAD);
            rootNode.addLight(pointLight);
        }
    }

    private void addPointLights(int count) {
        for (int i = 0; i < count; i++) {
            PointLight pointLight = new PointLight();
            pointLight.setColor(ColorRGBA.randomColor().mult(2));
            pointLight.setRadius(FastMath.nextRandomFloat() * 30);
            pointLight.setPosition(new Vector3f(FastMath.nextRandomFloat() * 12 * 20, 10, FastMath.nextRandomFloat() * 12 * 20));
            rootNode.addLight(pointLight);
        }
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

    void addSingleSphere() {
        rootNode.attachChild(cube);
    }

    void addSphereGrid() {
        SimpleBatchNode simpleBatchNode = new SimpleBatchNode();
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                Geometry clone = cube.clone();
                clone.setLocalTranslation(x * 12, FastMath.nextRandomFloat() * 10, y * 12);
                simpleBatchNode.attachChild(clone);
            }
        }
        simpleBatchNode.batch();
        rootNode.attachChild(simpleBatchNode);
        simpleBatchNode.updateGeometricState();
    }

    public static void main(String[] args) {
        new YadefTest().start();
    }
}
