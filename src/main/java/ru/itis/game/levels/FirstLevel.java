package ru.itis.game.levels;

import org.joml.Vector4f;
import ru.itis.game.Entities;
import ru.itis.game.components.*;
import ru.itis.gengine.application.Application;
import ru.itis.gengine.gamelogic.Entity;
import ru.itis.gengine.gamelogic.LevelBase;
import ru.itis.gengine.gamelogic.World;
import ru.itis.gengine.gamelogic.components.BoxCollider;
import ru.itis.gengine.gamelogic.components.Camera;
import ru.itis.gengine.gamelogic.components.Mesh;
import ru.itis.gengine.gamelogic.components.Transform;
import ru.itis.gengine.gamelogic.primitives.MeshData;
import ru.itis.gengine.gamelogic.primitives.Primitives;
import ru.itis.gengine.network.model.NetworkEntity;
import ru.itis.gengine.renderer.Shader;
import ru.itis.gengine.renderer.Texture;

import java.util.ArrayList;
import java.util.List;


public class FirstLevel extends LevelBase {
    Shader baseShader;
    static boolean isStarted;

    @Override
    public void startServer(World world) {
        System.out.println("SERVER LEVEL START!");
        isStarted = false;
        world.getRenderer().setClearColor(0.65f, 0.75f, 0.81f, 1.0f);

        baseShader = new Shader(
                "resources/shaders/vertex_shader.glsl",
                "resources/shaders/fragment_shader.glsl"
        );

        Texture texture = new Texture("resources/textures/blue_girl.png");
        MeshData data = Primitives.createSquare(2.f, 2.f);
        Mesh mesh = new Mesh(data, false, texture, baseShader);
        Entity player1Entity = world.instantiateEntity(Entities.FIRST_PLAYER.id, true, "Player1");
        player1Entity.addComponent(mesh);
        player1Entity.addComponent(new BoxCollider(1.f, 1.f));
        player1Entity.addComponent(new PlayerMove(21));
        player1Entity.getTransform().setPosition(-2.f, 0.f, 0.f);

        Texture backgroundTexture = new Texture ("resources/textures/witch_knight_02.png");
        MeshData backgroundData = Primitives.createSquare(43.f, 29.f);
        Mesh backgroundMesh = new Mesh(backgroundData, false, backgroundTexture, baseShader);
        Entity backEntity = world.findEntityById(Entities.BACK.id).orElse(null);
        if (backEntity == null) {
            backEntity = world.instantiateEntity(Entities.BACK.id, false, "back");
            backEntity.addComponent(backgroundMesh);
            backEntity.getTransform().setPosition(0, 0, (float) -0.5);
        }

        createCamera(world, player1Entity.getTransform(), 11);
        createTreeServer(world);
        createMushroomServer(world);

        player1Entity.sendCurrentState();
    }

    @Override
    public void startClient(World world) {
        System.out.println("CLIENT LEVEL START!");
        world.getRenderer().setClearColor(0.65f, 0.75f, 0.81f, 1.0f);
        baseShader = new Shader(
                "resources/shaders/vertex_shader.glsl",
                "resources/shaders/fragment_shader.glsl"
        );

        Texture texture = new Texture("resources/textures/red_girl.png");
        MeshData data = Primitives.createSquare(2.f, 2.f);
        Mesh mesh = new Mesh(data, false, texture, baseShader);
        Entity player2Entity = world.instantiateEntity(Entities.SECOND_PLAYER.id, true, "Player2");
        player2Entity.addComponent(mesh);
        player2Entity.addComponent(new BoxCollider(1.f, 1.f));
        player2Entity.addComponent(new PlayerMove(22));
        player2Entity.getTransform().setPosition(2.f, 0, 0);

        Entity backEntity = world.findEntityById(Entities.BACK.id).orElse(null);
        if (backEntity == null) {
            Texture backgroundTexture = new Texture ("resources/textures/witch_knight_02.png");
            MeshData backgroundData = Primitives.createSquare(43.f, 29.f);
            Mesh backgroundMesh = new Mesh(backgroundData, false, backgroundTexture, baseShader);
            backEntity = world.instantiateEntity(Entities.BACK.id, false, "back");
            backEntity.addComponent(backgroundMesh);
            backEntity.getTransform().setPosition(0, 0, (float) -0.5
            );
        }

        createCamera(world, player2Entity.getTransform(), 12);
        player2Entity.sendCurrentState();
    }

