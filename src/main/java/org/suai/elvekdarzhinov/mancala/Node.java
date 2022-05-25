package org.suai.elvekdarzhinov.mancala;

import java.util.ArrayList;

public class Node {
    Node parent;
    ArrayList<Node> children;
    Game curState;
    int prelimEstimate;
    int finalEstimate;
    int prevMove;

    Node(final Game G) {
        this.parent = null;
        this.children = new ArrayList<>();
        this.curState = new Game(G);
        this.prelimEstimate = 0;
        this.finalEstimate = 0;
        this.prevMove = 0;
    }

    void addChild(Node N, int move) {
        this.children.add(N);
        N.parent = this;
        N.prevMove = move;
    }

    Node maxChild() {
        int max = Integer.MIN_VALUE;
        int indexOfMax = 0;

        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).finalEstimate > max) {
                max = this.children.get(i).finalEstimate;
                indexOfMax = i;
            }
        }

        return this.children.get(indexOfMax);
    }

    Node minChild() {
        int min = Integer.MAX_VALUE;
        int indexOfMin = 0;

        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).finalEstimate < min) {
                min = this.children.get(i).finalEstimate;
                indexOfMin = i;
            }
        }

        return this.children.get(indexOfMin);
    }

    boolean estimateEnd() {
        switch (this.curState.checkEnd()) {
            case 1 -> {
                this.finalEstimate = Integer.MIN_VALUE;
                return true;
            }
            case 2 -> {
                this.finalEstimate = Integer.MAX_VALUE;
                return true;
            }
            case 3 -> {
                this.finalEstimate = 0;
                return true;
            }
        }

        return false;
    }

    void estimate(int treeDepth) {
        if (treeDepth == 0) {
            this.prelimEstimate = this.curState.board[13] - this.curState.board[6];
            this.finalEstimate = this.prelimEstimate;
            return;
        }

        if (this.children.size() != 0) {
            if (this.curState.moveOrder == 0) {
                this.finalEstimate = this.minChild().finalEstimate;
            } else if (this.curState.moveOrder == 1) {
                this.finalEstimate = this.maxChild().finalEstimate;
            }
        }
    }

    boolean alphaBeta(int moveOrder, Node parentNode) {
        if (parentNode == null) {
            return false;
        }

        int numOfChildren = this.children.size();

        if (moveOrder == 0 && numOfChildren != 0) {
            if (this.children.get(numOfChildren - 1).finalEstimate < this.prelimEstimate) {
                this.prelimEstimate = this.children.get(numOfChildren - 1).finalEstimate;

                if (this.prelimEstimate <= parentNode.prelimEstimate) {
                    this.finalEstimate = this.prelimEstimate;
                    return true;
                }
            }
        } else if (moveOrder == 1 && numOfChildren != 0) {
            if (this.children.get(numOfChildren - 1).finalEstimate > this.prelimEstimate) {
                this.prelimEstimate = this.children.get(numOfChildren - 1).finalEstimate;

                if (this.prelimEstimate >= parentNode.prelimEstimate) {
                    this.finalEstimate = this.prelimEstimate;
                    return true;
                }
            }
        }

        return false;
    }


    static Node makeTree(final int treeDepth, final Game G, final Node parent) {
        Node N = new Node(G);

        if (N.estimateEnd()) {
            return N;
        }

        if (treeDepth > 0) {
            if (N.curState.moveOrder == 0) {
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

    int bestMove() {
        if (this.children.size() != 0) {
            if (this.curState.moveOrder == 1) {
                return this.maxChild().prevMove;
            } else {
                return this.minChild().prevMove;
            }
        } else {
            return 0;
        }
    }
}