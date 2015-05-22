package gg.zue.yadef;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;
import gg.zue.yadef.renderpasses.DeferredRenderManager;
import gg.zue.yadef.renderpasses.LightManager;
import gg.zue.yadef.renderpasses.PostDeferredManager;

/**
 * Created by MiZu on 21.05.2015.
 */
public class DeferredRenderer implements SceneProcessor {
    Application application;
    AssetManager assetManager;
    GBuffer gBuffer;
    boolean initialized = false;
    RenderManager renderManager;
    ViewPort viewPort;

    DeferredRenderManager DeferredRenderManager;
    LightManager lightManager;
    PostDeferredManager postDeferredManager;

    public DeferredRenderer(Application application) {
        this.application = application;
        this.assetManager = application.getAssetManager();
        this.gBuffer = new GBuffer();

        this.DeferredRenderManager = new DeferredRenderManager();
        this.lightManager = new LightManager(assetManager);
        this.postDeferredManager = new PostDeferredManager(assetManager);
    }

    @Override
    public void initialize(RenderManager renderManager, ViewPort viewPort) {
        reshape(viewPort, viewPort.getCamera().getWidth(), viewPort.getCamera().getHeight());
        this.renderManager = renderManager;
        this.viewPort = viewPort;
    }

    @Override
    public void reshape(ViewPort viewPort, int width, int height) {
        if (width != 0 && height != 0) {
            this.gBuffer.reshape(height, width);
            initialized = true;
            System.out.println(width + " : " + height);
        }
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
        DeferredRenderManager.renderOpaqueQueue(gBuffer, renderManager, viewPort, renderQueue);
        lightManager.render(gBuffer, renderManager, viewPort);

        postDeferredManager.render(gBuffer, renderManager, viewPort, renderQueue);
        postDeferredManager.renderSkyQueue(renderManager, viewPort, renderQueue);
        postDeferredManager.renderTranslucentQueue(renderManager, viewPort, renderQueue);

        postDeferredManager.renderDebug(gBuffer, renderManager);
        //lightManager.renderDebug(gBuffer, renderManager);


        postDeferredManager.drawFrameOnScreen(gBuffer, renderManager);
    }


    @Override
    public void postFrame(FrameBuffer frameBuffer) {

    }

    @Override
    public void cleanup() {

    }
}
