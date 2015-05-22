uniform mat4 g_WorldViewProjectionMatrix;

out vec4 lightColorInnerAngle;
out vec4 lightPositionOuterAngle;
out vec4 lightDirectionRange;

uniform vec4 m_spotLightPositionAngle;
uniform vec4 m_spotLightDirectionRange;
uniform vec4 m_spotLightColorInnerAngle;

const float offsetMod[5]=float[5](0,1,1,1,1);
const vec3 upMod[5]=vec3[5](    vec3(0),vec3(1),vec3(1),vec3(-1),vec3(-1));
const vec3 leftMod[5]=vec3[5](  vec3(0),vec3(1),vec3(-1),vec3(-1),vec3(1));
void main(){
    int vertexId=gl_VertexID;
    lightColorInnerAngle=m_spotLightColorInnerAngle;
    lightPositionOuterAngle=m_spotLightPositionAngle;
    lightDirectionRange=m_spotLightDirectionRange;
    vec3 left=normalize(cross(m_spotLightDirectionRange.xyz,vec3(0,1,0)));
    vec3 up=cross(left,m_spotLightDirectionRange.xyz);

    vec3 mod=(left*leftMod[vertexId])+(up*upMod[vertexId]);
    vec3 origin=m_spotLightPositionAngle.xyz;
    vec3 centerPoint=origin+offsetMod[vertexId]*m_spotLightDirectionRange.xyz*m_spotLightDirectionRange.a;
    float range=m_spotLightDirectionRange.a;
    float sine=sin(m_spotLightPositionAngle.a*2);
    gl_Position=g_WorldViewProjectionMatrix*vec4(centerPoint+mod*sine*range,1);
}