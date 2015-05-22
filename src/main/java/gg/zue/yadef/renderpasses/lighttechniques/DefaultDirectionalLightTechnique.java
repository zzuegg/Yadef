package gg.zue.yadef.renderpasses.lighttechniques;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.shader.VarType;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.LightTechnique;

import java.util.ArrayList;

/**
 * Created by MiZu on 21.05.2015.
 */
public class DefaultDirectionalLightTechnique implements LightTechnique<DirectionalLight> {
    AssetManager assetManager;
    private Geometry fsQuad;
    private Material directionalLightMaterial;

    public DefaultDirectionalLightTechnique(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.fsQuad = new Geometry("FsQuad", new Quad(1, 1));
        this.directionalLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/DirectionalLight/DirectionalLight.j3md");
    }

    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<DirectionalLight> lightList) {
        gBuffer.passGBufferToShader(directionalLightMaterial);
        if (lightList.size() > 0) {
            Vector3f[] dlDirection = new Vector3f[lightList.size()];
            Vector3f[] dlColor = new Vector3f[lightList.size()];
            int count = 0;
            for (DirectionalLight directionalLight : lightList) {
                dlDirection[count] = directionalLight.getDirection();
                dlColor[count] = directionalLight.getColor().toVector3f();
                count++;
            }

            directionalLightMaterial.setParam("lightCount", VarType.Int, lightList.size());
            directionalLightMaterial.setParam("lightDirections", VarType.Vector3Array, dlDirection);
            directionalLightMaterial.setParam("lightColors", VarType.Vector3Array, dlColor);
            renderManager.setForcedMaterial(directionalLightMaterial);
            renderManager.setForcedTechnique(null);
            fsQuad.setMaterial(directionalLightMaterial);
            renderManager.renderGeometry(fsQuad);
        }
    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<DirectionalLight> lightList) {

    }
}
