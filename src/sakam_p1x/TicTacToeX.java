package sakam_p1x;
import java.util.Scanner;

/**
 * The TicTacToe (herein referred to as TTT) class contains all relevant
 * properties and methods needed to 1) play a full game of TTT and 2) keep
 * track of scores between X and O in a given instance of this class.
 *
 * @author Vik Akam
 * @version 1.0
 */
public class TicTacToeX {
    /*
     *  fields that are independent of a particular game (and always
     *  inherent to TTT) are:
     *  - number of players
     *  - player pieces ("X", "O" and an empty placeholder critical to
     *      allowing the game to function)
     *  - players (declared as 1 and 2)
     *  - reference to empty piece and the index of tie game score (EMPTY_IDX
     *      = 0)
     */
    final private static int NUM_PLAYERS = 2;
    final private static String[] PLAYER_PIECES = {" ", "X", "O"};
    final private static int[] PLAYERS = new int[]{1, 2};
    final private static int EMPTY_IDX = 0;

    /* fields that are required to play a game:
     *  - game board represented as a 2D int array
     *  - board length (conventionally 3, but scales out to any length)
     *  - max # of pieces that can be on the board
     *    -- board is always an odd-length'd square, so max # pieces = board
     *     length ^ 2 length
     *  - current player turn
     *  - current winner of a game (representation and piece)
     */
    private int[][] gameBoard;
    private int boardLength;
    private int maxPieces;
    private int numPiecesOnBoard;
    private int currWinner;
    private String currWinnerPiece;
    private int playerTurnIdx;
    private int playerTurn;

    /*
     * field required to keep track of score during a full TTT session
     *  (i.e. playing again in the same program):
     *    - scoreboard
     */
    private int[] scoreBoard;

    /**
     * The constructor sets up the
     * scoreboard to hold the scores of 2 players + a tie game counter.
     */
    public TicTacToeX() {
        scoreBoard = new int[PLAYER_PIECES.length];// holds scores for 2
        // players + a counter for tie games
    }

    /**
     * Prompts a user for TTT board size. Input must be an odd number between
     * 3-25. Usage must precede all other method usages for a given playthrough.
     *
     * @param keyboard Scanner object for getting user integer input.
     * @return An odd integer of board size between 3-25.
     */
    public static int promptBoardSize(Scanner keyboard) {
        int input; // hold user input

        final int MIN_SIZE = 3; // min and max sizes of game board
        final int MAX_SIZE = 25;

        do {
            System.out.print("Please enter an odd number for board size " +
                    "(between 3-25): ");

            // validate user input is an integer
            while (!keyboard.hasNextInt()) {
                System.out.println("Sorry, that's not a valid integer!");

                keyboard.next();

                // reprompt user for integer
                System.out.print("Enter an odd number for board size " +
                        "(between 3-25): ");
            }

            input = keyboard.nextInt();

        // input must be odd and between min and max bounds
        } while (input < MIN_SIZE || input > MAX_SIZE || input % 2 == 0);

        return input;
    }

    /**
     * Resets the game board for a new game (i.e.
     * creating a new empty board with the class's board length, resetting
     * number of pieces on the board to 0, resetting current winner to
     * empty/tie, and player turn to arbitrary 0.
     *
     * @param boardLen An integer representing length of TTT board to create.
     */
    public void resetBoard(int boardLen) {
        boardLength = boardLen;
        maxPieces = boardLen * boardLen;
        gameBoard = new int[boardLength][boardLength];
        numPiecesOnBoard = 0;
        currWinner = EMPTY_IDX;
        playerTurnIdx = 0;
    }

    /**
     * Runs a full game of TTT (i.e. rotating players
     * between X and O, showing the game board when necessary, and allowing
     * the player to choose a valid spot to place on the board. This method
     * also checks for a winner after each turn.
     *
     * @param keyboard A scanner object to allow player input.
     */
    public void playGame(Scanner keyboard) {

        /*
         * a game playthrough consists of rotating between players, showing
         * the gameboard between each turn, and running a full player turn
         */
        do {
            rotatePlayer();
            displayGameBoard();
            runPlayerTurn(keyboard);

        /*
         * game stops once a winner is found or number of pieces on the board
         * are too many
         */
        } while (!winnerExists() && numPiecesOnBoard < maxPieces);

        /*
         * after a game is done, show the winner of the game, the
         * resultant gameboard, and update scoreboard with new result
         */
        declareWinner();
        displayGameBoard();
        updateScoreboard();
    }

    /**
     * Allows players (i.e. X, O) to alternate turns
     * in TTT so that no player goes twice or more in a row.
     */
    private void rotatePlayer() {
        // idx is 0 or 1 - uses as index to access PLAYERS
        playerTurnIdx %= NUM_PLAYERS;

        /*
         * turn is 1 or 2 - uses as index to access PLAYER_PIECES at X or O if
         * needed, AND as non-0 game board pieces (0 = empty)
         */
        playerTurn = PLAYERS[playerTurnIdx];

        playerTurnIdx++;
    }

