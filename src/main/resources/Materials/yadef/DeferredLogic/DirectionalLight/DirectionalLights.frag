#extension GL_ARB_separate_shader_objects : enable
uniform mat3 g_NormalMatrix;

uniform vec3[200] m_lightDirections;
uniform vec3[200] m_lightColors;
uniform int m_lightCount;

uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbWorldNormal;

in vec2 fragTexCoord;



layout (location = 0) out vec4 lightOut;

void main(){
    vec3 worldNormal=texture(m_gbWorldNormal,fragTexCoord).xyz;
    for(int i=0;i<m_lightCount;i++){
        float lambert = clamp(dot(worldNormal, g_NormalMatrix*normalize(m_lightDirections[i]*-1.0)), 0.0, 1.0);
        //float lightPower=max(dot(normalize((g_NormalMatrix*(normalize(m_lightDirections[i]*vec3(-1))))).xyz,normalize(worldNormal)),0);
        lightOut += vec4(lambert*m_lightColors[i],0);
    }

    //lightOut=normalize(lightOut);
    //lightOut=vec4(fragTexCoord,0,0);
}