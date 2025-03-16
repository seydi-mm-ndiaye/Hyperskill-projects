package engine;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class Controller {

    private ConcurrentHashMap<Integer, Quizz> quizzes = new ConcurrentHashMap<>();

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<Quizz> getQuizz(@PathVariable("id") int id) {
        Quizz quizz = quizzes.getOrDefault(id, null);
        if (quizz == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(quizz);
    }

    @GetMapping("/quizzes")
    public ResponseEntity<List<Quizz>> getQuizzes() {
        List<Quizz> results = new ArrayList<>();
        quizzes.forEach((k, v) -> results.add(v));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(results);
    }

    @PostMapping("/quizzes")
    public ResponseEntity<Quizz> postQuizzes(@Validated(QuizzValidations.QuizzInput.class) @RequestBody Quizz quizz) {
        quizz.setId(quizzes.size() + 1);
        quizzes.put(quizz.getId(), quizz);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(quizz);
    }

    @PostMapping("/quizzes/{id}/solve")
    public ResponseEntity<String> postQuizz(@PathVariable("id") int id, @Validated(QuizzValidations.QuizzResponse.class) @RequestBody Quizz response) {
        Quizz quizz = quizzes.getOrDefault(id, null);

        if(quizz == null)
            return ResponseEntity.notFound().build();

        if (quizz.isCorrect(response.getAnswer()))
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"success\":true, \"feedback\": \"Congratulations, you're right!\"}");
        else
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"success\":false, \"feedback\": \"Wrong answer! Please, try again.\"}");


    }

}
