package ru.itis.game.components;

import ru.itis.game.network.ObjectPosition;
import ru.itis.gengine.gamelogic.Component;
import ru.itis.gengine.gamelogic.components.Transform;
import ru.itis.gengine.network.model.NetworkComponentState;

public class SecondPlayerMove extends Component {
    private Transform transform;

    public SecondPlayerMove(int id) {
        super(id, true);
    }

    @Override
    public void initialize() {
        transform = getEntity().getTransform();
    }

    @Override
    public void setState(NetworkComponentState state) {
        ObjectPosition position = (ObjectPosition) state;
        transform.setPosition(position.x(), position.y(), 0.f);
    }
}
