package chess;

import chess.ChessBoard.SquareDesc;

public class Evaluator {

    private static final int WHITE = ChessBoard.WHITE;

    private static final int KNIGHT = ChessBoard.KNIGHT;
    private static final int BISHOP = ChessBoard.BISHOP;
    private static final int ROOK   = ChessBoard.ROOK;
    private static final int QUEEN  = ChessBoard.QUEEN;
    private static final int KING   = ChessBoard.KING;
    private static final int PAWN   = ChessBoard.PAWN;
    private static final int EMPTY  = ChessBoard.EMPTY;

    static final int MATE = -1000000;


    int lazyEval(ChessBoard board){
        return 0;
    }


    int eval(ChessBoard board) {

        int boardScore = 0;
        int myTurn = board.getTurn();
        int enemyTurn = 1 - myTurn;


        if (isInMate(board)) {
            return MATE;
        }
        if (isStalemate(board)) {
            return 0;
        }

        boardScore = materialAndPosition(board, myTurn) - materialAndPosition(board, enemyTurn)
//                + bishopPair(board, myTurn) - bishopPair(board, enemyTurn)
//                + pawnNumberBonus(board, myTurn) - pawnNumberBonus(board, enemyTurn)
//                + pawnStructureBonus(board, myTurn) - pawnStructureBonus(board, enemyTurn)
//                + basicImbalance(board, myTurn) - basicImbalance(board, enemyTurn)
//                + check(board)
//
//                + casteled(board, myTurn) - casteled(board, enemyTurn)
        ;

        return boardScore;
    }



    private int casteled (ChessBoard board, int turn){

        return (board.hascastled[turn]) ? 5 : 0;
    }

    private int bishopPair (ChessBoard board, int turn){
        int b = 0;
        for (SquareDesc piece : board.getPieces(turn)){
            if (piece.type == BISHOP) b++;
        }
        return (b == 0 | b == 1) ? 0 : 5;
    }

    private int numberOfPawns(ChessBoard board, int turn){
        int p = 0;
        for (SquareDesc piece : board.getPieces(turn)){
            if (piece.type == PAWN) p++;
        }
        return p;
    }

    private int pawnNumberBonus (ChessBoard board, int turn){
        int n = numberOfPawns(board, turn);
        return n;
    }

    private int pawnStructureBonus (ChessBoard board, int turn){
        return 0;
    }

    private int basicImbalance(ChessBoard board, int turn){
        int fac = 1;
        return fac * (board.getPieces(turn).length);
    }

    private int totalPieces(ChessBoard board){
        return (board.getPieces(board.getTurn()).length + board.getPieces(1-board.getTurn()).length);
    }




    private int materialAndPosition(ChessBoard board, int turn) {

        boolean endGame = isEndGame(board);

        int maxMyPieces = board.getPieces(turn).length;
        SquareDesc[] myPieces = board.getPieces(turn);

        int score = 0;

        for (int p = 0; p < maxMyPieces; p ++) {
            SquareDesc piece = myPieces[p];
            int pieceTempScore = pieceValue[piece.type];
            int pieceFinalScore = pieceTempScore + locationValue(piece, turn, endGame);
            score += pieceFinalScore;
        }
        return score;
    }



    private boolean isEndGame(ChessBoard board) {
        return totalPieces(board) < 8;
    }

    private boolean isStalemate (ChessBoard b) {
        Move[] moveArray;
        moveArray = (Move[]) b.generateMoves().toArray(new Move[0]);
        if (moveArray.length == 0 && !b.inCheck()) {
            return true;
        }
        return false;
    }

    private int check (ChessBoard board){
        return board.inCheck() ? -2 : 0;
    }

    private boolean isInMate (ChessBoard b){
        Move[] moveArray;
        moveArray = (Move[]) b.generateMoves().toArray(new Move[0]);
        if (moveArray.length == 0 && b.inCheck()) {
            return true;
        }
        return false;
    }

    private static int locationValue(SquareDesc piece, int currentPlayer, boolean endGame) {
        int ycoord;
        if (currentPlayer == WHITE) {
            ycoord = piece.y;
        }
        else {
            ycoord = (7-piece.y);
        }
        ycoord *= 8;
        if (piece.type == PAWN) {
            int loc = ycoord + piece.x;
            return pawnpos[loc];
        }
        else if (piece.type == KNIGHT) {
            int loc = ycoord + piece.x;
            return knightpos[loc];
        }
        else if (piece.type == BISHOP) {
            int loc = ycoord + piece.x ;
            return bishoppos[loc];
        }
        else if (piece.type == ROOK) {
            int loc = ycoord + piece.x;
            return rookpos[loc];
        }
        else if (piece.type == QUEEN) {
            int loc = ycoord + piece.x;
            return queenpos[loc];
        }
        else if (piece.type == KING && endGame) {
            int loc = ycoord + piece.x;
            return kingposend[loc];
        }
        else if (piece.type == KING) {
            int loc = ycoord + piece.x;
            return kingposstart[loc];
        }
        else return 6666666;
    }


    private static int[] pieceValue = new int[7];
    static {
        pieceValue[PAWN]   = 100;
        pieceValue[KNIGHT] = 320;
        pieceValue[BISHOP] = 330;
        pieceValue[ROOK]   = 500;
        pieceValue[QUEEN]  = 900;
        pieceValue[KING]   = 3000;
        pieceValue[EMPTY]  = 0;
    }


    private static int bishoppos[] = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10,-10,-10,-10,-10,-20,};

    private static int knightpos[] = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,};

    private static int pawnpos[] = {
            0,   0,  0,  0,  0,  0,  0,  0,
            5,  10, 10,-20,-20, 10, 10,  5,
            5,  -5,-10,  0,  0,-10, -5,  5,
            0,   0,  0, 20, 20,  0,  0,  0,
            5,   5, 10, 25, 25, 10,  5,  5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            0,   0,  0,  0,  0,  0,  0,  0,};

    private static int rookpos[] =   {
            0,   0,  0,  5,  5,  0,  0,  0,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            5,  10, 10, 10, 10, 10, 10,  5,
            0,   0,  0,  0,  0,  0,  0,  0};

    private static int queenpos[] =   {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -10,  5,  5,  5,  5,  5,  0,-10,
            0,  0,  5,  5,  5,  5,  0, -5,
            -5,  0,  5,  5,  5,  5,  0, -5,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20,};

    private static int kingposstart[] =   {
            20, 30, 10,  0,  0, 20, 30, 20,
            20, 20,  0,  0,  0,  0, 20, 20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30};

    private static int kingposend[] =   {
            -50,-30,-30,-30,-30,-30,-30,-50,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -50,-40,-30,-20,-20,-30,-40,-50};

}