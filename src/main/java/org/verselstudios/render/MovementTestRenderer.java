package org.verselstudios.render;

import org.verselstudios.Image.Texture;
import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Time;
import org.verselstudios.math.Transform;
import org.verselstudios.model.QuadRenderSystem;
import org.verselstudios.world.DepthObject;

public class MovementTestRenderer extends DepthObject {


    private static final double speed = 1;
    private static final double radius = 2;

    private double angle = 0;

    public MovementTestRenderer() {
        super(new Transform(), QuadRenderSystem.makeQuad(new Rectangle(-1,-1,2,2)), new Texture(null));
    }

    @Override
    protected void preRender() {
        super.preRender();

        double dt = Time.deltaTime();
        angle += dt*speed;

        double a = angle%1;
        double b = Math.floor(angle)/64;

        double y = Math.cos(b*Math.TAU)*radius;
        double s = Math.sin(b*Math.TAU);
        double z = Math.sin(a*Math.TAU)*radius*s;
        double x = Math.cos(a*Math.TAU)*radius*s;

        this.modelTransform = new Transform(x, y, z, 0, -a*Math.TAU - Math.PI/2, 0, 1, 1,1);
    }
}
