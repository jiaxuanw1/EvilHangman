import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class EvilHangmanGUI {

    public static void main(String[] args) {
        new EvilHangmanGUI();
    }

    public EvilHangmanGUI() {
        JFrame frame = new JFrame("Evil Hangman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension frameSize = new Dimension(260, 250);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int leftBound = (screenSize.width - frameSize.width) / 2;
        int upperBound = (screenSize.height - frameSize.height) / 2;
        frame.setBounds(leftBound, upperBound, frameSize.width, frameSize.height);
        frame.setResizable(false);

        JPanel contentPane = new JPanel();
        frame.setContentPane(contentPane);
        contentPane.setLayout(null);

        // Set system theme
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting system theme!");
        }

        // Title label
        JLabel titleLabel = new JLabel("EVIL HANGMAN");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 28));
        titleLabel.setBounds(15, 15, 420, 25);
        contentPane.add(titleLabel);

        // Number of Guesses label
        JLabel numGuessesLabel = new JLabel("Number of guesses: ");
        numGuessesLabel.setFont(new Font("Consolas", Font.PLAIN, 15));
        numGuessesLabel.setBounds(15, 60, 155, 25);
        contentPane.add(numGuessesLabel);

        // Number of Guesses spinner
        JSpinner numGuessesSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 26, 1));
        numGuessesSpinner.setBounds(170, 60, 50, 25);
        numGuessesLabel.setLabelFor(numGuessesSpinner);
        contentPane.add(numGuessesSpinner);

        // Word Length label
        JLabel wordLengthLabel = new JLabel("Word length: ");
        wordLengthLabel.setFont(new Font("Consolas", Font.PLAIN, 15));
        wordLengthLabel.setBounds(15, 90, 155, 25);
        contentPane.add(wordLengthLabel);

        // Word Length spinner
        JSpinner wordLengthSpinner = new JSpinner(new SpinnerNumberModel(8, 2, 30, 1));
        wordLengthSpinner.setBounds(170, 90, 50, 25);
        wordLengthLabel.setLabelFor(wordLengthSpinner);
        contentPane.add(wordLengthSpinner);

        // Random Length checkbox
        JCheckBox randomLengthCheckBox = new JCheckBox("Random length?");
        randomLengthCheckBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
        randomLengthCheckBox.setBounds(100, 120, 200, 25);
        randomLengthCheckBox.addActionListener(e -> {
            wordLengthSpinner.setEnabled(!randomLengthCheckBox.isSelected());
        });
        contentPane.add(randomLengthCheckBox);

        // Start Game button
        JButton startGameButton = new JButton("Start Game!");
        startGameButton.setFont(new Font("Monospaced", Font.BOLD, 15));
        startGameButton.setBounds(15, 160, 130, 30);
        startGameButton.addActionListener(e -> {
            try {
                numGuessesSpinner.commitEdit();
            } catch (ParseException ex) {
                numGuessesSpinner.setValue(0);
            }

            try {
                wordLengthSpinner.commitEdit();
            } catch (ParseException ex) {
                numGuessesSpinner.setValue(0);
            }

            try {
                EvilHangmanGameManager game;
                int numGuesses = (Integer) numGuessesSpinner.getValue();
                if (randomLengthCheckBox.isSelected()) {
                    game = new EvilHangmanGameManager(numGuesses);
                } else {
                    int wordLength = (Integer) wordLengthSpinner.getValue();
                    game = new EvilHangmanGameManager(numGuesses, wordLength);
                }
                new EvilHangmanGUIGame(game);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
        contentPane.add(startGameButton);

        frame.setVisible(true);
    }

    private class EvilHangmanGUIGame {

        private final EvilHangmanGameManager game;

        private JFrame frame;

        private JLabel messageLabel;
        private JLabel guessesLeftLabel;
        private JLabel wordLabel;
        private JTextField guessInput;
        private JButton guessButton;
        private JLabel usedLettersTitleLabel;
        private JLabel guessedLettersLabel;

        public EvilHangmanGUIGame(EvilHangmanGameManager game) {
            this.game = game;

            frame = new JFrame("Evil Hangman Game");
            Dimension frameSize = new Dimension(575, 375);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int leftBound = (screenSize.width - frameSize.width) / 2;
            int upperBound = (screenSize.height - frameSize.height) / 2;
            frame.setBounds(leftBound, upperBound, (int) frameSize.getWidth(), (int) frameSize.getHeight());
            frame.setResizable(false);

            JPanel contentPane = new JPanel();
            frame.setContentPane(contentPane);
            contentPane.setLayout(null);

            game.startGame();

            // Label displaying message at top
            messageLabel = new JLabel("The word is " + game.getWordLength() + " letters long.");
            messageLabel.setFont(new Font("Consolas", Font.PLAIN, 25));
            messageLabel.setBounds(
                    centeredLeftBound(messageLabel),
                    35,
                    2000, 30);
            contentPane.add(messageLabel);

            // Label displaying number of guesses left
            guessesLeftLabel = new JLabel("You have " + game.getGuessesRemaining() + " guess" +
                    (game.getGuessesRemaining() == 1 ? "." : "es."));
            guessesLeftLabel.setFont(new Font("Consolas", Font.PLAIN, 20));
            guessesLeftLabel.setBounds(
                    centeredLeftBound(guessesLeftLabel),
                    messageLabel.getY() + messageLabel.getHeight() + 5,
                    2000, 25);
            contentPane.add(guessesLeftLabel);

            // Label displaying word
            wordLabel = new JLabel(game.getWordPattern());
            Font originalFont = new Font("Monospaced", Font.BOLD, 30);
            Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
            attributes.put(TextAttribute.TRACKING, 0.15);
            wordLabel.setFont(originalFont.deriveFont(attributes));
            while (textWidth(wordLabel) > frame.getWidth() - 45) {
                Font currentFont = wordLabel.getFont();
                wordLabel.setFont(currentFont.deriveFont(currentFont.getSize() - 1.0f));
            }
            wordLabel.setBounds(
                    centeredLeftBound(wordLabel),
                    guessesLeftLabel.getY() + guessesLeftLabel.getHeight() + 20,
                    frame.getWidth(), 40);
            contentPane.add(wordLabel);

            usedLettersTitleLabel = new JLabel("Used letters:");
            usedLettersTitleLabel.setFont(new Font("Consolas", Font.PLAIN, 20));
            usedLettersTitleLabel.setBounds(
                    centeredLeftBound(usedLettersTitleLabel),
                    wordLabel.getY() + wordLabel.getHeight() + 40,
                    150, 25);
            contentPane.add(usedLettersTitleLabel);

            // Label displaying guessed letters
            guessedLettersLabel = new JLabel("");
            guessedLettersLabel.setFont(new Font("Consolas", Font.PLAIN, 18));
            guessedLettersLabel.setBounds(
                    centeredLeftBound(guessedLettersLabel),
                    usedLettersTitleLabel.getY() + usedLettersTitleLabel.getHeight() + 10,
                    550, 25);
            contentPane.add(guessedLettersLabel);

            // Guess input field
            guessInput = new JTextField();
            guessInput.setFont(new Font("Monospaced", Font.PLAIN, 15));
            guessInput.setSize(20, 30);

            // Enter Guess button
            guessButton = new JButton("Enter Guess!");
            guessButton.setFont(new Font("Consolas", Font.PLAIN, 15));
            guessButton.setSize(130, guessInput.getHeight());

            guessInput.setBounds(
                    (frame.getWidth() - guessInput.getWidth() - guessButton.getWidth()) / 2,
                    guessedLettersLabel.getY() + guessedLettersLabel.getHeight() + 20,
                    20, 30
            );
            guessInput.addActionListener(e -> guessButton.doClick());
            contentPane.add(guessInput);

            guessButton.setBounds(
                    guessInput.getX() + guessInput.getWidth() + 10,
                    guessInput.getY(),
                    130, guessInput.getHeight()
            );
            guessButton.addActionListener(e -> {
                String input = guessInput.getText().trim().toLowerCase();
                if (input.length() != 1) {
                    updateLabelText(messageLabel, "Please enter exactly one letter!");
                    guessInput.setText("");
                } else {
                    char guess = input.charAt(0);
                    try {
                        int guessCount = game.guess(guess);
                        updateDisplayStatus(guess, guessCount);
                    } catch (IllegalArgumentException ex) {
                        updateLabelText(messageLabel, ex.getMessage());
                        guessInput.setText("");
                    }
                }
            });
            contentPane.add(guessButton);

            frame.setVisible(true);
        }

        private void updateDisplayStatus(char guess, int guessCount) {
            // Update guessed letters display
            StringBuilder guessedLetters = new StringBuilder();
            for (char letter : game.getLettersGuessed()) {
                guessedLetters.append(letter).append(" ");
            }
            guessedLetters.deleteCharAt(guessedLetters.length() - 1);
            updateLabelText(guessedLettersLabel, guessedLetters.toString());

            // Clear input field text
            guessInput.setText("");

            if (game.isFinished()) {
                // Disable input field and button
                guessInput.setEnabled(false);
                guessButton.setEnabled(false);

                // Reveal word
                updateLabelText(wordLabel, game.getWordList().get((int) (Math.random() * game.getWordList().size())));
                messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));

                // Player wins
                if (game.checkWin()) {
                    messageLabel.setForeground(new Color(0, 128, 0)); // green
                    updateLabelText(messageLabel, "You win!");
                    updateLabelText(guessesLeftLabel, "You had " + game.getGuessesRemaining() + " guess" +
                            (game.getGuessesRemaining() == 1 ? "" : "es") + " left.");
                }
                // Player loses
                else {
                    messageLabel.setForeground(new Color(175, 0, 0)); // red
                    updateLabelText(messageLabel, "You lose!");
                    updateLabelText(guessesLeftLabel, "The word was:");
                }
            } else {
                if (guessCount > 1) {
                    updateLabelText(messageLabel, "Yes, there are " + guessCount + " copies of " + guess + ".");
                } else if (guessCount == 1) {
                    updateLabelText(messageLabel, "Yes, there is 1 copy of " + guess + ".");
                } else {
                    updateLabelText(messageLabel, "Sorry, there are no " + guess + "'s.");
                }
                updateLabelText(wordLabel, game.getWordPattern());
                updateLabelText(guessesLeftLabel, "You have " + game.getGuessesRemaining() + " guess" +
                        (game.getGuessesRemaining() == 1 ? "" : "es") + " left.");
            }
        }

        /**
         * Returns the width, in pixels, of the label's text according to the label's font.
         */
        private int textWidth(JLabel label) {
            return label.getFontMetrics(label.getFont()).stringWidth(label.getText());
        }

        /**
         * Returns the x-value of the left boundary needed to center the specified label in the frame.
         */
        private int centeredLeftBound(JLabel label) {
            return (frame.getWidth() - textWidth(label)) / 2;
        }

        /**
         * Sets the label's text to the specified String and re-centers the label in the frame.
         */
        private void updateLabelText(JLabel label, String text) {
            label.setText(text);
            label.setLocation(centeredLeftBound(label), label.getY());
        }

    }

}
