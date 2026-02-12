#version 330 core

in vec3 position;
in vec4 color;
in vec2 texCoord;

uniform mat4 projection;
uniform mat4 model;
uniform mat4 view;

out vec2 vTexCoord;
out vec4 vColor;
out vec4 vPos;

void main() {
    vTexCoord = texCoord;
    vColor = color;
    vec4 worldPos = model * vec4(position, 1.0);
    vPos = worldPos;
    gl_Position = projection * view * worldPos;
}