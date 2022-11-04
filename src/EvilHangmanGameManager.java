import java.io.*;
import java.util.*;

public class EvilHangmanGameManager {

    private List<String> wordList;
    private List<Character> lettersGuessed;
    private int wordLength;
    private int guessesRemaining;

    public EvilHangmanGameManager(int numGuesses) {
        if (numGuesses < 1) {
            throw new IllegalArgumentException("Please enter a positive number of guesses!");
        }

        wordList = new ArrayList<String>();
        loadDictionary("dictionary.txt");

        // Generate random word length, weighted by frequency in dictionary
        int wordIndex = (int) (Math.random() * wordList.size());
        wordLength = wordList.get(wordIndex).length();

        lettersGuessed = new ArrayList<Character>();
        guessesRemaining = numGuesses;
    }

    public EvilHangmanGameManager(int numGuesses, int wordLength) {
        this(numGuesses);

        int numWordsWithLength = 0;
        for (String word : wordList) {
            if (word.length() == wordLength) {
                numWordsWithLength++;
            }
        }
        if (numWordsWithLength == 0) {
            throw new IllegalArgumentException("No words with specified length in the dictionary!");
        }
        this.wordLength = wordLength;
    }

    public void startGame() {
        // Remove words that are not the right length
        List<String> newWordList = new ArrayList<String>();
        for (String word : wordList) {
            if (word.length() == wordLength) {
                newWordList.add(word);
            }
        }
        wordList = newWordList;
    }

    /**
     * Records the guessed letter and updates the word list accordingly if the game is ongoing.
     *
     * @param guess letter to guess
     * @return occurrences of the guessed letter in the word, or -1 if game has finished
     */
    public int guess(char guess) {
        if (guess < 'a' || guess > 'z') {
            throw new IllegalArgumentException("Please enter a letter in the alphabet!");
        }
        if (lettersGuessed.contains(guess)) {
            throw new IllegalArgumentException("Letter has already been guessed!");
        }
        if (!isFinished()) {
            updateWordList(guess);
            int guessCount = getCharCount(guess, getWordPattern());
            if (guessCount == 0) {
                guessesRemaining--;
            }
            return guessCount;
        } else {
            return -1;
        }
    }

    public String getWordPattern() {
        return getPattern(wordList.get(0), lettersGuessed);
    }

    public List<String> getWordList() {
        return wordList;
    }

    public List<Character> getLettersGuessed() {
        return lettersGuessed;
    }

    public int getGuessesRemaining() {
        return guessesRemaining;
    }

    public int getWordLength() {
        return wordLength;
    }

    public boolean checkWin() {
        return wordList.size() == 1 && !getWordPattern().contains("_");
    }

    public boolean isFinished() {
        return checkWin() || guessesRemaining == 0;
    }

    private void updateWordList(char guess) {
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

    private List<String> chooseWordFamily(Collection<List<String>> wordFamilies, char guess) {
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

    private String getPattern(String word, List<Character> letters) {
        StringBuilder wordPattern = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            wordPattern.append(letters.contains(c) ? c : '_');
        }
        return wordPattern.toString();
    }

    private int getCharCount(char c, String str) {
        int charCount = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                charCount++;
            }
        }
        return charCount;
    }

    private void loadDictionary(String filepath) {
        InputStream is = getClass().getResourceAsStream(filepath);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String word = br.readLine();
            while (word != null) {
                wordList.add(word.toLowerCase());
                word = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
