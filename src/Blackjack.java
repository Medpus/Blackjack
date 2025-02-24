import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Blackjack {
    private ArrayList<Card> deck;
    private final Random random = new Random();

    // Dealer
    private ArrayList<Card> dealerHand;
    private int dealerSum;
    private int dealerAceCount;

    // Player
    private ArrayList<Card> playerHand;
    private int playerSum;
    private int playerAceCount;

    // Window Properties
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int CARD_WIDTH = 110;
    private static final int CARD_HEIGHT = 154;

    // UI Components
    private final JFrame frame = new JFrame("Blackjack");
    private final JPanel gamePanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawDealerHand(g);
            drawPlayerHand(g);
            checkGameState(g);
        }
    };
    private final JPanel buttonPanel = new JPanel();
    private final JButton hitButton = new JButton("Hit");
    private final JButton stayButton = new JButton("Stay");

    public Blackjack() {
        startGame();
        setupUI();
    }

    private void setupUI() {
        frame.setVisible(true);
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        stayButton.setFocusable(false);
        buttonPanel.add(hitButton);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        setupButtonActions();
    }

    private void setupButtonActions() {
        hitButton.addActionListener(e -> playerDrawCard());

        stayButton.addActionListener(e -> {
            hitButton.setEnabled(false);
            stayButton.setEnabled(false);
            dealerDrawCards();
            gamePanel.repaint();
        });
    }

    private void startGame() {
        buildDeck();
        shuffleDeck();

        // Dealer starts with **one** card
        dealerHand = new ArrayList<>();
        dealerSum = 0;
        dealerAceCount = 0;
        drawCardForDealer();

        // Player starts with two cards
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;
        drawCardForPlayer();
        drawCardForPlayer();
    }

    private void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] suits = {"C", "D", "H", "S"};

        for (String suit : suits) {
            for (String value : values) {
                deck.add(new Card(value, suit));
            }
        }
    }

    private void shuffleDeck() {
        for (int i = deck.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }
    }

    private void playerDrawCard() {
        if (deck != null && !deck.isEmpty()) {
            drawCardForPlayer();
            if (reducePlayerAce() > 21) {
                hitButton.setEnabled(false);
            }
            gamePanel.repaint();
        }
    }

    private void dealerDrawCards() {
        while (dealerSum < 17 && deck != null && !deck.isEmpty()) {
            drawCardForDealer();
        }
        reduceDealerAce();
    }

    private void drawCardForPlayer() {
        if (!deck.isEmpty()) {
            Card card = deck.removeLast();
            playerSum += card.getValue();
            if (card.isAce()) {
                playerAceCount++;
            }
            playerHand.add(card);
        }
    }

    private void drawCardForDealer() {
        if (!deck.isEmpty()) {
            Card card = deck.removeLast();
            dealerSum += card.getValue();
            if (card.isAce()) {
                dealerAceCount++;
            }
            dealerHand.add(card);
        }
    }

    private int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }

    private int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }

    private void drawDealerHand(Graphics g) {
        for (int i = 0; i < dealerHand.size(); i++) {
            Image cardImg;
            if (i == 0 || !stayButton.isEnabled()) {
                cardImg = getCardImage(dealerHand.get(i).toString());
            } else {
                cardImg = getCardImage("BACK");
            }
            g.drawImage(cardImg, 20 + (CARD_WIDTH + 5) * i, 20, CARD_WIDTH, CARD_HEIGHT, null);
        }
    }

    private void drawPlayerHand(Graphics g) {
        for (int i = 0; i < playerHand.size(); i++) {
            Image cardImg = getCardImage(playerHand.get(i).toString());
            g.drawImage(cardImg, 20 + (CARD_WIDTH + 5) * i, 320, CARD_WIDTH, CARD_HEIGHT, null);
        }
    }

    private void checkGameState(Graphics g) {
        if (!stayButton.isEnabled() || playerSum > 21) {
            stayButton.setEnabled(false);
            dealerSum = reduceDealerAce();
            playerSum = reducePlayerAce();

            String message = determineGameOutcome();
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString(message, 220, 250);
        }
    }

    private String determineGameOutcome() {
        if (playerSum > 21) {
            return "You Lose!";
        } else if (dealerSum > 21) {
            return "You Win!";
        } else if (playerSum == dealerSum) {
            return "Tie!";
        } else if (playerSum > dealerSum) {
            return "You Win!";
        } else {
            return "You Lose!";
        }
    }

    private Image getCardImage(String cardName) {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource("./cards/" + cardName + ".png"))).getImage();
    }
}
