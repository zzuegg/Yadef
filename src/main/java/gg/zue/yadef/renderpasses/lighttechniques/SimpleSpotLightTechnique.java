package gg.zue.yadef.renderpasses.lighttechniques;

import com.jme3.asset.AssetManager;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.shader.VarType;
import com.jme3.util.BufferUtils;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.LightTechnique;
import jme3tools.optimize.GeometryBatchFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by MiZu on 21.05.2015.
 */
public class SimpleSpotLightTechnique implements LightTechnique<SpotLight> {

    private final AssetManager assetManager;
    private Material spotLightMaterial;
    private Geometry spotLightGeometry;


    public SimpleSpotLightTechnique(AssetManager assetManager) {
        this.assetManager = assetManager;
        spotLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/SpotLight/SpotLight.j3md");
        spotLightGeometry = buildGeometry(1);
        spotLightMaterial.setInt("maxLights",1);
    }

    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<SpotLight> lightList) {
        gBuffer.passGBufferToShader(spotLightMaterial);
        if (renderManager.getForcedRenderState() != null) {
            renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        }
        for (SpotLight spotLight : lightList) {
            Vector3f position = spotLight.getPosition();
            Vector3f direction = spotLight.getDirection();
            Vector3f color = spotLight.getColor().toVector3f();
            spotLightMaterial.setParam("lightCount", VarType.Int, 1);
            spotLightMaterial.setParam("spotLightPositionAngle", VarType.Vector4Array, new Vector4f[]{new Vector4f(position.x, position.y, position.z, FastMath.sin(spotLight.getSpotOuterAngle()))});
            spotLightMaterial.setParam("spotLightDirectionRange", VarType.Vector4Array, new Vector4f[]{new Vector4f(direction.x, direction.y, direction.z, spotLight.getSpotRange())});
            spotLightMaterial.setParam("spotLightColorInnerAngle", VarType.Vector4Array, new Vector4f[]{new Vector4f(color.x, color.y, color.z, spotLight.getSpotInnerAngle())});
            spotLightGeometry.setMaterial(spotLightMaterial);
            renderManager.setForcedTechnique(null);
            if (renderManager.getForcedRenderState() != null) {
                renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
            }
            renderManager.renderGeometry(spotLightGeometry);
        }
        if (renderManager.getForcedRenderState() != null) {
            renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        }
    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<SpotLight> lightList) {
        for (SpotLight spotLight : lightList) {
            Vector3f position = spotLight.getPosition();
            Vector3f direction = spotLight.getDirection();
            Vector3f color = spotLight.getColor().toVector3f();
            spotLightMaterial.setParam("lightCount", VarType.Int, 1);
            spotLightMaterial.setParam("spotLightPositionAngle", VarType.Vector4Array, new Vector4f[]{new Vector4f(position.x, position.y, position.z, FastMath.sin(spotLight.getSpotOuterAngle()))});
            spotLightMaterial.setParam("spotLightDirectionRange", VarType.Vector4Array, new Vector4f[]{new Vector4f(direction.x, direction.y, direction.z, spotLight.getSpotRange())});
            spotLightMaterial.setParam("spotLightColorInnerAngle", VarType.Vector4Array, new Vector4f[]{new Vector4f(color.x, color.y, color.z, spotLight.getSpotInnerAngle())});
            spotLightGeometry.setMaterial(spotLightMaterial);
            spotLightMaterial.getAdditionalRenderState().setWireframe(true);
            spotLightMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
            renderManager.setForcedTechnique("DebugSpotLights");
            renderManager.renderGeometry(spotLightGeometry);
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
