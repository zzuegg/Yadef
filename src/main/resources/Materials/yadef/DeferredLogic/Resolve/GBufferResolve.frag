in vec2 fragTexCoord;
uniform sampler2D m_gbLight;
uniform sampler2D m_gbLightSpecular;
uniform sampler2D m_gbAlbedo;
uniform sampler2D m_gbSpecular;
void main(){
    vec3 light=texture(m_gbLight,fragTexCoord).xyz;
    vec3 lightSpecular=texture(m_gbLightSpecular,fragTexCoord).xyz;
    vec3 albedo=texture(m_gbAlbedo,fragTexCoord).xyz;
    vec3 specular=texture(m_gbSpecular,fragTexCoord).xyz;
    gl_FragColor=vec4((light.xyz*albedo.xyz)+(lightSpecular*specular.xyz),1);
}