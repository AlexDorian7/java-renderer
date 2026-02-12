#version 330 core

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};

struct PointLight {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    vec3 attenuation;
};

struct DirectionalLight {
    vec3 direction;

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
uniform DirectionalLight directionalLight;
uniform PointLight lights[16];
uniform int lightAmount;

uniform vec3 viewPos;

out vec4 FragColor;



vec3 CalcDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDir)
{
    vec3 lightDir = normalize(light.direction);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    // combine results
    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, vTexCoord));
    vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, vTexCoord));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, vTexCoord));
    return (ambient + diffuse + specular);
}

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    // attenuation
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.attenuation.x + light.attenuation.y * distance +
    light.attenuation.z * (distance * distance));
    // combine results
    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, vTexCoord));
    vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, vTexCoord));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, vTexCoord));
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
}

void main() {
    vec4 color = texture(material.diffuse, vTexCoord);

    // properties
    vec3 norm = normalize(vNormal);
    vec3 viewDir = normalize(viewPos - vPos.xyz);

    // phase 1: Directional lighting
    vec3 result = CalcDirectionalLight(directionalLight, norm, viewDir);
    // phase 2: Point lights
    for(int i = 0; i < lightAmount; i++)
        result += CalcPointLight(lights[i], norm, vPos.xyz, viewDir);
    // phase 3: Spot light
    //result += CalcSpotLight(spotLight, norm, vPos.xyz, viewDir);

    FragColor = vec4(result, color.a);
    if (FragColor.a < 0.001) discard;
}
