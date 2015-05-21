in vec2 fragTexCoord;
uniform sampler2D m_gbOutput;
void main(){
    gl_FragColor=texture(m_gbOutput,fragTexCoord);
}