package org.verselstudios.physics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.verselstudios.math.Time;
import org.verselstudios.physics.material.PhysicsMaterials;
import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.geometry.PxBoxGeometry;
import physx.physics.*;

public class PhysicsWorld {

    private static final Logger LOGGER = LogManager.getLogger(PhysicsWorld.class);

    private static final PhysicsWorld instance = new PhysicsWorld();
    private final PxPhysics physics;
    private final PxScene scene;

    public static PhysicsWorld getInstance() {
        return instance;
    }

    private PhysicsWorld() {
        // get PhysX library version
        int version = PxTopLevelFunctions.getPHYSICS_VERSION();
        int versionMajor = version >> 24;
        int versionMinor = (version >> 16) & 0xff;
        int versionMicro = (version >> 8) & 0xff;
        LOGGER.info(String.format("PhysX loaded, version: %d.%d.%d\n", versionMajor, versionMinor, versionMicro));

        // create PhysX foundation object
        PxDefaultAllocator allocator = new PxDefaultAllocator();
        PxDefaultErrorCallback errorCb = new PxDefaultErrorCallback();
        PxFoundation foundation = PxTopLevelFunctions.CreateFoundation(version, allocator, errorCb);

        // create PhysX main physics object
        PxTolerancesScale tolerances = new PxTolerancesScale();
        physics = PxTopLevelFunctions.CreatePhysics(version, foundation, tolerances);

        // create the CPU dispatcher, can be shared among multiple scenes
        int numThreads = 4;
        PxDefaultCpuDispatcher cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(numThreads);

        // create a physics scene
        PxVec3 tmpVec = new PxVec3(0f, -9.81f, 0f);
        PxSceneDesc sceneDesc = new PxSceneDesc(tolerances);
        sceneDesc.setGravity(tmpVec);
        sceneDesc.setCpuDispatcher(cpuDispatcher);
        sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
        scene = physics.createScene(sceneDesc);


        PxTransform tmpPose = new PxTransform(PxIDENTITYEnum.PxIdentity);
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        // create a large static box with size 20x1x20 as ground
//        PxBoxGeometry groundGeometry = new PxBoxGeometry(10f, 0.5f, 10f);   // PxBoxGeometry uses half-sizes
//        PxShape groundShape = physics.createShape(groundGeometry, PhysicsMaterials.DEFAULT, true, shapeFlags);
//        PxRigidStatic ground = physics.createRigidStatic(tmpPose);
//        groundShape.setSimulationFilterData(tmpFilterData);
//        ground.attachShape(groundShape);
//        scene.addActor(ground);
    }

    public void simulate() {
        double dt = Time.deltaTime();
        scene.simulate((float) dt);
        scene.fetchResults(true);
    }

    public PxPhysics getPhysics() {
        return physics;
    }

    public PxScene getScene() {
        return scene;
    }
}
