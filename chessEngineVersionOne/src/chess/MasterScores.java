package chess;

import java.util.ArrayList;
import java.util.List;

public class MasterScores {

    private List<List<Integer>> masterScoresList;
    private List<Integer> masterScoresListHelper;

    private Engine engine;
    private ChessBoard originalBoard;
    private MasterList masterList;
    private int depth, numberOfBoardsGenerated;
    private boolean successfulExpansion, earlyAbort = false;


    public MasterScores(Engine engine, MasterList masterList) {
        this.originalBoard = originalBoard;
        this.engine = engine;
        this.masterList = masterList;

//		this.depth = 0;
//		this.numberOfBoardsGenerated = 0;
    }

//	public String toString() {
////		for (List<Integer> listBoards : this.masterList) {
////
////			for (ChessBoard boards : listBoards) {
////				System.out.println(boards.toString());
////			}
////		}
////		return this.masterList.toString();
//	}
//	//include wether to min or max!
}