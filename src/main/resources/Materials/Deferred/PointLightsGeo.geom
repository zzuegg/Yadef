layout(points) in;
layout(triangle_strip, max_vertices = 24) out;

uniform mat4 g_WorldViewProjectionMatrix;
uniform vec3 g_CameraLeft;
uniform vec3 g_CameraUp;
uniform vec3 g_CameraDirection;
in vec3 geoColor[];
out vec4 pointLightPositionRadius;
out vec3 lightColor;



void emitQuad(vec3 center,float size){
  vec3 right=g_CameraLeft*size;
  vec3 up = g_CameraUp*size;
  vec3 position = (center.xyz-up+right);
  vec4 screenPos=g_WorldViewProjectionMatrix * vec4(position, 1.0);
  gl_Position =screenPos;
  lightColor=geoColor[0];
  pointLightPositionRadius=gl_in[0].gl_Position;
  EmitVertex();

  position = (center.xyz-up-right);
  screenPos=g_WorldViewProjectionMatrix * vec4(position, 1.0);
  gl_Position =screenPos;
  lightColor=geoColor[0];
  pointLightPositionRadius=gl_in[0].gl_Position;
  EmitVertex();

  position = (center.xyz+up+right);
  screenPos=g_WorldViewProjectionMatrix * vec4(position, 1.0);
  gl_Position =screenPos;
  lightColor=geoColor[0];
  pointLightPositionRadius=gl_in[0].gl_Position;
  EmitVertex();

  position = (center.xyz+up-right);
  screenPos=g_WorldViewProjectionMatrix * vec4(position, 1.0);
  gl_Position =screenPos;
  lightColor=geoColor[0];
  pointLightPositionRadius=gl_in[0].gl_Position;
  EmitVertex();

  EndPrimitive();
}

const vec4 cubeVerts[8] = vec4[8](
    vec4(-1 , -1, -1,1),  //LB   0
     vec4(-1, 1, -1,1), //L T   1
    vec4(1, -1, -1,1), //R B    2
    vec4( 1, 1, -1,1),  //R T   3
                        //back face
    vec4(-1, -1, 1,1), // LB  4
     vec4(-1, 1, 1,1), // LT  5
    vec4(1, -1, 1,1),  // RB  6
     vec4(1, 1, 1,1)  // RT  7
    );

const int  cubeIndices[24]  = int [24]
    (
      0,1,2,3, //front
      7,6,3,2, //right
      7,5,6,4,  //back or whatever
      4,0,6,2, //btm
      1,0,5,4, //left
      3,1,7,5
    );

void emitBox(vec3 center, float size){
  vec4 centerPos=vec4(center,0);
  for(int i=0;i<24;i=i+4){
    for(int x=i;x<i+4;x++){
      pointLightPositionRadius=gl_in[0].gl_Position;
      lightColor=geoColor[0];
      gl_Position=g_WorldViewProjectionMatrix*vec4(center+(cubeVerts[cubeIndices[x]].xyz*size),1);
      EmitVertex();
    }
    EndPrimitive();
  }
}

void main(){
  vec4 center= gl_in[0].gl_Position;
  vec3 position=center.xyz;
  float size=center.a;


  //emitQuad(center.xyz,size);
  emitBox(center.xyz,size);
}

