package org.suai.elvekdarzhinov.mancala;

public class Main {
    public static void main(String[] args) {
        new Game(4).runGame(10, Game.MoveOrder.PLAYER);
    }
}