    /**
     * Checks if the winner of the current game
     * was one of the players in the PLAYERS array (i.e. 1, 2). If so, it
     * matches it to the player piece and prints that piece as winner. Else
     * it's declared a tie via a non-PLAYER (i.e. 0).
     */
    private void declareWinner() {
        if (currWinner == EMPTY_IDX) {
            System.out.println("No winner, - it was a tie!");
        } else {
            currWinnerPiece = PLAYER_PIECES[currWinner]; // X or O

            System.out.printf("%s, you have won the game!\n",
                    currWinnerPiece);
        }
    }

    /**
     * Conducts all operations necessary to:
     * 1) clearly display whose turn it is,
     * 2) choose a valid row and column on the board to place his/her piece, and
     * 3) add the "piece" (more specifically a representation of the piece) to
     *      the board.
     *
     * @param keyboard A scanner object to allow player input.
     */
    private void runPlayerTurn(Scanner keyboard) {

        int row, col; // declare variables to hold row and column choices

        // show player turn via X or O
        System.out.printf("%s, it is your turn.\n",
                PLAYER_PIECES[playerTurn]);

        /*
         * ask for row and column input as long as input is invalid (i.e.
         * taken by another piece on the board)
         */
        do {
            row = getBoardIdx("row", keyboard);

            col = getBoardIdx("column", keyboard);
        } while (isInvalidInput(row, col));

        // add piece via current player and choice of board spot
        addPieceToBoard(playerTurn, row, col);
    }

    /**
     * Asks the user for the row or column they
     * want to place a TTT piece at. It also validates the input by checking
     * for valid integer and within game board bounds.
     *
     * @param rowOrCol A string representing row or column to prompt from user.
     * @param keyboard A scanner object to get user input.
     * @return An integer representing row or column.
     */
    private int getBoardIdx(String rowOrCol, Scanner keyboard) {
        int input; // store user input

        // repeat prompting until user integer input is within board bounds
        do {
            System.out.print("Which " + rowOrCol + "? ");

            // validate user has integer input
            while (!keyboard.hasNextInt()) { // scan ahead for valid int token

                System.out.println("Sorry, that's not a valid integer input.");

                keyboard.next(); // pass the current token if invalid

                System.out.print("Which " + rowOrCol + "? ");
            }

            input = keyboard.nextInt(); // store valid int token

            keyboard.nextLine(); // ready the delimiter check on scanner object

        // board bounds from 0 to its length
        } while (input < 0 || input >= boardLength);

        return input;
    }

    /**
     * Passed in a player representation and
     * the player's row/column choices to add their
     * piece to game board and update total number of pieces on the board.
     *
     * @param player Player representation (1 or 2).
     * @param row Player's board row choice.
     * @param col Player's board column choice.
     */
    private void addPieceToBoard(int player, int row, int col) {
        numPiecesOnBoard += 1;
        gameBoard[row][col] = player;
    }

    /**
     * Runs a number of operations on the board to
     * check if a player placed 3 of their pieces in a row (or board
     * length-in a row). The operations check every row, column, and diagonal
     * for the same type of piece in a given line.
     *
     * @return A boolean if a winner does exist.
     */
    private boolean winnerExists() {

        /*
           there can be no winner if total number of pieces on the board is
           less than the board length
         */
        if (numPiecesOnBoard < boardLength) {
            return false;
        }

        // run the operations checking each row/column/diagonal
        return rowContainsWinner() || columnContainsWinner() ||
                diagonalContainsWinner() || reverseDiagContainsWinner();
    }

    /**
     * Checks each row for recurring player
     * placement in a given row.
     *
     * @return A boolean stating if winner was found in a row.
     */
    private boolean rowContainsWinner() {
        int startPiece; // hold start piece of row; used to self-compare row

        // nested for loop to check every row up to board length
        for (int row = 0; row < boardLength; row++) {

            startPiece = gameBoard[row][0]; // start piece of a row

            // no winner if there wasn't a piece placed at the start of the row
            if (startPiece != EMPTY_IDX) {

                /*
                 * if a row piece differs from the start piece, winner
                 * can't be in this row; move onto next row to save time
                 */
                int col = 1;
                while (col < boardLength &&
                        gameBoard[row][col] == startPiece) {

                    col++;
                }

                col--; // backtrack to within board bounds

                /*
                 * check why while loop was ended
                 * if current piece differs from start piece, no winner;
                 * else set the game's current winner to the start piece of
                 * this row and return true
                 */
                if (col == boardLength - 1 &&
                        gameBoard[row][col] == startPiece) {

                    currWinner = startPiece;
                    return true;
                }
            }
        }

        return false; // returns false if none of rows had winner
    }

