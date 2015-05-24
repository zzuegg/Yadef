package gg.zue.yadef.renderpasses.lighttechniques;

import com.jme3.asset.AssetManager;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.*;
import com.jme3.shader.VarType;
import com.jme3.util.BufferUtils;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.LightTechnique;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by MiZu on 21.05.2015.
 */
public class PatchedSpotLightTechnique implements LightTechnique<SpotLight> {

    private final Material spotLightMaterial;
    private final Geometry spotLightGeometry;
    private final int maxLights;

    public PatchedSpotLightTechnique(AssetManager assetManager, int maxUniformParameters) {
        spotLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/SpotLight/SpotLight.j3md");
        spotLightMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        /*
        uniform mat4 g_WorldViewProjectionMatrix;

        out vec4 lightColorInnerAngle;
        out vec4 lightPositionOuterAngle;
        out vec4 lightDirectionRange;

        uniform int m_lightCount;
        uniform vec4[MAX_LIGHTS] m_spotLightPositionAngle;
        uniform vec4[MAX_LIGHTS] m_spotLightDirectionRange;
        uniform vec4[MAX_LIGHTS] m_spotLightColorInnerAngle;

        const float offsetMod[5]=float[5](0,1,1,1,1);
        const vec3 upMod[5]=vec3[5](    vec3(0),vec3(1),vec3(1),vec3(-1),vec3(-1));
        const vec3 leftMod[5]=vec3[5](  vec3(0),vec3(1),vec3(-1),vec3(-1),vec3(1));
        const int id[18]=int[18](0, 1, 2, 0, 2, 3, 0, 3, 4, 0, 4, 1,1, 3, 2, 4, 3, 1);
         */

        maxLights = (maxUniformParameters - 200) / 12;
        //maxLights=334;
        System.out.println("Spotlights: " + maxLights);
        spotLightMaterial.setInt("maxLights", maxLights);
        spotLightGeometry = buildGeometry(maxLights);
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
                updateMeshForRendering(size);
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
                updateMeshForRendering(size);
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

    private Field vertexCountField;

    private void updateMeshForRendering(int size) {
        if (vertexCountField == null) {
            try {
                vertexCountField = spotLightGeometry.getMesh().getClass().getDeclaredField("vertCount");
                vertexCountField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        try {
            vertexCountField.set(spotLightGeometry.getMesh(), size * 18);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Geometry buildGeometry(int count) {
        Mesh mesh = new Mesh();
        Vector3f[] positions = new Vector3f[5];
        positions[0] = new Vector3f(0, 0, 0);
        positions[1] = new Vector3f(-1, -1, -1);
        positions[2] = new Vector3f(1, -1, -1);
        positions[3] = new Vector3f(1, -1, 1);
        positions[4] = new Vector3f(-1, -1, 1);
        int[] indices = new int[]{
                0, 1, 2,
                0, 2, 3,
                0, 3, 4,
                0, 4, 1,
                1, 3, 2,
                4, 3, 1,
        };
        Vector3f[] pTmp = new Vector3f[count * indices.length];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < indices.length; j++) {
                pTmp[i * indices.length + j] = positions[indices[j]];
            }
        }
        //mesh.setBuffer(VertexBuffer.Type.Index, 3, indices);
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(pTmp));
        Geometry tmp = new Geometry("SpotLight", mesh);
        System.out.println(tmp.getMesh().getVertexCount());
        return tmp;
        /*Collection<Geometry> plGeo = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            plGeo.add(tmp.clone());
        }
        Mesh spotLightMesh = new Mesh();
        GeometryBatchFactory.mergeGeometries(plGeo, spotLightMesh);
        return new Geometry("SpotLightPatch", spotLightMesh);*/
    }

}
