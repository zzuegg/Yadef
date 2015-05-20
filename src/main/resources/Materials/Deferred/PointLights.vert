uniform mat4 g_WorldViewProjectionMatrix;

in vec3 inPosition;

out vec3 lightColor;
out vec4 pointLightPositionRadius;
uniform int m_lightCount;
uniform vec4[500] m_pointLightPositionRadius;
uniform vec3[500] m_pointLightColors;

const int vertCount=30;

void main(){
    int lightId=int(gl_VertexID/vertCount);
    if(lightId>m_lightCount){
        gl_Position=vec4(-2,-2,-2,-2);
    }else{
    pointLightPositionRadius=m_pointLightPositionRadius[lightId];
    lightColor=m_pointLightColors[lightId];
    gl_Position=g_WorldViewProjectionMatrix*vec4(pointLightPositionRadius.xyz+(inPosition*pointLightPositionRadius.a),1);
    }
}