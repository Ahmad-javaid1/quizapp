package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView questionText, timerText;
    private RadioGroup answerGroup;
    private RadioButton answer1, answer2, answer3, answer4;
    private Button nextButton, prevButton, submitButton, viewAnswerButton;

    private String[] questions = {
            "Question 1: Why did the scarecrow win an award?",
            "Question 2: What do you call fake spaghetti?",
            "Question 3: Why don't skeletons fight each other?",
            "Question 4: What do you get when you cross a snowman with a vampire?",
            "Question 5: Why did the bicycle fall over?",
            "Question 6: How does a penguin build its house?",
            "Question 7: Why don't some fish play piano?",
            "Question 8: What's orange and sounds like a parrot?",
            "Question 9: Why did the golfer bring two pairs of pants?",
            "Question 10: What do you call an alligator in a vest?",
            "Question 11: Why was the math book sad?",
            "Question 12: How do you organize a space party?",
            "Question 13: Why was the computer cold?",
            "Question 14: What does a cloud wear under his raincoat?",
            "Question 15: Why did the tomato turn red?",
            "Question 16: Why did the chicken join a band?",
            "Question 17: Why can't you give Elsa a balloon?",
            "Question 18: What do you call cheese that isn't yours?",
            "Question 19: Why did the photo go to jail?",
            "Question 20: Why do cows have hooves instead of feet?"
    };

    private String[][] answers = {
            {"Because it was outstanding in its field", "Because it was scary", "Because it was colorful", "Because it could dance"},
            {"An impasta", "A noodle", "A spaghetti", "A meatball"},
            {"Because they don't have the guts", "Because they are too bony", "Because they love peace", "Because they like hugs"},
            {"Frostbite", "A melted snowman", "A cool vampire", "An ice cube"},
            {"Because it was two-tired", "Because it was sad", "Because it was rusty", "Because it was flat"},
            {"Igloos it together", "With ice bricks", "By hammering ice", "By rolling snow"},
            {"Because they are fishy", "Because they have no hands", "Because they swim", "Because they can't read music"},
            {"A carrot", "A kumquat", "An orange parrot", "A pumpkin"},
            {"In case he got a hole in one", "To look fashionable", "To be warm", "To share with a friend"},
            {"An investigator", "A detective", "A croc in disguise", "A gatorade"},
            {"Because it had too many problems", "Because it was torn", "Because it was too smart", "Because it needed help"},
            {"You planet", "You rocket", "You blast it", "You moon it"},
            {"Because it left its Windows open", "Because it was too fast", "Because it was hacked", "Because it was shut down"},
            {"Thunderwear", "Snow pants", "A raincoat", "Cloud pants"},
            {"Because it saw the salad dressing", "Because it was embarrassed", "Because it was hot", "Because it was ripe"},
            {"Because it had the drumsticks", "Because it loved to dance", "Because it was loud", "Because it was a rooster"},
            {"Because she will let it go", "Because she hates balloons", "Because it's too cold", "Because she loves ice"},
            {"Nacho cheese", "Mozzarella", "Swiss cheese", "Brie cheese"},
            {"Because it was framed", "Because it was too dark", "Because it was a crime", "Because it was blurry"},
            {"Because they lactose", "Because they hoof better", "Because they can't wear shoes", "Because they are cows"}
    };

    private int[] correctAnswers = {0, 2, 0, 0, 0, 3, 1, 0, 0, 0, 0, 0, 1, 0, 3, 0, 0, 0, 0, 0};

    private int currentQuestionIndex = 0;
    private int score = 0;
    private int[] userAnswers = new int[questions.length];
    private boolean[] isCorrect = new boolean[questions.length]; // answer correct or not

    private int viewAnswerPenalty = -1;
    private boolean[] answerViewed = new boolean[questions.length];


    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionText = findViewById(R.id.question_text);
        timerText = findViewById(R.id.timer_text);
        answerGroup = findViewById(R.id.answer_group);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        submitButton = findViewById(R.id.submit_button);
        viewAnswerButton = findViewById(R.id.view_answer_button);


        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = -1;
            isCorrect[i] = false;
        }


        loadQuestion();

        // 2 minutes
        startTimer(120000);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(MainActivity.this, "Please select an answer before proceeding.", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveUserAnswer();
                if (currentQuestionIndex < questions.length - 1) {
                    currentQuestionIndex++;
                    loadQuestion();
                }
                updateButtons();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserAnswer();

                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--;
                    loadQuestion();
                }
                updateButtons();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserAnswer();
                calculateScore();
                timer.cancel();


                questionText.setVisibility(View.GONE);
                answerGroup.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                prevButton.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);


                int maxScore = questions.length * 5;
                double percentage = ((double) score / maxScore) * 100;


                TextView scoreText = findViewById(R.id.score_text);
                scoreText.setText("Final Score: " + score + "/" + maxScore + " (" + String.format("%.2f", percentage) + "%)");
                scoreText.setVisibility(View.VISIBLE);


                LinearLayout correctAnswersList = findViewById(R.id.correct_answers_list);
                correctAnswersList.removeAllViews();
                for (int i = 0; i < questions.length; i++) {
                    TextView answerView = new TextView(MainActivity.this);
                    char selectedAnswerChar = userAnswers[i] == -1 ? '-' : (char) ('A' + userAnswers[i]);
                    char correctAnswerChar = (char) ('A' + correctAnswers[i]);
                    String icon = isCorrect[i] ? " ✔️" : " ❌ Correct answer: " + correctAnswerChar;
                    answerView.setText("Question " + (i + 1) + ": " + selectedAnswerChar + icon);
                    correctAnswersList.addView(answerView);
                }
                correctAnswersList.setVisibility(View.VISIBLE);


                Button retryButton = findViewById(R.id.retry_button);
                retryButton.setVisibility(View.VISIBLE);
                retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetQuiz();
                    }
                });
            }
        });


        for (int i = 0; i < answerViewed.length; i++) {
            answerViewed[i] = false;
        }

        viewAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!answerViewed[currentQuestionIndex]) {
                    score += viewAnswerPenalty;
                    answerViewed[currentQuestionIndex] = true;
                    Toast.makeText(MainActivity.this, "Answer viewed. Score deducted.", Toast.LENGTH_SHORT).show();
                }


                int correctAnswerIndex = correctAnswers[currentQuestionIndex];
                switch (correctAnswerIndex) {
                    case 0:
                        answer1.setChecked(true);
                        break;
                    case 1:
                        answer2.setChecked(true);
                        break;
                    case 2:
                        answer3.setChecked(true);
                        break;
                    case 3:
                        answer4.setChecked(true);
                        break;
                }
            }
        });

    }

    private void saveUserAnswer() {
        int selectedId = answerGroup.getCheckedRadioButtonId();
        int selectedAnswerIndex = -1;

        if (selectedId == R.id.answer1) {
            selectedAnswerIndex = 0;
        } else if (selectedId == R.id.answer2) {
            selectedAnswerIndex = 1;
        } else if (selectedId == R.id.answer3) {
            selectedAnswerIndex = 2;
        } else if (selectedId == R.id.answer4) {
            selectedAnswerIndex = 3;
        }

        userAnswers[currentQuestionIndex] = selectedAnswerIndex;
    }

    private void loadQuestion() {
        questionText.setText(questions[currentQuestionIndex]);
        answer1.setText(answers[currentQuestionIndex][0]);
        answer2.setText(answers[currentQuestionIndex][1]);
        answer3.setText(answers[currentQuestionIndex][2]);
        answer4.setText(answers[currentQuestionIndex][3]);


        answerGroup.clearCheck();
        if (userAnswers[currentQuestionIndex] != -1) {
            switch (userAnswers[currentQuestionIndex]) {
                case 0:
                    answer1.setChecked(true);
                    break;
                case 1:
                    answer2.setChecked(true);
                    break;
                case 2:
                    answer3.setChecked(true);
                    break;
                case 3:
                    answer4.setChecked(true);
                    break;
            }
        }


        viewAnswerButton.setVisibility(answerViewed[currentQuestionIndex] ? View.GONE : View.VISIBLE);
    }


    private void updateButtons() {
        if (currentQuestionIndex == 0) {
            prevButton.setVisibility(View.GONE);
        } else {
            prevButton.setVisibility(View.VISIBLE);
        }

        if (currentQuestionIndex == questions.length - 1) {
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
    }

    private void resetQuiz() {
        currentQuestionIndex = 0;
        score = 0;
        userAnswers = new int[questions.length];
        isCorrect = new boolean[questions.length];
        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = -1;
            isCorrect[i] = false;
        }
        answerViewed = new boolean[questions.length];
        loadQuestion();
        startTimer(120000);


        questionText.setVisibility(View.VISIBLE);
        answerGroup.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        viewAnswerButton.setVisibility(View.VISIBLE);
        prevButton.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);


        findViewById(R.id.score_text).setVisibility(View.GONE);
        findViewById(R.id.correct_answers_list).setVisibility(View.GONE);
        findViewById(R.id.retry_button).setVisibility(View.GONE);
    }

    private void calculateScore() {
        score = 0;
        for (int i = 0; i < userAnswers.length; i++) {
            if (userAnswers[i] == correctAnswers[i]) {
                if (answerViewed[i]){
                    score += 4;
                    isCorrect[i] = true;
                }else {
                    score += 5;
                    isCorrect[i] = true;
                }

            } else {
                score -= 1;
                isCorrect[i] = false;
            }
        }
    }



    private void startTimer(long timeInMillis) {
        timer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerText.setText(String.format("Time left: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {

                Toast.makeText(MainActivity.this, "Time's up! Submitting the quiz...", Toast.LENGTH_SHORT).show();


                saveUserAnswer();


                calculateScore();


                questionText.setVisibility(View.GONE);
                answerGroup.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                prevButton.setVisibility(View.GONE);
                submitButton.setVisibility(View.GONE);


                int maxScore = questions.length * 5;
                double percentage = ((double) score / maxScore) * 100;

                TextView scoreText = findViewById(R.id.score_text);
                scoreText.setText("Final Score: " + score + "/" + maxScore + " (" + String.format("%.2f", percentage) + "%)");
                scoreText.setVisibility(View.VISIBLE);


                LinearLayout correctAnswersList = findViewById(R.id.correct_answers_list);
                correctAnswersList.removeAllViews();
                for (int i = 0; i < questions.length; i++) {
                    TextView answerView = new TextView(MainActivity.this);
                    char selectedAnswerChar = userAnswers[i] == -1 ? '-' : (char) ('A' + userAnswers[i]);
                    char correctAnswerChar = (char) ('A' + correctAnswers[i]);
                    String icon = isCorrect[i] ? " ✔️" : " ❌ Correct answer: " + correctAnswerChar;
                    answerView.setText("Question " + (i + 1) + ": " + selectedAnswerChar + icon);
                    correctAnswersList.addView(answerView);
                }
                correctAnswersList.setVisibility(View.VISIBLE);


                Button retryButton = findViewById(R.id.retry_button);
                retryButton.setVisibility(View.VISIBLE);
                retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetQuiz();
                    }
                });
            }
        }.start();
    }
}
