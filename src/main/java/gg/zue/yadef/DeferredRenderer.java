package gg.zue.yadef;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Limits;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.opengl.GLRenderer;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;
import gg.zue.yadef.renderpasses.DeferredRenderManager;
import gg.zue.yadef.renderpasses.LightManager;
import gg.zue.yadef.renderpasses.PostDeferredManager;

/**
 * Created by MiZu on 21.05.2015.
 */
public class DeferredRenderer implements SceneProcessor {
    private final Application application;
    private final AssetManager assetManager;
    private final GBuffer gBuffer;
    private boolean initialized = false;
    private RenderManager renderManager;
    private ViewPort viewPort;

    private final DeferredRenderManager DeferredRenderManager;
    private final LightManager lightManager;
    private final PostDeferredManager postDeferredManager;


    private boolean debugLightVolumes = false;
    private boolean debugGBufferTextures = false;

    public DeferredRenderer(Application application) {
        this.application = application;
        this.assetManager = application.getAssetManager();
        this.gBuffer = new GBuffer();
        int maxUniformParameters=((GLRenderer)application.getRenderer()).getLimits().get(Limits.VertexUniformComponents);
        this.DeferredRenderManager = new DeferredRenderManager();
        this.lightManager = new LightManager(assetManager,maxUniformParameters);
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

        if (debugGBufferTextures) {
            postDeferredManager.renderDebug(gBuffer, renderManager);
        }
        if (debugLightVolumes) {
            lightManager.renderDebug(gBuffer, renderManager);
        }

        postDeferredManager.drawFrameOnScreen(gBuffer, renderManager);
    }

    public void setDebugLightVolumes(boolean debugLightVolumes) {
        this.debugLightVolumes = debugLightVolumes;
    }

    public void setDebugGBufferTextures(boolean debugGBufferTextures) {
        this.debugGBufferTextures = debugGBufferTextures;
    }

    @Override
    public void postFrame(FrameBuffer frameBuffer) {

    }

    @Override
    public void cleanup() {

    }
}
