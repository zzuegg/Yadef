package gg.zue.jmedeferred;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.*;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shader.VarType;
import com.jme3.texture.FrameBuffer;
import com.jme3.util.BufferUtils;
import com.jme3.util.TempVars;
import jme3tools.optimize.GeometryBatchFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by MiZu on 19.05.2015.
 */
public class DeferredSceneprocessor implements SceneProcessor {
    private RenderManager renderManager;
    private ViewPort viewPort;
    private GBuffer gBuffer;
    private boolean initialized = false;

    private FrameBuffer outputFramebuffer;

    private AssetManager assetManager;
    private Geometry resolveQuad;
    private Material resolveMaterial;
    private Material debugMaterial;

    private Geometry pointLightPatch;

    private boolean usePointLightGeometryShader = false;

    public DeferredSceneprocessor(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.resolveQuad = new Geometry("FsQuad", new Quad(1, 1));
        this.resolveMaterial = new Material(assetManager, "Materials/Deferred/DeferredLogic.j3md");
        this.debugMaterial = new Material(assetManager, "Materials/Deferred/Debug/DeferredDebug.j3md");
        this.resolveQuad.setMaterial(resolveMaterial);

        generatePointLightMesh();
    }

    private void generatePointLightMesh() {
        Mesh pointLightMesh = new Mesh();
        Geometry geometry = new Geometry("PointLight", new Sphere(6, 6, 1));
        System.out.println(geometry.getMesh().getVertexCount());
        Collection<Geometry> plGeo = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            plGeo.add(geometry.clone());
        }
        GeometryBatchFactory.mergeGeometries(plGeo, pointLightMesh);
        pointLightPatch = new Geometry("PointLights", pointLightMesh);
        pointLightPatch.setMaterial(resolveMaterial);
    }

    @Override
    public void initialize(RenderManager renderManager, ViewPort viewPort) {
        this.renderManager = renderManager;
        this.viewPort = viewPort;
        this.gBuffer = new GBuffer();
        reshape(viewPort, viewPort.getCamera().getWidth(), viewPort.getCamera().getHeight());
        initialized = true;
    }

    @Override
    public void reshape(ViewPort viewPort, int width, int height) {
        gBuffer.reshape(height, width);
        gBuffer.passGBufferToShader(resolveMaterial);
        gBuffer.passGBufferToShader(debugMaterial);
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void preFrame(float v) {

    }

    @Override
    public void postQueue(RenderQueue renderQueue) {
        outputFramebuffer = viewPort.getOutputFrameBuffer();

        renderDeferredPass();
        renderLightCalculationPass();
        renderResolvePass();

        cleanupAfterDeferredPass();
        renderDebugScreen();
    }

    private void renderDebugScreen() {
        renderManager.setForcedMaterial(debugMaterial);
        renderManager.setForcedTechnique("DeferredDebug");
        renderManager.renderGeometry(resolveQuad);
        renderManager.setForcedMaterial(null);
        renderManager.setForcedTechnique(null);
    }

    private void cleanupAfterDeferredPass() {
        renderManager.setForcedTechnique(null);
        renderManager.setForcedMaterial(null);
        renderManager.setForcedRenderState(null);
    }

    private void renderResolvePass() {
        renderManager.getRenderer().setFrameBuffer(outputFramebuffer);
        renderManager.setForcedMaterial(resolveMaterial);
        renderManager.setForcedTechnique("DeferredResolve");
        renderManager.renderGeometry(resolveQuad);
    }
    long ms;
    private void renderLightCalculationPass() {
        if(ms<System.currentTimeMillis()){
            usePointLightGeometryShader=!usePointLightGeometryShader;
            System.out.println(usePointLightGeometryShader);
            ms=System.currentTimeMillis()+5000;
        }
        ArrayList<DirectionalLight> directionalLights = new ArrayList<>();
        ArrayList<PointLight> pointLights = new ArrayList<>();
        ColorRGBA ambientLight = new ColorRGBA(0, 0, 0, 0);
        TempVars tempVars = TempVars.get();
        for (Spatial spatial : viewPort.getScenes()) {
            for (Light light : spatial.getWorldLightList()) {
                if (light instanceof DirectionalLight) {
                    directionalLights.add((DirectionalLight) light);
                } else if (light instanceof AmbientLight) {
                    ambientLight.addLocal(light.getColor());
                } else if (light instanceof PointLight) {
                    if (light.intersectsFrustum(viewPort.getCamera(), tempVars)) {
                        pointLights.add((PointLight) light);
                    }
                }
            }
        }
        tempVars.release();
        renderManager.getRenderer().setFrameBuffer(gBuffer.getLightFrameBuffer());
        renderManager.getRenderer().setBackgroundColor(ambientLight);
        renderManager.getRenderer().clearBuffers(true, false, false);
        RenderState renderState = new RenderState();
        renderState.setBlendMode(RenderState.BlendMode.Additive);
        renderState.setDepthTest(false);
        renderState.setDepthWrite(false);
        renderManager.setForcedRenderState(renderState);


        if (directionalLights.size() > 0) {
            Vector3f[] dlDirection = new Vector3f[directionalLights.size()];
            Vector3f[] dlColor = new Vector3f[directionalLights.size()];
            int count = 0;
            for (DirectionalLight directionalLight : directionalLights) {
                dlDirection[count] = directionalLight.getDirection();
                dlColor[count] = directionalLight.getColor().toVector3f();
                count++;
            }

            resolveMaterial.setParam("lightCount", VarType.Int, directionalLights.size());
            resolveMaterial.setParam("directionalLightDirections", VarType.Vector3Array, dlDirection);
            resolveMaterial.setParam("directionalLightColors", VarType.Vector3Array, dlColor);
            renderManager.setForcedMaterial(resolveMaterial);
            renderManager.setForcedTechnique("CalculateDirectionalLights");
            renderManager.renderGeometry(resolveQuad);
        }

        if (pointLights.size() > 0) {
            Vector4f[] pointLightPositionRadius = new Vector4f[pointLights.size()];
            Vector3f[] pointLightColors = new Vector3f[pointLights.size()];
            int[] pointLightId = new int[pointLights.size()];
            int count = 0;
            for (PointLight pointLight : pointLights) {

                Vector3f position = pointLight.getPosition();
                pointLightPositionRadius[count] = new Vector4f(position.x, position.y, position.z, pointLight.getRadius());
                pointLightColors[count] = pointLight.getColor().toVector3f();
                pointLightId[count] = count;
                count++;

            }
            if (usePointLightGeometryShader) {
                Mesh mesh = new Mesh();
                mesh.setBuffer(VertexBuffer.Type.Position, 4, BufferUtils.createFloatBuffer(pointLightPositionRadius));
                mesh.setBuffer(VertexBuffer.Type.Color, 3, BufferUtils.createFloatBuffer(pointLightColors));
                mesh.setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createIntBuffer(pointLightId));
                mesh.setMode(Mesh.Mode.Points);
                renderState.setFaceCullMode(RenderState.FaceCullMode.Front);
                renderManager.setForcedMaterial(resolveMaterial);
                renderManager.setForcedTechnique("CalculatePointLightsGeo");
                Geometry geometry = new Geometry("PointLights", mesh);
                renderManager.renderGeometry(geometry);
            } else {
                renderState.setFaceCullMode(RenderState.FaceCullMode.Front);
                renderManager.setForcedMaterial(resolveMaterial);
                renderManager.setForcedTechnique("CalculatePointLights");
                for (int i = 0; i < pointLightColors.length; i = i + 500) {
                    int size = Math.min(i + 500, pointLightColors.length);
                    Vector3f[] pointLightColorsTmp = Arrays.copyOfRange(pointLightColors, i, size);
                    Vector4f[] pointLightPositionRadiusTmp = Arrays.copyOfRange(pointLightPositionRadius, i, size);
                    resolveMaterial.setParam("lightCount", VarType.Int, size);
                    resolveMaterial.setParam("pointLightPositionRadius", VarType.Vector4Array, pointLightPositionRadiusTmp);
                    resolveMaterial.setParam("pointLightColors", VarType.Vector3Array, pointLightColorsTmp);
                    renderManager.renderGeometry(pointLightPatch);
                }
            }
        }
        renderManager.setForcedRenderState(null);
    }

    private void renderDeferredPass() {
        renderManager.getRenderer().setFrameBuffer(gBuffer.getRenderFrameBuffer());
        renderManager.getRenderer().setBackgroundColor(new ColorRGBA(Float.NaN, Float.NaN, Float.NaN, Float.NaN));
        renderManager.getRenderer().clearBuffers(true, true, true);
        renderManager.renderViewPortQueues(viewPort, true);
    }

    @Override
    public void postFrame(FrameBuffer frameBuffer) {

    }

    @Override
    public void cleanup() {

    }
}
