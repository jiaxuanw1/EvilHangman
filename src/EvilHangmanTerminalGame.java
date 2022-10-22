import java.util.Scanner;

public class EvilHangmanTerminalGame {

    private final Scanner scanner;

    public static void main(String[] args) {
        new EvilHangmanTerminalGame();
    }

    public EvilHangmanTerminalGame() {
        scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");

        do {
            EvilHangmanGameManager game = new EvilHangmanGameManager(10);
            game.startGame();

            System.out.println("The word is " + game.getWordLength() + " letters long.");
            System.out.println("You have " + game.getGuessesRemaining() + " guesses.");

            while (!game.isFinished()) {
                // Prompt user for guess
                char guess = promptGuess(game);
                int guessCount = game.guess(guess);

                // Display number of occurrences of the guessed letter in the word family
                if (guessCount > 1) {
                    System.out.println("Yes, there are " + guessCount + " copies of " + guess + ".");
                } else if (guessCount == 1) {
                    System.out.println("Yes, there is 1 copy of " + guess + ".");
                } else {
                    System.out.println("Sorry, there are no " + guess + "'s.");
                }

                // Game is still ongoing
                if (!game.isFinished()) {
                    if (game.getGuessesRemaining() > 1) {
                        System.out.println("You have " + game.getGuessesRemaining() + " guesses left.");
                    } else {
                        System.out.println("You have 1 guess left.");
                    }
                    System.out.print("Used letters:");
                    for (char letter : game.getLettersGuessed()) {
                        System.out.print(" " + letter);
                    }
                    System.out.println("\nWord: " + game.getWordPattern());
                }
            }

            // Player wins
            if (game.checkWin()) {
                System.out.println("\nYou win!");
                System.out.println("The word was: " + game.getWordList().get(0));
                if (game.getGuessesRemaining() > 1) {
                    System.out.println("You had " + game.getGuessesRemaining() + " guesses left.");
                } else {
                    System.out.println("You had 1 guess left.");
                }
            }
            // Player loses
            else {
                System.out.println("\nYou lose!");
                System.out.println("The word was: " + game.getWordList().get(0));
            }
        } while (promptPlayAgain());
    }

    private char promptGuess(EvilHangmanGameManager game) {
        while (true) {
            System.out.print("\nEnter guess: ");
            String input = scanner.next().trim().toLowerCase();
            char c = input.charAt(0);

            if (input.equals("exit")) {
                System.exit(0);
            }

            if (input.length() != 1) {
                System.out.println("Please enter exactly one letter!");
            } else if (c < 'a' || c > 'z') {
                System.out.println("Please enter a letter in the alphabet!");
            } else if (game.getLettersGuessed().contains(c)) {
                System.out.println("Letter has already been guessed, please enter another guess!");
            } else {
                return c;
            }
        }
    }

    private boolean promptPlayAgain() {
        while (true) {
            System.out.print("\nPlay again? (y/n): ");
            String input = scanner.next().trim().toLowerCase();
            if (input.equals("y")) {
                System.out.println();
                return true;
            } else if (input.equals("n")) {
                System.exit(0);
            }
        }
    }

}
