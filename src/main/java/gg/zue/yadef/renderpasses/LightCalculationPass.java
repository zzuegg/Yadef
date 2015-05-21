package gg.zue.yadef.renderpasses;

import com.jme3.asset.AssetManager;
import com.jme3.light.*;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shader.VarType;
import com.jme3.util.TempVars;
import gg.zue.yadef.GBuffer;
import jme3tools.optimize.GeometryBatchFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by MiZu on 21.05.2015.
 */
public class LightCalculationPass {
    AssetManager assetManager;
    RenderState renderState;
    Material directionalLightMaterial, pointLightMaterial;
    private Geometry fsQuad, pointLightGeometry;

    public LightCalculationPass(AssetManager assetManager) {
        this.assetManager = assetManager;

        this.fsQuad = new Geometry("FsQuad", new Quad(1, 1));
        this.pointLightGeometry = generatePointLightMesh();

        this.directionalLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/DirectionalLight/DirectionalLight.j3md");
        this.pointLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/PointLight/PointLight.j3md");

        this.renderState = new RenderState();
        this.renderState.setStencil(true, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.TestFunction.Less, RenderState.TestFunction.Less);
        this.renderState.setBlendMode(RenderState.BlendMode.Additive);
        this.renderState.setDepthTest(false);
        this.renderState.setDepthWrite(false);
    }


    public void render(GBuffer gBuffer, RenderManager renderManager, ViewPort viewPort, RenderQueue renderQueue) {
        gBuffer.passGBufferToShader(directionalLightMaterial);
        gBuffer.passGBufferToShader(pointLightMaterial);
        renderManager.setForcedRenderState(renderState);

        ArrayList<AmbientLight> ambientLights = new ArrayList<>();
        ArrayList<PointLight> pointLights = new ArrayList<>();
        ArrayList<SpotLight> spotLights = new ArrayList<>();
        ArrayList<DirectionalLight> directionalLights = new ArrayList<>();
        getVisibleLights(ambientLights, pointLights, spotLights, directionalLights, viewPort.getScenes().get(0).getWorldLightList(), viewPort.getCamera());

        renderManager.getRenderer().setFrameBuffer(gBuffer.getLightFrameBuffer());

        //Ambient Light
        ColorRGBA ambientColor = new ColorRGBA(0, 0, 0, 0);
        ambientLights.forEach(ambientLight -> ambientColor.addLocal(ambientLight.getColor()));
        renderManager.getRenderer().setFrameBuffer(gBuffer.getLightFrameBuffer());
        renderManager.getRenderer().setBackgroundColor(ambientColor);
        renderManager.getRenderer().clearBuffers(true, false, false);

        //Directional Light
        if (directionalLights.size() > 0) {
            Vector3f[] dlDirection = new Vector3f[directionalLights.size()];
            Vector3f[] dlColor = new Vector3f[directionalLights.size()];
            int count = 0;
            for (DirectionalLight directionalLight : directionalLights) {
                dlDirection[count] = directionalLight.getDirection();
                dlColor[count] = directionalLight.getColor().toVector3f();
                count++;
            }

            directionalLightMaterial.setParam("lightCount", VarType.Int, directionalLights.size());
            directionalLightMaterial.setParam("lightDirections", VarType.Vector3Array, dlDirection);
            directionalLightMaterial.setParam("lightColors", VarType.Vector3Array, dlColor);
            renderManager.setForcedMaterial(directionalLightMaterial);
            renderManager.setForcedTechnique("CalculateDirectionalLights");
            renderManager.renderGeometry(fsQuad);
        }


        //Point Light
        renderState.setFaceCullMode(RenderState.FaceCullMode.Front);
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
            renderManager.setForcedMaterial(pointLightMaterial);
            renderManager.setForcedTechnique("CalculatePointLights");
            for (int i = 0; i < pointLightColors.length; ) {
                int size = Math.min(500, pointLightColors.length - i);
                Vector3f[] pointLightColorsTmp = Arrays.copyOfRange(pointLightColors, i, i + size);
                Vector4f[] pointLightPositionRadiusTmp = Arrays.copyOfRange(pointLightPositionRadius, i, i + size);
                i = i + size;
                pointLightMaterial.setParam("lightCount", VarType.Int, size - 1);
                pointLightMaterial.setParam("lightPositionRadius", VarType.Vector4Array, pointLightPositionRadiusTmp);
                pointLightMaterial.setParam("lightColors", VarType.Vector3Array, pointLightColorsTmp);
                renderManager.renderGeometry(pointLightGeometry);
            }
        }
        renderState.setFaceCullMode(RenderState.FaceCullMode.Back);

        //Spot lights
        //todo:


        renderManager.setForcedRenderState(null);
        renderManager.setForcedTechnique(null);
        renderManager.setForcedMaterial(null);
    }

    private Geometry generatePointLightMesh() {
        Mesh pointLightMesh = new Mesh();
        Geometry geometry = new Geometry("PointLight", new Sphere(6, 6, 1));
        System.out.println(geometry.getMesh().getTriangleCount());
        Collection<Geometry> plGeo = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            plGeo.add(geometry.clone());
        }
        GeometryBatchFactory.mergeGeometries(plGeo, pointLightMesh);
        Geometry pointLightPatch = new Geometry("PointLights", pointLightMesh);
        return pointLightPatch;
    }

    private void getVisibleLights(ArrayList<AmbientLight> ambientLights, ArrayList<PointLight> pointLights, ArrayList<SpotLight> spotLights, ArrayList<DirectionalLight> directionalLights, LightList lightlist, Camera camera) {
        TempVars tempVars = TempVars.get();
        for (Light light : lightlist) {
            switch (light.getType()) {
                case Directional:
                    directionalLights.add((DirectionalLight) light);
                    break;
                case Point:
                    if (light.intersectsFrustum(camera, tempVars)) {
                        pointLights.add((PointLight) light);
                    }
                    break;
                case Spot:
                    if (light.intersectsFrustum(camera, tempVars)) {
                        spotLights.add((SpotLight) light);
                    }
                    break;
                case Ambient:
                    ambientLights.add((AmbientLight) light);
                    break;
            }
        }
        tempVars.release();
    }
}
