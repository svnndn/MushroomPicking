package ru.itis.game;

public enum Entities {
    BACK(-1),
    FIRST_PLAYER(0),

    SECOND_PLAYER(1),
    FIRST_COUNTER(20),
    SECOND_COUNTER(21),
    BLUE_MUSHROOM_1(31),
    RED_MUSHROOM_1(32),
    TREE_1(41),
    TREE_2(42),
    TREE_3(43),
    TREE_4(44),
    TREE_5(45),
    TREE_6(46),
    TREE_7(47),
    TREE_8(48),
    TREE_9(49),
    ;

    public final int id;

    Entities(int id) {
        this.id = id;
    }
}
