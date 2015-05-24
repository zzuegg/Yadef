package gg.zue.yadef.renderpasses.lighttechniques;

import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by MiZu on 21.05.2015.
 */
public class PatchedPointLightTechnique implements LightTechnique<PointLight> {
    private final AssetManager assetManager;
    private final Material pointLightMaterial;
    private final Geometry pointLightGeometry;
    private final int maxLights;

    public PatchedPointLightTechnique(AssetManager assetManager, int maxUniformParameters) {
        this.assetManager = assetManager;

        this.pointLightMaterial = new Material(assetManager, "Materials/yadef/DeferredLogic/PointLight/PointLight.j3md");
        maxLights = (maxUniformParameters - 16 - 4 - 12 - 1) / 8;
        pointLightMaterial.setInt("maxLights", maxLights);
        this.pointLightGeometry = generatePointLightMesh(maxLights);
    }

    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<PointLight> lightList) {
        gBuffer.passGBufferToShader(pointLightMaterial);
        renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Front);
        if (lightList.size() > 0) {
            Vector4f[] pointLightPositionRadius = new Vector4f[lightList.size()];
            Vector3f[] pointLightColors = new Vector3f[lightList.size()];
            int count = 0;
            for (PointLight pointLight : lightList) {
                Vector3f position = pointLight.getPosition();
                pointLightPositionRadius[count] = new Vector4f(position.x, position.y, position.z, pointLight.getRadius());
                pointLightColors[count] = pointLight.getColor().toVector3f();
                count++;

            }
            renderManager.setForcedTechnique(null);
            for (int i = 0; i < pointLightColors.length; ) {
                int size = Math.min(maxLights, pointLightColors.length - i);
                Vector3f[] pointLightColorsTmp = Arrays.copyOfRange(pointLightColors, i, i + size);
                Vector4f[] pointLightPositionRadiusTmp = Arrays.copyOfRange(pointLightPositionRadius, i, i + size);
                i = i + size;
                updateMeshForRendering(size);
                pointLightMaterial.setParam("lightCount", VarType.Int, size);
                pointLightMaterial.setParam("lightPositionRadius", VarType.Vector4Array, pointLightPositionRadiusTmp);
                pointLightMaterial.setParam("lightColors", VarType.Vector3Array, pointLightColorsTmp);
                pointLightGeometry.setMaterial(pointLightMaterial);
                renderManager.renderGeometry(pointLightGeometry);
            }

        }
        renderManager.getForcedRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<PointLight> lightList) {
        RenderState forcedRenderState = renderManager.getForcedRenderState();
        RenderState renderState = new RenderState();
        renderState.setFaceCullMode(RenderState.FaceCullMode.Front);
        renderState.setWireframe(true);
        renderManager.setForcedRenderState(renderState);
        if (lightList.size() > 0) {
            Vector4f[] pointLightPositionRadius = new Vector4f[lightList.size()];
            Vector3f[] pointLightColors = new Vector3f[lightList.size()];
            int count = 0;
            for (PointLight pointLight : lightList) {
                Vector3f position = pointLight.getPosition();
                pointLightPositionRadius[count] = new Vector4f(position.x, position.y, position.z, pointLight.getRadius());
                pointLightColors[count] = pointLight.getColor().toVector3f();
                count++;

            }
            renderManager.setForcedTechnique("DebugPointLights");
            for (int i = 0; i < pointLightColors.length; ) {
                int size = Math.min(maxLights, pointLightColors.length - i);
                Vector3f[] pointLightColorsTmp = Arrays.copyOfRange(pointLightColors, i, i + size);
                Vector4f[] pointLightPositionRadiusTmp = Arrays.copyOfRange(pointLightPositionRadius, i, i + size);
                i = i + size;
                updateMeshForRendering(size);
                pointLightMaterial.setParam("lightCount", VarType.Int, size);
                pointLightMaterial.setParam("lightPositionRadius", VarType.Vector4Array, pointLightPositionRadiusTmp);
                pointLightMaterial.setParam("lightColors", VarType.Vector3Array, pointLightColorsTmp);
                renderManager.renderGeometry(pointLightGeometry);
            }
        }
        renderManager.setForcedRenderState(forcedRenderState);
    }

    private Field vertexCountField;

    private void updateMeshForRendering(int size) {
        if (vertexCountField == null) {
            try {
                vertexCountField = pointLightGeometry.getMesh().getClass().getDeclaredField("vertCount");
                vertexCountField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        try {
            vertexCountField.set(pointLightGeometry.getMesh(), size * 144);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Geometry generatePointLightMesh(int count) {
        Vector3f[] positions = new Vector3f[30];
        positions[0] = new Vector3f(0.5877852f, 0.0f, -0.80901706f);
        positions[1] = new Vector3f(0.29389256f, 0.5090369f, -0.80901706f);
        positions[2] = new Vector3f(-0.29389262f, 0.5090369f, -0.80901706f);
        positions[3] = new Vector3f(-0.5877852f, -5.138581E-8f, -0.80901706f);
        positions[4] = new Vector3f(-0.29389253f, -0.5090369f, -0.80901706f);
        positions[5] = new Vector3f(0.29389253f, -0.5090369f, -0.80901706f);
        positions[6] = new Vector3f(0.5877852f, 0.0f, -0.80901706f);
        positions[7] = new Vector3f(0.95105654f, 0.0f, -0.30901697f);
        positions[8] = new Vector3f(0.47552824f, 0.82363915f, -0.30901697f);
        positions[9] = new Vector3f(-0.47552833f, 0.8236391f, -0.30901697f);
        positions[10] = new Vector3f(-0.95105654f, -8.3144E-8f, -0.30901697f);
        positions[11] = new Vector3f(-0.47552818f, -0.82363915f, -0.30901697f);
        positions[12] = new Vector3f(0.47552818f, -0.82363915f, -0.30901697f);
        positions[13] = new Vector3f(0.95105654f, 0.0f, -0.30901697f);
        positions[14] = new Vector3f(0.9510565f, 0.0f, 0.3090171f);
        positions[15] = new Vector3f(0.4755282f, 0.8236391f, 0.3090171f);
        positions[16] = new Vector3f(-0.4755283f, 0.82363904f, 0.3090171f);
        positions[17] = new Vector3f(-0.9510565f, -8.3143995E-8f, 0.3090171f);
        positions[18] = new Vector3f(-0.47552815f, -0.8236391f, 0.3090171f);
        positions[19] = new Vector3f(0.47552815f, -0.8236391f, 0.3090171f);
        positions[20] = new Vector3f(0.9510565f, 0.0f, 0.3090171f);
        positions[21] = new Vector3f(0.5877852f, 0.0f, 0.80901706f);
        positions[22] = new Vector3f(0.29389256f, 0.5090369f, 0.80901706f);
        positions[23] = new Vector3f(-0.29389262f, 0.5090369f, 0.80901706f);
        positions[24] = new Vector3f(-0.5877852f, -5.138581E-8f, 0.80901706f);
        positions[25] = new Vector3f(-0.29389253f, -0.5090369f, 0.80901706f);
        positions[26] = new Vector3f(0.29389253f, -0.5090369f, 0.80901706f);
        positions[27] = new Vector3f(0.5877852f, 0.0f, 0.80901706f);
        positions[28] = new Vector3f(0.0f, 0.0f, -1.0f);
        positions[29] = new Vector3f(0.0f, 0.0f, 1.0f);
        int[] indices = new int[]{
                0, 1, 7, 1, 8, 7, 1, 2, 8, 2, 9, 8, 2, 3, 9, 3, 10, 9, 3, 4, 10, 4, 11, 10, 4, 5, 11, 5, 12, 11, 5, 6, 12, 6, 13, 12, 7, 8, 14, 8, 15, 14, 8, 9, 15, 9, 16, 15, 9, 10, 16, 10, 17, 16, 10, 11, 17, 11, 18, 17, 11, 12, 18, 12, 19, 18, 12, 13, 19, 13, 20, 19, 14, 15, 21, 15, 22, 21, 15, 16, 22, 16, 23, 22, 16, 17, 23, 17, 24, 23, 17, 18, 24, 18, 25, 24, 18, 19, 25, 19, 26, 25, 19, 20, 26, 20, 27, 26, 0, 28, 1, 1, 28, 2, 2, 28, 3, 3, 28, 4, 4, 28, 5, 5, 28, 6, 21, 22, 29, 22, 23, 29, 23, 24, 29, 24, 25, 29, 25, 26, 29, 26, 27, 29};
        Vector3f[] pTmp = new Vector3f[count * indices.length];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < indices.length; j++) {
                pTmp[i * indices.length + j] = positions[indices[j]];
            }
        }
        //mesh.setBuffer(VertexBuffer.Type.Index, 3, indices);
        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(pTmp));
        Geometry tmp = new Geometry("SpotLight", mesh);
        System.out.println(tmp.getMesh().getVertexCount());
        return tmp;

        /*Mesh pointLightMesh = new Mesh();
        Geometry geometry = new Geometry("PointLight", new Sphere(6, 6, 1));
        System.out.println(geometry.getMesh().getTriangleCount());
        Collection<Geometry> plGeo = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            plGeo.add(geometry.clone());
        }
        GeometryBatchFactory.mergeGeometries(plGeo, pointLightMesh);
        Geometry pointLightPatch = new Geometry("PointLights", pointLightMesh);
        return pointLightPatch;*/
    }
}