    /**
     * Follows the same logic as the column
     * contains row method, except its nested for loop implementation is
     * modified to check down columns instead of rows for the same recurring
     * piece.
     *
     * @return A boolean stating if winner was found in a column.
     */
    private boolean columnContainsWinner() {
        int startPiece;

        for (int col = 0; col < boardLength; col++) {
            startPiece = gameBoard[0][col];
            if (startPiece != EMPTY_IDX) {
                int row = 1;

                while (row < boardLength &&
                        gameBoard[row][col] == startPiece) {

                    row++;
                }

                row--; // backtrack to within board bounds

                if (row == boardLength - 1 &&
                        gameBoard[row][col] == startPiece) {

                    currWinner = startPiece;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the same recurring piece exists
     * down the up-down, left-right diagonal. Same logic to check for winner
     * as rowContainsWinner().
     *
     * @return A boolean stating if winner was found in this diagonal.
     */
    private boolean diagonalContainsWinner() {
        int startPiece = gameBoard[0][0]; // start at top left

        if (startPiece != EMPTY_IDX) {
            int row = 1;
            int col = 1;

            while (row < boardLength && gameBoard[row][col] == startPiece) {
                row++;
                col++;
            }

            row--;
            col--; // backtrack to within board bounds

            if (row == boardLength - 1 && gameBoard[row][col] == startPiece) {
                currWinner = startPiece;
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the same recurring piece
     * exists down the up-down, right-left diagonal. Same logic as checking
     * diagonal.
     *
     * @return A boolean stating if winner was found in this diagonal.
     */
    private boolean reverseDiagContainsWinner() {
        int startPiece = gameBoard[0][boardLength - 1]; // start at top right

        if (startPiece != EMPTY_IDX) {
            int row = 1;
            int col = boardLength - 2;

            while (row < boardLength && gameBoard[row][col] == startPiece) {
                row++;
                col--;
            }

            row--;
            col++; // backtrack to within board bounds

            if (row == boardLength - 1 && gameBoard[row][col] == startPiece) {
                currWinner = startPiece;
                return true;
            }
        }

        return false;
    }

    /**
     * Passed in integers representing row and
     * column choices from user to check if that row and column is already
     * occupied by a player piece. If so, it tells user it was invalid.
     *
     * @param row An integer representing user row choice.
     * @param col An integer representing user column choice.
     * @return A boolean representing if invalid input or not.
     */
    private boolean isInvalidInput(int row, int col) {

        // player pieces on board are represented by 1 or 2, and no piece by 0
        if (gameBoard[row][col] != EMPTY_IDX) {

            // invalid choice if piece exists
            System.out.println("Bad location, try again...");
            return true;
        }
        return false;
    }

    /**
     * Allows for a user-friendly
     * visualization of the current game board. The method iterates over the 2D
     * array representation of the TTT board in the current game to format it
     * with player pieces and visual separators.
     */
    private void displayGameBoard() {
        int playerRepr;
        String pieceValue;

        System.out.println();

        // print column headers (0, 1, 2, etc.)
        System.out.print(" ");
        for (int col = 0; col < boardLength; col++) {
            System.out.printf("%3d", col);
        }
        System.out.println();

        // iterate over game board
        for (int row = 0; row < boardLength; row++) {
            /*
             * print a full row: row number, player representation piece, and
             * vertical separator
             */

            // current row number
            System.out.printf("%2d", row);

            // player representation piece (0 (none), 1 or 2) with separator
            for (int col = 0; col < boardLength; col++) {
                playerRepr = gameBoard[row][col]; // 0, 1 or 2

                // translates 1 or 2 to X or O
                pieceValue = translateValToXO(playerRepr);

                // player representation with column separator
                System.out.printf("%2s|", pieceValue);
            }

            // print row separator
            System.out.println();
            System.out.print("  ");
            for (int col = 0; col < boardLength; col++) {
                System.out.print("---");
            }

            System.out.println();
        }
    }

    /**
     * Passed in a player piece value
     * and translates it to "space", X or O. 1 maps to X and 2 to O in the
     * PLAYER_PIECES array. 0 maps to " " and represents no piece in this spot
     * on the board.
     *
     * @param positionValue Player piece representation on game board (1 or 2).
     * @return A String translated as "X", "O" or " ".
     */
    private String translateValToXO(int positionValue) {
        return PLAYER_PIECES[positionValue];
    }

    /**
     * Increments the score of the current winner.
     * Current winner may be 1 or 2 (X or O), or 0 (tie).
     */
    private void updateScoreboard() {
        scoreBoard[currWinner]++;
    }

    /**
     * Uses the TTT class's scoreboard to show
     * the number of wins for X, O and the number of ties.
     */
    public void displayStatistics() {
        System.out.println("\nGame Stats");

        // iterate for number of players (2)
        for (int playerTurnIdx = 0; playerTurnIdx < NUM_PLAYERS;
             playerTurnIdx++) {

            // display player piece and his/her score from scoreboard
            System.out.printf("%s has won %d games.\n",
                    PLAYER_PIECES[PLAYERS[playerTurnIdx]],
                    scoreBoard[PLAYERS[playerTurnIdx]]
            );
        }

        // display tie games
        System.out.printf("There have been %d tie games.\n",
                scoreBoard[EMPTY_IDX]);
    }
}
