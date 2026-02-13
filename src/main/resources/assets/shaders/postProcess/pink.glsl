#version 330 core

in vec2 vTexCoord;

uniform sampler2D color;
uniform sampler2D depth;

out vec4 FragColor;

void main() {
    vec4 texColor = texture(color, vTexCoord);
    float avg = (texColor.r * texColor.g + texColor.b)/3;
    FragColor = vec4(vec3(1, 0, .75) * avg, texColor.a);
}
