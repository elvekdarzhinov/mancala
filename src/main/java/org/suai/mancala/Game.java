package org.suai.mancala;

import java.util.Random;
import java.util.Scanner;

public class Game {
    protected int[] board;
    protected int allStones;
    protected int moveCount;
    protected int moveOrder;

    Game(final int stones) {
        this.allStones = stones * 12;
        this.moveCount = 0;
        this.moveOrder = 0;

        this.board = new int[14];
        for (int i = 0; i < 14; i++) {
            this.board[i] = stones;
        }
        board[6] = 0;
        board[13] = 0;
    }

    Game(final Game G) {
        this.allStones = G.allStones;
        this.moveCount = G.moveCount;
        this.moveOrder = G.moveOrder;

        this.board = new int[14];
        System.arraycopy(G.board, 0, this.board, 0, 14);
    }

    void runGame(int treeDepth, int firstMove) {
        moveOrder = firstMove;

        int move = 0;
        int computerMove = 0;
        Scanner scan = new Scanner(System.in);

        while (checkEnd() == 0) {
            if (moveOrder == 0) {
                printBoard(computerMove % 7);

                move = scan.nextInt();
                if (move == 100) { // Manually exit the game
                    return;
                }
                move--;

                while (!checkMove(move)) {
                    System.out.print("Impossible move. Try something else: ");
                    move = scan.nextInt();
                    move--;
                }

                makeMove(move);
            } else {
                printBoard(move);

                if (treeDepth > 0) {
                    computerMove = computerMove(treeDepth);
                } else {
                    computerMove = randomMove();
                }

                //std::cin.get();
                //std::cin.get();

                makeMove(computerMove);
            }
        }

        scan.close();


        if (moveOrder == 0) {
            moveOrder = 3;
            printBoard(computerMove % 7);
        } else {
            moveOrder = 3;
            printBoard(move);
        }

        printResult();
    }

    int checkEnd() {
        int playerStones = 0;
        int computerStones = 0;

        for (int i = 0, j = 7; i < 6; i++, j++) {
            playerStones += board[i];
            computerStones += board[j];
        }

        if (playerStones == 0) {
            board[13] += computerStones;
            for (int i = 7; i < 13; i++) {
                board[i] = 0;
            }
        } else if (computerStones == 0) {
            board[6] += playerStones;
            for (int i = 0; i < 6; i++) {
                board[i] = 0;
            }
        }

        if (board[6] > allStones / 2) {
            return 1; // Player wins
        } else if (board[13] > allStones / 2) {
            return 2; // CMP wins
        }

        if ((board[6] == allStones / 2) && (board[13] == allStones / 2)) {
            return 3; // TIE
        }

        return 0; // Move is possible
    }

    boolean checkMove(final int move) {
        if (moveOrder == 0) {
            return move >= 0 && move <= 5 && board[move] != 0;
        } else if (moveOrder == 1) {
            return move >= 7 && move <= 12 && board[move] != 0;
        }

        return true;
    }

    void printResult() {
        int result = checkEnd();

        switch (result) {
            case 1 -> System.out.println("YOU WIN!");
            case 2 -> System.out.println("YOU LOSE!");
            case 3 -> System.out.println("TIE!");
        }
    }

    int computerMove(int treeDepth) {
        Node gameTreeRoot = Node.makeTree(treeDepth, this, null);

        return gameTreeRoot.bestMove();
    }

    // temporary
    int randomMove() {
        Random rand = new Random();

        int move = 7 + (rand.nextInt(6));
        while (board[move] == 0) {
            move = 7 + (rand.nextInt(6));
        }
        return move;
    }

    void doMove(final int move) {
        boolean beenOnOtherSide = false;
        int curHole = 0;

        for (int i = 1; i < board[move] + 1; i++) {
            curHole = (move + i) % 14;
            if ((moveOrder == 0 && curHole != 13) || (moveOrder == 1 && curHole != 6)) {
                if ((moveOrder == 0 && curHole > 6) || (moveOrder == 1 && curHole < 6)) {
                    beenOnOtherSide = true;
                }
                board[curHole] += 1;
            } else {
                board[move] += 1;
            }
        }
        board[move] = 0;

        if (board[curHole] != 0) {
            if (beenOnOtherSide && ((moveOrder == 0 && curHole < 6) || (moveOrder == 1 && curHole > 6 && curHole < 13))) {
                doMove(curHole); // additional move
            }
        }

        if (moveOrder == 0) {
            while (curHole > 6 && curHole < 13 && (board[curHole] == 2 || board[curHole] == 3)) {
                board[6] += board[curHole];
                board[curHole] = 0;
                board[curHole] = 0;
                curHole--;
            }
        } else if (moveOrder == 1) {
            while (curHole >= 0 && curHole < 6 && (board[curHole] == 2 || board[curHole] == 3)) {
                board[13] += board[curHole];
                board[curHole] = 0;
                curHole--;
            }
        }
    }

    void makeMove(final int move) {
        doMove(move);
        moveCount++;
        moveOrder = (moveOrder + 1) % 2;
    }

    void printBoard(final int prevMove) {
        System.out.println("Move count: " + moveCount);

        System.out.print("Previous move: ");
        if (moveCount != 0) {
            System.out.println(prevMove + 1);
        } else {
            System.out.println("-");
        }

        if (moveOrder == 0) {
            System.out.println("YOUR MOVE:\n");
        } else if (moveOrder == 1) {
            System.out.println("COMPUTER MOVE:\n");
        } else {
            System.out.println("\n");
        }

        System.out.print(board[13] + "| ");

        for (int i = 7; i < 13; i++) {
            System.out.print(board[i] + " ");
        }
        System.out.println();

        for (int i = 0; i < 6; i++) {
            System.out.print(board[i] + " ");
        }
        System.out.println("|" + board[6] + "\n");

    }

    public static void makeGame(final int stones, final int moveOrder, final int treeDepth) {
        Game G = new Game(stones);
        G.runGame(treeDepth, moveOrder);
    }
}