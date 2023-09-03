package com.example.mathapp;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    int dialogText = R.string.leave_uncomplete;
    
    boolean isComplete = false;
    int difficulty;
    
    int numTries = 0;
    
    ArrayList<String> usedEquations = new ArrayList<>();
    
    TextView answer;
    TextView info;
    TextView toast;
    TextView progress;
    TextView equation;
    
    PreferenceManager preferenceManager;
    
    String [] equations;
    
    int [] buttonIds = { R.id.button_main_0, R.id.button_main_1, R.id.button_main_2, R.id.button_main_3, R.id.button_main_4, R.id.button_main_5, R.id.button_main_6, R.id.button_main_7, R.id.button_main_8, R.id.button_main_9 };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    
        preferenceManager = new PreferenceManager(this);
    
        initWelcomeButtons();
    }
    
    public void initWelcomeButtons() {
        Button button_welcome_start = findViewById(R.id.button_welcome_start);
        Button button_welcome_about = findViewById(R.id.button_welcome_about);
        Button button_welcome_settings = findViewById(R.id.button_welcome_settings);
    
        button_welcome_start.setOnClickListener(view -> renderGame());
        button_welcome_about.setOnClickListener(view -> renderAbout());
        button_welcome_settings.setOnClickListener(view -> renderSettings());
    }
    
    public void renderGame() {
        setContentView(R.layout.activity_main);
    
        this.answer = findViewById(R.id.txt_answer);
        this.toast = findViewById(R.id.txt_toast);
        this.progress = findViewById(R.id.txt_progress);
        this.equation = findViewById(R.id.txt_equation);
        this.info = findViewById(R.id.txt_info);
        
        Button button_check = findViewById(R.id.button_check);
        button_check.setOnClickListener(view -> check());
    
        Button button_undo = findViewById(R.id.button_undo);
        button_undo.setOnClickListener(view -> undo());
    
        Button button_leave = findViewById(R.id.button_leave);
    
        button_leave.setOnClickListener(v -> {
            if (this.isComplete) {
                dialogText = R.string.leave_complete;
            }
            MyDialogFragment dialogFragment = new MyDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "my_dialog");
        });
    
        initControls();
        handleDifficulty();
        nextEquation();
    }
    
    public void renderAbout() {
        setContentView(R.layout.about);
        
        Button button_about_start = findViewById(R.id.button_about_start);
        button_about_start.setOnClickListener(view -> renderGame());
    }
    
    public void renderSettings() {
        setContentView(R.layout.settings);
        
        Button button_settings_start = findViewById(R.id.button_settings_start);
        Button button_settings_5 = findViewById(R.id.button_settings_5);
        Button button_settings_10 = findViewById(R.id.button_settings_10);
        Button button_settings_15 = findViewById(R.id.button_settings_15);
        Button button_settings_norwegian = findViewById(R.id.button_settings_norwegian);
        Button button_settings_english = findViewById(R.id.button_settings_english);
        
        button_settings_start.setOnClickListener(view -> renderGame());
        button_settings_5.setOnClickListener(view -> selectDifficulty("5"));
        button_settings_10.setOnClickListener(view -> selectDifficulty("10"));
        button_settings_15.setOnClickListener(view -> selectDifficulty("15"));
        button_settings_norwegian.setOnClickListener(view -> selectLanguage("no"));
        button_settings_english.setOnClickListener(view -> selectLanguage("en"));
    }
    
    @SuppressLint("SetTextI18n")
    public void nextEquation() {
        this.progress.setText(this.usedEquations.size() + "/" + this.equations.length);
        
        if (this.usedEquations.size() == this.equations.length) {
            gameOver();
            return;
        }
        
        String selectedEquation = null;
        
        for (String equation : this.equations) {
            if (!this.usedEquations.contains(equation)) {
                selectedEquation = equation;
                break;
            }
        }
        
        this.equation.setText(selectedEquation);
        this.usedEquations.add(selectedEquation);
    }
    
    public void undo() {
        String currentText = this.answer.getText().toString();
    
        if (!currentText.isEmpty()) {
            String updatedText = currentText.substring(0, currentText.length() - 1);
            this.answer.setText(updatedText);
        }
    }
    
    private void initControls() {
        for(int buttonId : this.buttonIds) {
            Button button = findViewById(buttonId);
    
            button.setOnClickListener(view -> {
                this.toast.setText("");
                this.answer.append(button.getText());
            });
        }
    }
    
    @SuppressLint("ResourceAsColor")
    public void check() {
        int ans = Integer.parseInt(answer.getText().toString());
        int result = handleEquation();
        
        messageToast(ans, result);
    }
    
    public int handleEquation() {
        String[] parts = equation.getText().toString().split("\\s+");
        
        int first = Integer.parseInt(parts[0]);
        String operator = parts[1];
        int second = Integer.parseInt(parts[2]);
        
        switch (operator) {
            case "+":
                return first + second;
            case "-":
                return first - second;
            case "x":
                return first * second;
            default:
                return first / second;
        }
    }
    
    public void messageToast(int ans, int result) {
        if (ans == result) {
            toast.setText(R.string.correct);
            answer.setText("");
            numTries = 0;
            nextEquation();
        }
        if (ans > result && numTries >= 1) {
            toast.setText(R.string.less);
            return;
        }
        if (ans < result && numTries >= 1) {
            toast.setText(R.string.greater);
            return;
        }
        
        numTries += 1;
        toast.setText(R.string.wrong);
    }
    
    public void gameOver() {
        for(int id : this.buttonIds) {
            Button button = findViewById(id);
            button.setEnabled(false);
        }
        
        Button button_check = findViewById(R.id.button_check);
        button_check.setEnabled(false);
        
        Button button_undo = findViewById(R.id.button_undo);
        button_undo.setEnabled(false);
    
        this.toast.setText(R.string.complete);
        this.answer.setText("");
        this.info.setText("");
        this.equation.setText("");
        this.isComplete = true;
    }
    
    @SuppressLint("SetTextI18n")
    public void handleDifficulty() {
        Resources res = getResources();
        
        List<String> allEquations = Arrays.asList(res.getStringArray(R.array.equations));
        Collections.shuffle(allEquations);
        
        this.difficulty = Integer.parseInt(preferenceManager.getDifficulty());
        
        equations = new String[this.difficulty];
        
        for (int i = 0; i < this.difficulty && i < allEquations.size(); i++) {
            this.equations[i] = allEquations.get(i);
        }
        
        this.progress.setText(usedEquations.size() + "/" + equations.length);
    }
    public void selectDifficulty(String difficulty) {
        this.preferenceManager.setDifficulty(difficulty);
    }
    
    public void selectLanguage(String languageCode) {
        Locale newLocale = new Locale(languageCode);
        Locale.setDefault(newLocale);
        Configuration config = new Configuration();
        config.setLocale(newLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    
        this.preferenceManager.setSelectedLanguage(languageCode);
        
        recreate();
    }
}
