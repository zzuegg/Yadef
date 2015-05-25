#extension GL_ARB_separate_shader_objects : enable

uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewProjectionMatrixInverse;
uniform vec2 g_Resolution;
uniform vec3 g_CameraPosition;

in vec3 lightColor;
in vec4 pointLightPositionRadius;

layout (location = 0) out vec3 lightOut;
layout (location = 1) out vec3 lightOutSpecular;

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
        //todo: should be optimized and use glsllibs for the calculations
        vec2 texSample=gl_FragCoord.xy/g_Resolution;
        vec3 worldNormal=texture(m_gbWorldNormal,texSample).xyz;
        vec3 worldPos=getPosition(texture(m_gbDepth,texSample).r,texSample);
        vec3 lightVector =pointLightPositionRadius.xyz-worldPos;
        vec3 lightDir = normalize(lightVector);
        float lambert = clamp(dot(worldNormal, g_NormalMatrix*lightDir), 0.0, 1.0);
        float dist = length(lightVector);
        float fallof = attenuation(pointLightPositionRadius.a, dist);


        vec3 incidenceVector = -lightDir; //a unit vector
        vec3 reflectionVector = reflect(g_NormalMatrix*incidenceVector, worldNormal); //also a unit vector
        vec3 surfaceToCamera = normalize(g_CameraPosition - worldPos); //also a unit vector
        float cosAngle = max(0.0, dot(g_NormalMatrix*surfaceToCamera, reflectionVector));
        float specularCoefficient = pow(cosAngle, 10);

        vec3 diffuseFactor=fallof*lambert*lightColor;

        lightOut=diffuseFactor;//vec4(diffuseFactor,specularCoefficient);
        lightOutSpecular=specularCoefficient*lightColor*fallof;
}