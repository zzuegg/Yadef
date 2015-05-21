package gg.zue.yadef.renderpasses;

import com.jme3.material.RenderState;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import gg.zue.yadef.GBuffer;


/**
 * Created by MiZu on 21.05.2015.
 */
public class DeferredRenderManager {
    private RenderState deferredRenderState;

    public DeferredRenderManager() {
        deferredRenderState = new RenderState();
        deferredRenderState.setStencil(true, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Increment, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Increment, RenderState.TestFunction.Always, RenderState.TestFunction.Always);
    }

    public void renderOpaqueQueue(GBuffer gBuffer, RenderManager renderManager, ViewPort viewPort, RenderQueue renderQueue) {
        renderManager.getRenderer().setFrameBuffer(gBuffer.getDeferredFrameBuffer());
        renderManager.getRenderer().clearBuffers(true, true, true);
        renderManager.setForcedRenderState(deferredRenderState);
        renderQueue.renderQueue(RenderQueue.Bucket.Opaque, renderManager, viewPort.getCamera(), true);
        renderManager.setForcedTechnique(null);
        renderManager.setForcedRenderState(null);
    }
}
