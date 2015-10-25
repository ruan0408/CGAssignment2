#version 120

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

	}

	gl_FragColor = texture2D(colorMap, texCoord) * final_color;
}