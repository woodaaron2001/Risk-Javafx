package application;
//
public class NormalPlayer implements Player {
    String name;
    int units;
    int ownership;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setUnit(int unit) {
        units = unit;
    }

    @Override
    public int getUnit() {
        return units;
    }

    @Override
    public int getOwnership() {
        return ownership;
    }
}
