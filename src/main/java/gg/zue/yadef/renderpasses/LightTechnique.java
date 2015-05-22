package gg.zue.yadef.renderpasses;

import com.jme3.light.Light;
import com.jme3.renderer.RenderManager;
import gg.zue.yadef.GBuffer;

import java.util.ArrayList;

/**
 * Created by MiZu on 21.05.2015.
 */
public interface LightTechnique<T extends Light> {
    void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<T> lightList);

    void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<T> lightList);
}
