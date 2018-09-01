package chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static chess.ChessTree.typeOfNode.LEAF;

public class NegaMaxer {

    Move performNegaMaxMoveSearch (ChessTree<ChessBoard> tree){

        List<Integer> nmh = negaMaxHelper(tree);

        List<Move> moves = tree.getData().generateMoves();

        int smallest = Collections.min(nmh);

        int indexOfBigMove = nmh.indexOf(smallest);
        Move themove = moves.get(indexOfBigMove);


        return themove;
    }



    private List<Integer> negaMaxHelper(ChessTree<ChessBoard> tree){
        List<Integer> scores = new ArrayList<>();

        int howDeep = Integer.MAX_VALUE;

        for (Tree<ChessBoard> childTree : tree.getChildren()) {
            int negaScore = negaMax(((ChessTree) childTree), howDeep);
            scores.add(negaScore);

        }
        return scores;
    }



    private int negaMax (ChessTree<ChessBoard> tree, int howDeep){

        if (howDeep <= 0 || tree.getTypeOfNode() == LEAF){
            return tree.getScore();
        }

        int value = Integer.MIN_VALUE;

        for (Tree<ChessBoard> childTree : tree.getChildren()) {

            value = Math.max(value,
                    (-negaMax((ChessTree) childTree, howDeep - 1))
            );

            ((ChessTree) childTree).setRippleScore(value);
        }
        return value;
    }




}