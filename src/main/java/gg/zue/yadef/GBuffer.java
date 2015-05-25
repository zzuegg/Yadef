package gg.zue.yadef;

import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

/**
 * Created by MiZu on 21.05.2015.
 */
public class GBuffer {
    private FrameBuffer deferredFrameBuffer;
    private FrameBuffer lightFrameBuffer;
    private FrameBuffer outputFrameBuffer;
    //private Texture2D worldPositionLinearDepth;
    private Texture2D worldNormal;
    private Texture2D albedo;
    private Texture2D specular;
    private Texture2D light;
    private Texture2D lightSpecular;
    private Texture2D depthStencil;
    private Texture2D outputTexture;

    private int height;
    private int width;

    public void reshape(int height, int width) {
        if (height != this.height || width != this.width) {
            this.height = height;
            this.width = width;
            deferredFrameBuffer = new FrameBuffer(width, height, 1);
            deferredFrameBuffer.setMultiTarget(true);

            lightFrameBuffer = new FrameBuffer(width, height, 1);
            lightFrameBuffer.setMultiTarget(true);
            outputFrameBuffer = new FrameBuffer(width, height, 1);

            //worldPositionLinearDepth = applyFilters(new Texture2D(width, height, Image.Format.RGBA32F));
            worldNormal = applyFilters(new Texture2D(width, height, Image.Format.RGB16F));
            albedo = applyFilters(new Texture2D(width, height, Image.Format.RGB8));
            specular = applyFilters(new Texture2D(width, height, Image.Format.RGB8));
            depthStencil = new Texture2D(width, height, Image.Format.Depth24Stencil8);
            light = applyFilters(new Texture2D(width, height, Image.Format.RGB16F));
            lightSpecular = applyFilters(new Texture2D(width, height, Image.Format.RGB16F));
            outputTexture = applyFilters(new Texture2D(width, height, Image.Format.RGBA16F));

            //deferredFrameBuffer.addColorTexture(worldPositionLinearDepth);
            deferredFrameBuffer.addColorTexture(worldNormal);
            deferredFrameBuffer.addColorTexture(albedo);
            deferredFrameBuffer.addColorTexture(specular);
            deferredFrameBuffer.setDepthTexture(depthStencil);

            lightFrameBuffer.setDepthTexture(depthStencil);
            lightFrameBuffer.addColorTexture(light);
            lightFrameBuffer.addColorTexture(lightSpecular);
            outputFrameBuffer.setDepthTexture(depthStencil);
            outputFrameBuffer.setColorTexture(outputTexture);
        }
    }

    private Texture2D applyFilters(Texture2D texture2D) {
        texture2D.setMagFilter(Texture.MagFilter.Nearest);
        texture2D.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        return texture2D;
    }

    public FrameBuffer getLightFrameBuffer() {
        return lightFrameBuffer;
    }

    public FrameBuffer getDeferredFrameBuffer() {
        return deferredFrameBuffer;
    }

    public FrameBuffer getOutputFrameBuffer() {
        return outputFrameBuffer;
    }

    public void passGBufferToShader(Material material) {

        //checkParamAndSet(material, "gbWorldPosLinearDepth", worldPositionLinearDepth);
        checkParamAndSet(material, "gbWorldNormal", worldNormal);
        checkParamAndSet(material, "gbAlbedo", albedo);
        checkParamAndSet(material, "gbSpecular", specular);
        checkParamAndSet(material, "gbLight", light);
        checkParamAndSet(material, "gbLightSpecular", lightSpecular);
        checkParamAndSet(material, "gbDepth", depthStencil);
        checkParamAndSet(material, "gbOutput", outputTexture);
    }

    private void checkParamAndSet(Material material, String parameter, Texture2D texture) {
        //todo: add check if parameter exists
        material.setParam(parameter, VarType.Texture2D, texture);
    }
}
