package ru.itis.game.components;

import org.joml.Vector4f;
import ru.itis.game.network.ObjectPosition;
import ru.itis.gengine.application.Application;
import ru.itis.gengine.base.Direction;
import ru.itis.gengine.events.Events;
import ru.itis.gengine.events.Key;
import ru.itis.gengine.gamelogic.Component;
import ru.itis.gengine.gamelogic.Physics;
import ru.itis.gengine.gamelogic.components.Transform;
import ru.itis.gengine.network.model.NetworkComponentState;

public class PlayerMove extends Component {
    public float moveSpeed = 6.0f;
    private Transform transform;
    private Events events;

    public PlayerMove(int id) {
        super(id, true);
    }

    @Override
    public void initialize() {
        transform = getEntity().getTransform();
        events = getEntity().getEvents();
    }

    @Override
    public void update(float deltaTime) {
        Physics physics = getEntity().getPhysics();

        Direction direction = null;
        float speed = moveSpeed * deltaTime;
        boolean playerMoved = false;

        float screenHeight = 28.f;
        float screenWidth = 42.f;

        float width = 2.f;
        float height = 2.f;

        if (events.isKeyPressed(Key.W) ) {
            direction = Direction.Up;
        }
        if (events.isKeyPressed(Key.S)) {
            direction = Direction.Down;
        }
        if (events.isKeyPressed(Key.A)) {
            direction = Direction.Left;
        }
        if (events.isKeyPressed(Key.D)) {
            direction = Direction.Right;
        }

        if (direction != null && physics.moveAcceptable(getEntity(), speed, direction)) {
            Vector4f newPosition = new Vector4f(transform.getPosition());

            switch (direction) {
                case Up:
                    newPosition.y += speed;
                    if (newPosition.y + height / 2.0f > screenHeight / 2.0f) {
                        newPosition.y = screenHeight / 2.0f - height / 2.0f;
                    }
                    break;
                case Down:
                    newPosition.y -= speed;
                    if (newPosition.y - height / 2.0f < -screenHeight / 2.0f) {
                        newPosition.y = -screenHeight / 2.0f + height / 2.0f;
                    }
                    break;
                case Left:
                    newPosition.x -= speed;
                    if (newPosition.x - width / 2.0f < -screenWidth / 2.0f) {
                        newPosition.x = -screenWidth / 2.0f + width / 2.0f;
                    }
                    break;
                case Right:
                    newPosition.x += speed;
                    if (newPosition.x + width / 2.0f > screenWidth / 2.0f) {
                        newPosition.x = screenWidth / 2.0f - width / 2.0f;
                    }
                    break;
            }

            transform.setPosition(newPosition.x, newPosition.y, newPosition.z);

            playerMoved = true;
        }

        if(playerMoved) {
            sendCurrentState();
        }
    }

    @Override
    public NetworkComponentState getState() {
        Vector4f coordinates = transform.getPosition();
        return new ObjectPosition(coordinates.x, coordinates.y);
    }
}
