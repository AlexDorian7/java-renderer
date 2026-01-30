#version 330 core

in vec2 vTexCoord;

uniform sampler2D depth;

out vec4 FragColor;

void main() {
    float d = texture(depth, vTexCoord).r;
    FragColor = vec4(vec3(d), 1.0);
}
