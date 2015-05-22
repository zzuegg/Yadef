package gg.zue.yadef.renderpasses.lighttechniques;

import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.LightTechnique;

import java.util.ArrayList;

/**
 * Created by MiZu on 21.05.2015.
 */
public class DefaultAmbientLightTechnique implements LightTechnique<AmbientLight> {
    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<AmbientLight> lightList) {
        ColorRGBA ambientColor = new ColorRGBA(0, 0, 0, 0);
        for (AmbientLight ambientLight : lightList) {
            ambientColor.addLocal(ambientLight.getColor());
        }
        renderManager.getRenderer().setBackgroundColor(ambientColor);
        renderManager.getRenderer().clearBuffers(true, false, false);
    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<AmbientLight> lightList) {

    }
}
