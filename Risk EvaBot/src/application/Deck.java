package application;
//
import CardsFolder.Card;
import CardsFolder.CardType;
import CardsFolder.Cards;
import CardsFolder.WildCard;
import java.util.ArrayList;
import java.util.Random;

public class Deck {
    ArrayList<Cards> deck;
    Deck(){
        deck = new ArrayList<>(44);
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[0]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[1]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[2]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[3]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[4]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[5]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[6]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[7]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[8]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[9]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[10]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[11]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[12]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[13]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[14]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[15]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[16]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[17]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[18]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[19]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[20]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[21]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[22]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[23]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[24]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[25]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[26]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[27]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[28]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[29]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[30]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[31]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[32]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[33]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[34]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[35]));
        deck.add(new Card(CardType.CAVALRY, constants.COUNTRY_NAMES[36]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[37]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[38]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[39]));
        deck.add(new Card(CardType.ARTILLERY, constants.COUNTRY_NAMES[40]));
        deck.add(new Card(CardType.INFANTRY, constants.COUNTRY_NAMES[41]));
        deck.add(new WildCard());
        deck.add(new WildCard());
        shuffle();
    }

    public void shuffle(){
        int random;
        Random r = new Random();
        for(int i = 0; i < deck.size(); i++){
            random = r.nextInt(deck.size());
            swap(deck, i, random);
        }
    }

    private void swap(ArrayList<Cards> deck, int i, int j){
        Cards temp = deck.get(i);
        deck.set(i, deck.get(j));
        deck.set(j, temp);
    }

    public boolean isEmpty(){
        return this.deck.size() == 0;
    }

    public int size(){
        return deck.size();
    }

    public Cards getTop() {
        return deck.remove(deck.size() - 1);
    }

    @Override
    public String toString() {
        String str = "Current Deck:\n";
        for (Cards card : deck) {
            str += card.toString();
        }
        return str;
    }
}
