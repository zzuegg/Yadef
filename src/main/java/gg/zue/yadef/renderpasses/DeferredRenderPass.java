package gg.zue.yadef.renderpasses;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import gg.zue.yadef.GBuffer;

/**
 * Created by MiZu on 21.05.2015.
 */
public interface DeferredRenderPass {
    public void render(GBuffer gBuffer, RenderManager renderManager, ViewPort viewPort, RenderQueue renderQueue);
}
