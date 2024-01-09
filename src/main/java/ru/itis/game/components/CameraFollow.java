package ru.itis.game.components;

import ru.itis.gengine.gamelogic.Component;
import ru.itis.gengine.gamelogic.components.Transform;

public class CameraFollow extends Component {
    private Transform target;
    private Transform transform;

    public CameraFollow(int id, Transform target) {
        super(id, false);
        this.target = target;
    }

    @Override
    public void initialize() {
        transform = getEntity().getTransform();
    }

    @Override
    public void update(float deltaTime) {
        if (target != null) {
            float newX = target.getPosition().x;
            float newY = target.getPosition().y;

            float leftLimit = -16.f;
            float rightLimit = 16.f;
            float topLimit = 9.f;
            float bottomLimit = -9.f;

            newX = Math.max(leftLimit, Math.min(newX, rightLimit));
            newY = Math.max(bottomLimit, Math.min(newY, topLimit));

            transform.setPosition(newX, newY, transform.getPosition().z);
        }
    }

    public void setTarget(Transform target) {
        this.target = target;
    }
}