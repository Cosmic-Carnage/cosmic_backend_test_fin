package com.nighthawk.spring_portfolio.mvc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.nighthawk.spring_portfolio.mvc.jokes.Jokes;
import com.nighthawk.spring_portfolio.mvc.jokes.JokesJpaRepository;
import com.nighthawk.spring_portfolio.mvc.leaderboard.Leaderboard;
import com.nighthawk.spring_portfolio.mvc.leaderboard.LeaderboardJpaRepository;
import com.nighthawk.spring_portfolio.mvc.note.Note;
import com.nighthawk.spring_portfolio.mvc.note.NoteJpaRepository;
import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;
import com.nighthawk.spring_portfolio.mvc.quiz.Quiz;
import com.nighthawk.spring_portfolio.mvc.quiz.QuizJpaRepository;
import com.nighthawk.spring_portfolio.mvc.spacebook.SpacebookJpaRepository;
import com.nighthawk.spring_portfolio.mvc.usr.Usr;
import com.nighthawk.spring_portfolio.mvc.usr.UsrDetailsService;

import java.util.HashMap;
import java.util.List;

@Component
@Configuration // Scans Application for ModelInit Bean, this detects CommandLineRunner
public class ModelInit {  
    @Autowired JokesJpaRepository jokesRepo;
    @Autowired NoteJpaRepository noteRepo;
    @Autowired PersonDetailsService personService;
    @Autowired UsrDetailsService usrService;
    @Autowired
    LeaderboardJpaRepository leaderboardRepo;
    @Autowired
    SpacebookJpaRepository spacebookRepo;
    @Autowired
    QuizJpaRepository quizRepo;

    @Bean
    CommandLineRunner run() {  // The run() method will be executed after the application starts
        return args -> {

            // Joke database is populated with starting jokes
            String[] jokesArray = Jokes.init();
            for (String joke : jokesArray) {
                List<Jokes> jokeFound = jokesRepo.findByJokeIgnoreCase(joke);  // JPA lookup
                if (jokeFound.size() == 0)
                    jokesRepo.save(new Jokes(null, joke, 0, 0)); //JPA save
            }

            // Person database is populated with test data
            Person[] personArray = Person.init();
            for (Person person : personArray) {
                //findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase
                List<Person> personFound = personService.list(person.getName(), person.getEmail());  // lookup
                if (personFound.size() == 0) {
                    personService.save(person);  // save

                    // Each "test person" starts with a "test note"
                    String text = "Test " + person.getEmail();
                    Note n = new Note(text, person);  // constructor uses new person as Many-to-One association
                    noteRepo.save(n);  // JPA Save                  
                }
            }

            // Usr database test data
            Usr[] usrArray = Usr.init();
            for (Usr usr : usrArray) {
                //findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase
                List<Usr> usrFound = usrService.list(usr.getName(), usr.getEmail());  // lookup
                if (usrFound.size() == 0) {
                    usrService.save(usr);  // save               
                }
            }


            HashMap<String, Integer> leaderboardHash = Leaderboard.init();
            for (String leaderboard : leaderboardHash.keySet()) {
                List<Leaderboard> leaderboardFound = leaderboardRepo.findByLeaderboardIgnoreCase(leaderboard);
                if (leaderboardFound.size() == 0)
                    leaderboardRepo.save(new Leaderboard(null, leaderboard, leaderboardHash.get(leaderboard)));
            }

            List<String> questionList = Quiz.init();
            for (String question : questionList) {
                List<Quiz> quizFound = quizRepo.findByQuestionIgnoreCase(question);
                if (quizFound.size() == 0) {
                    Quiz newQuiz = new Quiz();
                    newQuiz.setQuestion(question);
                    quizRepo.save(newQuiz);
                }
            }
        };
    }
}

