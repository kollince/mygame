package mygame.ru;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ExecuteGame {

    //private Texture ball1;
    Stage stage;
    private Texture ball;
    private Texture boots;

    private float ballX, ballY;
    private float bootsX, bootsY;
    private final float gravity = -0.1f;  // Гравитация
    private final float bounceStrength = 16f;  // Сила отскока
    private float ballSpeedY;  // Вертикальная скорость мяча
    private float ballSpeedX;  // Горизонтальная скорость мяча
    private boolean isGameStarted = false;
    private boolean isGameTheEnd = false;

    private final float DEFAULT_COUNTDOWN = 3;
    private float countdown = DEFAULT_COUNTDOWN;

    private final float hitRadiusMultiplier = 1.9f; // Коэффициент увеличения радиуса клика
    private static final float RANDOM_TRAJECTORY_CHANGE_PROBABILITY = 0.7f; // Вероятность изменения траектории

    private int hitCount = 0; // Количество ударов
    private int sameTrajectoryCount = 0; // Количество одинаковых ударов
    private float lastBallAngle = 0; // Последний угол мяча

    //private final float BALL_WIDTH = 200;
    private final float ballRadius = GameData.BALL_WIDTH/2;  // Радиус мяча (например, 32 пикселя)
    private float ballHeight; // Автоматически рассчитанная высота

    private float ballRotation = 0; // Текущий угол мяча
    private float rotationSpeed = 0; // Скорость вращения
    private static final float SPEED = 500; // Скорость в пикселях в секунду
    private Vector3 coordsBootsX;
    private float lastBallRotation = 0;
    private float lastBallX = 0;
    private float lastBallY = 0;
    private long lastUpdateTime = 0;
    private boolean isBallStuck = false;


    public ExecuteGame(Stage stage) {
        this.ball = GameData.getBall();
        this.boots = GameData.getBoots();
        this.ballSpeedY = GameData.getBallSpeedY();
        this.ballSpeedX = GameData.getBallSpeedX();
        this.stage = stage;
        float aspectRatio = (float) ball.getHeight() / ball.getWidth();
        ballHeight = GameData.BALL_WIDTH * aspectRatio;
        GameData.setBallHeight(ballHeight);

        ballX = (stage.getViewport().getWorldWidth() - GameData.BALL_WIDTH) / 2;
        GameData.setBallX(ballX);
        ballY = stage.getViewport().getWorldHeight() - GameData.getBallHeight() - 10;
        GameData.setBallY(ballY);

        coordsBootsX = new Vector3(boots.getWidth(), 0, 0);
        bootsX = (stage.getViewport().getWorldWidth() - boots.getWidth()) / 2;
        GameData.setBootsX(bootsX);
        bootsY = (stage.getViewport().getWorldHeight() - boots.getHeight()) / 3 ;
        GameData.setBootsY(bootsY);
    }

    public void execute(float delta){
        ballSpeedY  += GameData.getGravity();
        if (!GameData.getIsGameStarted()) {
            ballX = (stage.getViewport().getWorldWidth() - GameData.BALL_WIDTH) / 2;
            GameData.setBallX(ballX);
            ballY = stage.getViewport().getWorldHeight() - GameData.getBallHeight() - 10;
            GameData.setBallY(ballY);
            return; // Если игра не началась, не обновляем позицию мяча
        }
        if (GameData.getIsGameTheEnd()){
            ballX = (stage.getViewport().getWorldWidth() - GameData.BALL_WIDTH) / 2;
            GameData.setBallX(ballX);
            ballY = stage.getViewport().getWorldHeight() - GameData.getBallHeight() - 10;
            GameData.setBallY(ballY);
            ballRotation = 0;
            GameData.setBallRotation(0);
            rotationSpeed = 0;
            lastBallAngle = 0;
            ballSpeedY = 0;
            ballSpeedX = 0;
            GameData.setBallSpeedY(0);
            GameData.setBallSpeedX(0);
            sameTrajectoryCount = 0;
        }
        // Центр мяча для корректного расчета коллизий
        float ballCenterX = GameData.getBallX() + GameData.BALL_WIDTH / 2;
        float ballCenterY = GameData.getBallY() + GameData.getBallHeight() / 2;
        // Проверка столкновения с бутсой
        if (!GameData.getIsGameTheEnd()) {
            // Если игра на паузе, ничего не обновляем
            if (GameData.getIsPause() || GameData.getCountdown() > 0) {
                return;
            }

            if (ballCenterY - ballRadius <= GameData.getBootsY() + boots.getHeight() &&
                ballCenterY + ballRadius > GameData.getBootsY() &&
                ballCenterX + ballRadius > GameData.getBootsX() &&
                ballCenterX - ballRadius < GameData.getBootsX() + boots.getWidth()) {
                // Вычисление положения удара и случайного отклонения угла
                float impactPosition = (ballCenterX - GameData.getBootsX()) / boots.getWidth(); // От 0 (левый край) до 1 (правый край)
                float randomOffset = MathUtils.random(-1f, 1f); // Легкая случайность угла
                // Задание силы отскока и бокового импульса
//                ballSpeedY = bounceStrength;
                ballSpeedY = GameData.getBounceStrength();
                GameData.setBallSpeedY(ballSpeedY);
                ballSpeedX = (impactPosition - 0.5f) * 30f + randomOffset; // Ближе к краю — сильнее боковой импульс
                GameData.setBallSpeedX(ballSpeedX);
                // Проверка одинаковой траектории (смягчено)
                float newAngle = Math.abs(ballSpeedY / (ballSpeedX + 0.01f));
                boolean trajectoryChanged = Math.abs(newAngle - lastBallAngle) > 0.05f; // Было 0.1f, теперь 0.05f
                if (trajectoryChanged) {
                    sameTrajectoryCount = Math.max(0, sameTrajectoryCount - 2); // Плавно уменьшаем
                } else {
                    sameTrajectoryCount++;
                }
                lastBallAngle = newAngle;
                // Если 6 раз одинаковая траектория — меняем угол (было 5)
                if (sameTrajectoryCount >= 6) {
                    for (int i = 0; i < 3; i++) { // Три попытки изменить траекторию
                        float oldSpeedX = ballSpeedX;
//                        ballSpeedY = -bounceStrength * 0.9f;
                        ballSpeedY = -GameData.getBounceStrength() * 0.9f;
                        GameData.setBallSpeedY(ballSpeedY);
                        ballSpeedX += MathUtils.random(-10f, 10f);
                        GameData.setBallSpeedX(ballSpeedX);

                        float adjustedAngle = Math.abs(ballSpeedY / (ballSpeedX + 0.01f));
                        if (Math.abs(adjustedAngle - lastBallAngle) > 0.05f) { // Если угол изменился (тоже 0.05f)
                            trajectoryChanged = true;
                            break;
                        } else {
                            ballSpeedX = oldSpeedX; // Возвращаем прежнюю скорость, если угол не поменялся
                            GameData.setBallSpeedX(ballSpeedX);
                        }
                    }
                    // Дополнительный сдвиг мяча, если он застрял
                    if (GameData.getBallX() < 10) {
                        ballX += 150; // Было 200, сделано мягче
                        GameData.setBallX(ballX);
                    } else {
                        ballX -= 150;
                        GameData.setBallX(ballX);
                    }
                    sameTrajectoryCount = Math.max(3, sameTrajectoryCount - 3); // Плавный сброс
                }
                // Проверяем, застрял ли мяч в углу
                boolean nearLeft =  GameData.getBallX() <= 10;
                boolean nearRight = GameData.getBallX() + GameData.BALL_WIDTH >= stage.getViewport().getWorldWidth() - 10;
                boolean nearTop = GameData.getBallY() + GameData.getBallHeight() >= stage.getViewport().getWorldHeight() - 10;
                boolean isStuckInCorner = (nearLeft || nearRight) && nearTop;
                adjustRotation();
            }
        }
        // Случайное небольшое изменение траектории
        if (MathUtils.random() < RANDOM_TRAJECTORY_CHANGE_PROBABILITY) {
            ballSpeedX += MathUtils.random(-2f, 2f);
            GameData.setBallSpeedX(ballSpeedX);
            ballSpeedY += MathUtils.random(-1f, 1f);
            GameData.setBallSpeedY(ballSpeedY);
        }

        ballX += ballSpeedX;
        GameData.setBallX(ballX);
        ballY += ballSpeedY;
        GameData.setBallY(ballY);
        if (GameData.getBallY() < 0) {
            ballY = 0;
            GameData.setBallY(0);
        }
        // Невидимые стены (левая, правая, потолок, пол)
        if (GameData.getBallY() <= 0) { // Пол
            ballY = 0;
            GameData.setBallY(0);
            ballSpeedY = -ballSpeedY * 0.8f;
            GameData.setBallSpeedY(ballSpeedY);
            GameData.setIsGameTheEnd(true);
            adjustRotation();
        }

        Vector3 coordsBall = new Vector3(GameData.getBallX(), GameData.getBallY(), 0);
        stage.getCamera().unproject(coordsBall);
        if (GameData.getBallY() + GameData.getBallHeight() > stage.getViewport().getWorldHeight()) { // Потолок
            ballY = stage.getViewport().getWorldHeight() - GameData.getBallHeight();
            GameData.setBallY(ballY);
            ballSpeedY = -ballSpeedY * 0.8f;
            GameData.setBallSpeedY(ballSpeedY);
            adjustRotation();
        }
        if (GameData.getBallX() < 0) { // Левая стена
            ballX = 0;
            GameData.setBallX(0);
            ballSpeedX = -ballSpeedX * 0.8f;
            GameData.setBallSpeedX(ballSpeedX);
            adjustRotation();
        }
        if (GameData.getBallX() + GameData.BALL_WIDTH > stage.getViewport().getWorldWidth()) { // Правая стена
            ballX = stage.getViewport().getWorldWidth() - GameData.BALL_WIDTH;
            GameData.setBallX(ballX);
            ballSpeedX = -ballSpeedX * 0.8f;
            GameData.setBallSpeedX(ballSpeedX);
            adjustRotation();
        }
        ballRotation += rotationSpeed;
        GameData.setBallRotation(ballRotation);
    }

    private void adjustRotation() {
        float angle = Math.abs((float) Math.atan2(ballSpeedY, ballSpeedX)); // Угол удара
        float minRotationSpeed = 2f;  // Минимальная скорость вращения
        float maxRotationSpeed = 15f; // Максимальная скорость вращения
        rotationSpeed = Math.signum(ballSpeedX) * (maxRotationSpeed - (angle / (float) Math.PI * maxRotationSpeed));
        if (Math.abs(rotationSpeed) < minRotationSpeed) {
            rotationSpeed = Math.signum(rotationSpeed) * minRotationSpeed;
        }
    }




//    public float getBallHeight() {
//        return ballHeight;
//    }

//    public float getBallRotation() {
//        return ballRotation;
//    }

//    public Texture getBall() {
//        return ball;
//    }

//    public Texture  getBoots() {
//        return boots;
//    }

}

