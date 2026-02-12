#version 330 core

in vec3 position;
in vec4 color;
in vec2 texCoord;
in vec3 normal;
in vec3 tangent;

uniform mat4 projection;
uniform mat4 model;
uniform mat4 view;

out vec2 vTexCoord;
out vec4 vColor;
out vec4 vPos;
out vec3 vNormal;

void main() {
    vTexCoord = texCoord;
    vColor = color;
    vNormal = mat3(transpose(inverse(model))) * normal;
    vec4 worldPos = model * vec4(position, 1.0);
    vPos = worldPos;
    gl_Position = projection * view * worldPos;
}