#extension GL_ARB_separate_shader_objects : enable
uniform mat3 g_NormalMatrix;
in vec3 normal;
in vec3 binormal;
in vec3 tangent;
in vec4 position;
in vec2 texCoord;

//layout (location = 0) out vec4 WorldPosDepthOut;
layout (location = 0) out vec4 NormalOut;
layout (location = 1) out vec3 DiffuseOut;
layout (location = 2) out vec3 SpecularOut;

uniform sampler2D m_diffuseTexture;
uniform sampler2D m_normalTexture;

void main(){
    //WorldPosDepthOut=position;
    vec3 textureNormal = normalize(texture2D(m_normalTexture, texCoord).xyz * vec3(2.0,-2.0,2.0) - vec3(1.0,-1.0,1.0));
    NormalOut=vec4(g_NormalMatrix*normalize(tangent * textureNormal.x + binormal * textureNormal.y + normal * textureNormal.z),1);
    //NormalOut=vec4(normal,1);
    vec3 diffuse=texture(m_diffuseTexture,texCoord).xyz;
    DiffuseOut=diffuse;
    SpecularOut=vec3(1,0,0);
}