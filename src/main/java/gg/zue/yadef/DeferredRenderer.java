package gg.zue.yadef;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;
import gg.zue.yadef.renderpasses.GBufferPass;
import gg.zue.yadef.renderpasses.LightCalculationPass;
import gg.zue.yadef.renderpasses.RenderToScreenPass;

/**
 * Created by MiZu on 21.05.2015.
 */
public class DeferredRenderer implements SceneProcessor {
    Application application;
    AssetManager assetManager;
    GBuffer gBuffer;
    boolean initialized;
    RenderManager renderManager;
    ViewPort viewPort;

    GBufferPass GBufferPass;
    LightCalculationPass lightCalculationPass;
    RenderToScreenPass renderToScreenPass;

    public DeferredRenderer(Application application) {
        this.application = application;
        this.assetManager = application.getAssetManager();
        this.gBuffer = new GBuffer();

        this.GBufferPass = new GBufferPass();
        this.lightCalculationPass = new LightCalculationPass(assetManager);
        this.renderToScreenPass = new RenderToScreenPass(assetManager);
    }

    @Override
    public void initialize(RenderManager renderManager, ViewPort viewPort) {
        reshape(viewPort, viewPort.getCamera().getWidth(), viewPort.getCamera().getHeight());
        this.renderManager = renderManager;
        this.viewPort = viewPort;
        initialized = true;
    }

    @Override
    public void reshape(ViewPort viewPort, int width, int height) {
        this.gBuffer.reshape(height, width);
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void preFrame(float v) {

    }

    @Override
    public void postQueue(RenderQueue renderQueue) {
        GBufferPass.render(gBuffer, renderManager, viewPort, renderQueue);
        lightCalculationPass.render(gBuffer, renderManager, viewPort);

        renderToScreenPass.render(gBuffer, renderManager, viewPort, renderQueue);
        renderQueue.renderQueue(RenderQueue.Bucket.Sky, renderManager, viewPort.getCamera());
        renderQueue.renderQueue(RenderQueue.Bucket.Translucent, renderManager, viewPort.getCamera());


        renderToScreenPass.renderDebug(gBuffer, renderManager);
        lightCalculationPass.renderDebug(gBuffer, renderManager);
        renderManager.getRenderer().setFrameBuffer(null);

        renderToScreenPass.finalizeFrame(gBuffer, renderManager);
    }


    @Override
    public void postFrame(FrameBuffer frameBuffer) {

    }

    @Override
    public void cleanup() {

    }
}
