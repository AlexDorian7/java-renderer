#version 330 core

in vec3 position;
in vec4 color;
in vec2 texCoord;
in vec3 normal;
in vec3 tangent;
in vec3 bitangent;

uniform mat4 projection;
uniform mat4 modelView;

out vec2 vTexCoord;
out vec4 vColor;
out vec3 pos;
out vec3 nor;
out vec3 tan;
out vec3 bitan;

void main() {
    pos = position;
    nor = normal;
    tan = tangent;
    bitan = bitangent;
    vTexCoord = texCoord;
    vColor = color;
    vec4 viewPos = modelView * vec4(position, 1.0);
    gl_Position = projection * viewPos;
}
