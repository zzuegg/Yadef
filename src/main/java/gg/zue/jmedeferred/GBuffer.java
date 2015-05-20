package gg.zue.jmedeferred;

import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

/**
 * Created by MiZu on 19.05.2015.
 */
public class GBuffer {
    private FrameBuffer renderFrameBuffer;
    private FrameBuffer lightFrameBuffer;
    private Texture2D worldPositionLinearDepth;
    private Texture2D worldNormal;
    private Texture2D albedo;
    private Texture2D specular;
    private Texture2D light;
    private Texture2D depthStencil;
    int height, width;

    public void reshape(int height, int width) {
        if (height != this.height || width != this.width) {
            this.height = height;
            this.width = width;
            renderFrameBuffer = new FrameBuffer(width, height, 1);
            renderFrameBuffer.setMultiTarget(true);

            lightFrameBuffer = new FrameBuffer(width, height, 1);


            worldPositionLinearDepth = applyFilters(new Texture2D(width, height, Image.Format.RGBA16F));
            worldNormal = applyFilters(new Texture2D(width, height, Image.Format.RGB16F));
            albedo = applyFilters(new Texture2D(width, height, Image.Format.RGB8));
            specular = applyFilters(new Texture2D(width, height, Image.Format.RGB8));
            depthStencil = applyFilters(new Texture2D(width, height, Image.Format.Depth24Stencil8));
            light = applyFilters(new Texture2D(width, height, Image.Format.RGB16F));

            renderFrameBuffer.addColorTexture(worldPositionLinearDepth);
            renderFrameBuffer.addColorTexture(worldNormal);
            renderFrameBuffer.addColorTexture(albedo);
            renderFrameBuffer.addColorTexture(specular);
            renderFrameBuffer.setDepthTexture(depthStencil);

            lightFrameBuffer.setDepthTexture(depthStencil);
            lightFrameBuffer.setColorTexture(light);
        }
    }

    Texture2D applyFilters(Texture2D texture2D) {
        texture2D.setMagFilter(Texture.MagFilter.Nearest);
        texture2D.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        return texture2D;
    }

    protected FrameBuffer getRenderFrameBuffer() {
        return renderFrameBuffer;
    }

    protected FrameBuffer getLightFrameBuffer() {
        return lightFrameBuffer;
    }

    public void passGBufferToShader(Material material) {
        /*Texture2D gbWorldPosLinearDepth;
        Texture2D gbWorldNormal;
        Texture2D gbAlbedo;
        Texture2D gbSpecular;*/
        checkParamAndSet(material, "gbWorldPosLinearDepth", worldPositionLinearDepth);
        checkParamAndSet(material, "gbWorldNormal", worldNormal);
        checkParamAndSet(material, "gbAlbedo", albedo);
        checkParamAndSet(material, "gbSpecular", specular);
        checkParamAndSet(material, "gbLight", light);
    }

    private void checkParamAndSet(Material material, String parameter, Texture2D texture) {
        material.setParam(parameter, VarType.Texture2D, texture);
        System.out.println("Passed Parameter " + parameter);
    }
}
