package engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Arrays;

@Getter
@JsonPropertyOrder({"id"})
public class Quizz {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;
    @NotEmpty(groups = {QuizzValidations.QuizzInput.class})
    @JsonProperty("title")
    private String title;
    @NotEmpty(groups = {QuizzValidations.QuizzInput.class})
    @JsonProperty("text")
    private String text;
    @NotNull(groups = {QuizzValidations.QuizzInput.class})
    @Size(groups = {QuizzValidations.QuizzInput.class}, min = 2)
    @JsonProperty("options")
    private  String[] options;
    @Size(groups = {QuizzValidations.QuizzResponse.class}, min = 0)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int[] answer;

    public Quizz(){}

    public Quizz(int id, String title, String text, String[] options, int[] answer){
        this.id = id;
        this.title = title;
        this.text = text;
        this.answer = (answer == null)?(new int[]{}):answer;
        this.options = options;
    }

    @JsonCreator
    public Quizz( @JsonProperty("title") String title,
                  @JsonProperty("text") String text,
                  @JsonProperty("options") String[] options,
                  @JsonProperty("answer") int[] answer){
        this.id = 1;
        this.title = title;
        this.text = text;
        this.answer = (answer == null)?(new int[]{}):answer;
        this.options = options;
    }

    public int[] getAnswer(){
        return this.answer;
    }

    public boolean isCorrect(int[] answer){
        return Arrays.equals(this.answer, answer);
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }

}
