in vec3 inPosition;
in vec2 inTexCoord;
out vec2 fragTexCoord;

void main(){
    gl_Position=vec4(inPosition.xy*2-1,0.0,1.0);
    fragTexCoord=inTexCoord.xy;
}