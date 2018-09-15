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

    static int eval(ChessBoard board) {
        int boardScore;
        int myTurn = board.getTurn();
        int enemyTurn = 1 - myTurn;
        if (isInMate(board)) {
            return MATE;
        }
        if (isStalemate(board)) {
            return 0;
        }
        boardScore = allConsideredFactors(board, myTurn)
                - allConsideredFactors(board, enemyTurn)
                + weightedImbalanceModifier(board)
                ;

        boolean debugPrinting = true;
        if (!debugPrinting){
            printDebug (board, myTurn, enemyTurn, boardScore);
        }


        return boardScore;
    }

    private static int allConsideredFactors (ChessBoard board, int turn){
        int playersScore = materialAndPosition(board, turn)
//                + bishopPairBonus(board, turn)
//                + pawnStructureBonus(board, turn)
//                + casteledBonus(board, turn)
//                + outpostBonus(board, turn)
//                + rookOnOpenFileBonus (board, turn)
//
//                + inCheckPenalty(board)
//                + doublePawnPenalty(board, turn)
//                + singleBishopColorPenalty (board, turn)
//

                ;



        return playersScore;
    }


    private static void printDebug (ChessBoard board, int myTurn, int enemyTurn, int boardScore){
        printStuff (board, myTurn, allConsideredFactors(board, myTurn));
        printStuff (board, enemyTurn, allConsideredFactors(board, enemyTurn));
        System.out.println("--- WiM: "+ weightedImbalanceModifier(board) + " -");
        System.out.println("----- Final Score: " + boardScore + " ---\n");
    }
    private static void printStuff(ChessBoard board, int turn, int playersScore){

        System.out.println("- turn: "+turn);
        System.out.println("MaP: " + materialAndPosition(board, turn));
        System.out.println("---");
        System.out.println("BpB: " +bishopPairBonus(board, turn));

        System.out.println("PsB: " +pawnStructureBonus(board, turn));
        System.out.println("CaB: "+casteledBonus(board, turn));
        System.out.println("OpB: "+outpostBonus(board, turn));
        System.out.println("RoF: " +rookOnOpenFileBonus (board, turn));
        System.out.println("---");
        System.out.println("IcP: "+inCheckPenalty(board));
        System.out.println("DpP: "+doublePawnPenalty(board, turn));
        System.out.println("BcP: " +singleBishopColorPenalty (board, turn));
        System.out.println("---");

        System.out.println("finalPlayerScore = " + playersScore);
        System.out.println("-");
    }

    private static int materialAndPosition(ChessBoard board, int turn) {
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

    private static int rookOnOpenFileBonus (ChessBoard board, int turn){
        int rookOnOpenFileBonus= 15;
        int finalBonus = 0;
        nextPiece:
        for (SquareDesc piece : board.getPieces(turn)){
            if (piece.type == ROOK) {
                for (int y = 1; y < 7; y++) {
                    SquareDesc potentialBlocker = board.getSquare(piece.x, y);
                    if (potentialBlocker.type == PAWN && potentialBlocker.color == turn){
                        continue nextPiece;
                    }
                }
                finalBonus += rookOnOpenFileBonus;
            }
        }
        return finalBonus;
    }

    private static int casteledBonus (ChessBoard board, int turn){
        int casteledBonus = 30;
        return (board.hascastled[turn]) ? casteledBonus : 0;
    }

    private static int bishopPairBonus (ChessBoard board, int turn){
        int bishopPairBonus = 25;
        int b = numberOfFriendlyBishops(board, turn);
        return (b == 0 | b == 1) ? 0 : bishopPairBonus;
    }

    private static int pawnStructureBonus (ChessBoard board, int turn){
        int pawnStructureBonus = 10;
        int finalBonus = 0;
        int dealingWithDirection = turn * 2 - 1; // white == 1, black == -1
        for (SquareDesc piece : board.getPieces(turn)){
            if (piece.type == PAWN) {
                if (piece.x - 1 >= 0){
                    SquareDesc potentialPawnAlly = board.getSquare(piece.x - 1,  piece.y - dealingWithDirection);
                    if (potentialPawnAlly.type == PAWN && potentialPawnAlly.color == turn){
                        finalBonus += pawnStructureBonus;
                    }
                }
                if (piece.x + 1 <= 7){
                    SquareDesc potentialPawnAlly = board.getSquare(piece.x + 1,  piece.y - dealingWithDirection);
                    if (potentialPawnAlly.type == PAWN && potentialPawnAlly.color == turn){
                        finalBonus += pawnStructureBonus;
                    }
                }
            }
        }
        return finalBonus;
    }

    private static int outpostBonus (ChessBoard board, int turn){
        int outpostBonus = 20;
        int finalBonus = 0;
        int dealingWithDirection = turn * 2 - 1; // white == 1, black == -1
        for (SquareDesc piece : board.getPieces(turn)){
            if (piece.type == PAWN && piece.y != 1 && piece.y != 6) {
                if (piece.x - 1 >= 0){
                    SquareDesc potentialAlly = board.getSquare(piece.x - 1,  piece.y + dealingWithDirection);
                    if ((potentialAlly.type == BISHOP || potentialAlly.type == KNIGHT) && potentialAlly.color == turn){
                        finalBonus += outpostBonus;
                    }
                }
                if (piece.x + 1 <= 7){
                    SquareDesc potentialAlly = board.getSquare(piece.x + 1,  piece.y + dealingWithDirection);
                    if ((potentialAlly.type == BISHOP || potentialAlly.type == KNIGHT) && potentialAlly.color == turn){
                        finalBonus += outpostBonus;
                    }
                }
            }
        }
        return finalBonus;
    }

    private static int weightedImbalanceModifier(ChessBoard board){
        // create incentive for trades when ahead, penalise them when behind
        // (by approx 5 points in realistic midgame)
        int weightImbalanceModifier = 75;
        int myTotalPieces = board.getPieces(board.getTurn()).length;
        int yourTotalPieces = board.getPieces(1 - board.getTurn()).length;
        float ratio = ((float) myTotalPieces) / ((float) yourTotalPieces);
        int finalModifier = (int) (ratio * weightImbalanceModifier - weightImbalanceModifier);
        return finalModifier;
    }

    private static int doublePawnPenalty (ChessBoard board, int turn) {
        int doubledPawnPenalty = -10;
        int finalPenalty = 0;
        for (SquareDesc piece : board.getPieces(turn)){
            if (piece.type == PAWN) {
                int y = 6;
                while (y > 0){
                    if (y == piece.y) {
                        y--;
                        continue;
                    }
                    SquareDesc potentialDoublePawn = board.getSquare(piece.x,  y);
                    if (potentialDoublePawn.type == PAWN &&
                            potentialDoublePawn.color == turn){
                        finalPenalty += doubledPawnPenalty;
                    }
                    y--;
                }
            }
        }
        return finalPenalty;
    }

    private static int singleBishopColorPenalty(ChessBoard board, int turn) {
        int pawnOnMyColorPenalty = -5;
        int finalPenalty = pawnOnMyColorPenalty * (-2); // penalty starts at three pawns on my colour
        if (numberOfFriendlyBishops(board, turn) == 1) {
            for (SquareDesc piece : board.getPieces(turn)) {
                if (piece.type == BISHOP){
                    int black = (piece.x + piece.y) % 2;
                    for (SquareDesc enemyPiece : board.getPieces(1-turn)) {
                        int enemyCol = (piece.x + piece.y) % 2;
                        if (enemyPiece.type == PAWN && enemyCol == black) {
                            finalPenalty += pawnOnMyColorPenalty;
                        }
                    }
                }
            }
            return finalPenalty;
        }
        return 0;
    }

    private static int inCheckPenalty (ChessBoard board){
        return board.inCheck() ? -2 : 0;
    }

    private static int numberOfFriendlyBishops (ChessBoard board, int turn){
        int b = 0;
        for (SquareDesc piece : board.getPieces(turn)){
            if (piece.type == BISHOP) b++;
            if (b > 1) return b;
        }
        return b;
    }

    private static int totalPieces(ChessBoard board){
        return (board.getPieces(board.getTurn()).length + board.getPieces(1-board.getTurn()).length);
    }

    private static boolean isEndGame(ChessBoard board) {
        return totalPieces(board) < 8;
    }

    private static boolean isStalemate (ChessBoard b) {
        Move[] moveArray;
        moveArray = (Move[]) b.generateMoves().toArray(new Move[0]);
        if (moveArray.length == 0 && !b.inCheck()) {
            return true;
        }
        return false;
    }

    private static boolean isInMate (ChessBoard b){
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
            -30,  5, 10, 15, 15, 11,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,};

    private static int pawnpos[] = {
            0,   0,  0,  0,  0,  0,  0,  0,
            5,  10, 10,-20,-20, 10, 10,  5,
            5,   5,  5,  0,  0, -5,  0,  5,
            5,   5,  5, 20, 20,  0,  0,  0,
            5,   5, 10, 25, 25, 10,  5,  5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            0,   0,  0,  0,  0,  0,  0,  0,};

    private static int rookpos[] =   {
            0,   0,  0,  5,  5,  0,  0,  0,
            5,   0,  0,  0,  0,  0,  0,  5,
            5,   0,  0,  0,  0,  0,  0,  5,
            10,  5,  5,  5,  5,  5,  5, 10,
            10,  0,  0,  0,  0,  0,  0,  0,
            -5,  0,  0,  0,  0,  0,  0, -5,
            5,  10, 10, 10, 10, 10, 10,  5,
            0,   0,  0,  0,  0,  0,  0,  0};

    private static int queenpos[] =   {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -10,  5,  5,  5,  5,  5,  0,-10,
            0,    0,  5,  5,  5,  5,  0, -5,
            -5,   0,  5,  5,  5,  5,  0, -5,
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