Plugin.register('versel_renderer', {
    title: 'Versel Renderer',
    author: 'Versel',
    icon: 'icon',
    description: 'Versel custom model exporter',
    version: '1.0.0',
    variant: 'both',

    onload() {

        // --- CODEC ---
        const codec = new Codec('versel_codec', {
            name: 'Versel Renderer',
            extension: 'versel',
            remember: true,
            support_partial_export: true,

            // EXPORT
            compile(model, options) {
                const out = {
                    meta: {
                        format: "versel",
                        version: 1
                    },
                    textures: [],
                    cubes: []
                };

                // TEXTURES
                Texture.all.forEach(tex => {
                    out.textures.push({
                        id: tex.id,
                        name: tex.name,
                        width: tex.width,
                        height: tex.height,
                        path: tex.path
                    });
                });

                // First, build a mapping from texture UUID -> numeric ID
                const textureIdMap = {};
                Texture.all.forEach((tex, index) => {
                    textureIdMap[tex.uuid] = index; // assign 0,1,2,...
                });

                // Then, when serializing cubes:
                Cube.all.forEach(cube => {
                    const cubeData = {
                        name: cube.name,
                        from: cube.from.slice(),
                        to: cube.to.slice(),
                        origin: cube.origin.slice(),
                        rotation: cube.rotation ? cube.rotation.slice() : [0, 0, 0],
                        inflate: cube.inflate,
                        uv_offset: cube.uv_offset ? cube.uv_offset.slice() : [0, 0],
                        faces: {}
                    };

                    ['north', 'south', 'east', 'west', 'up', 'down'].forEach(dir => {
                        const f = cube.faces[dir];
                        if (f) {
                            cubeData.faces[dir] = {
                                uv: f.uv ? f.uv.slice() : null,
                                texture: f.texture ? textureIdMap[f.texture] : null, // <-- numeric texture ID
                                cullface: f.cullface || null,
                                rotation: f.rotation || 0,
                                tint: f.tint || null
                            };
                        }
                    });

                    out.cubes.push(cubeData);
                });

                return JSON.stringify(out, null, 2);
            },

            // IMPORT (OPTIONAL)
            parse(model, data) {
                const json = JSON.parse(data);

                if (!json) return;

                // TEXTURES
                if (json.textures) {
                    json.textures.forEach(t => {
                        new Texture({
                            name: t.name,
                            path: t.path
                        }).add();
                    });
                }

                // CUBES
                if (json.cubes) {
                    json.cubes.forEach(c => {
                        new Cube({
                            name: c.name,
                            from: c.from,
                            to: c.to,
                            origin: c.origin,
                            rotation: c.rotation,
                            inflate: c.inflate,
                            uv_offset: c.uv_offset,
                            faces: c.faces
                        }).addTo(Group.all[0] || Outliner.root);
                    });
                }
            },
        });

        // --- MODEL FORMAT ---
        const format = new ModelFormat('versel_format', {
            id: 'versel_format',
            icon: 'icon',
            name: 'Versel Model',
            description: 'Versel Renderer Model',
            category: 'other',
            target: 'Versel Engine',
            show_on_start_screen: true,
            can_convert_to: true,
            codec: codec,

            // ===== TEXTURE / UV =====
            box_uv: false,
            optional_box_uv: true,
            uv_rotation: false,
            single_texture: false,
            single_texture_default: false,
            per_texture_uv_size: false,
            per_group_texture: false,
            texture_folder: false,
            select_texture_for_particles: false,
            texture_mcmeta: false,

            // ===== MODEL STRUCTURE =====
            model_identifier: false,
            parent_model_id: false,
            centered_grid: false,
            block_size: 16,
            forward_direction: '+z',
            euler_order: 'ZYX',

            // ===== ELEMENTS =====
            rotate_cubes: true,
            stretch_cubes: false,
            integer_size: false,
            meshes: false,
            splines: false,
            texture_meshes: false,
            billboards: false,
            locators: false,

            // ===== RENDER / MATERIAL =====
            pbr: true,
            render_sides: 'front',

            // ===== MODES (PANELS) =====
            edit_mode: true,
            paint_mode: true,
            display_mode: false,
            image_editor: false, // setting this to true disables edit mode stuff

            // ===== ANIMATION (IMPORTANT EVEN IF UNUSED) =====
            animated_textures: false,
            animation_mode: false,
            animation_files: false,
            animation_controllers: false,
            animation_loop_wrapping: false,
            animation_grouping: 'custom',

            // ===== MISC =====
            bone_rig: false,
            armature_rig: false,
            vertex_color_ambient_occlusion: false,
            java_face_properties: false,
            java_cube_shading_properties: false,
            cullfaces: false,
            node_name_regex: '',
            rotation_limit: false,
            rotation_snap: false,
            per_animator_rotation_interpolation: false,
            quaternion_interpolation: false,
            pose_mode: false,
            box_uv_float_size: false
        });

        codec.format = format;
        format.codec = codec;

        const exportAction = new Action('export_versel_model', {
            name: 'Export Versel Model',
            icon: 'icon',
            category: 'file',
            click() {
                codec.export();
            }
        });

        MenuBar.addAction('file.export', 'export_versel_model');
        codec.export_action = exportAction;

        Formats.versel_format = format;
        Codecs.versel_codec = codec;

        console.log(Codecs);
    },

    onunload() {
        // Clean up
        if (Formats.versel_format) delete Formats.versel_format;
        if (Codecs.versel_codec) delete Codecs.versel_codec;
    }
});
