package chess;

import java.util.ArrayList;
import java.util.List;

public class MasterList {
    private Engine engine;
    private ChessBoard originalBoard;
    private List<List<ChessBoard>> masterList, masterListTemp;
    private List<ChessBoard> tempBoardHolder;
    private int depth, numberOfBoardsGenerated;
    private boolean successfulExpansion, earlyAbort = false;


    public MasterList(Engine engine, ChessBoard originalBoard) {
        this.originalBoard = originalBoard;
        this.engine = engine;
        this.masterList = new ArrayList<>();
        this.masterList = createFirstPly();
        this.depth = 0;
        this.numberOfBoardsGenerated = 0;
    }

    @Override
    public String toString() {
        for (List<ChessBoard> listBoards : this.masterList) {

            for (ChessBoard boards : listBoards) {
                System.out.println(boards.toString());
            }
        }
        return this.masterList.toString();
    }

    private List<List<ChessBoard>> createFirstPly () {
        List<ChessBoard> nextPlyFromOrigin = nextPly(this.originalBoard);
        for (ChessBoard b : nextPlyFromOrigin) {
            // Wrap next boards in a List of length one
            List<ChessBoard> listOfOneBoard = new ArrayList<>();
            listOfOneBoard.add(b);
            this.masterList.add(listOfOneBoard);
        }
        return this.masterList;
    }

    private List<ChessBoard> nextPly (ChessBoard board) {
        @SuppressWarnings("unchecked")
        List<Move> moveList = board.generateMoves();
//		int moveListSize = moveList.size();
        List<ChessBoard> possibleBoards = new ArrayList<>();
        for (Move move : moveList) {
            ChessBoard newBoard = new ChessBoard(board);
            newBoard.makeMove(move);
            possibleBoards.add(newBoard);
        }
        return possibleBoards;
    }



    void developMasterList (long allowedTime) {
        clearMasterListTemp();
        this.masterListTemp.addAll(this.masterList);

        this.successfulExpansion = false;
        for (List<ChessBoard> listOfBoardsConnectedWithMove : this.masterList) {
            int index = this.masterList.indexOf(listOfBoardsConnectedWithMove);
            developMasterListTemp(listOfBoardsConnectedWithMove, index);
        }
        if (this.successfulExpansion) {
            clearMasterList();
            this.masterList.addAll(this.masterListTemp);
            this.depth++;
        }
    }

    private void developMasterListTemp (List<ChessBoard> boardConnectedToIndex, int index) {
        clearTempBoardHolder();

        for (List<ChessBoard> listOfTempBoardsConnectedWithMove : this.masterListTemp) {

            int index2 = this.masterListTemp.indexOf(listOfTempBoardsConnectedWithMove);
            this.developTempBoardHolder(listOfTempBoardsConnectedWithMove, index2);
            this.masterListTemp.set(index, this.tempBoardHolder);

            if (testForTime()) {
                break;
            }
        }
        if (!testForTime ()) {
            this.successfulExpansion = true;
        }
        //		this.masterListTemp.set(index, this.tempBoardHolder);
    }

    private void developTempBoardHolder (List<ChessBoard> listOfTempBoardsConnectedWithMove, int index2) {
        clearTempBoardHolder();
        for (ChessBoard board : listOfTempBoardsConnectedWithMove) {

            List<ChessBoard> expandedBoard = nextPly(board);
            this.tempBoardHolder.addAll(expandedBoard);

            this.numberOfBoardsGenerated++;

            if (testForTime()) {
                break;
            }

        }
    }











    private boolean testForTime () {
        if (earlyAbort) {
            return true;
        }
        else {
            return false;
        }
    }




    ChessBoard getOriginalBoard() {
        return originalBoard;
    }

    private void clearMasterList () {
        this.masterList = new ArrayList<>();
    }

    private void clearMasterListTemp () {
        this.masterListTemp = new ArrayList<>();
    }

    private void clearTempBoardHolder () {
        this.tempBoardHolder = new ArrayList<>();
    }

    Engine getEngine() {
        return engine;
    }

    List<List<ChessBoard>> getMasterList() {
        return masterList;
    }

    int getMasterListDeepLength () {
        int ans = 0;
        for (List<ChessBoard> ls : this.masterList) {
            for(ChessBoard board : ls) {
                ans++;
            }
        }
        return ans;
    }

    int getDepth() {
        return depth;
    }

    boolean isSuccessfulExpansion() {
        return successfulExpansion;
    }

    void setSuccessfulExpansion(boolean successfulExpansion) {
        this.successfulExpansion = successfulExpansion;
    }

    int getNumberOfBoardsGenerated() {
        return numberOfBoardsGenerated;
    }



}