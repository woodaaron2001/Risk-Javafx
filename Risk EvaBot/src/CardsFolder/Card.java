package CardsFolder;
//
public class Card implements Cards{
    private CardType cardType;
    private String territory;
    private int value;

    public static int INFANTRY = 1;
    public static int CAVALRY = 5;
    public static int ARTILLERY = 10;

    public Card(CardType cardType, String territory){
        this.cardType = cardType;
        this.territory = territory;
        switch (this.cardType){
            case INFANTRY:
                value = INFANTRY;
                break;
            case CAVALRY:
                value = CAVALRY;
                break;
            case ARTILLERY:
                value = ARTILLERY;
                break;
            case WILDCARD:
                value = 0;
                break;
        }
    }

    public CardsFolder.CardType getCardType() {
        return cardType;
    }

    public String getTerritory() {
        return territory;
    }

    public boolean equals(Object o){
        if(o instanceof Card)
            return (((Card) o).getCardType() == getCardType() ) &&
                    (((Card) o).getTerritory().equals(getTerritory()));
        return false;
    }

    @Override
    public String toString() {
        switch (cardType){
            case INFANTRY:
                return "Card type: Infantry | Territory: " + territory + "\n";
            case CAVALRY:
                return "Card type: Cavalry | Territory: " + territory + "\n";
            case ARTILLERY:
                return "Card type: Artillery | Territory: " + territory + "\n";
            default:
                return null;
        }
    }

    @Override
    public int compare(Object o){
        if(o instanceof Cards){
            //System.out.println("Test1");
            if(((Cards) o).getCardType().equals(CardType.WILDCARD))
                return 1;

            return Integer.compare(getValue(), ((Cards) o).getValue());
        }
        return -1;
    }

    @Override
    public int getValue() {
        return value;
    }
}
