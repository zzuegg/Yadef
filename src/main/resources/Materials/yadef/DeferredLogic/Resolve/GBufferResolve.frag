in vec2 fragTexCoord;
uniform sampler2D m_gbWorldNormal;
uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbLight;
uniform sampler2D m_gbAlbedo;
void main(){
    vec4 light=texture(m_gbLight,fragTexCoord);
    vec4 albedo=texture(m_gbAlbedo,fragTexCoord);
    gl_FragColor=vec4((light.xyz*albedo.xyz)+(light.a*light.xyz*albedo.xyz),1);
    //gl_FragColor=texture(m_gbWorldNormal,fragTexCoord);
}