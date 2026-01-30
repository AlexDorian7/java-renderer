#version 330 core

in vec2 vTexCoord;

uniform sampler2D color;
uniform sampler2D depth;

out vec4 FragColor;

void main() {
    vec4 texColor = texture(color, vTexCoord);
    FragColor = texColor;
}
