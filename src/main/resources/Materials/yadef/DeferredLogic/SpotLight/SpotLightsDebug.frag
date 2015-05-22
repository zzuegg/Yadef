in vec4 lightColorInnerAngle;
in vec4 lightPositionOuterAngle;
in vec4 lightDirectionRange;

void main(){
        gl_FragColor=vec4(lightColorInnerAngle.xyz,1);
}