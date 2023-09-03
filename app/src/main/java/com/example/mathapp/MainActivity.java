package com.example.mathapp;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    
    static int dialogText = R.string.leave_uncomplete;
    PreferenceManager preferenceManager;
    
    ArrayList<String> usedEquations = new ArrayList<>();
    
    String [] equations;
    
    TextView answer;
    TextView info;
    TextView toast;
    TextView progress;
    TextView equation;
    
    int difficulty;
    
    int numTries = 0;
    
    int [] buttonIds = { R.id.button_main_0, R.id.button_main_1, R.id.button_main_2, R.id.button_main_3, R.id.button_main_4, R.id.button_main_5, R.id.button_main_6, R.id.button_main_7, R.id.button_main_8, R.id.button_main_9 };
    
    boolean isComplete = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
    
        preferenceManager = new PreferenceManager(this);
    
        initWelcomeButtons();
    }
    
    public void initWelcomeButtons() {
        findViewById(R.id.button_welcome_start).setOnClickListener(view -> renderGame());
        findViewById(R.id.button_welcome_about).setOnClickListener(view -> renderAbout());
        findViewById(R.id.button_welcome_settings).setOnClickListener(view -> renderSettings());
    }
    
    public void renderGame() {
        setContentView(R.layout.activity_main);
    
        this.answer = findViewById(R.id.txt_answer);
        this.toast = findViewById(R.id.txt_toast);
        this.progress = findViewById(R.id.txt_progress);
        this.equation = findViewById(R.id.txt_equation);
        this.info = findViewById(R.id.txt_info);
        
        findViewById(R.id.button_check).setOnClickListener(view -> check());
        findViewById(R.id.button_undo).setOnClickListener(view -> undo());
        findViewById(R.id.button_leave).setOnClickListener(v -> displayDialog());
    
        initControls();
        handleDifficulty();
        nextEquation();
    }
    
    public void renderAbout() {
        setContentView(R.layout.about);
        
        findViewById(R.id.button_about_start).setOnClickListener(view -> renderGame());
    }
    
    public void renderSettings() {
        setContentView(R.layout.settings);
    
        findViewById(R.id.button_settings_start).setOnClickListener(view -> renderGame());
        findViewById(R.id.button_settings_5).setOnClickListener(view -> selectDifficulty("5"));
        findViewById(R.id.button_settings_10).setOnClickListener(view -> selectDifficulty("10"));
        findViewById(R.id.button_settings_15).setOnClickListener(view -> selectDifficulty("15"));
        findViewById(R.id.button_settings_norwegian).setOnClickListener(view -> selectLanguage("no"));
        findViewById(R.id.button_settings_english).setOnClickListener(view -> selectLanguage("en"));
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
    
    @SuppressLint("ResourceAsColor")
    public void check() {
        int ans = Integer.parseInt(answer.getText().toString());
        int result = handleEquation();
        
        messageToast(ans, result);
    }
    
    public void undo() {
        String currentText = this.answer.getText().toString();
    
        if (!currentText.isEmpty()) {
            String updatedText = currentText.substring(0, currentText.length() - 1);
            this.answer.setText(updatedText);
        }
    }
    
    public void displayDialog() {
        if (this.isComplete) {
            dialogText = R.string.leave_complete;
        }
        MyDialogFragment dialogFragment = new MyDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "my_dialog");
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
            return;
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
        
        findViewById(R.id.button_check).setEnabled(false);
        findViewById(R.id.button_undo).setEnabled(false);
    
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
    
    public void selectLanguage(String language) {
        Locale newLocale = new Locale(language);
        Locale.setDefault(newLocale);
        Configuration config = new Configuration();
        config.setLocale(newLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    
        this.preferenceManager.setSelectedLanguage(language);
        
        recreate();
    }
}
