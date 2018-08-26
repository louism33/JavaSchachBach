package chess;

import java.util.ArrayList;
import java.util.List;

public class TreeMaker {
//    BoardTree boardTree;


//    TreeMaker(BoardTree<ChessBoard> boardTree){
////        this.boardTree = boardTree;
//    }


//    void deepenTree (BoardTree<ChessBoard> boardTree){
//        List<Move> moveList = boardTree.getBoard().generateMoves();
//
//        for (Move move : moveList) {
//            ChessBoard newBoard = new ChessBoard(boardTree.getBoard());
//            newBoard.makeMove(move);
//            BoardTree<ChessBoard> babyTree = new BoardTree<>(newBoard);
//            babyTree.setParent(boardTree);
//            boardTree.addChild(babyTree);
//        }
//    }


//    private List<ChessBoard> nextPly (ChessBoard board) {
//        @SuppressWarnings("unchecked")
//        List<Move> moveList = board.generateMoves();
////		int moveListSize = moveList.size();
//        List<ChessBoard> possibleBoards = new ArrayList<>();
//        for (Move move : moveList) {
//            ChessBoard newBoard = new ChessBoard(board);
//            newBoard.makeMove(move);
//            possibleBoards.add(newBoard);
//        }
//        return possibleBoards;
//    }

}
