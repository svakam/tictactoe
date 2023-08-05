package sakam_p1x;
import java.util.Scanner;

/**
 * The P1 class holds the TicTacToe game with 2 players. Users can repeat the
 * game as many times as desired.
 *
 * @author Vik Akam
 * @version 1.0
 */
public class P1x {
    /**
     * Entry point for program. Greets the user and creates a TicTacToe game
     * instance. User chooses board size and plays the game until a winner or
     * tie. Scoreboard is displayed and user is prompted to repeat the game.
     * Scoreboard updates with each game.
     *
     * @param args A String array containing command-line arguments.
     */
    public static void main(String[] args) {
        char userRepeat; // hold user repeat
        final char NO_REPEAT = 'n';
        int boardSize;
        Scanner keyboard = new Scanner(System.in); // create Scanner object
        TicTacToeX gameManager = new TicTacToeX();

        printWelcomeMessage();

        do {
            // get board size from user
            boardSize = TicTacToeX.promptBoardSize(keyboard);

            // game reset with new board size before new game
            gameManager.resetBoard(boardSize);

            gameManager.playGame(keyboard); // playing game requires user input

            gameManager.displayStatistics();

            userRepeat = getUserRepeat(keyboard);
        } while (userRepeat != NO_REPEAT);

        printGoodbyeMessage();

        keyboard.close(); // close the Scanner
    }

    /**
     * Displays the welcome message to the user.
     */
    private static void printWelcomeMessage() {
        System.out.println("\nWelcome to TicTacToe!\n");
    }

    /**
     * Prompts user whether to repeat the game or not.
     *
     * @param keyboard A Scanner object to get user input.
     * @return A character 'y' or 'n'.
     */
    private static char getUserRepeat(Scanner keyboard) {
        char userRepeat; // store user repeat
        final char YES = 'y'; // yes or no constants for user
        final char NO = 'n';

        // prompt as long as answer is not 'y' or 'n'
        do {
            System.out.print("Do you want to play again? (y/n) ");
            userRepeat = keyboard.nextLine().toLowerCase().charAt(0);
        } while (userRepeat != YES && userRepeat != NO);

        return userRepeat;
    }

    /**
     * Bids the user goodbye.
     */
    private static void printGoodbyeMessage() {
        System.out.println("\nThanks for playing! ;)\n");
    }
}