    @Override
    public void createEntityNetworkEvent(NetworkEntity entity) {
        World world = Application.shared.getWorld();
        int id = entity.getId();
        if(world.findEntityById(id).isPresent()) {
            System.out.println("ENTITY WITH ID " + id + " ALREADY EXISTS");
            return;
        }
        System.out.println("CREATE ENTITY WITH ID: " + id);
        if(id == Entities.FIRST_PLAYER.id) {
            Texture texture = new Texture("resources/textures/blue_girl.png");
            MeshData data = Primitives.createSquare(2.f, 2.f);
            Mesh mesh = new Mesh(data, false, texture, baseShader);
            Entity player1Entity = world.instantiateEntity(id, true, "Player1");
            player1Entity.addComponent(mesh);
            player1Entity.addComponent(new BoxCollider(1.f, 1.f));
            player1Entity.addComponent(new SecondPlayerMove(21));
            player1Entity.getTransform().setPosition(-1.f, 0.f, 0);
            player1Entity.applyEntity(entity);
        } else if(id == Entities.SECOND_PLAYER.id) {
            Texture texture = new Texture("resources/textures/red_girl.png");
            MeshData data = Primitives.createSquare(2.f, 2.f);
            Mesh mesh = new Mesh(data, false, texture, baseShader);
            Entity player2Entity = world.instantiateEntity(id, true, "Player2");
            player2Entity.addComponent(mesh);
            player2Entity.addComponent(new BoxCollider(1.f, 1.f));
            player2Entity.addComponent(new SecondPlayerMove(22));
            player2Entity.getTransform().setPosition(1.f, 0.f, 0);
            player2Entity.applyEntity(entity);
        }
        else if(id >= Entities.TREE_1.id && id <= Entities.TREE_9.id) {
            createTreeClient(world, entity);
        }
        else if(id == Entities.BLUE_MUSHROOM_1.id) {
            createBlueMushroomClient(world, entity);
        }
        else if(id == Entities.RED_MUSHROOM_1.id) {
            createRedMushroomClient(world, entity);
        }
    }

    @Override
    public void terminate() {
        baseShader.delete();
    }

    public static void gameplay() {
        World world = Application.shared.getWorld();
        Entity player1 = world.findEntityByName("Player1").orElse(null);
        Entity blueMushroom = world.findEntityByName("blue_mush1").orElse(null);
        Entity player2 = world.findEntityByName("Player2").orElse(null);
        Entity redMushroom = world.findEntityByName("red_mush1").orElse(null);
        if ((player1 != null & blueMushroom != null) | (player2 != null & redMushroom != null)) {
            isStarted = true;
            checkCollisions(world, player1, blueMushroom);
            checkCollisions(world, player2, redMushroom);
        }
        if (isStarted) {
            boolean blueWin = world.findEntityByName("blue_mush1").orElse(null) == null;
            boolean redWin = world.findEntityByName("red_mush1").orElse(null) == null;
            if (blueWin) {
                isStarted = false;
                new Victory(1);
                Victory.isEnd = true;
            }
            if (redWin) {
                isStarted = false;
                new Victory(2);
                Victory.isEnd = true;
            }
        }
    }

    private static void checkCollisions(World world, Entity player, Entity mushroom) {
        if (player != null && mushroom != null) {
            BoxCollider playerCollider = player.getComponentWithType(BoxCollider.class);
            BoxCollider blueMushroomCollider = mushroom.getComponentWithType(BoxCollider.class);

            if (playerCollider != null && blueMushroomCollider != null) {
                float playerMinX = playerCollider.getMinX();
                float playerMaxX = playerCollider.getMaxX();
                float playerMinY = playerCollider.getMinY();
                float playerMaxY = playerCollider.getMaxY();

                float blueMushroomMinX = blueMushroomCollider.getMinX();
                float blueMushroomMaxX = blueMushroomCollider.getMaxX();
                float blueMushroomMinY = blueMushroomCollider.getMinY();
                float blueMushroomMaxY = blueMushroomCollider.getMaxY();

                boolean overlapX = (playerMaxX - 1 > blueMushroomMinX && blueMushroomMaxX + 1 > playerMinX) || (playerMaxX + 1 > blueMushroomMinX && blueMushroomMaxX - 1 > playerMinX);
                boolean overlapY = (playerMaxY - 1 > blueMushroomMinY && blueMushroomMaxY + 1 > playerMinY) || (playerMaxY + 1 > blueMushroomMinY && blueMushroomMaxY - 1 > playerMinY);

                if (overlapX && overlapY) {
                    world.destroy(mushroom);
                }
            }
        }
    }

    private void createCamera(World world, Transform target, int cameraId) {
        Entity cameraEntity = world.instantiateEntity(cameraId, false, "camera_" + cameraId);
        Camera camera = new Camera();
        cameraEntity.addComponent(camera);

        camera.setFieldOfView(60.f);
        camera.setShader(baseShader);
        cameraEntity.getTransform().setPosition(0.f, 0.f, 8.f);

        CameraFollow cameraFollow = new CameraFollow(101 + cameraId, target);
        cameraEntity.addComponent(cameraFollow);
    }

    private final List<Vector4f> treePositions = new ArrayList<Vector4f>();

