package consegna_4;

public class RoomInfo {
    private final int temp;
    private final int value;
    public RoomInfo(int temp, int value) {
        this.temp = temp;
        this.value = value;
    }
    public int getTemp() {
        return temp;
    }
    public int getValue() {
        return value;
    }
    @Override
    public String toString() {
        return "Temperature: " + temp + " Value: " + value;
    }
}
