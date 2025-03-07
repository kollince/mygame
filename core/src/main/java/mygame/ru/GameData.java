package mygame.ru;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class GameData {

    private static float ballSpeedY = 0;  // Вертикальная скорость мяча
    private static float ballSpeedX = 0;  // Горизонтальная скорость мяча

    private static float ballX, ballY;  // Позиции мяча на экране
    private static float bootsX, bootsY;  // Позиции бутс на экране

    private static float ballRotation = 0;  // Угол вращения мяча

    private static float ballHeight;  // Высота мяча
    public static final float BALL_WIDTH = 200;  // Ширина мяча

    private static float gravity = -0.1f;  // Гравитация
    private static float bounceStrength = 18f;  // Сила отскока мяча

    private static boolean isGameStarted = false;  // Статус начала игры
    private static boolean isGameTheEnd = false;  // Статус конца игры
    private static boolean isPause = false;  // Статус паузы

    private static final float DEFAULT_COUNTDOWN = 3;  // Значение по умолчанию для отсчета времени
    private static float countdown = DEFAULT_COUNTDOWN;  // Время до начала игры (обратный отсчет)


    // Получить текстуру мяча
    public static Texture getBall() {
        return TextureManager.getInstance().getBall();
    }

    // Получить текстуру бутс
    public static Texture getBoots() {
        return TextureManager.getInstance().getBoots();
    }

    // Получить вертикальную скорость мяча
    public static float getBallSpeedY() {
        return ballSpeedY;
    }

    // Получить горизонтальную скорость мяча
    public static float getBallSpeedX() {
        return ballSpeedX;
    }

    // Установить вертикальную скорость мяча
    public static void setBallSpeedY(float ballSpeedY) {
        GameData.ballSpeedY = ballSpeedY;
    }

    // Установить горизонтальную скорость мяча
    public static void setBallSpeedX(float ballSpeedX) {
        GameData.ballSpeedX = ballSpeedX;
    }

    // Получить текущую позицию мяча по оси X
    public static float getBallX() {
        return ballX;
    }

    // Установить позицию мяча по оси X
    public static void setBallX(float ballX) {
        GameData.ballX = ballX;
    }

    // Получить текущую позицию мяча по оси Y
    public static float getBallY() {
        return ballY;
    }

    // Установить позицию мяча по оси Y
    public static void setBallY(float ballY) {
        GameData.ballY = ballY;
    }

    // Получить позицию бутс по оси X
    public static float getBootsX() {
        return bootsX;
    }

    // Установить позицию бутс по оси X
    public static void setBootsX(float bootsX) {
        GameData.bootsX = bootsX;
    }

    // Получить позицию бутс по оси Y
    public static float getBootsY() {
        return bootsY;
    }

    // Установить позицию бутс по оси Y
    public static void setBootsY(float bootsY) {
        GameData.bootsY = bootsY;
    }

    // Получить текущий угол вращения мяча
    public static float getBallRotation() {
        return ballRotation;
    }

    // Установить угол вращения мяча
    public static void setBallRotation(float ballRotation) {
        GameData.ballRotation = ballRotation;
    }

    // Получить высоту мяча
    public static float getBallHeight() {
        return ballHeight;
    }

    // Установить высоту мяча
    public static void setBallHeight(float ballHeight) {
        GameData.ballHeight = ballHeight;
    }

    // Получить гравитацию
    public static float getGravity() {
        return gravity;
    }

    // Получить силу отскока мяча
    public static float getBounceStrength() {
        return bounceStrength;
    }

    // Проверить, завершена ли игра
    public static boolean getIsGameTheEnd() {
        return isGameTheEnd;
    }

    // Установить статус окончания игры
    public static void setIsGameTheEnd(boolean isGameTheEnd) {
        GameData.isGameTheEnd = isGameTheEnd;
    }

    // Проверить, на паузе ли игра
    public static boolean getIsPause() {
        return isPause;
    }

    // Проверить, началась ли игра
    public static boolean getIsGameStarted() {
        return isGameStarted;
    }

    // Установить статус начала игры
    public static void setIsGameStarted(boolean isGameStarted) {
        GameData.isGameStarted = isGameStarted;
    }

    // Получить текущее время обратного отсчета
    public static float getCountdown() {
        return countdown;
    }

    // Установить время обратного отсчета
    public static void setCountdown(float countdown) {
        GameData.countdown = countdown;
    }

    // Получить значение времени обратного отсчета по умолчанию
    public static float getDefaultCountdown(){
        return DEFAULT_COUNTDOWN;
    }
}
