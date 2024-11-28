package praktikum.objects;



import lombok.Getter;
import lombok.Setter;


import java.util.List;

public class IngredientsResponse {

    @Getter
    @Setter
    public String success;

    @Getter
    @Setter
    public List<Ingredients> data;

    public IngredientsResponse() { }

    public IngredientsResponse(String success, List<Ingredients> data) {
        this.success = success;
        this.data = data;


    }
}
