package com.example.darkshadowprank;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.animation.ObjectAnimator;

public class MainActivity extends AppCompatActivity {

    private EditText codeInput;
    private TextView errorText;
    private View rootLayout;
    
    // متغيرات الأصوات
    private MediaPlayer hackSound;
    private MediaPlayer robotVoice;
    private MediaPlayer wrongCodeSound;
    private MediaPlayer successSound;
    
    // متغير الاهتزاز
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // إخفاء شريط العنوان
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // وضع الغمر الكامل (إخفاء أزرار الموبايل وشريط الإشعارات)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        
        // منع الشاشة من النوم
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        // ربط العناصر
        codeInput = findViewById(R.id.codeInput);
        errorText = findViewById(R.id.errorText);
        rootLayout = findViewById(R.id.rootLayout);
        View secretBar = findViewById(R.id.secretBar);
        TextView eye1 = findViewById(R.id.eye1);
        TextView eye2 = findViewById(R.id.eye2);
        
        // تهيئة الاهتزاز
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // ========== تهيئة الأصوات ==========
        hackSound = MediaPlayer.create(this, R.raw.hack_sound);
        robotVoice = MediaPlayer.create(this, R.raw.robot_voice);
        wrongCodeSound = MediaPlayer.create(this, R.raw.wrong_code);
        successSound = MediaPlayer.create(this, R.raw.success_sound);
        
        // تشغيل صوت الهاكر عند فتح التطبيق
        if (hackSound != null) {
            hackSound.start();
        }
        
        // تشغيل صوت الروبوت كخلفية (مع تكرار)
        if (robotVoice != null) {
            robotVoice.setLooping(true);
            robotVoice.setVolume(0.5f, 0.5f);
            robotVoice.start();
        }

        // تحريك العيون
        animateEye(eye1, 100f, 150f, 3000);
        animateEye(eye2, -100f, 100f, 3500);

        // عند الضغط على البار السري
        secretBar.setOnClickListener(v -> {
            codeInput.setVisibility(View.VISIBLE);
            codeInput.requestFocus();
            android.view.inputmethod.InputMethodManager imm = 
                (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(codeInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        });

        // مراقبة ما يكتبه المستخدم
        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("4466")) {
                    // الكود صحيح: إيقاف الأصوات وإغلاق التطبيق
                    stopAllSounds();
                    if (successSound != null) {
                        successSound.start();
                    }
                    finish();
                } else if (s.length() == 4) {
                    // الكود خطأ: تشغيل صوت الإنذار + اهتزاز + وميض أحمر
                    triggerWrongCodeEffect();
                    
                    errorText.setText("الكود غلط يا معنص! مش هتخرج غير بالكود 😈");
                    errorText.setVisibility(View.VISIBLE);
                    codeInput.setText("");
                } else {
                    errorText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // اعتراض زر الرجوع
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // صوت روبوت عند محاولة الهروب
                if (robotVoice != null) {
                    robotVoice.seekTo(0);
                    robotVoice.start();
                }
                // اهتزاز خفيف
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(300);
                    }
                }
                errorText.setText("مفيش هروب! ادخل الكود السري يا صاحبي 😂");
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    // دالة تأثيرات الكود الغلط (صوت + اهتزاز + وميض أحمر)
    private void triggerWrongCodeEffect() {
        // 1. صوت الإنذار
        if (wrongCodeSound != null) {
            wrongCodeSound.seekTo(0);
            wrongCodeSound.start();
        }
        
        // 2. اهتزاز الموبايل
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
        
        // 3. وميض الشاشة بالأحمر
        if (rootLayout != null) {
            rootLayout.setBackgroundColor(0xFFFF0000); // أحمر
            rootLayout.postDelayed(() -> {
                if (rootLayout != null) {
                    rootLayout.setBackgroundColor(0xFF000000); // يرجع أسود
                }
            }, 300); // 300 مللي ثانية
        }
    }

    // دالة لإيقاف كل الأصوات
    private void stopAllSounds() {
        if (hackSound != null) { hackSound.stop(); hackSound.release(); hackSound = null; }
        if (robotVoice != null) { robotVoice.stop(); robotVoice.release(); robotVoice = null; }
        if (wrongCodeSound != null) { wrongCodeSound.stop(); wrongCodeSound.release(); wrongCodeSound = null; }
        if (successSound != null) { successSound.stop(); successSound.release(); successSound = null; }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAllSounds();
    }

    // دالة تحريك العيون
    private void animateEye(View eye, float xDelta, float yDelta, long duration) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(eye, "translationX", 0f, xDelta);
        animX.setDuration(duration);
        animX.setRepeatCount(ObjectAnimator.INFINITE);
        animX.setRepeatMode(ObjectAnimator.REVERSE);
        animX.start();

        ObjectAnimator animY = ObjectAnimator.ofFloat(eye, "translationY", 0f, yDelta);
        animY.setDuration(duration);
        animY.setRepeatCount(ObjectAnimator.INFINITE);
        animY.setRepeatMode(ObjectAnimator.REVERSE);
        animY.start();
    }
}
