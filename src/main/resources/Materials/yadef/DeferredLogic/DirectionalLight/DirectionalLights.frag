#extension GL_ARB_separate_shader_objects : enable
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewProjectionMatrixInverse;
uniform vec3 g_CameraPosition;

uniform vec3[MAX_LIGHTS] m_lightDirections;
uniform vec3[MAX_LIGHTS] m_lightColors;
uniform int m_lightCount;

uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbWorldNormal;
uniform sampler2D m_gbDepth;

in vec2 fragTexCoord;



layout (location = 0) out vec4 lightOut;

vec3 getPosition(in float depth, in vec2 uv){
    vec4 pos = vec4(uv, depth, 1.0) * 2.0 - 1.0;
    pos = g_ViewProjectionMatrixInverse * pos;
    return pos.xyz / pos.w;
}

void main(){
    vec3 worldNormal=texture(m_gbWorldNormal,fragTexCoord).xyz;
    vec3 worldPos=getPosition(texture(m_gbDepth,fragTexCoord).r,fragTexCoord);
    for(int i=0;i<m_lightCount;i++){
        float lambert = clamp(dot(worldNormal, g_NormalMatrix*normalize(m_lightDirections[i]*-1.0)), 0.0, 1.0);

        vec3 incidenceVector = normalize(m_lightDirections[i]); //a unit vector
        vec3 reflectionVector = reflect(g_NormalMatrix*incidenceVector, worldNormal); //also a unit vector
        vec3 surfaceToCamera = normalize(g_CameraPosition - worldPos); //also a unit vector
        float cosAngle = max(0.0, dot(g_NormalMatrix*surfaceToCamera, reflectionVector));
        float specularCoefficient = pow(cosAngle, 10);
        //specularCoefficient=specularCoefficient*angleFallof*distanceFallof;
        //lightOut +=vec4(specularCoefficient,0,0,0);
        lightOut += vec4(lambert*m_lightColors[i],specularCoefficient);
    }

    //lightOut=normalize(lightOut);
    //lightOut=vec4(fragTexCoord,0,0);
}