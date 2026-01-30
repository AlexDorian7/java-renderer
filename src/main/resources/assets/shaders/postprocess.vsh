#version 330 core

in vec3 position;
in vec2 texCoord;

out vec2 vTexCoord;
out vec4 vPos;

void main() {
    vTexCoord = texCoord;
    gl_Position = vec4(position.xy, 0, 1);
}
