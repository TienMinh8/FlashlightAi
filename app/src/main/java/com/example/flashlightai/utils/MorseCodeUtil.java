package com.example.flashlightai.utils;

import android.os.Handler;
import android.os.Looper;

import com.example.flashlightai.service.FlashlightService;

import java.util.HashMap;
import java.util.Map;

/**
 * Tiện ích để chuyển đổi và phát mã Morse qua đèn flash
 */
public class MorseCodeUtil {
    private static final Map<Character, String> MORSE_CODE_MAP = new HashMap<>();
    private static final int DOT_DURATION = 200; // thời gian chấm (ms)
    private static final int DASH_DURATION = DOT_DURATION * 3; // thời gian gạch (ms)
    private static final int SYMBOL_GAP = DOT_DURATION; // khoảng cách giữa các ký tự trong một chữ cái (ms)
    private static final int LETTER_GAP = DOT_DURATION * 3; // khoảng cách giữa các chữ cái (ms)
    private static final int WORD_GAP = DOT_DURATION * 7; // khoảng cách giữa các từ (ms)

    private FlashlightService flashlightService;
    private Handler handler;
    private Runnable currentTask;
    private boolean isPlaying = false;

    static {
        // Khởi tạo ánh xạ ký tự sang mã Morse
        MORSE_CODE_MAP.put('A', ".-");
        MORSE_CODE_MAP.put('B', "-...");
        MORSE_CODE_MAP.put('C', "-.-.");
        MORSE_CODE_MAP.put('D', "-..");
        MORSE_CODE_MAP.put('E', ".");
        MORSE_CODE_MAP.put('F', "..-.");
        MORSE_CODE_MAP.put('G', "--.");
        MORSE_CODE_MAP.put('H', "....");
        MORSE_CODE_MAP.put('I', "..");
        MORSE_CODE_MAP.put('J', ".---");
        MORSE_CODE_MAP.put('K', "-.-");
        MORSE_CODE_MAP.put('L', ".-..");
        MORSE_CODE_MAP.put('M', "--");
        MORSE_CODE_MAP.put('N', "-.");
        MORSE_CODE_MAP.put('O', "---");
        MORSE_CODE_MAP.put('P', ".--.");
        MORSE_CODE_MAP.put('Q', "--.-");
        MORSE_CODE_MAP.put('R', ".-.");
        MORSE_CODE_MAP.put('S', "...");
        MORSE_CODE_MAP.put('T', "-");
        MORSE_CODE_MAP.put('U', "..-");
        MORSE_CODE_MAP.put('V', "...-");
        MORSE_CODE_MAP.put('W', ".--");
        MORSE_CODE_MAP.put('X', "-..-");
        MORSE_CODE_MAP.put('Y', "-.--");
        MORSE_CODE_MAP.put('Z', "--..");
        MORSE_CODE_MAP.put('0', "-----");
        MORSE_CODE_MAP.put('1', ".----");
        MORSE_CODE_MAP.put('2', "..---");
        MORSE_CODE_MAP.put('3', "...--");
        MORSE_CODE_MAP.put('4', "....-");
        MORSE_CODE_MAP.put('5', ".....");
        MORSE_CODE_MAP.put('6', "-....");
        MORSE_CODE_MAP.put('7', "--...");
        MORSE_CODE_MAP.put('8', "---..");
        MORSE_CODE_MAP.put('9', "----.");
        MORSE_CODE_MAP.put('.', ".-.-.-");
        MORSE_CODE_MAP.put(',', "--..--");
        MORSE_CODE_MAP.put('?', "..--..");
        MORSE_CODE_MAP.put('!', "-.-.--");
        MORSE_CODE_MAP.put('/', "-..-.");
        MORSE_CODE_MAP.put('(', "-.--.");
        MORSE_CODE_MAP.put(')', "-.--.-");
        MORSE_CODE_MAP.put('&', ".-...");
        MORSE_CODE_MAP.put(':', "---...");
        MORSE_CODE_MAP.put(';', "-.-.-.");
        MORSE_CODE_MAP.put('=', "-...-");
        MORSE_CODE_MAP.put('+', ".-.-.");
        MORSE_CODE_MAP.put('-', "-....-");
        MORSE_CODE_MAP.put('_', "..--.-");
        MORSE_CODE_MAP.put('"', ".-..-.");
        MORSE_CODE_MAP.put('$', "...-..-");
        MORSE_CODE_MAP.put('@', ".--.-.");
    }

    public MorseCodeUtil(FlashlightService flashlightService) {
        this.flashlightService = flashlightService;
        this.handler = new Handler(Looper.getMainLooper());
    }

