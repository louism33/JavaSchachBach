package chess;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static chess.ChessBoard.*;

class GraphicsMain {

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private JPanel chessBoard;
    private final JLabel message = new JLabel("");
    private static final String COLS = "ABCDEFGH";
    private Engine engine;
    private GraphicsImageManager gim;
    private ChessBoard board;
    private List<ChessBoard> pastBoards, futureBoards;
    private Color black, white;
    private List<Integer> takenPieces;

    GraphicsMain(ChessBoard board, Engine engine) {
        initializeGraphicsHelper();
        pastBoards = new ArrayList<>();
        futureBoards = new ArrayList<>();
        this.board = board;
        this.possibleMove = new PossibleMove(board);
        this.engine = engine;
        this.engine.newGame();
        takenPieces = new ArrayList<>();
        initializeGui();
    }

    private void initializeGraphicsHelper(){
        this.gim = new GraphicsImageManager();
        black = gim.getBlack();
        white = gim.getWhite();
    }

    private void initializeGui() {
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);

        JButton newG = new JButton("New Game");
        newG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ChessBoard newGame = new ChessBoard();
                newGame.turn = 1;
                setBoard(newGame);
                updateButtons(board);
                possibleMove = new PossibleMove(board);
            }
        });
        tools.add(newG);

        JButton prevBoard = new JButton("Prev Board");
        prevBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int numberOfBoards = pastBoards.size();
                if (numberOfBoards > 0){
                    ChessBoard oldBoard = pastBoards.get(numberOfBoards - 1);
                    futureBoards.add(board);
                    oldBoard.turn = 1 - board.turn;
                    setBoard(oldBoard);
                    updateButtons(board);
                    pastBoards.remove(numberOfBoards - 1);
                    possibleMove = new PossibleMove(board);

                } else {
                    System.out.println("No past boards.");
                }
            }
        });
        tools.add(prevBoard);

        JButton nextBoard = new JButton("Next Board");
        nextBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int numberOfBoards = futureBoards.size();
                if (numberOfBoards > 0){
                    ChessBoard futureBoard = futureBoards.get(numberOfBoards - 1);
                    pastBoards.add(board);
                    futureBoard.turn = 1 - board.turn;
                    setBoard(futureBoard);
                    updateButtons(board);
                    futureBoards.remove(numberOfBoards - 1);
                    possibleMove = new PossibleMove(board);
                }
                else {
                    System.out.println("No next boards.");
                }
            }
        });
        tools.add(nextBoard);

        JButton takenPiece = new JButton("ListMoves");
        takenPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(board.generateMoves());
                updateButtons(board);
            }
        });
        tools.add(takenPiece);
        tools.addSeparator();

        SpinnerModel model = new SpinnerNumberModel(1000, 100, 100000, 100);
        JSpinner spinner = new JSpinner(model);

        tools.add(spinner);
        tools.addSeparator();

        JButton aiMove =
                new JButton("AI Move");
        aiMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int timeForMove = (int) spinner.getValue();
                if (timeForMove < 100) timeForMove = 100;
                if (timeForMove > 100000) timeForMove = 100000;

                long startTime = System.currentTimeMillis();
                Move m = engine.searchGraphics(getBoard(), startTime, timeForMove);
                dealWithMove(m);
                StandAloneGraphics.checkForEnd(getBoard());
            }
        });
        tools.add(aiMove);
        tools.addSeparator();
        tools.add(message);

        gui.add(new JLabel("?"), BorderLayout.LINE_START);
        chessBoard = new JPanel(new GridLayout(0, 9));
        chessBoard.setBorder(new LineBorder(Color.BLACK));
        gui.add(chessBoard);

        initButtons(board);

        chessBoard.add(new JLabel(""));
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii + 1),
                            SwingConstants.CENTER));
        }
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + ((8-ii)),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(chessBoardSquares[jj][ii]);
                }
            }
        }
    }

    private void updateButtons(ChessBoard board){
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                ChessBoard.SquareDesc sq = this.board.getSquare((jj), (7-ii));
                ImageIcon imageIcon = gim.iconFinder(sq);
                chessBoardSquares[jj][ii].setIcon(imageIcon);
            }
        }
    }

    private void initButtons(ChessBoard board){
        Insets buttonMargin = new Insets(0,0,0,0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);

                ChessBoard.SquareDesc sq = this.board.getSquare((jj), (7-ii));

                ImageIcon imageIcon = gim.iconFinder(sq);

                b.setIcon(imageIcon);
                Color backgroundColor;
                if ((jj + ii) % 2 == 0) {
                    backgroundColor = white;
                } else {
                    backgroundColor = black;
                }

                b.setBackground(backgroundColor);

                int x = jj;
                int y = 7-ii;
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        ChessBoard.SquareDesc sq = board.getSquare((x), (y));
                        translateMove(sq);
                    }
                });
                chessBoardSquares[jj][ii] = b;
            }
        }
    }

    private PossibleMove possibleMove;

    private void translateMove(ChessBoard.SquareDesc square){
        possibleMove.addSquare(square);
    }

    private class PossibleMove{
        ChessBoard board;
        String a, b;
        ChessBoard.SquareDesc source, destination;

        PossibleMove(ChessBoard board){
            this.board = board;
        }

        private void addSquare(ChessBoard.SquareDesc square){
            if (source == null){
                source = square;
                destination = null;
            }
            else if (source != null && destination == null){
                destination = square;
                checkMove(source, destination);
            }
            else {
                reset();
            }
        }

        private void checkMove(ChessBoard.SquareDesc source, ChessBoard.SquareDesc destination){
            int srcx = source.x;
            int srcy = source.y;
            int destx = destination.x;
            int desty = destination.y;

            boolean capture = destination.type != EMPTY;

            Move possibleMove = new Move(srcx, srcy, destx, desty, capture);

            if (movesAsStrings().contains(possibleMove.toString())){
                dealWithMove(possibleMove);
                reset();
            } else {
                reset();
            }
        }

        private void reset(){
            a = null;
            b = null;
            source = null;
            destination = null;
        }

        private List<String> movesAsStrings (){
            List<String> movesStrings = new ArrayList<>();
            List<Move> moves = board.generateMoves();
            for (Move move : moves){
                movesStrings.add(move.toString());
            }
            return movesStrings;
        }

        @Override
        public String toString() {
            return (a + " " + b);
        }
    }

    private void dealWithMove(Move m){
        updateBoard(m);
        updateArt(m);
    }

    private void updateBoard(Move move){
        if (move.capture) {
            int destx = move.destx;
            int desty = move.desty;
            ChessBoard.SquareDesc s = board.getSquare(destx, desty);
            takenPieces.add(s.type);
        }

        ChessBoard recordBoard = new ChessBoard(board);
        pastBoards.add(recordBoard);
        board.makeMove(move);
        futureBoards = new ArrayList<>();
        setBoard(board);
        StandAloneGraphics.checkForEnd(getBoard());
    }

    private void updateArt(Move move){

        int srcx = move.srcx;
        int srcy = move.srcy;
        int destx = move.destx;
        int desty = move.desty;

        ChessBoard.SquareDesc readDestFromBoard = board.getSquare(destx, desty);
        Icon icon = gim.iconFinder(readDestFromBoard);
        chessBoardSquares[srcx][7-srcy].setIcon(null);
        chessBoardSquares[destx][7-desty].setIcon(null);
        chessBoardSquares[destx][7-desty].setIcon(icon);

        if (readDestFromBoard.type == PAWN){

            ChessBoard.SquareDesc destPlusOne = board.getSquare(destx, desty+1);
            Icon icon1 = gim.iconFinder(destPlusOne);

            ChessBoard.SquareDesc destMinusOne = board.getSquare(destx, desty-1);
            Icon icon2 = gim.iconFinder(destMinusOne);

            chessBoardSquares[destx][7-destPlusOne.y].setIcon(icon1);
            chessBoardSquares[destx][7-destMinusOne.y].setIcon(icon2);

        } else if (readDestFromBoard.type == KING && readDestFromBoard.x == 2){

            ChessBoard.SquareDesc destLeftOne = board.getSquare(destx-2, desty);
            Icon icon1 = gim.iconFinder(destLeftOne);

            ChessBoard.SquareDesc destRightOne = board.getSquare(destx+1, desty);
            Icon icon2 = gim.iconFinder(destRightOne);

            chessBoardSquares[destx-2][7-desty].setIcon(icon1);
            chessBoardSquares[destx+1][7-desty].setIcon(icon2);

        }   else if (readDestFromBoard.type == KING && readDestFromBoard.x == 6){

            ChessBoard.SquareDesc destLeftOne = board.getSquare(destx-1, desty);
            Icon icon1 = gim.iconFinder(destLeftOne);

            ChessBoard.SquareDesc destRightOne = board.getSquare(destx+1, desty);
            Icon icon2 = gim.iconFinder(destRightOne);

            chessBoardSquares[destx-1][7-desty].setIcon(icon1);
            chessBoardSquares[destx+1][7-desty].setIcon(icon2);

        }
    }

    final JComponent getGui() {
        return gui;
    }

    ChessBoard getBoard() {
        return board;
    }

    void setBoard(ChessBoard board) {
        this.board = board;
    }
}
