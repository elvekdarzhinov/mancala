package org.suai.elvekdarzhinov.mancala;

import java.util.Arrays;
import java.util.Scanner;

public class Game {
    private static final int PLAYER_KALAH = 6;
    private static final int CMP_KALAH = 13;

    private final int[] board;
    private int moveCount;
    private MoveOrder moveOrder;

    public Game(int stones) {
        this.moveCount = 0;
        this.moveOrder = MoveOrder.PLAYER;
        this.board = new int[14];
        Arrays.fill(board, stones);
        board[PLAYER_KALAH] = 0;
        board[CMP_KALAH] = 0;
    }

    public Game(Game G) {
        this.moveCount = G.moveCount;
        this.moveOrder = G.moveOrder;
        this.board = new int[14];
        System.arraycopy(G.board, 0, this.board, 0, 14);
    }

    public void runGame(int treeDepth, MoveOrder firstMove) {
        moveOrder = firstMove;

        int move = 0;
        int computerMove = 0;
        Scanner scan = new Scanner(System.in);

        while (checkEnd() == 0) {
            if (moveOrder == MoveOrder.PLAYER) {
                printBoard(computerMove % 7);

                move = scan.nextInt();
                move--;

                while (!checkMove(move)) {
                    System.out.print("Impossible move. Try something else: ");
                    move = scan.nextInt();
                    move--;
                }

                makeMove(move);
            } else {
                printBoard(move);

                computerMove = computerMove(treeDepth);

                makeMove(computerMove);
            }
        }

        if (moveOrder == MoveOrder.PLAYER) {
            moveOrder = MoveOrder.SPECIAL;
            printBoard(computerMove % 7);
        } else {
            moveOrder = MoveOrder.SPECIAL;
            printBoard(move);
        }

        printResult();
    }

    public int checkEnd() {
        int playerStones = 0;
        int computerStones = 0;

        for (int i = 0, j = 7; i < PLAYER_KALAH; i++, j++) {
            playerStones += board[i];
            computerStones += board[j];
        }

        if (playerStones == 0) {
            board[CMP_KALAH] += computerStones;
            for (int i = 7; i < CMP_KALAH; i++) {
                board[i] = 0;
            }
        } else if (computerStones == 0) {
            board[PLAYER_KALAH] += playerStones;
            for (int i = 0; i < PLAYER_KALAH; i++) {
                board[i] = 0;
            }
        }

        int allStones = playerStones + computerStones + board[PLAYER_KALAH] + board[CMP_KALAH];

        if (board[PLAYER_KALAH] > allStones / 2) {
            return 1; // Player wins
        } else if (board[CMP_KALAH] > allStones / 2) {
            return 2; // CMP wins
        }

        if ((board[PLAYER_KALAH] == allStones / 2) && (board[CMP_KALAH] == allStones / 2)) {
            return 3; // TIE
        }

        return 0; // Move is possible
    }

    public boolean checkMove(int move) {
        if (moveOrder == MoveOrder.PLAYER) {
            return move >= 0 && move <= 5 && board[move] != 0;
        } else if (moveOrder == MoveOrder.COMPUTER) {
            return move >= 7 && move <= 12 && board[move] != 0;
        }
        return true;
    }

    private void printResult() {
        int result = checkEnd();

        switch (result) {
            case 1 -> System.out.println("YOU WIN!");
            case 2 -> System.out.println("YOU LOSE!");
            case 3 -> System.out.println("TIE!");
        }
    }

    private int computerMove(int treeDepth) {
        Node gameTreeRoot = Node.makeTree(treeDepth, this, null);

        return gameTreeRoot.bestMove();
    }

    private void doMove(int move) {
        boolean beenOnOtherSide = false;
        int curHole = 0;

        for (int i = 1; i < board[move] + 1; i++) {
            curHole = (move + i) % 14;
            if ((moveOrder == MoveOrder.PLAYER && curHole != CMP_KALAH) || (moveOrder == MoveOrder.COMPUTER && curHole != PLAYER_KALAH)) {
                if ((moveOrder == MoveOrder.PLAYER && curHole > PLAYER_KALAH) || (moveOrder == MoveOrder.COMPUTER && curHole < PLAYER_KALAH)) {
                    beenOnOtherSide = true;
                }
                board[curHole] += 1;
            } else {
                board[move] += 1;
            }
        }
        board[move] = 0;

        if (board[curHole] != 0) {
            if (beenOnOtherSide && ((moveOrder == MoveOrder.PLAYER && curHole < PLAYER_KALAH) || (moveOrder == MoveOrder.COMPUTER && curHole > PLAYER_KALAH && curHole < CMP_KALAH))) {
                doMove(curHole); // additional move
            }
        }

        if (moveOrder == MoveOrder.PLAYER) {
            while (curHole > PLAYER_KALAH && curHole < CMP_KALAH && (board[curHole] == 2 || board[curHole] == 3)) {
                board[PLAYER_KALAH] += board[curHole];
                board[curHole] = 0;
                board[curHole] = 0;
                curHole--;
            }
        } else if (moveOrder == MoveOrder.COMPUTER) {
            while (curHole >= 0 && curHole < PLAYER_KALAH && (board[curHole] == 2 || board[curHole] == 3)) {
                board[CMP_KALAH] += board[curHole];
                board[curHole] = 0;
                curHole--;
            }
        }
    }

    public void makeMove(int move) {
        doMove(move);
        moveCount++;
        moveOrder = moveOrder.opposite();
    }

    private void printBoard(int prevMove) {
        System.out.println("Move count: " + moveCount);

        System.out.print("Previous move: ");
        if (moveCount != 0) {
            System.out.println(prevMove + 1);
        } else {
            System.out.println("-");
        }

        if (moveOrder == MoveOrder.PLAYER) {
            System.out.println("YOUR MOVE:\n");
        } else if (moveOrder == MoveOrder.COMPUTER) {
            System.out.println("COMPUTER MOVE:\n");
        } else {
            System.out.println("\n");
        }

        System.out.print(board[CMP_KALAH] + "| ");

        for (int i = 7; i < CMP_KALAH; i++) {
            System.out.print("\t" + board[i]);
        }
        System.out.println();

        for (int i = 0; i < PLAYER_KALAH; i++) {
            System.out.print("\t" + board[i]);
        }
        System.out.println("\t|" + board[PLAYER_KALAH] + "\n");

    }

    public MoveOrder getMoveOrder() {
        return moveOrder;
    }

    public int getPlayerKalah() {
        return board[PLAYER_KALAH];
    }

    public int getCmpKalah() {
        return board[CMP_KALAH];
    }

    public enum MoveOrder {
        PLAYER, COMPUTER, SPECIAL;

        public MoveOrder opposite() {
            return this == PLAYER ? COMPUTER : PLAYER;
        }
    }
}
