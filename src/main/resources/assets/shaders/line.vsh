#version 330 core

in vec3 position;
in vec4 color;

uniform mat4 projection;
uniform mat4 model;
uniform mat4 view;

out vec4 vColor;

void main() {
    vColor = color;
    vec4 viewPos = view * model * vec4(position, 1.0);
    gl_Position = projection * viewPos;
}
