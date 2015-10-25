#version 120

//varying vec4 v;
//
////varying vec2 texCoord;
//uniform sampler2D colorMap;
//
//void main() {
//    vec3 lightDir;
//    float dist;
//
//    /* Compute the ambient and globalAmbient terms */
//    ambient =  gl_LightSource[0].ambient * gl_FrontMaterial.ambient;
//    globalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;
//
//    lightDir = vec3(gl_LightSource[1].position-v);
//    dist = length(lightDir);
//    gl_FragColor = (2.0/(dist*dist))*texture2D(colorMap, gl_TexCoord[0].st);
//    if(dist < 1.0){
//        gl_FragColor = dist*texture2D(colorMap, gl_TexCoord[0].st);
//    }
//
//    //This would implement replace mode
//    //gl_FragColor = texture2D(colorMap,texCoord);
//}

varying vec3 normal, lightDir, eyeVec;
uniform sampler2D colorMap;
varying vec2 texCoord;

void main (void)
{
	vec4 final_color =
	    (gl_FrontLightModelProduct.sceneColor * gl_FrontMaterial.ambient) +
	    (gl_LightSource[1].ambient * gl_FrontMaterial.ambient);

	vec3 N = normalize(normal);
	vec3 L = normalize(lightDir);
	vec3 D = normalize(gl_LightSource[1].spotDirection);

	float lambertTerm = dot(N,L);

	if((dot(-L, D) > gl_LightSource[1].spotCosCutoff) && (lambertTerm > 0.0))
	{
		final_color += gl_LightSource[1].diffuse * gl_FrontMaterial.diffuse * lambertTerm;

//		vec3 E = normalize(eyeVec);
//		vec3 R = reflect(-L, N);
//		float specular = pow( max(dot(R, E), 0.0),
//		                 gl_FrontMaterial.shininess );
//		final_color += gl_LightSource[1].specular *
//		               gl_FrontMaterial.specular *
//					   specular;
	}

	gl_FragColor = texture2D(colorMap, texCoord) * final_color;
}