package gg.zue.yadef.renderpasses.lighttechniques;

import com.jme3.asset.AssetManager;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.scene.shape.Box;
import gg.zue.yadef.GBuffer;
import gg.zue.yadef.renderpasses.LightTechnique;

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
        spotLightGeometry = new Geometry("SpotLight", new Box(new Vector3f(-1, 0, -1), new Vector3f(1, 1, 1)));
    }

    @Override
    public void render(GBuffer gBuffer, RenderManager renderManager, ArrayList<SpotLight> lightList) {

    }

    @Override
    public void renderDebug(GBuffer gBuffer, RenderManager renderManager, ArrayList<SpotLight> lightList) {
        Vector3f[] points = new Vector3f[8];
        for (int i = 0; i < 8; i++) {
            points[i] = new Vector3f();
        }
        for (SpotLight spotLight : lightList) {
            Vector3f direction = spotLight.getDirection();
            spotLightMaterial.setVector3("spotLightPosition", spotLight.getPosition());
            spotLightMaterial.setVector4("spotLightDirectionRange", new Vector4f(direction.x, direction.y, direction.z, spotLight.getSpotRange()));
            spotLightGeometry.setMaterial(spotLightMaterial);
            spotLightMaterial.getAdditionalRenderState().setWireframe(true);
            spotLightMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            renderManager.setForcedTechnique("DebugSpotLights");
            renderManager.renderGeometry(spotLightGeometry);
        }
    }

    private Geometry createFrustum(Vector3f[] pts, int i) {
        WireFrustum frustum = new WireFrustum(pts);
        Geometry frustumMdl = new Geometry("f", frustum);
        frustumMdl.setCullHint(Spatial.CullHint.Never);
        frustumMdl.setShadowMode(RenderQueue.ShadowMode.Off);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        frustumMdl.setMaterial(mat);
        switch (i) {
            case 0:
                frustumMdl.getMaterial().setColor("Color", ColorRGBA.Pink);
                break;
            case 1:
                frustumMdl.getMaterial().setColor("Color", ColorRGBA.Red);
                break;
            case 2:
                frustumMdl.getMaterial().setColor("Color", ColorRGBA.Green);
                break;
            case 3:
                frustumMdl.getMaterial().setColor("Color", ColorRGBA.Blue);
                break;
            default:
                frustumMdl.getMaterial().setColor("Color", ColorRGBA.White);
                break;
        }

        frustumMdl.updateGeometricState();
        return frustumMdl;
    }

}
