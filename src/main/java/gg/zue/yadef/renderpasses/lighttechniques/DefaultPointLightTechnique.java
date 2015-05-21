package gg.zue.yadef.renderpasses.lighttechniques;

import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Sphere;
import com.jme3.shader.VarType;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.LightTechnique;
import javafx.scene.AmbientLight;
import jme3tools.optimize.GeometryBatchFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by MiZu on 21.05.2015.
 */
public class DefaultPointLightTechnique implements LightTechnique<PointLight> {
    AssetManager assetManager;
    Material pointLightMaterial;
    Geometry pointLightGeometry;

    public DefaultPointLightTechnique(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.pointLightGeometry = generatePointLightMesh();
        this.pointLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/PointLight/PointLight.j3md");
    }

    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<PointLight> lightList) {
        gBuffer.passGBufferToShader(pointLightMaterial);
        renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        if (lightList.size() > 0) {
            Vector4f[] pointLightPositionRadius = new Vector4f[lightList.size()];
            Vector3f[] pointLightColors = new Vector3f[lightList.size()];
            int[] pointLightId = new int[lightList.size()];
            int count = 0;
            for (PointLight pointLight : lightList) {
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
        renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<PointLight> lightList) {
        RenderState forcedRenderState = renderManager.getForcedRenderState();
        RenderState renderState = new RenderState();
        renderState.setWireframe(true);
        renderManager.setForcedRenderState(renderState);
        //renderManager.getForcedRenderState().setDepthTest(true);
        if (lightList.size() > 0) {
            Vector4f[] pointLightPositionRadius = new Vector4f[lightList.size()];
            Vector3f[] pointLightColors = new Vector3f[lightList.size()];
            int[] pointLightId = new int[lightList.size()];
            int count = 0;
            for (PointLight pointLight : lightList) {
                Vector3f position = pointLight.getPosition();
                pointLightPositionRadius[count] = new Vector4f(position.x, position.y, position.z, 2);
                pointLightColors[count] = pointLight.getColor().toVector3f();
                pointLightId[count] = count;
                count++;

            }
            renderManager.setForcedMaterial(pointLightMaterial);
            renderManager.setForcedTechnique("DebugPointLights");
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
        renderManager.setForcedRenderState(forcedRenderState);
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
}
