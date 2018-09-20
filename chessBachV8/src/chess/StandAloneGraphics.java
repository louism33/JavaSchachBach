package chess;

import javax.swing.*;

public class StandAloneGraphics {

    static boolean startingBoard = true;
    private ChessBoard board;
    private static int totalMoves = 1;
    private static int WIDTH = 700, HEIGHT = 600;




    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        Engine engine = new Engine();
//        board.makeMove(new Move("e2e4"));
        Runnable r = new Runnable() {

            @Override
            public void run() {
                ChessGraphics cb =
                        new ChessGraphics(board, engine);

                JFrame f = new JFrame("ChessChamp");
                f.add(cb.getGui());
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setLocationByPlatform(true);

                f.pack();
                f.setMinimumSize(f.getSize());
                f.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }



    StandAloneGraphics(){

        Engine player = new Engine();

        player.newGame(5 * 60 * 1000, 0);

        board = player.getBoard();

        JFrame jFrame = new JFrame("Louis' Chess Game");
//        jFrame.add(graphics);
        jFrame.setSize(WIDTH, HEIGHT);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);




        //Graphics graphics = new Graphics(board);

        JFrame f = new JFrame("ChessChamp");
//        f.add(graphics.getGui());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        f.pack();
        // ensures the minimum size is enforced.
        f.setMinimumSize(f.getSize());
        f.setVisible(true);


    }

    void masterLoop(ChessBoard board) {

        Move[] moveArray;
        String prompt;

        if (board.getTurn() == ChessBoard.WHITE) prompt = "White";
        else prompt = "Black";
        System.out.println("\n\nPosition (" + prompt + " to move):\n" + board);
        moveArray = (Move[]) board.generateMoves().toArray(new Move[0]);

        checkForEnd(board, moveArray);
    }

//                while(true) {
////                    System.out.print(prompt + " move (or \"go\" or \"quit\")> ");
//
//                    System.out.println("This is move number " + totalMoves+".");
//
//                    }
//
// totalMoves++;
//                startingBoard = false;

//                System.out.println(prompt + " made move "+m);








    static void checkForEnd (ChessBoard b, Move[] moveArray){

        moveArray = (Move[]) b.generateMoves().toArray(new Move[0]);
        if (moveArray.length == 0) {
            if (b.inCheck()) System.out.println("Checkmate");
            else System.out.println("Stalemate");
        }
    }
}
