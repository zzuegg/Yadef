uniform mat3 g_NormalMatrix;
uniform mat4 g_WorldMatrix;
uniform mat4 g_WorldViewProjectionMatrix;

in vec3 inPosition;
in vec3 inNormal;
in vec2 inTexCoord;
in vec4 inTangent;

out vec3 normal;
out vec3 binormal;
out vec3 tangent;
out vec4 position;
out vec2 texCoord;



void main(){
    vec4 pos=vec4(inPosition,1.0);
    gl_Position=g_WorldViewProjectionMatrix * pos;
    position=g_WorldMatrix*pos;
    texCoord=inTexCoord;
    normal=g_NormalMatrix*inNormal;
    tangent=inTangent.xyz;
    binormal=cross(tangent,normal);
}

