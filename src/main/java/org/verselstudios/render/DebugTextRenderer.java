package org.verselstudios.render;

import org.verselstudios.math.Vector3d;
import org.verselstudios.render.font.Font;

public class DebugTextRenderer implements Renderer {
    @Override
    public void render() {

        for (int i=0; i<16; i++) {
            String str = "";
            for (char c=(char)(i*16); c<(i+1)*16; c++) {
                str += c;
            }
            Font.EMOJI.renderString(new Vector3d(0, -i*32, 0), str);
        }


    }
}