    /**
     * Chuyển đổi văn bản thành mã Morse
     */
    public String convertTextToMorse(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        StringBuilder morseBuilder = new StringBuilder();
        String upperText = text.toUpperCase();
        
        for (int i = 0; i < upperText.length(); i++) {
            char c = upperText.charAt(i);
            
            if (c == ' ') {
                morseBuilder.append("   "); // khoảng cách giữa các từ
            } else if (MORSE_CODE_MAP.containsKey(c)) {
                morseBuilder.append(MORSE_CODE_MAP.get(c));
                
                // Thêm khoảng cách giữa các chữ cái nếu không phải ký tự cuối cùng
                if (i < upperText.length() - 1 && upperText.charAt(i + 1) != ' ') {
                    morseBuilder.append(" ");
                }
            }
        }
        
        return morseBuilder.toString();
    }

    /**
     * Phát mã Morse thông qua đèn flash
     */
    public void playMorseCode(String text, float speedFactor) {
        if (text == null || text.isEmpty() || flashlightService == null) {
            return;
        }
        
        // Dừng task hiện tại nếu có
        stopMorseCode();
        
        // Thiết lập thời gian dựa vào hệ số tốc độ
        final int dotTime = (int) (DOT_DURATION / speedFactor);
        final int dashTime = (int) (DASH_DURATION / speedFactor);
        final int symbolSpace = (int) (SYMBOL_GAP / speedFactor);
        final int letterSpace = (int) (LETTER_GAP / speedFactor);
        final int wordSpace = (int) (WORD_GAP / speedFactor);
        
        // Chuyển đổi văn bản thành mã Morse
        String morseCode = convertTextToMorse(text);
        
        // Đánh dấu đang phát và bắt đầu phát
        isPlaying = true;
        playMorseSequence(morseCode, dotTime, dashTime, symbolSpace, letterSpace, wordSpace, 0);
    }

    /**
     * Phát tín hiệu SOS
     */
    public void playSOS(float speedFactor) {
        playMorseCode("SOS", speedFactor);
    }

    /**
     * Dừng phát mã Morse
     */
    public void stopMorseCode() {
        if (currentTask != null) {
            handler.removeCallbacks(currentTask);
            currentTask = null;
        }
        
        // Đảm bảo đèn flash tắt khi dừng
        if (flashlightService != null && isPlaying) {
            try {
                flashlightService.turnOffFlash();
            } catch (Exception e) {
                // Phương thức không tồn tại hoặc có lỗi
            }
        }
        
        isPlaying = false;
    }

    /**
     * Phát một chuỗi mã Morse
     */
    private void playMorseSequence(String morseCode, int dotTime, int dashTime, int symbolSpace,
                                 int letterSpace, int wordSpace, int position) {
        if (!isPlaying || position >= morseCode.length()) {
            // Hoàn thành phát mã Morse
            stopMorseCode();
            return;
        }
        
        char currentSymbol = morseCode.charAt(position);
        int delay = 0;
        
        switch (currentSymbol) {
            case '.': // Chấm
                flashOn();
                delay = dotTime;
                break;
            case '-': // Gạch
                flashOn();
                delay = dashTime;
                break;
            case ' ': // Khoảng cách giữa các chữ cái
                flashOff();
                
                // Kiểm tra xem đây là khoảng cách giữa các chữ cái hay giữa các từ
                if (position + 1 < morseCode.length() && position + 2 < morseCode.length() &&
                    morseCode.charAt(position + 1) == ' ' && morseCode.charAt(position + 2) == ' ') {
                    delay = wordSpace;
                    position += 2; // Bỏ qua 2 khoảng trắng tiếp theo
                } else {
                    delay = letterSpace;
                }
                break;
            default:
                delay = symbolSpace;
                break;
        }
        
        // Tạo task tiếp theo
        final int nextPosition = position + 1;
        currentTask = () -> {
            if (currentSymbol == '.' || currentSymbol == '-') {
                flashOff();
                handler.postDelayed(() -> playMorseSequence(morseCode, dotTime, dashTime, symbolSpace,
                    letterSpace, wordSpace, nextPosition), symbolSpace);
            } else {
                playMorseSequence(morseCode, dotTime, dashTime, symbolSpace, letterSpace, wordSpace, nextPosition);
            }
        };
        
        // Lên lịch cho task tiếp theo
        handler.postDelayed(currentTask, delay);
    }

    /**
     * Bật đèn flash
     */
    private void flashOn() {
        if (flashlightService != null) {
            try {
                flashlightService.turnOnFlash();
            } catch (Exception e) {
                // Phương thức không tồn tại hoặc có lỗi
            }
        }
    }

    /**
     * Tắt đèn flash
     */
    private void flashOff() {
        if (flashlightService != null) {
            try {
                flashlightService.turnOffFlash();
            } catch (Exception e) {
                // Phương thức không tồn tại hoặc có lỗi
            }
        }
    }

    /**
     * Kiểm tra xem đang phát mã Morse hay không
     */
    public boolean isPlaying() {
        return isPlaying;
    }
} 