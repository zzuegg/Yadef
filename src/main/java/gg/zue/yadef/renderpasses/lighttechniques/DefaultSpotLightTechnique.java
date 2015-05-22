package gg.zue.yadef.renderpasses.lighttechniques;

import com.jme3.asset.AssetManager;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.*;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.LightTechnique;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by MiZu on 21.05.2015.
 */
public class DefaultSpotLightTechnique implements LightTechnique<SpotLight> {

    private final AssetManager assetManager;
    private Material spotLightMaterial;
    private Geometry spotLightGeometry;

    public DefaultSpotLightTechnique(AssetManager assetManager) {
        this.assetManager = assetManager;
        spotLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/SpotLight/SpotLight.j3md");
//        Node node = (Node) assetManager.loadModel("Models/yadef/SpotLight2.blend");
//        spotLightGeometry = (Geometry) ((Node) ((Node) node.getChild(0)).getChild(0)).getChild(0);
//         spotLightGeometry = new Geometry("SpotLight", new Box(new Vector3f(-1, 0, -1), new Vector3f(1, 1, 1)));
        Mesh mesh = new Mesh();
        Vector3f[] positions = new Vector3f[5];
        positions[0] = new Vector3f(0, 0, 0);
        positions[1] = new Vector3f(-1, -1, -1);
        positions[1] = new Vector3f(1, -1, -1);
        positions[1] = new Vector3f(1, -1, 1);
        positions[1] = new Vector3f(-1, -1, 1);
        int[] indices = new int[]{
                0, 1, 2,
                0, 2, 3,
                0, 3, 4,
                0, 4, 1,
                1, 3, 2,
                4, 3, 1,
        };
        mesh.setBuffer(VertexBuffer.Type.Index, 3, indices);
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(positions));
        spotLightGeometry = new Geometry("SpotLight", mesh);
    }

    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<SpotLight> lightList) {
        gBuffer.passGBufferToShader(spotLightMaterial);
        for (SpotLight spotLight : lightList) {
            Vector3f position = spotLight.getPosition();
            Vector3f direction = spotLight.getDirection();
            Vector3f color = spotLight.getColor().toVector3f();
            spotLightMaterial.setVector4("spotLightPositionAngle", new Vector4f(position.x, position.y, position.z, FastMath.sin(spotLight.getSpotOuterAngle())));
            spotLightMaterial.setVector4("spotLightDirectionRange", new Vector4f(direction.x, direction.y, direction.z, spotLight.getSpotRange()));
            spotLightMaterial.setVector4("spotLightColorInnerAngle", new Vector4f(color.x, color.y, color.z, spotLight.getSpotInnerAngle()));
            spotLightGeometry.setMaterial(spotLightMaterial);
            renderManager.setForcedTechnique(null);
            spotLightMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
            renderManager.renderGeometry(spotLightGeometry);
        }
    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<SpotLight> lightList) {
        for (SpotLight spotLight : lightList) {
            Vector3f position = spotLight.getPosition();
            Vector3f direction = spotLight.getDirection();
            Vector3f color = spotLight.getColor().toVector3f();
            spotLightMaterial.setVector4("spotLightPositionAngle", new Vector4f(position.x, position.y, position.z, spotLight.getSpotOuterAngle()));
            spotLightMaterial.setVector4("spotLightDirectionRange", new Vector4f(direction.x, direction.y, direction.z, spotLight.getSpotRange()));
            spotLightMaterial.setVector4("spotLightColorInnerAngle", new Vector4f(color.x, color.y, color.z, spotLight.getSpotInnerAngle()));
            spotLightGeometry.setMaterial(spotLightMaterial);
            spotLightMaterial.getAdditionalRenderState().setWireframe(true);
            spotLightMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
            renderManager.setForcedTechnique("DebugSpotLights");
            renderManager.renderGeometry(spotLightGeometry);
        }
    }

}
