package CardsFolder;
/* This card interface is being used so that we can handle the difference
* Between Wildcards and regular territory cards
* Since wildcards don't have a particular unit type or a territory
* We don't need to worry about this issue if we use the card interface to understand
* All cards have a simple type.
* */
//
public interface Cards {
    CardType getCardType();
    int getValue();
    int compare(Object o);
    String toString();
    String getTerritory();
}
