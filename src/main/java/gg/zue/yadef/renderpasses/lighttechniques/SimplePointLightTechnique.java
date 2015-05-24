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
import jme3tools.optimize.GeometryBatchFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by MiZu on 21.05.2015.
 */
public class SimplePointLightTechnique implements LightTechnique<PointLight> {
    AssetManager assetManager;
    Material pointLightMaterial;
    Geometry pointLightGeometry;

    public SimplePointLightTechnique(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.pointLightGeometry = generatePointLightMesh(1);
        this.pointLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/PointLight/PointLight.j3md");
    }

    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<PointLight> lightList) {
        gBuffer.passGBufferToShader(pointLightMaterial);
        renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        for (PointLight pointLight : lightList) {
            Vector3f position = pointLight.getPosition();
            pointLightMaterial.setParam("lightCount", VarType.Int, 1);
            pointLightMaterial.setParam("lightPositionRadius", VarType.Vector4Array, new Vector4f[]{new Vector4f(position.x, position.y, position.z, pointLight.getRadius())});
            pointLightMaterial.setParam("lightColors", VarType.Vector3Array, new Vector3f[]{pointLight.getColor().toVector3f()});
            pointLightGeometry.setMaterial(pointLightMaterial);
            renderManager.renderGeometry(pointLightGeometry);
        }
        renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<PointLight> lightList) {
        RenderState forcedRenderState = renderManager.getForcedRenderState();
        RenderState renderState = new RenderState();
        renderState.setWireframe(true);
        renderManager.setForcedRenderState(renderState);
        renderManager.setForcedTechnique("DebugPointLights");
        for (PointLight pointLight : lightList) {
            Vector3f position = pointLight.getPosition();
            pointLightMaterial.setParam("lightCount", VarType.Int, 1);
            pointLightMaterial.setParam("lightPositionRadius", VarType.Vector4Array, new Vector4f[]{new Vector4f(position.x, position.y, position.z, pointLight.getRadius())});
            pointLightMaterial.setParam("lightColors", VarType.Vector3Array, new Vector3f[]{pointLight.getColor().toVector3f()});
            pointLightGeometry.setMaterial(pointLightMaterial);
            renderManager.renderGeometry(pointLightGeometry);
        }
        renderManager.setForcedRenderState(forcedRenderState);
    }

    private Geometry generatePointLightMesh(int count) {
        Mesh pointLightMesh = new Mesh();
        Geometry geometry = new Geometry("PointLight", new Sphere(6, 6, 1));
        System.out.println(geometry.getMesh().getTriangleCount());
        Collection<Geometry> plGeo = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            plGeo.add(geometry.clone());
        }
        GeometryBatchFactory.mergeGeometries(plGeo, pointLightMesh);
        Geometry pointLightPatch = new Geometry("PointLights", pointLightMesh);
        return pointLightPatch;
    }
}
