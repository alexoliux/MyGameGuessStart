package mirea.buryakov.myguessstar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String myUrl = "https://kinowar.com/50-%D1%81%D0%B0%D0%BC%D1%8B%D1%85-%D1%81%D0%B5%D0%BA%D1%81%D1%83%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D1%85-%D0%B0%D0%BA%D1%82%D1%80%D0%B8%D1%81-%D1%81%D0%BE%D0%B2%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%BD%D0%BE/";

    private ArrayList<String> urls;
    private ArrayList<String> names;
    private ArrayList<Button> buttons;

    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private ImageView imageViewStar;

    private int numberOfQuestion;
    private int numberOfRightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urls = new ArrayList<>();
        names = new ArrayList<>();
        buttons = new ArrayList<>();
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        imageViewStar = findViewById(R.id.imageViewStar);
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        playGame();
    }

    private void playGame() {
        ExecutorService executorService = Executors.newCachedThreadPool();
//        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String content = ActorsDownloadUtil.downloadContentActors(myUrl);
                String start = "<h3>1. ";
                String finish = "<p><em>Читайте также:</em></p>";
                Pattern pattern = Pattern.compile(start + "(.*?)" + finish);
                Matcher matcher = pattern.matcher(content);
                String splitContent = "";
                while (matcher.find()) {
                    splitContent = matcher.group(1);
                }
                Pattern patternName = Pattern.compile("alt=\"(.*?)\"");
                Pattern patternImage = Pattern.compile("src=\"(.*?)\"");
                Matcher matcherImg = patternImage.matcher(splitContent);
                Matcher matcherName = patternName.matcher(splitContent);
                while (matcherImg.find()) {
                    urls.add(matcherImg.group(1));
                }
                while (matcherName.find()) {
                    names.add(matcherName.group(1));
                }

                generateQuestion();
                Bitmap bitmap = ActorsDownloadUtil.downloadImageActors(urls.get(numberOfQuestion));
                final Bitmap finalBitmap = bitmap;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageViewStar.setImageBitmap(bitmap);
                        for (int i = 0; i < buttons.size(); i++) {
                            if (i == numberOfRightAnswer) {
                                buttons.get(i).setText(names.get(numberOfQuestion));
                            } else {
                                int wrongAnswer  = generateWrongAnswer();
                                buttons.get(i).setText(names.get(wrongAnswer));
                            }
                        }
                    }
                });
            }
        });
    }


    private void generateQuestion() {
        numberOfQuestion = (int) (Math.random() * names.size());
        numberOfRightAnswer = (int) (Math.random() * buttons.size());
    }

    private int generateWrongAnswer() {
        return (int) (Math.random() * names.size());
    }

    public void onClickAnswer(View view) {
        Button button = (Button) view;
        String tag = button.getTag().toString();
        if (Integer.parseInt(tag) == numberOfRightAnswer) {
            Toast.makeText(this, "Верно!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Неправильно! Правильный ответ: " + names.get(numberOfQuestion), Toast.LENGTH_SHORT).show();
        }
        playGame();
    }
}