#version 120

varying vec4 v;

varying vec2 texCoord;

void main() {
    v = gl_ModelViewMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = ftransform();

//    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
//    gl_FrontColor = gl_Color;
//    texCoord = vec2(gl_MultiTexCoord0); //will be interpolated.
}