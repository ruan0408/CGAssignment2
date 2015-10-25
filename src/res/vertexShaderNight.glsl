#version 120

//varying vec4 v;
//varying vec2 texCoord;
//
//void main() {
//    v = gl_ModelViewMatrix * gl_Vertex;
//    gl_TexCoord[0] = gl_MultiTexCoord0;
//    gl_Position = gl_ProjectionMatrix * v;
//
////    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
////    gl_FrontColor = gl_Color;
////    texCoord = vec2(gl_MultiTexCoord0); //will be interpolated.
//}

varying vec3 normal, lightDir, eyeVec;
varying vec2 texCoord;

void main()
{
	normal = gl_NormalMatrix * gl_Normal;
	texCoord = vec2(gl_MultiTexCoord0); //will be interpolated.

	vec3 vVertex = vec3(gl_ModelViewMatrix * gl_Vertex);

	lightDir = vec3(gl_LightSource[1].position.xyz - vVertex);
	eyeVec = -vVertex;

	gl_Position = ftransform();
}