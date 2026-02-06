#version 330 core

in vec3 position;
in vec4 color;
in vec2 texCoord;
in vec3 normal;
in vec3 tangent;

uniform mat4 projection;
uniform mat4 modelView;

out vec2 vTexCoord;
out vec4 vColor;
out vec4 vPos;

void main() {
    vTexCoord = texCoord;
    vColor = color;
    vec4 viewPos = modelView * vec4(position, 1.0);
    vPos = viewPos;
    gl_Position = projection * viewPos;
}