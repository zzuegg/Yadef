#extension GL_ARB_separate_shader_objects : enable

uniform mat3 g_NormalMatrix;
uniform vec2 g_Resolution;

in vec4 lightColorInnerAngle;
in vec4 lightPositionOuterAngle;
in vec4 lightDirectionRange;

layout (location = 0) out vec4 lightOut;

uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbWorldNormal;

float attenuation(float radius,float distance){
    float attenuation=max(radius-distance,0)/radius;
    return attenuation;
}

void main(){
        vec2 texSample=gl_FragCoord.xy/g_Resolution;
        vec3 worldNormal=texture(m_gbWorldNormal,texSample).xyz;
        vec3 worldPos=texture(m_gbWorldPosLinearDepth,texSample).xyz;

        vec3 surfaceToLight=lightPositionOuterAngle.xyz-worldPos;
        vec3 lightDirection =lightDirectionRange.xyz;
        lightDirection = normalize(lightDirection);


        float lambert = clamp(dot(worldNormal, g_NormalMatrix*normalize(surfaceToLight)), 0.0, 1.0);

        float distanceFallof = attenuation(lightDirectionRange.a, length(surfaceToLight));
        float outerAngleCos = cos(lightPositionOuterAngle.a);
        float currAngleCos = dot(-lightDirection, normalize(surfaceToLight));
        float innerAngleCos = cos(lightColorInnerAngle.a);

        float angleFallof = clamp((currAngleCos-outerAngleCos)/(innerAngleCos-outerAngleCos), 0.0, 1.0);
        vec4 color=vec4(lightColorInnerAngle.xyz,1);
        color=color*distanceFallof*angleFallof*lambert;
        lightOut=color;
}