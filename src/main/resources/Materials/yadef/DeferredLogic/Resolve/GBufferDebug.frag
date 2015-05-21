in vec2 fragTexCoord;
uniform sampler2D m_gbWorldNormal;
uniform sampler2D m_gbWorldPosLinearDepth;
uniform sampler2D m_gbLight;
uniform sampler2D m_gbAlbedo;
uniform sampler2D m_gbOutput;
void main(){
    vec2 sampleTexCoord=fragTexCoord;
    if(fragTexCoord.x>0.8){
        float texId=sampleTexCoord.y*5;
        sampleTexCoord.x=(sampleTexCoord.x-0.8)*5.0;
        sampleTexCoord.y=mod(sampleTexCoord.y*5,1);

        if(texId>4){
            gl_FragColor=vec4(texture(m_gbWorldPosLinearDepth,sampleTexCoord).xyz,1);
        }else if(texId>3){
            gl_FragColor=vec4(texture(m_gbWorldNormal,sampleTexCoord).xyz,1);
        }else if(texId>2){
            gl_FragColor=vec4(texture(m_gbLight,sampleTexCoord).xyz,1);
        }else if(texId>1){
            gl_FragColor=vec4(texture(m_gbAlbedo,sampleTexCoord).xyz,1);
        }else if(texId>0){
            gl_FragColor=texture(m_gbOutput,sampleTexCoord);
            //gl_FragColor=vec4(depth,depth,depth,1);
        }
    }else{
        discard;
    }
}