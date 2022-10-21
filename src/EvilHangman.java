import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EvilHangman {

    private List<String> wordList = new ArrayList<String>();
    private List<Character> lettersGuessed = new ArrayList<Character>();
    private int guessesRemaining;

    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        EvilHangman hangman = new EvilHangman();
        do {
            hangman.newGame();
        } while (hangman.promptPlayAgain());
    }

    public void newGame() {
        // Reset word list
        wordList.clear();
        loadDictionary("dictionary.txt");

        // Generate random word length
        int wordIndex = (int) (Math.random() * wordList.size());
        int wordLength = wordList.get(wordIndex).length();

        // Remove words that are not the right length
        List<String> newWordList = new ArrayList<String>();
        for (String word : wordList) {
            if (word.length() == wordLength) {
                newWordList.add(word);
            }
        }
        wordList = newWordList;

        // Reset list of guesses and number of remaining guesses
        lettersGuessed.clear();
        guessesRemaining = 10;

        System.out.println("The word is " + wordLength + " letters long.");
        System.out.println("You have " + guessesRemaining + " guesses.");
        while (guessesRemaining > 0) {
            char guess = promptGuess();
            updateWordList(guess);
            updateGameStatus(guess);
        }
    }

    public char promptGuess() {
        while (true) {
            System.out.print("\nEnter guess: ");
            String input = scanner.next().trim().toLowerCase();
            char c = input.charAt(0);

            if (input.length() != 1) {
                System.out.println("Please enter exactly one letter!");
            } else if (c - 'a' < 0 || c - 'a' > 25) {
                System.out.println("Please enter a letter in the alphabet!");
            } else if (lettersGuessed.contains(c)) {
                System.out.println("Letter has already been guessed, please enter another guess!");
            } else {
                return c;
            }
        }
    }

    public void updateWordList(char guess) {
        // Add guessed letter to list of guesses
        lettersGuessed.add(guess);
        Collections.sort(lettersGuessed);

        // Generate all word families with updated guess list
        Map<String, List<String>> wordFamilies = new HashMap<String, List<String>>();
        for (String word : wordList) {
            String pattern = getPattern(word, lettersGuessed);
            if (!wordFamilies.containsKey(pattern)) {
                wordFamilies.put(pattern, new ArrayList<String>());
            }
            wordFamilies.get(pattern).add(word);
        }

        // Choose the most ideal word family and make it the new word list
        wordList = chooseWordFamily(wordFamilies.values(), guess);
    }

    public List<String> chooseWordFamily(Collection<List<String>> wordFamilies, char guess) {
        // Get all word families with the largest number of words
        int largestSize = 0;
        List<List<String>> largestWordFamilies = new ArrayList<List<String>>();
        for (List<String> wordFamily : wordFamilies) {
            if (wordFamily.size() > largestSize) {
                largestSize = wordFamily.size();
                largestWordFamilies.clear();
                largestWordFamilies.add(wordFamily);
            } else if (wordFamily.size() == largestSize) {
                largestWordFamilies.add(wordFamily);
            }
        }

        // If there is only 1 largest word family, choose it
        if (largestWordFamilies.size() == 1) {
            return largestWordFamilies.get(0);
        }

        // If tie, choose word family with least occurrences of guessed letter
        List<String> lowestGuessFamily = largestWordFamilies.get(0);
        int lowestGuessCount = getCharCount(guess, lowestGuessFamily.get(0));
        for (List<String> wordFamily : largestWordFamilies) {
            int guessCount = getCharCount(guess, wordFamily.get(0));
            if (guessCount < lowestGuessCount) {
                lowestGuessFamily = wordFamily;
                lowestGuessCount = guessCount;
            }
        }

        return lowestGuessFamily;
    }

    public String getPattern(String word, List<Character> letters) {
        StringBuilder wordPattern = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            wordPattern.append(letters.contains(c) ? c : '-');
        }
        return wordPattern.toString();
    }

    public int getCharCount(char c, String str) {
        int charCount = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                charCount++;
            }
        }
        return charCount;
    }

    public void updateGameStatus(char guess) {
        String word = wordList.get(0);
        String wordPattern = getPattern(word, lettersGuessed);

        // Display number of occurrences of the guessed letter in the word family
        int guessCount = getCharCount(guess, wordPattern);
        if (guessCount > 1) {
            System.out.println("Yes, there are " + guessCount + " copies of " + guess + ".");
        } else if (guessCount == 1) {
            System.out.println("Yes, there is 1 copy of " + guess + ".");
        } else {
            System.out.println("Sorry, there are no " + guess + "'s.");
            guessesRemaining--;
        }

        // Player has won if only 1 possible word and all its letters have been guessed
        if (wordList.size() == 1 && !wordPattern.contains("-")) {
            System.out.println("\nYou win!");
            System.out.println("The word was: " + word);
            if (guessesRemaining > 1) {
                System.out.println("You had " + guessesRemaining + " guesses left.");
            } else {
                System.out.println("You had 1 guess left.");
            }
            guessesRemaining = 0;
        }
        // Game continues if there are still guesses remaining
        else if (guessesRemaining > 0) {
            if (guessesRemaining > 1) {
                System.out.println("You have " + guessesRemaining + " guesses left.");
            } else {
                System.out.println("You have 1 guess left.");
            }
            System.out.print("Used letters:");
            for (char letter : lettersGuessed) {
                System.out.print(" " + letter);
            }
            System.out.println("\nWord: " + wordPattern);
        }
        // Player has lost if there are no guesses remaining
        else {
            System.out.println("\nYou lose!");
            System.out.println("The word was: " + word);
        }
    }

    public boolean promptPlayAgain() {
        while (true) {
            System.out.print("\nPlay again? (y/n): ");
            String input = scanner.next().trim().toLowerCase();
            if (input.equals("y")) {
                System.out.println("");
                return true;
            } else if (input.equals("n")) {
                System.exit(0);
            }
        }
    }

    public void loadDictionary(String filepath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String word = br.readLine();
            while (word != null) {
                wordList.add(word.toLowerCase());
                word = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}