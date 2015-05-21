package gg.zue.yadef.renderpasses;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import gg.zue.yadef.GBuffer;

/**
 * Created by MiZu on 21.05.2015.
 */
public class RenderToScreenPass {
    AssetManager assetManager;
    private RenderState renderState;
    private Geometry fsQuad;
    private Material resolveGBufferMaterial;
    private Material renderToScreenMaterial;

    public RenderToScreenPass(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.fsQuad = new Geometry("FsQuad", new Quad(1, 1));
        this.resolveGBufferMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/Resolve/GBufferResolve.j3md");
        this.renderToScreenMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/Common/RenderToScreen.j3md");
        this.renderState = new RenderState();
        this.renderState.setStencil(true, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.StencilOperation.Keep, RenderState.TestFunction.Less, RenderState.TestFunction.Less);
        this.renderState.setDepthWrite(false);
        this.renderState.setDepthTest(false);
    }

    public void render(GBuffer gBuffer, RenderManager renderManager, ViewPort viewPort, RenderQueue renderQueue) {
        gBuffer.passGBufferToShader(resolveGBufferMaterial);
        renderManager.getRenderer().setFrameBuffer(gBuffer.getOutputFrameBuffer());
        renderManager.getRenderer().setBackgroundColor(ColorRGBA.BlackNoAlpha);
        renderManager.getRenderer().clearBuffers(true, false, false);

        renderManager.setForcedRenderState(renderState);
        renderManager.setForcedMaterial(resolveGBufferMaterial);
        renderManager.setForcedTechnique("GBufferResolve");

        renderManager.renderGeometry(fsQuad);

        renderManager.setForcedRenderState(null);
        renderManager.setForcedMaterial(null);
        renderManager.setForcedTechnique(null);
    }

    public void finalizeFrame(GBuffer gBuffer, RenderManager renderManager) {
        gBuffer.passGBufferToShader(renderToScreenMaterial);
        renderManager.setForcedMaterial(renderToScreenMaterial);
        renderManager.setForcedTechnique("RenderToScreen");

        renderManager.renderGeometry(fsQuad);
        renderManager.setForcedRenderState(null);
        renderManager.setForcedMaterial(null);
        renderManager.setForcedTechnique(null);
    }

    public void renderDebug(GBuffer gBuffer, RenderManager renderManager) {
        gBuffer.passGBufferToShader(resolveGBufferMaterial);
        renderManager.setForcedMaterial(resolveGBufferMaterial);
        renderManager.setForcedTechnique("GBufferDebug");
        //renderManager.setForcedRenderState(renderState);
        renderManager.renderGeometry(fsQuad);

        renderManager.setForcedRenderState(null);
        renderManager.setForcedMaterial(null);
        renderManager.setForcedTechnique(null);
    }
}
