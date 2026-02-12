#version 330 core

in vec3 position;
in vec4 color;
in vec2 texCoord;
in vec3 normal;
in vec3 tangent;

uniform mat4 projection;
uniform mat4 model;
uniform mat4 view;

uniform vec3 viewPos;

out vec2 vTexCoord;
out vec4 vColor;
out vec4 vPos;
out mat3 TBN;
out vec3 TangentViewPos;
out vec3 TangentFragPos;

void main() {
    mat3 normalMatrix = mat3(transpose(inverse(model)));

    vec3 bitangant = normalize(cross(normal, tangent));
    vec3 T = normalMatrix * tangent;
    vec3 B = normalMatrix * bitangant;
    vec3 N = normalMatrix * normal;
    TBN = mat3(T, B, N);

    mat3 TTBN = transpose(TBN);

    vec4 worldPos = model * vec4(position, 1.0);

    TangentViewPos = TTBN * viewPos;
    TangentFragPos = TTBN * worldPos.xyz;

    vTexCoord = texCoord;
    vColor = color;

    vPos = worldPos;
    gl_Position = projection * view * worldPos;
}