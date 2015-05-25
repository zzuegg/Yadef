in vec2 fragTexCoord;
uniform sampler2D m_gbWorldNormal;
uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbLight;
uniform sampler2D m_gbLightSpecular;
uniform sampler2D m_gbAlbedo;
void main(){
    vec3 light=texture(m_gbLight,fragTexCoord).xyz;
    vec3 lightSpecular=texture(m_gbLightSpecular,fragTexCoord).xyz;
    vec4 albedo=texture(m_gbAlbedo,fragTexCoord);

    gl_FragColor=vec4((light.xyz*albedo.xyz)+(lightSpecular*albedo.xyz),1);
    //gl_FragColor=texture(m_gbWorldNormal,fragTexCoord);
}