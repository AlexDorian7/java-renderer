#version 330 core

in vec2 vTexCoord;
in vec4 vColor;

uniform sampler2D sampler0;

out vec4 FragColor;

void main() {
    vec4 texColor = texture(sampler0, vTexCoord);
    FragColor = texColor * vColor;
}
