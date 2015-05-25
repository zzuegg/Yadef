#extension GL_ARB_separate_shader_objects : enable
uniform mat4 g_ViewProjectionMatrixInverse;
uniform mat3 g_NormalMatrix;
uniform vec2 g_Resolution;
uniform vec3 g_CameraPosition;

in vec4 lightColorInnerAngle;
in vec4 lightPositionOuterAngle;
in vec4 lightDirectionRange;

layout (location = 0) out vec3 lightOut;
layout (location = 1) out vec3 lightOutSpecular;

//uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbDepth;
uniform sampler2D m_gbWorldNormal;

float attenuation(float radius,float distance){
    float attenuation=max(radius-distance,0)/radius;
    return attenuation;
}

vec3 getPosition(in float depth, in vec2 uv){
    vec4 pos = vec4(uv, depth, 1.0) * 2.0 - 1.0;
    pos = g_ViewProjectionMatrixInverse * pos;
    return pos.xyz / pos.w;
}

void main(){
        //todo: should be optimized and use glsllibs for the calculations
        vec2 texSample=gl_FragCoord.xy/g_Resolution;
        vec3 worldNormal=texture(m_gbWorldNormal,texSample).xyz;
        vec3 worldPos=getPosition(texture(m_gbDepth,texSample).r,texSample);
        vec3 surfaceToLight=lightPositionOuterAngle.xyz-worldPos;
        vec3 lightDirection =lightDirectionRange.xyz;
        lightDirection = normalize(lightDirection);


        float lambert = clamp(dot(worldNormal, g_NormalMatrix*normalize(surfaceToLight)), 0.0, 1.0);

        float distanceFallof = attenuation(lightDirectionRange.a, length(surfaceToLight));
        float outerAngleCos = cos(lightPositionOuterAngle.a);
        float currAngleCos = dot(-lightDirection, normalize(surfaceToLight));
        float innerAngleCos = cos(lightColorInnerAngle.a);

        float angleFallof = clamp((currAngleCos-outerAngleCos)/(innerAngleCos-outerAngleCos), 0.0, 1.0);
        vec3 color=vec3(lightColorInnerAngle.xyz);
        color=color*distanceFallof*angleFallof*lambert;

        vec3 incidenceVector = lightDirection; //a unit vector
        vec3 reflectionVector = reflect(g_NormalMatrix*incidenceVector, worldNormal); //also a unit vector
        vec3 surfaceToCamera = normalize(g_CameraPosition - worldPos); //also a unit vector
        float cosAngle = max(0.0, dot(g_NormalMatrix*surfaceToCamera, reflectionVector));
        float specularCoefficient = pow(cosAngle, 10);
        specularCoefficient=specularCoefficient*angleFallof*distanceFallof;
        lightOut=color;//,specularCoefficient);//specularCoefficient*angleFallof*distanceFallof);
        lightOutSpecular=lightColorInnerAngle.xyz*specularCoefficient;
}