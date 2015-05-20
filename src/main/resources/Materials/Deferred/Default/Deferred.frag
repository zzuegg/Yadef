in vec3 normal;
in vec3 binormal;
in vec3 tangent;
in vec4 position;
in vec2 texCoord;

layout (location = 0) out vec4 WorldPosDepthOut;
layout (location = 1) out vec4 NormalOut;
layout (location = 2) out vec4 DiffuseOut;
layout (location = 3) out vec4 SpecularOut;

void main(){
    WorldPosDepthOut=position;
    NormalOut=vec4(normal,1);
    DiffuseOut=vec4(0.5,0.5,0.5,0);
    SpecularOut=vec4(0,1,0,0);
}