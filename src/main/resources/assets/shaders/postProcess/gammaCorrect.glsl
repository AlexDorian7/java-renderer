#version 330 core

in vec2 vTexCoord;

uniform sampler2D color;
uniform sampler2D depth;

out vec4 FragColor;

void main() {
    float gamma = 1.5;

    vec4 texColor = texture(color, vTexCoord);
    texColor.rgb = pow(texColor.rgb, vec3(1.0/gamma));
    FragColor = texColor;
}