    private Vector4f getRandomPosition() {
        float leftLimit = -16.f;
        float rightLimit = 16.f;
        float topLimit = 9.f;
        float bottomLimit = -9.f;

        Vector4f position = new Vector4f();
        boolean validPosition = false;
        int attempts = 0;
        final int maxAttempts = 100;

        if (treePositions.isEmpty()) {
            position.set(-2.f, 0.f, (float) -0.4);
            treePositions.add(position);
            position.set(2.f, 0.f, (float) -0.4);
            treePositions.add(position);
        }

        while (!validPosition && attempts < maxAttempts) {
            float x = (float) (Math.random() * (rightLimit - leftLimit)) + leftLimit;
            float y = (float) (Math.random() * (topLimit - bottomLimit)) + bottomLimit;
            position.set(x, y, (float) -0.4);

            boolean overlap = false;
            for (Vector4f existingPosition : treePositions) {
                float distance = position.distance(existingPosition);
                if (distance < 4.0f) {
                    overlap = true;
                    break;
                }
            }

            if (!overlap) {
                validPosition = true;
                treePositions.add(position);
            } else {
                attempts++;
            }
        }

        if (!validPosition) {
            return null;
        }

        return position;
    }

    private void createTreeServer(World world) {
        Texture textureBlock = new Texture("resources/textures/tree.png");

        for (int number = 0; number < 10; number++) {
            Entity block = world.instantiateEntity(Entities.TREE_1.id + number, true, "tree" + number);
            MeshData meshData = Primitives.createSquare(3.f, 3.f);
            Mesh mesh = new Mesh(meshData, false, textureBlock, baseShader);

            block.addComponent(mesh);
            block.addComponent(new BoxCollider(2.f, 3.f));
            block.addComponent(new BlockPosition(1));

            Vector4f position = getRandomPosition();
            if (position != null) {
                block.getTransform().setPosition(position);
                block.sendCurrentState();
            } else {
                System.out.println("Не удалось найти подходящую позицию для дерева " + number);
            }
        }
    }

    private void createTreeClient(World world, NetworkEntity entity) {
        int number = (entity.getId() - Entities.TREE_1.id);
        Texture textureBlock = new Texture("resources/textures/tree.png");

        Entity block = world.instantiateEntity(entity.getId(), true, "tree" + number);
        MeshData meshData = Primitives.createSquare(3.f, 3.f);
        Mesh mesh = new Mesh(meshData, false, textureBlock, baseShader);

        block.addComponent(mesh);
        block.addComponent(new BoxCollider(2.f, 3.f));
        block.addComponent(new BlockPosition(1));
        block.applyEntity(entity);
    }

    private void createMushroomServer(World world) {
        Texture blueTexture = new Texture("resources/textures/blue_mushroom.png");

        Entity blueMushroom = world.instantiateEntity(Entities.BLUE_MUSHROOM_1.id, true, "blue_mush1");
        MeshData blueMeshData = Primitives.createSquare(1.f, 1.f);
        Mesh blueMesh = new Mesh(blueMeshData, false, blueTexture, baseShader);

        blueMushroom.addComponent(blueMesh);
        blueMushroom.addComponent(new BoxCollider(1.f, 1.f));
        blueMushroom.addComponent(new BlockPosition(1));

        Vector4f position = getRandomPosition();
        if (position != null) {
            blueMushroom.getTransform().setPosition(position);
            blueMushroom.sendCurrentState();
        }

        Texture redTexture = new Texture("resources/textures/red_mushroom.png");

        Entity redMushroom = world.instantiateEntity(Entities.RED_MUSHROOM_1.id, true, "red_mush1");
        MeshData redMeshData = Primitives.createSquare(1.f, 1.f);
        Mesh mesh = new Mesh(redMeshData, false, redTexture, baseShader);

        redMushroom.addComponent(mesh);
        redMushroom.addComponent(new BoxCollider(1.f, 1.f));
        redMushroom.addComponent(new BlockPosition(1));

        position = getRandomPosition();
        if (position != null) {
            redMushroom.getTransform().setPosition(position);
            redMushroom.sendCurrentState();
        }
    }

    private void createBlueMushroomClient(World world, NetworkEntity entity) {
        Texture texture = new Texture("resources/textures/blue_mushroom.png");

        Entity blueMushroom = world.instantiateEntity(entity.getId(), true, "blue_mush1");
        MeshData meshData = Primitives.createSquare(1.f, 1.f);
        Mesh mesh = new Mesh(meshData, false, texture, baseShader);

        blueMushroom.addComponent(mesh);
        blueMushroom.addComponent(new BoxCollider(1.f, 1.f));
        blueMushroom.addComponent(new BlockPosition(1));
        blueMushroom.applyEntity(entity);
    }

    private void createRedMushroomClient(World world, NetworkEntity entity) {
        Texture texture = new Texture("resources/textures/red_mushroom.png");

        Entity redMushroom = world.instantiateEntity(entity.getId(), true, "red_mush1");
        MeshData meshData = Primitives.createSquare(1.f, 1.f);
        Mesh mesh = new Mesh(meshData, false, texture, baseShader);

        redMushroom.addComponent(mesh);
        redMushroom.addComponent(new BoxCollider(1.f, 1.f));
        redMushroom.addComponent(new BlockPosition(1));
        redMushroom.applyEntity(entity);
    }
}
