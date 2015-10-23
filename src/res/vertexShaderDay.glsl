#version 120

varying vec3 N;
varying vec4 v;

void main() {
    v = gl_ModelViewMatrix * gl_Vertex;
    N = vec3(normalize(gl_NormalMatrix * normalize(gl_Normal)));
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = ftransform();
}