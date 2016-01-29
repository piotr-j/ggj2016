package com.mygdx.game.utils;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by PiotrJ on 29/01/16.
 */
public class BodySteerable implements Steerable<Vector2> {
    private Body body;
    private float boundingRadius;
    private boolean tagged;

    private float maxLinearSpeed;
    private float maxLinearAcceleration;
    private float maxAngularSpeed;
    private float maxAngularAcceleration;
    private float zeroThreshold = 0.001f;

    private boolean independentFacing;

    public void setBoundingRadius (float boundingRadius) {
        this.boundingRadius = boundingRadius;
    }

    public Body getBody () {
        return body;
    }

    public BodySteerable setBody (Body body) {
        this.body = body;
        return this;
    }

    @Override public Vector2 getLinearVelocity () {
        return body.getLinearVelocity();
    }

    @Override public float getAngularVelocity () {
        return body.getAngularVelocity();
    }

    @Override public float getBoundingRadius () {
        return boundingRadius;
    }

    @Override public boolean isTagged () {
        return tagged;
    }

    @Override public void setTagged (boolean tagged) {
        this.tagged = tagged;
    }

    @Override public float getZeroLinearSpeedThreshold () {
        return zeroThreshold;
    }

    @Override public void setZeroLinearSpeedThreshold (float value) {
        this.zeroThreshold = value;
    }

    @Override public float getMaxLinearSpeed () {
        return maxLinearSpeed;
    }

    @Override public void setMaxLinearSpeed (float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override public float getMaxLinearAcceleration () {
        return maxLinearAcceleration;
    }

    @Override public void setMaxLinearAcceleration (float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override public float getMaxAngularSpeed () {
        return maxAngularSpeed;
    }

    @Override public void setMaxAngularSpeed (float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override public float getMaxAngularAcceleration () {
        return maxAngularAcceleration;
    }

    @Override public void setMaxAngularAcceleration (float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override public Vector2 getPosition () {
        return body.getPosition();
    }

    @Override public float getOrientation () {
        return body.getAngle();
    }

    @Override public void setOrientation (float orientation) {

    }

    @Override public float vectorToAngle (Vector2 vector) {
        return vec2ToAngle(vector);
    }

    @Override public Vector2 angleToVector (Vector2 outVector, float angle) {
        return angleToVec2(outVector, angle);
    }

    @Override public Location<Vector2> newLocation () {
        return new BodyLocation();
    }

    public static float vec2ToAngle (Vector2 vector) {
        return (float)Math.atan2(-vector.x, vector.y);
    }

    public static Vector2 angleToVec2 (Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    public static class BodyLocation implements Location<Vector2> {
        Vector2 position = new Vector2();
        float orientation;

        @Override
        public Vector2 getPosition () {
            return position;
        }

        @Override
        public float getOrientation () {
            return orientation;
        }

        @Override
        public void setOrientation (float orientation) {
            this.orientation = orientation;
        }

        @Override
        public Location<Vector2> newLocation () {
            return new BodyLocation();
        }

        @Override
        public float vectorToAngle (Vector2 vector) {
            return vec2ToAngle(vector);
        }

        @Override
        public Vector2 angleToVector (Vector2 outVector, float angle) {
            return angleToVec2(outVector, angle);
        }
    }
}
