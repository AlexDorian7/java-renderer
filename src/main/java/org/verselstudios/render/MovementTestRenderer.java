package org.verselstudios.render;

import org.verselstudios.Image.Texture;
import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Time;
import org.verselstudios.math.Transform;
import org.verselstudios.model.QuadRenderSystem;
import org.verselstudios.world.DepthObject;

public class MovementTestRenderer extends DepthObject {


    private static final double speed = 0.25;
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

        double s = Math.sin(angle*Math.TAU)*radius;
        double c = Math.cos(angle*Math.TAU)*radius;

        this.modelTransform = new Transform(c, 0, s, 0, -angle*Math.TAU - Math.PI/2, 0, 1, 1,1);
    }
}
