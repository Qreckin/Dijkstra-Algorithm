import java.util.ArrayList;

public class City {
    public String cityName;
    public int x;
    public int y;

    public ArrayList<String> connections;


    public City(String cityName, int x, int y){
        this.cityName = cityName;
        this.x = x;
        this.y = y;
        this.connections = new ArrayList<>();
    }
}
