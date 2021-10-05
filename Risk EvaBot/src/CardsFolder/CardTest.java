package CardsFolder;
////
import application.GameLogic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;


public class CardTest {
    @Test
    void GetValueEquals() throws Exception{
        Cards card1 = new Card(CardType.INFANTRY, "Ontario");
        Cards card2 = new Card(CardType.INFANTRY, "Quebec");
        Assertions.assertEquals(card1.getValue(), card2.getValue());
    }

    @Test
    void CompareCards() throws Exception{
        Cards card1 = new Card(CardType.INFANTRY, "Ontario");
        Cards card2 = new Card(CardType.INFANTRY, "Quebec");
        Cards card3 = new WildCard();
        Cards card4 = new Card(CardType.ARTILLERY, "Congo");

        Assertions.assertEquals(card1.compare(card2), 0);
        Assertions.assertEquals(card1.compare(card3), 1);
        Assertions.assertEquals(card1.compare(card4), -1);
    }

    @Test
    void CheckMatch(){
        Cards card1 = new Card(CardType.INFANTRY, "Ontario");
        Cards card2 = new Card(CardType.INFANTRY, "Quebec");
        Cards card3 = new WildCard();
        Cards card4 = new Card(CardType.ARTILLERY, "Congo");
        Cards card5 = new Card(CardType.CAVALRY, "N Africa");

        Cards[] allSimilarCards = {card1, card1, card1};
        Cards[] allDifferentCards = {card1, card5, card4};
        Cards[] wildCardPresent = {card1, card3, card2};

        Assertions.assertTrue(GameLogic.checkMatch(allSimilarCards));
        Assertions.assertTrue(GameLogic.checkMatch(allDifferentCards));
        Assertions.assertTrue(GameLogic.checkMatch(wildCardPresent));
    }

    @Test
    void CardSorting(){
        Cards card1 = new Card(CardType.INFANTRY, "Ontario");
        Cards card2 = new Card(CardType.INFANTRY, "Quebec");
        Cards card3 = new WildCard();
        Cards card4 = new Card(CardType.ARTILLERY, "Congo");
        Cards card5 = new Card(CardType.CAVALRY, "N Africa");

        Cards[] unsorted1 = {card5, card4, card1};
        Cards[] sorted1 = {card1, card5, card4};

        Cards[] unsorted2 = {card2, card3, card4};
        Cards[] sorted2 = {card3, card2, card4};

        GameLogic.cardSort(unsorted1);
        GameLogic.cardSort(unsorted2);

        for(Cards card: unsorted2){
            System.out.println(card);
        }
        Assertions.assertTrue(Arrays.deepEquals(unsorted1, sorted1));
        Assertions.assertTrue(Arrays.deepEquals(unsorted2, sorted2));
    }
}
