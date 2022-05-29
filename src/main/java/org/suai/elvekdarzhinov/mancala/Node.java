package org.suai.elvekdarzhinov.mancala;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final List<Node> children;
    private final Game curState;
    private int prelimEstimate;
    private int finalEstimate;
    private int prevMove;

    private Node(final Game G) {
        children = new ArrayList<>();
        curState = new Game(G);
        prelimEstimate = 0;
        finalEstimate = 0;
        prevMove = 0;
    }

    private void addChild(Node N, int move) {
        children.add(N);
        N.prevMove = move;
    }

    private Node maxChild() {
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

    private Node minChild() {
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

    private boolean estimateEnd() {
        switch (curState.checkEnd()) {
            case 1 -> {
                finalEstimate = Integer.MIN_VALUE;
                return true;
            }
            case 2 -> {
                finalEstimate = Integer.MAX_VALUE;
                return true;
            }
            case 3 -> {
                finalEstimate = 0;
                return true;
            }
        }

        return false;
    }

    private void estimate(int treeDepth) {
        if (treeDepth == 0) {
            prelimEstimate = curState.getCmpKalah() - curState.getPlayerKalah();
            finalEstimate = prelimEstimate;
            return;
        }

        if (children.size() != 0) {
            if (curState.getMoveOrder() == 0) {
                finalEstimate = minChild().finalEstimate;
            } else if (curState.getMoveOrder() == 1) {
                finalEstimate = maxChild().finalEstimate;
            }
        }
    }

    private boolean alphaBeta(int moveOrder, Node parentNode) {
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


    public static Node makeTree(final int treeDepth, final Game G, final Node parent) {
        Node N = new Node(G);

        if (N.estimateEnd()) {
            return N;
        }

        if (treeDepth > 0) {
            if (N.curState.getMoveOrder() == 0) {
                N.prelimEstimate = 100000;

                for (int i = 0; i < 6; i++) {
                    Game nextState = new Game(G);

                    if (nextState.checkMove(i)) {
                        nextState.makeMove(i);
                        N.addChild(makeTree(treeDepth - 1, nextState, N), i);

                        if (treeDepth > 1) {
                            if (N.alphaBeta(0, parent)) {
                                return N;
                            }
                        }
                    }
                }
            } else {
                N.prelimEstimate = -100000;

                for (int i = 7; i < 13; i++) {
                    Game nextState = new Game(G);

                    if (nextState.checkMove(i)) {
                        nextState.makeMove(i);
                        N.addChild(makeTree(treeDepth - 1, nextState, N), i);

                        if (treeDepth > 1) {
                            if (N.alphaBeta(1, parent)) {
                                return N;
                            }
                        }
                    }
                }
            }
        }

        N.estimate(treeDepth);

        return N;
    }

    public int bestMove() {
        if (children.size() != 0) {
            if (curState.getMoveOrder() == 1) {
                return maxChild().prevMove;
            } else {
                return minChild().prevMove;
            }
        } else {
            return 0;
        }
    }
}
