package com.mygdx.game.model;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.utils.BodyBuilder;
import com.mygdx.game.utils.FixtureDefBuilder;

import java.util.Iterator;


public class Box2DWorld {

    /*
     * Statics for calculation pixel to box2d metrics and vice versa
     */
    public static final float WORLD_TO_BOX = 0.01f; // 100px = 1m
    public static final float BOX_TO_WORLD = 100f;


    /*
     * Masks and categories used to filter collisions
     */
    public final static short WALKER_MASK = CATEGORY.WALKER;

    public final static class CATEGORY {
        public final static short WALKER = 0x0001;
    };

    private World world;

    private FixtureDefBuilder fixtureDefBuilder;
    private BodyBuilder bodyBuilder;

    private Box2DDebugRenderer debugRenderer;

    private Array<Body> bodies = new Array<Body>();

    public Box2DWorld(Vector2 gravity) {
        world = new World(gravity, true);
        debugRenderer = new Box2DDebugRenderer(true, true, false, true, false, true);

        bodyBuilder = new BodyBuilder(world);
        fixtureDefBuilder = new FixtureDefBuilder();
    }

    public void update(float dt) {
        // Those are quite high values because tree is complex object you need a lot of iterations or it will freak out
        // I typically use 5 velocity iterations and 3 position iterations in my other games
        world.step(1/60f, 10, 4);
        sweepDeadBodies();
        for (Joint joint : jointsToRemove) {
            world.destroyJoint(joint);
        }
        jointsToRemove.clear();
        for (int i = 0; i < jointDefs.size; i++) {
            JointDef def = jointDefs.get(i);
            JointCallback callback = jointCallbacks.get(i);
            Joint joint = world.createJoint(def);
            callback.jointCreated(joint);
        }
        jointDefs.clear();
        jointCallbacks.clear();
    }


    private Array<JointDef> jointDefs = new Array<JointDef>();
    private Array<JointCallback> jointCallbacks = new Array<JointCallback>();
    public <T> void createJoint (JointDef joint, JointCallback callback) {
        jointDefs.add(joint);
        jointCallbacks.add(callback);
    }

    private Array<Joint> jointsToRemove = new Array<Joint>();
    public void destroyJoint (Joint joint) {
        jointsToRemove.add(joint);
    }

    /*
	 * Bodies should be removed after world step to prevent simulation crash
	 */
	public void sweepDeadBodies() {
		world.getBodies(bodies);
		for (Iterator<Body> iter = bodies.iterator(); iter.hasNext();) {
			Body body = iter.next();
			if (body != null && (body.getUserData() instanceof PhysicsObject)) {
                PhysicsObject data = (PhysicsObject) body.getUserData();
				if (data.getFlagForDelete()) {
					getWorld().destroyBody(body);
				}
			}
		}
	}

    /*
	 * Box2D debug renderer
     */
    public void debugRender(Camera cam) {
        debugRenderer.render(world, cam.combined);
    }

    public World getWorld() {
        return world;
    }

    public BodyBuilder getBodyBuilder() {
        return bodyBuilder;
    }

    public FixtureDefBuilder getFixtureDefBuilder() {
        return fixtureDefBuilder;
    }

    public interface JointCallback {
        void jointCreated (Joint joint);
    }
}
