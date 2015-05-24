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
import com.jme3.shader.VarType;
import com.jme3.util.BufferUtils;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.LightTechnique;
import jme3tools.optimize.GeometryBatchFactory;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by MiZu on 21.05.2015.
 */
public class PatchedSpotLightTechnique implements LightTechnique<SpotLight> {

    private final AssetManager assetManager;
    private Material spotLightMaterial;
    private Geometry spotLightGeometry;
    int maxLights;
    public PatchedSpotLightTechnique(AssetManager assetManager, int maxUniformParameters) {
        this.assetManager = assetManager;
        spotLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/SpotLight/SpotLight.j3md");
        spotLightMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        maxLights=(maxUniformParameters-100)/12;
        //maxLights=334;
        System.out.println("Spotlights: "+maxLights);
        spotLightMaterial.setInt("maxLights", maxLights);
        spotLightGeometry=buildGeometry(maxLights);
    }

    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<SpotLight> lightList) {
        gBuffer.passGBufferToShader(spotLightMaterial);
        if (lightList.size() > 0) {
            Vector4f[] spotLightPositionAngle = new Vector4f[lightList.size()];
            Vector4f[] spotLightDirectionRange = new Vector4f[lightList.size()];
            Vector4f[] spotLightColorInnerAngle = new Vector4f[lightList.size()];
            int count = 0;
            for (SpotLight spotLight : lightList) {
                Vector3f position = spotLight.getPosition();
                Vector3f direction = spotLight.getDirection();
                Vector3f color = spotLight.getColor().toVector3f();
                spotLightPositionAngle[count] = new Vector4f(position.x, position.y, position.z, spotLight.getSpotOuterAngle());
                spotLightDirectionRange[count] = new Vector4f(direction.x, direction.y, direction.z, spotLight.getSpotRange());
                spotLightColorInnerAngle[count] = new Vector4f(color.x, color.y, color.z, spotLight.getSpotInnerAngle());
                count++;
            }
            spotLightGeometry.setMaterial(spotLightMaterial);
            if (renderManager.getForcedRenderState() != null) {
                renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
            }
            for (int i = 0; i < spotLightPositionAngle.length; ) {
                int size = Math.min(maxLights, spotLightPositionAngle.length - i);
                Vector4f[] spotLightPositionAngleTmp = Arrays.copyOfRange(spotLightPositionAngle, i, i + size);
                Vector4f[] spotLightDirectionRangeTmp = Arrays.copyOfRange(spotLightDirectionRange, i, i + size);
                Vector4f[] spotLightColorInnerAngleTmp = Arrays.copyOfRange(spotLightColorInnerAngle, i, i + size);
                i = i + size;
                spotLightMaterial.setParam("lightCount", VarType.Int, size);
                spotLightMaterial.setParam("spotLightPositionAngle", VarType.Vector4Array, spotLightPositionAngleTmp);
                spotLightMaterial.setParam("spotLightDirectionRange", VarType.Vector4Array, spotLightDirectionRangeTmp);
                spotLightMaterial.setParam("spotLightColorInnerAngle", VarType.Vector4Array, spotLightColorInnerAngleTmp);
                renderManager.renderGeometry(spotLightGeometry);
            }
            if (renderManager.getForcedRenderState() != null) {
                renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
            }
        }
    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<SpotLight> lightList) {
        if (lightList.size() > 0) {
            Vector4f[] spotLightPositionAngle = new Vector4f[lightList.size()];
            Vector4f[] spotLightDirectionRange = new Vector4f[lightList.size()];
            Vector4f[] spotLightColorInnerAngle = new Vector4f[lightList.size()];
            int count = 0;
            for (SpotLight spotLight : lightList) {
                Vector3f position = spotLight.getPosition();
                Vector3f direction = spotLight.getDirection();
                Vector3f color = spotLight.getColor().toVector3f();
                spotLightPositionAngle[count] = new Vector4f(position.x, position.y, position.z, spotLight.getSpotOuterAngle());
                spotLightDirectionRange[count] = new Vector4f(direction.x, direction.y, direction.z, spotLight.getSpotRange());
                spotLightColorInnerAngle[count] = new Vector4f(color.x, color.y, color.z, spotLight.getSpotInnerAngle());
                count++;
            }
            spotLightGeometry.setMaterial(spotLightMaterial);
            spotLightMaterial.getAdditionalRenderState().setWireframe(true);
            renderManager.setForcedTechnique(null);
            renderManager.setForcedTechnique("DebugSpotLights");
            if (renderManager.getForcedRenderState() != null) {
                renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            }
            for (int i = 0; i < spotLightPositionAngle.length; ) {
                int size = Math.min(maxLights, spotLightPositionAngle.length - i);
                Vector4f[] spotLightPositionAngleTmp = Arrays.copyOfRange(spotLightPositionAngle, i, i + size);
                Vector4f[] spotLightDirectionRangeTmp = Arrays.copyOfRange(spotLightDirectionRange, i, i + size);
                Vector4f[] spotLightColorInnerAngleTmp = Arrays.copyOfRange(spotLightColorInnerAngle, i, i + size);
                i = i + size;
                spotLightMaterial.setParam("lightCount", VarType.Int, size);
                spotLightMaterial.setParam("spotLightPositionAngle", VarType.Vector4Array, spotLightPositionAngleTmp);
                spotLightMaterial.setParam("spotLightDirectionRange", VarType.Vector4Array, spotLightDirectionRangeTmp);
                spotLightMaterial.setParam("spotLightColorInnerAngle", VarType.Vector4Array, spotLightColorInnerAngleTmp);
                renderManager.renderGeometry(spotLightGeometry);
            }
            if (renderManager.getForcedRenderState() != null) {
                renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
            }
        }
    }

    Geometry buildGeometry(int count) {
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
        Geometry tmp = new Geometry("SpotLight", mesh);
        Collection<Geometry> plGeo = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            plGeo.add(tmp.clone());
        }
        Mesh spotLightMesh = new Mesh();
        GeometryBatchFactory.mergeGeometries(plGeo, spotLightMesh);
        return new Geometry("SpotLightPatch", spotLightMesh);
    }

}
