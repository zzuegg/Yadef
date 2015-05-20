in vec2 fragTexCoord;
uniform sampler2D m_gbWorldNormal;
uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbLight;
uniform sampler2D m_gbAlbedo;
void main(){

    gl_FragColor=texture(m_gbLight,fragTexCoord)*texture(m_gbAlbedo,fragTexCoord);
}