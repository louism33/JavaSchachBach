package chess;
/*
 * Usage:
 *    `java StandAlone` : runs text-based chess interface
 *    `java StandAlone -m` : runs interface with printing of legal moves list disabled.
 *    `java StandAlone -l cmdfile` : runs chess game on the moves log stored in 'cmdfile'
 * 			each line of cmdfile is a command that you would normally enter manually into the interface.
 *          For example, cmdfile:
 * 				e2e4
 * 				go
 * 				e4e5
 * 				go
 * 				...
 * 			will make human moves for white (moving the e2 pawn forward) and computer moves for black
 */



import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StandAlone {

    private static boolean logfile = false; // true if we're using a logfile

    public static void main(String[] args) throws IOException {
        InputStreamReader stdin;
        if( args.length >= 2 && args[0].equals("-l") ){
            stdin = new FileReader(args[1]);
            logfile = true;
            System.out.println(" Reading command script from file '"+args[1]+"'...");
        }
        else{
            stdin = new InputStreamReader(System.in);
        }

        boolean printMoves = true; // controls printing of legal moves list
        if( args.length >= 1 && args[0].equals("-m") ){ //flag to disable
            printMoves = false;
        }
        else if( args.length >= 3 && args[2].equals("-m")){
            printMoves = false;
        }


        Move[] moveArray;
        ChessBoard b;
        String command, prompt;
        Move m;

        Engine player = new Engine();
        System.out.println("Computer player: "+player.getName());

        while(true) {
            player.newGame(5*60*1000, 0);  // time control of 5 0, not that it
            // matters much
            while (true) {
                b = player.getBoard();
                if (b.getTurn() == ChessBoard.WHITE) prompt = "White"; else prompt = "Black";
                System.out.println("\n\nPosition ("+prompt+" to move):\n"+b);
                moveArray = (Move[]) b.generateMoves().toArray(new Move[0]);
                if (moveArray.length == 0) {
                    if (b.inCheck()) System.out.println("Checkmate");
                    else System.out.println("Stalemate");
                    break;
                }

                if(printMoves){
                    System.out.println("Moves:");
                    System.out.print("   ");
                    for (int i=0; i<moveArray.length; i++) {
                        if ((i % 10) == 0 && i>0) System.out.print("\n   ");
                        System.out.print(moveArray[i]+" ");
                    }
                }
                System.out.println();
                while(true) {
                    System.out.print(prompt + " move (or \"go\" or \"quit\")> ");
                    command = readCommand(stdin);
                    if (command.equals("go")) {
                        m = player.computeMove(1*60*1000, 0);  /*
                         * simulate 1 minute on the
                         * clock
                         */
                        System.out.println("Computer Moves: " + m);
                        break;
                    } else if (command.equals("quit")) {
                        System.out.println("QUIT.\n");
                        System.exit(1);
                    } else {
                        m = null;
                        for (int i=0; i<moveArray.length; i++) {
                            if (command.equals(moveArray[i].toString())) {
                                m = moveArray[i];
                                break;
                            }
                        }
                        if (m != null) break;
                        System.out.println("\""+command+"\" is not a legal move");
                    }
                }
                player.applyMove(m);
                System.out.println(prompt + " made move "+m);
            }

            while(true) {
                System.out.print("Play again? (y/n):");
                command = readCommand(stdin);
                if (command.equals("n")) System.exit(1);
                if (command.equals("y")) break;
            }
        }
    }
    static String readCommand(InputStreamReader stdin) throws IOException {
        final int MAX = 100;
        int len = 0;
        char[] cbuf = new char[MAX];
        //len = stdin.read(cbuf, 0, MAX);
        for(int i=0; i<cbuf.length; i++){
            if(logfile && !stdin.ready()) return "quit"; // file is done.

            cbuf[i] = (char)stdin.read();
            len++;
            if(cbuf[i] == '\n')
                break;
            if(cbuf[i] == -1){
                System.out.println("An error occurred reading input");
                System.exit(1);
            }
        }

		/*if (len == -1){
            System.out.println("An error occurred reading input");
            System.exit(1);
        }*/
        return new String(cbuf, 0, len).trim();  /* trim() removes \n in unix and \r\n in windows */
    }
}