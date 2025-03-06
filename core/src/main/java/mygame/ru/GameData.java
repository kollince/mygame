package mygame.ru;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class GameData {

    private static Texture ball;
    private static Texture boots ;

    private static float ballSpeedY = 0;  // Вертикальная скорость мяча
    private static float ballSpeedX = 0;  // Горизонтальная скорость мяча

    private static float ballX, ballY;
    private static float bootsX, bootsY;

    private static float ballRotation = 0;

    private static float ballHeight;
    public static final float BALL_WIDTH = 200;

    private static float defaultGravity = -0.1f;  // Гравитация
    private static float defaultBounceStrength = 18f;  // Сила отскока
    //Полет мяча
    private static float gravity = -0.1f;  // Гравитация
    private static float bounceStrength = 18f;  // Сила отскока

    private static boolean isGameStarted = false;
    private static boolean isGameTheEnd = false;
    private static boolean isPause = false;

    private static final float DEFAULT_COUNTDOWN = 3;
    private static float countdown = DEFAULT_COUNTDOWN;


    public static Texture getBall() {
        return TextureManager.getInstance().getBall();
    }

    public static Texture getBoots() {
        return TextureManager.getInstance().getBoots();
    }

    public static float getBallSpeedY() {
        return ballSpeedY;
    }

    public static float getBallSpeedX() {
        return ballSpeedX;
    }

    public static void setBallSpeedY(float ballSpeedY) {
        GameData.ballSpeedY = ballSpeedY;
    }

    public static void setBallSpeedX(float ballSpeedX) {
        GameData.ballSpeedX = ballSpeedX;
    }


    public static float getBallX() {
        return ballX;
    }

    public static void setBallX(float ballX) {
        GameData.ballX = ballX;
    }

    public static float getBallY() {
        return ballY;
    }

    public static void setBallY(float ballY) {
        GameData.ballY = ballY;
    }

    public static float getBootsX() {
        return bootsX;
    }

    public static void setBootsX(float bootsX) {
        GameData.bootsX = bootsX;
    }

    public static float getBootsY() {
        return bootsY;
    }

    public static void setBootsY(float bootsY) {
        GameData.bootsY = bootsY;
    }

    public static float getBallRotation() {
        return ballRotation;
    }

    public static void setBallRotation(float ballRotation) {
        GameData.ballRotation = ballRotation;
    }

    public static float getBallHeight() {
        return ballHeight;
    }

    public static void setBallHeight(float ballHeight) {
        GameData.ballHeight = ballHeight;
    }

    public static float getDefaultGravity() {
        return defaultGravity;
    }

    public static float getDefaultBounceStrength() {
        return defaultBounceStrength;
    }

    public static float getGravity() {
        return gravity;
    }

    public static void setGravity(float gravity) {
        GameData.gravity = gravity;
    }

    public static float getBounceStrength() {
        return bounceStrength;
    }

    public static void setBounceStrength(float bounceStrength) {
        GameData.bounceStrength = bounceStrength;
    }

    public static boolean getIsGameTheEnd() {
        return isGameTheEnd;
    }

    public static void setIsGameTheEnd(boolean isGameTheEnd) {
        GameData.isGameTheEnd = isGameTheEnd;
    }

    public static boolean getIsPause() {
        return isPause;
    }

    public static void setIsPause(boolean isPause) {
        GameData.isPause = isPause;
    }

    public static void setDefaultBounceStrength(float defaultBounceStrength) {
        GameData.defaultBounceStrength = defaultBounceStrength;
    }


    public static boolean getIsGameStarted() {
        return isGameStarted;
    }

    public static void setIsGameStarted(boolean isGameStarted) {
        GameData.isGameStarted = isGameStarted;
    }

    public static float getCountdown() {
        return countdown;
    }

    public static void setCountdown(float countdown) {
        GameData.countdown = countdown;
    }
    public static float getDefaultCountdown(){
        return DEFAULT_COUNTDOWN;
    }
}
