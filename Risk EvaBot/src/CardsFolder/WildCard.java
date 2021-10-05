package CardsFolder;
//
public class WildCard implements Cards{
    private final int value;
    public WildCard(){
        value = 0;
    }
    @Override
    public CardType getCardType() {
        return CardType.WILDCARD;
    }

    @Override
    public String toString() {
        return "Card type: Wild Card \n";
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public int compare(Object o) {
        if(o instanceof WildCard)
            return 0;
        else if(o instanceof Card)
            return -1;
        return 1;
    }

    public boolean equals(Object o){
        return o instanceof WildCard;
    }
	@Override
	public String getTerritory() {
		
		return "Card";
	}
}
