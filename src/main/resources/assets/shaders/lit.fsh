#version 330 core

struct Material {
    vec3 ambient;
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};

struct Light {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

in vec2 vTexCoord;
in vec4 vColor;
in vec3 vNormal;
in vec4 vPos;

uniform sampler2D sampler0;

uniform Material material;
uniform Light light;

uniform vec3 ambientColor;
uniform vec3 viewPos;

out vec4 FragColor;

void main() {
    vec4 color = texture(material.diffuse, vTexCoord);

    vec3 norm = normalize(vNormal);
    vec3 lightDir = normalize(light.position - vPos.xyz);

    vec3 viewDir = normalize(viewPos - vPos.xyz);
    vec3 reflectDir = reflect(-lightDir, norm);

    // Ambient
    vec3 ambient = material.ambient * color.rgb * light.ambient;

    // Diffuse
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * diff * color.rgb;

    // Specular
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = light.specular * spec * texture(material.specular, vTexCoord).rgb;



    FragColor = vColor * vec4(ambient + diffuse + specular, color.a);
//    FragColor = vec4(normalize(vNormal) * 0.5 + 0.5, 1.0);
    if (FragColor.a < 0.001) discard;
}
