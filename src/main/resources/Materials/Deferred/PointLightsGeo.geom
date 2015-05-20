layout(points) in;
layout(triangle_strip, max_vertices = 4) out;

uniform mat4 g_WorldViewProjectionMatrix;
uniform vec3 g_CameraLeft;
uniform vec3 g_CameraUp;
uniform vec3 g_CameraDirection;
in vec3 geoColor[];
out vec4 pointLightPositionRadius;
out vec3 lightColor;
out vec2 samplePos;

void main(){
  vec4 center= gl_in[0].gl_Position;
  vec3 position=center.xyz;
  float size=center.a;
  center=center+vec4(g_CameraDirection,0)*size;
  //vec3 toCamera = normalize(g_CameraPosition - position);
  //vec3 upVector = vec3(0.0, 1.0, 0.0);
  //vec3 right = cross(toCamera, upVector)*size;
  //vec3 up=cross(toCamera*vec3(-1),normalize(right))*size;
  vec3 right=g_CameraLeft*size*2;
  vec3 up = g_CameraUp*size*2;
  position = (center.xyz-up+right);
  vec4 screenPos=g_WorldViewProjectionMatrix * vec4(position, 1.0);
  gl_Position =screenPos;
  lightColor=geoColor[0];
  samplePos=(screenPos.xy+vec2(1,1))*0.5;
  pointLightPositionRadius=gl_in[0].gl_Position;
  EmitVertex();

  position = (center.xyz-up-right);
  screenPos=g_WorldViewProjectionMatrix * vec4(position, 1.0);
  gl_Position =screenPos;
  lightColor=geoColor[0];
  samplePos=(screenPos.xy+vec2(1,1))*0.5;
  pointLightPositionRadius=gl_in[0].gl_Position;
  EmitVertex();

  position = (center.xyz+up+right);
  screenPos=g_WorldViewProjectionMatrix * vec4(position, 1.0);
  gl_Position =screenPos;
  lightColor=geoColor[0];
  samplePos=(screenPos.xy+vec2(1,1))*0.5;
  pointLightPositionRadius=gl_in[0].gl_Position;
  EmitVertex();

  position = (center.xyz+up-right);
  screenPos=g_WorldViewProjectionMatrix * vec4(position, 1.0);
  gl_Position =screenPos;
  lightColor=geoColor[0];
  samplePos=(screenPos.xy+vec2(1,1))*0.5;
  pointLightPositionRadius=gl_in[0].gl_Position;
  EmitVertex();

  EndPrimitive();
}