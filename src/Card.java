public class Card {
    String value;
    String suit;

    public Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    @Override
    public String toString() {
        return value + "-" + suit;
    }

    public int getValue() {
        return switch (value) {
            case "A" -> 11;
            case "J", "Q", "K" -> 10;
            default -> Integer.parseInt(value);
        };
    }

    public boolean isAce() {
        return value.equals("A");
    }
}
