#extension GL_ARB_separate_shader_objects : enable

uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewProjectionMatrixInverse;
uniform vec2 g_Resolution;
in vec3 lightColor;
in vec4 pointLightPositionRadius;

layout (location = 0) out vec4 lightOut;

uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbWorldNormal;
uniform sampler2D m_gbDepth;

vec3 getPosition(in float depth, in vec2 uv){
    vec4 pos = vec4(uv, depth, 1.0) * 2.0 - 1.0;
    pos = g_ViewProjectionMatrixInverse * pos;
    return pos.xyz / pos.w;
}

float attenuation(float radius,float distance){
    float attenuation=max(radius-distance,0)/radius;
    return attenuation;
}

void main(){
        vec2 texSample=gl_FragCoord.xy/g_Resolution;
        vec3 worldNormal=texture(m_gbWorldNormal,texSample).xyz;
        //vec3 worldPos=texture(m_gbWorldPosLinearDepth,texSample).xyz;
        vec3 worldPos=getPosition(texture(m_gbDepth,texSample).r,texSample);
        vec3 lightVector =pointLightPositionRadius.xyz-worldPos;
        vec3 lightDir = normalize(lightVector);
        float lambert = clamp(dot(worldNormal, g_NormalMatrix*lightDir), 0.0, 1.0);
        float dist = length(lightVector);
        float fallof = attenuation(pointLightPositionRadius.a, dist);
        lightOut=vec4(fallof*lambert*lightColor,1);
        //lightOut=vec4(lightColor,1);
}