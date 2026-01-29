#version 330 core

in vec2 vTexCoord;
in vec4 vColor;
in vec3 pos;
in vec3 nor;
in vec3 tan;
in vec3 bitan;

out vec4 FragColor;

void main() {
    FragColor = vec4((nor+1)/2, 1);
}
