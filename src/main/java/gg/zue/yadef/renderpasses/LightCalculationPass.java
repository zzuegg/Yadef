package gg.zue.yadef.renderpasses;

import com.jme3.asset.AssetManager;
import com.jme3.light.*;
import com.jme3.material.RenderState;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.util.TempVars;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.lighttechniques.DefaultAmbientLightTechnique;
import gg.zue.yadef.renderpasses.lighttechniques.DefaultDirectionalLightTechnique;
import gg.zue.yadef.renderpasses.lighttechniques.DefaultPointLightTechnique;


import java.util.ArrayList;


/**
 * Created by MiZu on 21.05.2015.
 */
public class LightCalculationPass {
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

    public LightCalculationPass() {
        this.renderState = new RenderState();
        this.renderState.setStencil(true, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.TestFunction.Less, RenderState.TestFunction.Less);
        this.renderState.setBlendMode(RenderState.BlendMode.Additive);
        this.renderState.setDepthTest(false);
        this.renderState.setDepthWrite(false);
    }

    public LightCalculationPass(AssetManager assetManager) {
        this();
        this.assetManager = assetManager;
        ambientLightLightTechnique = new DefaultAmbientLightTechnique();
        directionalLightLightTechnique = new DefaultDirectionalLightTechnique(assetManager);
        pointLightLightTechnique = new DefaultPointLightTechnique(assetManager);
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

        //Directional Light
        directionalLightLightTechnique.render(gBuffer, renderManager, directionalLights);

        //Point Light
        pointLightLightTechnique.render(gBuffer, renderManager, pointLights);

        //Spot lights
        //todo:


        renderManager.setForcedRenderState(null);
        renderManager.setForcedTechnique(null);
        renderManager.setForcedMaterial(null);
    }

    public void renderDebug(GBuffer gBuffer, RenderManager renderManager) {
        pointLightLightTechnique.renderDebug(gBuffer, renderManager, pointLights);
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
