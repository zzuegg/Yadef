in vec4 inPosition;
in vec3 inColor;

out vec3 geoColor;

void main(){
    gl_Position=inPosition;
    geoColor=inColor;
}