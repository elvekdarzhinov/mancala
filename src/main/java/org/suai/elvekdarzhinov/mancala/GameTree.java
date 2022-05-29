package org.suai.elvekdarzhinov.mancala;

import java.util.ArrayList;
import java.util.List;

public class GameTree {

    public static int generateMove(int treeDepth, Game game) {
        Node root = Node.makeTree(treeDepth, game, null);
        return root.bestMove();
    }

    private static class Node {
        private final List<Node> children;
        private final Game curState;
        private int prelimEstimate;
        private int finalEstimate;
        private int prevMove;

        Node(Game game) {
            children = new ArrayList<>();
            curState = new Game(game);
            prelimEstimate = 0;
            finalEstimate = 0;
            prevMove = 0;
        }

        void addChild(Node node, int move) {
            children.add(node);
            node.prevMove = move;
        }

        Node maxChild() {
            int max = Integer.MIN_VALUE;
            int indexOfMax = 0;

            for (int i = 0; i < children.size(); i++) {
                if (children.get(i).finalEstimate > max) {
                    max = children.get(i).finalEstimate;
                    indexOfMax = i;
                }
            }

            return children.get(indexOfMax);
        }

        Node minChild() {
            int min = Integer.MAX_VALUE;
            int indexOfMin = 0;

            for (int i = 0; i < children.size(); i++) {
                if (children.get(i).finalEstimate < min) {
                    min = children.get(i).finalEstimate;
                    indexOfMin = i;
                }
            }

            return children.get(indexOfMin);
        }

        boolean estimateEnd() {
            switch (curState.checkEnd()) {
                case PLAYER_WINS -> {
                    finalEstimate = Integer.MIN_VALUE;
                    return true;
                }
                case CMP_WINS -> {
                    finalEstimate = Integer.MAX_VALUE;
                    return true;
                }
                case TIE -> {
                    finalEstimate = 0;
                    return true;
                }
                default -> {
                    return false;
                }
            }
        }

        void estimate(int treeDepth) {
            if (treeDepth == 0) {
                prelimEstimate = curState.getCmpKalah() - curState.getPlayerKalah();
                finalEstimate = prelimEstimate;
                return;
            }

            if (children.size() != 0) {
                if (curState.getMoveOrder() == Game.MoveOrder.PLAYER) {
                    finalEstimate = minChild().finalEstimate;
                } else if (curState.getMoveOrder() == Game.MoveOrder.COMPUTER) {
                    finalEstimate = maxChild().finalEstimate;
                }
            }
        }

        boolean alphaBeta(int moveOrder, Node parentNode) {
            if (parentNode == null) {
                return false;
            }

            int numOfChildren = children.size();

            if (moveOrder == 0 && numOfChildren != 0) {
                if (children.get(numOfChildren - 1).finalEstimate < prelimEstimate) {
                    prelimEstimate = children.get(numOfChildren - 1).finalEstimate;

                    if (prelimEstimate <= parentNode.prelimEstimate) {
                        finalEstimate = prelimEstimate;
                        return true;
                    }
                }
            } else if (moveOrder == 1 && numOfChildren != 0) {
                if (children.get(numOfChildren - 1).finalEstimate > prelimEstimate) {
                    prelimEstimate = children.get(numOfChildren - 1).finalEstimate;

                    if (prelimEstimate >= parentNode.prelimEstimate) {
                        finalEstimate = prelimEstimate;
                        return true;
                    }
                }
            }

            return false;
        }

        public static Node makeTree(int treeDepth, Game game, Node parent) {
            Node gameTree = new Node(game);

            if (gameTree.estimateEnd()) {
                return gameTree;
            }

            if (treeDepth > 0) {
                if (gameTree.curState.getMoveOrder() == Game.MoveOrder.PLAYER) {
                    gameTree.prelimEstimate = Integer.MAX_VALUE;

                    for (int i = 0; i < 6; i++) {
                        Game nextState = new Game(game);

                        if (nextState.checkMove(i)) {
                            nextState.makeMove(i);
                            gameTree.addChild(makeTree(treeDepth - 1, nextState, gameTree), i);

                            if (treeDepth > 1) {
                                if (gameTree.alphaBeta(0, parent)) {
                                    return gameTree;
                                }
                            }
                        }
                    }
                } else {
                    gameTree.prelimEstimate = Integer.MIN_VALUE;

                    for (int i = 7; i < 13; i++) {
                        Game nextState = new Game(game);

                        if (nextState.checkMove(i)) {
                            nextState.makeMove(i);
                            gameTree.addChild(makeTree(treeDepth - 1, nextState, gameTree), i);

                            if (treeDepth > 1) {
                                if (gameTree.alphaBeta(1, parent)) {
                                    return gameTree;
                                }
                            }
                        }
                    }
                }
            }

            gameTree.estimate(treeDepth);

            return gameTree;
        }

        public int bestMove() {
            if (children.size() != 0) {
                if (curState.getMoveOrder() == Game.MoveOrder.COMPUTER) {
                    return maxChild().prevMove;
                } else {
                    return minChild().prevMove;
                }
            } else {
                return 0;
            }
        }
    }
}
