#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec4 color;
layout (location = 3) in vec3 normal;
layout (location = 4) in vec3 tangent;
layout (location = 5) in vec3 bitangent;

uniform mat4 projection;
uniform mat4 modelView;

out vec2 vTexCoord;
out vec4 vColor;

void main() {
    vTexCoord = texCoord;
    vColor = color;
    vec4 viewPos = modelView * vec4(position, 1.0);
    gl_Position = projection * viewPos;
}
