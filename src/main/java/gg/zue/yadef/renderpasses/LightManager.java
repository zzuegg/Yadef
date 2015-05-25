package gg.zue.yadef.renderpasses;

import com.jme3.asset.AssetManager;
import com.jme3.light.*;
import com.jme3.material.RenderState;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.TempVars;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.lighttechniques.*;


import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by MiZu on 21.05.2015.
 */
public class LightManager {
    private AssetManager assetManager;
    private final RenderState renderState;

    private final ArrayList<AmbientLight> ambientLights = new ArrayList<>();
    private final ArrayList<PointLight> pointLights = new ArrayList<>();
    private final ArrayList<SpotLight> spotLights = new ArrayList<>();
    private final ArrayList<DirectionalLight> directionalLights = new ArrayList<>();

    private LightTechnique<AmbientLight> ambientLightLightTechnique;
    private LightTechnique<DirectionalLight> directionalLightLightTechnique;
    private LightTechnique<PointLight> pointLightLightTechnique;
    private LightTechnique<SpotLight> spotLightLightTechnique;

    ExecutorService executorService;

    private LightManager() {
        this.renderState = new RenderState();
        this.renderState.setStencil(true, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.TestFunction.Less, RenderState.TestFunction.Less);
        this.renderState.setBlendMode(RenderState.BlendMode.Additive);
        this.renderState.setDepthTest(false);
        this.renderState.setDepthWrite(false);
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public LightManager(AssetManager assetManager, int maxUniformParameters) {
        this();
        this.assetManager = assetManager;
        ambientLightLightTechnique = new DefaultAmbientLightTechnique();
        directionalLightLightTechnique = new DefaultDirectionalLightTechnique(assetManager);
        pointLightLightTechnique = new PatchedPointLightTechnique(assetManager, maxUniformParameters);
        spotLightLightTechnique = new PatchedSpotLightTechnique(assetManager, maxUniformParameters);
    }


    public void render(GBuffer gBuffer, RenderManager renderManager, ViewPort viewPort) {


        renderManager.setForcedRenderState(renderState);
        renderManager.getRenderer().setFrameBuffer(gBuffer.getLightFrameBuffer());


        //Ambient Light
        ambientLightLightTechnique.render(gBuffer, renderManager, ambientLights);

        renderState.setDepthTest(false);
        //todo: change to a clean solution for clearing ambient specularity
        ((DefaultDirectionalLightTechnique) directionalLightLightTechnique).setAmbientLightToClear(((DefaultAmbientLightTechnique) ambientLightLightTechnique).getAmbientLight());
        directionalLightLightTechnique.render(gBuffer, renderManager, directionalLights);

        renderState.setDepthTest(true);
        renderState.setDepthFunc(RenderState.TestFunction.GreaterOrEqual);

        //Point Light
        pointLightLightTechnique.render(gBuffer, renderManager, pointLights);

        //Spot lights
        spotLightLightTechnique.render(gBuffer, renderManager, spotLights);


        renderManager.setForcedRenderState(null);
        renderManager.setForcedTechnique(null);
        renderManager.setForcedMaterial(null);
    }

    public void renderDebug(GBuffer gBuffer, RenderManager renderManager) {
        pointLightLightTechnique.renderDebug(gBuffer, renderManager, pointLights);
        spotLightLightTechnique.renderDebug(gBuffer, renderManager, spotLights);
    }

    public Future<Boolean> updateVisibleLights(Spatial startNode, Camera camera, boolean threaded) {
        if (threaded) {
            return executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    updateVisibleLights(startNode, camera);
                    return true;
                }
            });
        } else {
            updateVisibleLights(startNode, camera);
        }
        return null;
    }


    private Boolean updateVisibleLights(Spatial startNode, Camera camera) {

        ambientLights.clear();
        pointLights.clear();
        spotLights.clear();
        directionalLights.clear();
        LightList lightlist = startNode.getWorldLightList();
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
        return true;
    }
}
