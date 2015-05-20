uniform mat3 g_NormalMatrix;

uniform vec4[200] m_pointLightPositionRadius;
uniform vec3[200] m_pointLightColors;
uniform int m_lightCount;

uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbWorldNormal;

in vec2 fragTexCoord;



layout (location = 0) out vec4 lightOut;

float linear_fallof(float D, float r, float kl){
  return D / (D + kl * r);
}
float quad_fallof(float D, float r, float kq){
  float D2 = D*D;
  return D2 / (D2 + kq * r * r);
}
float fallof(float D, float r, float kl, float kq){
  return linear_fallof(D, r, kl) * quad_fallof(D, r, kq);
}

float attenuation(float radius,float distance){
    float attenuation=max(radius-distance,0)/radius;
    return attenuation;
}


void main(){
    vec3 worldNormal=texture(m_gbWorldNormal,fragTexCoord).xyz;
    vec3 worldPos=texture(m_gbWorldPosLinearDepth,fragTexCoord).xyz;
    for(int i=0;i<m_lightCount;i++){
        vec3 lightVector =m_pointLightPositionRadius[i].xyz-worldPos;
        vec3 lightDir = normalize(lightVector);
        float lambert = clamp(dot(worldNormal, g_NormalMatrix*lightDir), 0.0, 1.0);
        float dist = length(lightVector);
        float fallof = attenuation(m_pointLightPositionRadius[i].a, dist);
        lightOut+=vec4(fallof*lambert*m_pointLightColors[i],1);
    }
    //lightOut=vec4(fragTexCoord,0,0);
}