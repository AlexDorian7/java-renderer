#version 330 core

in vec2 vTexCoord;

uniform sampler2D color;
uniform sampler2D depth;

out vec4 FragColor;

void main() {
    float exposure = 1.2;

    vec4 texColor = texture(color, vTexCoord);
    texColor.rgb = vec3(1) - exp(-texColor.rgb * exposure);
    FragColor = texColor;
}
