#version 120

varying vec4 v;

varying vec2 texCoord;
uniform sampler2D colorMap;

void main() {
    vec3 lightDir;
    float dist;
    lightDir = vec3(gl_LightSource[1].position-v);
    dist = length(lightDir);
    gl_FragColor = (2.0/(dist*dist))*texture2D(colorMap, gl_TexCoord[0].st);
    if(dist < 1.0){
        gl_FragColor = dist*texture2D(colorMap, gl_TexCoord[0].st);
    }

    //This would implement replace mode
    //gl_FragColor = texture2D(colorMap,texCoord);
}