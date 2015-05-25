package gg.zue.yadef.renderpasses.lighttechniques;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
    private final Geometry fsQuad;
    private final Material directionalLightMaterial;
    private ColorRGBA ambientLightToClear;

    public DefaultDirectionalLightTechnique(AssetManager assetManager) {
        this.fsQuad = new Geometry("FsQuad", new Quad(1, 1));
        this.directionalLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/DirectionalLight/DirectionalLight.j3md");
        this.directionalLightMaterial.setInt("maxLights", 20);
    }

    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<DirectionalLight> lightList) {
        gBuffer.passGBufferToShader(directionalLightMaterial);
        //if (lightList.size() > 0) {
        Vector3f[] dlDirection = new Vector3f[lightList.size()];
        Vector3f[] dlColor = new Vector3f[lightList.size()];
        int count = 0;
        for (DirectionalLight directionalLight : lightList) {
            dlDirection[count] = directionalLight.getDirection();
            dlColor[count] = directionalLight.getColor().toVector3f();
            count++;
        }
        directionalLightMaterial.setVector3("ambientColorToClear", ambientLightToClear.toVector3f());
        directionalLightMaterial.setParam("lightCount", VarType.Int, lightList.size());
        if (lightList.size() > 0) {
            directionalLightMaterial.setParam("lightDirections", VarType.Vector3Array, dlDirection);
            directionalLightMaterial.setParam("lightColors", VarType.Vector3Array, dlColor);
        }
        //renderManager.setForcedMaterial(directionalLightMaterial);
        renderManager.setForcedTechnique(null);
        fsQuad.setMaterial(directionalLightMaterial);
        renderManager.renderGeometry(fsQuad);
        //}
        renderManager.setForcedMaterial(null);
    }

    public void setAmbientLightToClear(ColorRGBA ambientLightToClear) {
        this.ambientLightToClear = ambientLightToClear;
    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<DirectionalLight> lightList) {

    }
}
