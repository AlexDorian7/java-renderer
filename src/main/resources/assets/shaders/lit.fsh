#version 330 core

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    sampler2D normal;
    sampler2D height;
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
in vec4 vPos;
in mat3 TBN;
in vec3 TangentViewPos;
in vec3 TangentFragPos;

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

float sampleHeightMap(vec2 texCoord) {
    return -1 * (texture(material.height, texCoord) * 2 - 1).r;
}

vec2 ParallaxMapping(vec2 texCoords, vec3 viewDir)
{
    const float height_scale = 1;

    // number of depth layers
    const float minLayers = 8.0;
    const float maxLayers = 32.0;
    float numLayers = mix(maxLayers, minLayers, max(dot(vec3(0.0, 0.0, 1.0), viewDir), 0.0));

    // calculate the size of each layer
    float layerDepth = 1.0 / numLayers;
    // depth of current layer
    float currentLayerDepth = 0.0;
    // the amount to shift the texture coordinates per layer (from vector P)
    vec2 P = viewDir.xy * height_scale;
    vec2 deltaTexCoords = P / numLayers;

    // get initial values
    vec2  currentTexCoords     = texCoords;
    float currentDepthMapValue = sampleHeightMap(currentTexCoords);

    while(currentLayerDepth < currentDepthMapValue)
    {
        // shift texture coordinates along direction of P
        currentTexCoords -= deltaTexCoords;
        // get depthmap value at current texture coordinates
        currentDepthMapValue = sampleHeightMap(currentTexCoords);
        // get depth of next layer
        currentLayerDepth += layerDepth;
    }

    // get texture coordinates before collision (reverse operations)
    vec2 prevTexCoords = currentTexCoords + deltaTexCoords;

    // get depth after and before collision for linear interpolation
    float afterDepth  = currentDepthMapValue - currentLayerDepth;
    float beforeDepth = sampleHeightMap(prevTexCoords) - currentLayerDepth + layerDepth;

    // interpolation of texture coordinates
    float weight = afterDepth / (afterDepth - beforeDepth);
    vec2 finalTexCoords = prevTexCoords * weight + currentTexCoords * (1.0 - weight);

    return finalTexCoords;
}

void main() {
    vec3 tangentViewDir   = normalize(TangentViewPos - TangentFragPos);
    vec2 texCoords = ParallaxMapping(vTexCoord,  tangentViewDir);

    vec4 color = texture(material.diffuse, texCoords);

    // properties
    vec3 norm = texture(material.normal, texCoords).rgb;
    norm = norm * 2.0 - 1.0;
    norm = normalize(TBN * norm); // future maybe do this in vertex shader?
    vec3 viewDir = normalize(viewPos - vPos.xyz);

    // phase 1: Directional lighting
    vec3 result = CalcDirectionalLight(directionalLight, norm, viewDir);
    // phase 2: Point lights
    for(int i = 0; i < lightAmount; i++)
        result += CalcPointLight(lights[i], norm, vPos.xyz, viewDir);
    // phase 3: Spot light
    //result += CalcSpotLight(spotLight, norm, vPos.xyz, viewDir);

    FragColor = vec4(result, color.a);
//    FragColor = vec4(norm*0.5 + 0.5, 1);
    if (FragColor.a < 0.001) discard;
}
