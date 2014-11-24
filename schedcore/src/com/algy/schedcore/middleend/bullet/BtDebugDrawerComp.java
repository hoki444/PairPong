package com.algy.schedcore.middleend.bullet;

import com.algy.schedcore.IComp;
import com.algy.schedcore.middleend.CameraServer;
import com.algy.schedcore.middleend.ModelBatch3DComp;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;

public class BtDebugDrawerComp extends ModelBatch3DComp {
    public DebugDrawer debugDrawer;
    public int drawMode;
    public BitmapFont bitmapFont;
    
    public BtDebugDrawerComp () {
        drawMode = DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE;
    }
    
    public BtDebugDrawerComp (int drawMode) {
        this.drawMode = drawMode;
    }

    @Override
    public IComp duplicate() {
        return new BtDebugDrawerComp(drawMode);
    }
    
    @Override
    protected void onAdhered() {
        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(drawMode);
        bitmapFont = new BitmapFont();
        debugDrawer.setFont(bitmapFont);
    }

    @Override
    protected void onDetached() {
        debugDrawer.dispose();
        bitmapFont.dispose();
    }

    @Override
    public void render(ModelBatch modelBatch, Environment defaultEnv) {
        btCollisionWorld world = server(BtPhysicsWorld.class).world;
        debugDrawer.begin(server(CameraServer.class).getCamera());
        world.setDebugDrawer(debugDrawer);
        world.debugDrawWorld();
        debugDrawer.end();
    }
}
