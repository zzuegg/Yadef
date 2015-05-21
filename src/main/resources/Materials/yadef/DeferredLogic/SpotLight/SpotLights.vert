uniform mat4 g_WorldViewProjectionMatrix;

in vec3 inPosition;

out vec3 lightColor;

uniform vec3 m_spotLightPosition;
uniform vec4 m_spotLightDirectionRange;

const int vertCount=30;

void main(){
    //vec3 position=m_spotLightPosition+(m_spotLightDirectionRange.xyz*vec3(mix(0,m_spotLightDirectionRange.a,inPosition.z-1)));
    vec3 position=vec3(inPosition.x,inPosition.y,inPosition.z);
    lightColor=vec3(position.y,position.y,position.y);
    float factor=position.y;
    vec3 xyOffset=cross(m_spotLightDirectionRange.xyz*-1,vec3(inPosition.x,0,inPosition.y));
    position=m_spotLightPosition+(xyOffset*m_spotLightDirectionRange.xyz*position.y*m_spotLightDirectionRange.a);
    gl_Position=g_WorldViewProjectionMatrix*vec4(position,1);
}