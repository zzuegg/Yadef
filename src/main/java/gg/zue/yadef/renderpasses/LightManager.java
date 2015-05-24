package gg.zue.yadef.renderpasses;

import com.jme3.asset.AssetManager;
import com.jme3.light.*;
import com.jme3.material.RenderState;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.util.TempVars;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.lighttechniques.*;


import java.util.ArrayList;


/**
 * Created by MiZu on 21.05.2015.
 */
public class LightManager {
    AssetManager assetManager;
    RenderState renderState;

    ArrayList<AmbientLight> ambientLights = new ArrayList<>();
    ArrayList<PointLight> pointLights = new ArrayList<>();
    ArrayList<SpotLight> spotLights = new ArrayList<>();
    ArrayList<DirectionalLight> directionalLights = new ArrayList<>();

    LightTechnique<AmbientLight> ambientLightLightTechnique;
    LightTechnique<DirectionalLight> directionalLightLightTechnique;
    LightTechnique<PointLight> pointLightLightTechnique;
    LightTechnique<SpotLight> spotLightLightTechnique;

    public LightManager() {
        this.renderState = new RenderState();
        this.renderState.setStencil(true, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.TestFunction.Less, RenderState.TestFunction.Less);
        this.renderState.setBlendMode(RenderState.BlendMode.Additive);
        this.renderState.setDepthTest(false);
        this.renderState.setDepthWrite(false);
    }

    public LightManager(AssetManager assetManager) {
        this();
        this.assetManager = assetManager;
        ambientLightLightTechnique = new DefaultAmbientLightTechnique();
        directionalLightLightTechnique = new DefaultDirectionalLightTechnique(assetManager);
        pointLightLightTechnique = new PatchedPointLightTechnique(assetManager);
        spotLightLightTechnique = new PatchedSpotLightTechnique(assetManager);
    }


    public void render(GBuffer gBuffer, RenderManager renderManager, ViewPort viewPort) {


        renderManager.setForcedRenderState(renderState);
        renderManager.getRenderer().setFrameBuffer(gBuffer.getLightFrameBuffer());
        //todo: there is probably a better way
        ambientLights.clear();
        pointLights.clear();
        spotLights.clear();
        directionalLights.clear();
        getVisibleLights(ambientLights, pointLights, spotLights, directionalLights, viewPort.getScenes().get(0).getWorldLightList(), viewPort.getCamera());
        //todo: until here


        //Ambient Light
        ambientLightLightTechnique.render(gBuffer, renderManager, ambientLights);
        renderState.setDepthTest(false);
        //Directional Light
        directionalLightLightTechnique.render(gBuffer, renderManager, directionalLights);

        renderState.setDepthTest(true);
        renderState.setDepthFunc(RenderState.TestFunction.GreaterOrEqual);

        //Point Light
        pointLightLightTechnique.render(gBuffer, renderManager, pointLights);

        //Spot lights
        spotLightLightTechnique.render(gBuffer, renderManager, spotLights);
        //todo:


        renderManager.setForcedRenderState(null);
        renderManager.setForcedTechnique(null);
        renderManager.setForcedMaterial(null);
    }

    public void renderDebug(GBuffer gBuffer, RenderManager renderManager) {
        pointLightLightTechnique.renderDebug(gBuffer, renderManager, pointLights);
        spotLightLightTechnique.renderDebug(gBuffer, renderManager, spotLights);
    }

    private void getVisibleLights(ArrayList<AmbientLight> ambientLights, ArrayList<PointLight> pointLights, ArrayList<SpotLight> spotLights, ArrayList<DirectionalLight> directionalLights, LightList lightlist, Camera camera) {
        TempVars tempVars = TempVars.get();
        for (Light light : lightlist) {
            if (light.intersectsFrustum(camera, tempVars)) {
                switch (light.getType()) {

                    case Directional:
                        directionalLights.add((DirectionalLight) light);
                        break;
                    case Point:
                        pointLights.add((PointLight) light);
                        break;
                    case Spot:
                        spotLights.add((SpotLight) light);
                        break;
                    case Ambient:
                        ambientLights.add((AmbientLight) light);
                        break;
                }
            }
        }
        tempVars.release();
    }
}
