package chess;

import javax.swing.*;
import java.util.logging.Logger;

public class StandAloneGraphics {

    private static final Logger logger = Logger.getLogger(StandAloneGraphics.class.getName());

    public static void main(String[] args) {
//        logger.info("Starting Main Method");
        ChessBoard board = new ChessBoard();
        Engine engine = new Engine();
        Runnable r = new Runnable() {

            @Override
            public void run() {
                GraphicsMain cb =
                        new GraphicsMain(board, engine);

                JFrame f = new JFrame("Louis's Chess");
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

    static void checkForEnd (ChessBoard b){
        Move[] moves = (Move[]) b.generateMoves().toArray(new Move[0]);
        if (moves.length == 0) {
            if (b.inCheck()) System.out.println("Checkmate");
            else System.out.println("Stalemate");
        }
    }
}
