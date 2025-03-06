package mygame.ru;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ExecuteGame {
    Stage stage;
    private final Texture ball;
    private final Texture boots;

    private float ballX, ballY;

    private float ballSpeedY;  // Вертикальная скорость мяча
    private float ballSpeedX;  // Горизонтальная скорость мяча

    private static final float RANDOM_TRAJECTORY_CHANGE_PROBABILITY = 0.7f; // Вероятность изменения траектории

    private int sameTrajectoryCount = 0; // Количество одинаковых ударов
    private float lastBallAngle = 0; // Последний угол мяча

    private float ballRotation = 0; // Текущий угол мяча
    private float rotationSpeed = 0; // Скорость вращения

    public ExecuteGame(Stage stage) {
        this.ball = GameData.getBall();
        this.boots = GameData.getBoots();
        this.ballSpeedY = GameData.getBallSpeedY();
        this.ballSpeedX = GameData.getBallSpeedX();
        this.stage = stage;
        float aspectRatio = (float) ball.getHeight() / ball.getWidth();
        // Автоматически рассчитанная высота
        float ballHeight = GameData.BALL_WIDTH * aspectRatio;
        GameData.setBallHeight(ballHeight);

        ballX = (stage.getViewport().getWorldWidth() - GameData.BALL_WIDTH) / 2;
        GameData.setBallX(ballX);
        ballY = stage.getViewport().getWorldHeight() - GameData.getBallHeight() - 10;
        GameData.setBallY(ballY);

        float bootsX = (stage.getViewport().getWorldWidth() - boots.getWidth()) / 2;
        GameData.setBootsX(bootsX);
        float bootsY = (stage.getViewport().getWorldHeight() - boots.getHeight()) / 3;
        GameData.setBootsY(bootsY);
    }

    public void execute() {
        ballSpeedY += GameData.getGravity();
        if (!GameData.getIsGameStarted()) {
            resetBallPosition();
            return;
        }
        if (GameData.getIsGameTheEnd()) {
            resetBallPosition();
            resetBallState();
        }
        if (GameData.getIsPause() || GameData.getCountdown() > 0) {
            return;
        }
        handleBallCollision();
        applyRandomTrajectoryChange();

        updateBallPosition();
        handleWallCollisions();

        ballRotation += rotationSpeed;
        GameData.setBallRotation(ballRotation);
    }
    private void resetBallPosition() {
        ballX = (stage.getViewport().getWorldWidth() - GameData.BALL_WIDTH) / 2;
        ballY = stage.getViewport().getWorldHeight() - GameData.getBallHeight() - 10;
        GameData.setBallX(ballX);
        GameData.setBallY(ballY);
    }
    private void resetBallState() {
        ballRotation = rotationSpeed = lastBallAngle = ballSpeedY = ballSpeedX = 0;
        sameTrajectoryCount = 0;
        GameData.setBallRotation(0);
        GameData.setBallSpeedY(0);
        GameData.setBallSpeedX(0);
    }
    private void handleBallCollision() {
        float ballCenterX = GameData.getBallX() + GameData.BALL_WIDTH / 2;
        float ballCenterY = GameData.getBallY() + GameData.getBallHeight() / 2;
        float ballRadius = GameData.BALL_WIDTH / 2;
        boolean collisionWithBoots = ballCenterY - ballRadius <= GameData.getBootsY() + boots.getHeight() &&
            ballCenterY + ballRadius > GameData.getBootsY() &&
            ballCenterX + ballRadius > GameData.getBootsX() &&
            ballCenterX - ballRadius < GameData.getBootsX() + boots.getWidth();
        if (!collisionWithBoots) return;
        float impactPosition = (ballCenterX - GameData.getBootsX()) / boots.getWidth();
        float randomOffset = MathUtils.random(-1f, 1f);
        ballSpeedY = GameData.getBounceStrength();
        ballSpeedX = (impactPosition - 0.5f) * 30f + randomOffset;
        GameData.setBallSpeedY(ballSpeedY);
        GameData.setBallSpeedX(ballSpeedX);
        adjustTrajectory();
        adjustRotation();
    }
    private void adjustTrajectory() {
        float newAngle = Math.abs(ballSpeedY / (ballSpeedX + 0.01f));
        if (Math.abs(newAngle - lastBallAngle) > 0.05f) {
            sameTrajectoryCount = Math.max(0, sameTrajectoryCount - 2);
        } else {
            sameTrajectoryCount++;
        }
        lastBallAngle = newAngle;
        if (sameTrajectoryCount < 6) return;
        for (int i = 0; i < 3; i++) {
            float oldSpeedX = ballSpeedX;
            ballSpeedY = -GameData.getBounceStrength() * 0.9f;
            ballSpeedX += MathUtils.random(-10f, 10f);
            GameData.setBallSpeedY(ballSpeedY);
            GameData.setBallSpeedX(ballSpeedX);
            float adjustedAngle = Math.abs(ballSpeedY / (ballSpeedX + 0.01f));
            if (Math.abs(adjustedAngle - lastBallAngle) > 0.05f) break;
            ballSpeedX = oldSpeedX;
            GameData.setBallSpeedX(ballSpeedX);
        }
        if (GameData.getBallX() < 10) {
            ballX += 150;
        } else {
            ballX -= 150;
        }
        GameData.setBallX(ballX);
        sameTrajectoryCount = Math.max(3, sameTrajectoryCount - 3);
    }

    private void adjustRotation() {
        float angle = Math.abs((float) Math.atan2(ballSpeedY, ballSpeedX));
        float minRotationSpeed = 2f;
        float maxRotationSpeed = 15f;
        rotationSpeed = Math.signum(ballSpeedX) * (maxRotationSpeed - (angle / (float) Math.PI * maxRotationSpeed));
        if (Math.abs(rotationSpeed) < minRotationSpeed) {
            rotationSpeed = Math.signum(rotationSpeed) * minRotationSpeed;
        }
    }
    private void applyRandomTrajectoryChange() {
        if (MathUtils.random() < RANDOM_TRAJECTORY_CHANGE_PROBABILITY) {
            ballSpeedX += MathUtils.random(-2f, 2f);
            ballSpeedY += MathUtils.random(-1f, 1f);
            GameData.setBallSpeedX(ballSpeedX);
            GameData.setBallSpeedY(ballSpeedY);
        }
    }
    private void updateBallPosition() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;
        GameData.setBallX(ballX);
        GameData.setBallY(ballY);
        if (GameData.getBallY() < 0) {
            ballY = 0;
            GameData.setBallY(0);
        }
    }
    private void handleWallCollisions() {
        float worldWidth = stage.getViewport().getWorldWidth();
        float worldHeight = stage.getViewport().getWorldHeight();
        if (ballY <= 0) {
            handleFloorCollision();
        } else if (ballY + GameData.getBallHeight() > worldHeight) {
            ballY = worldHeight - GameData.getBallHeight();
            ballSpeedY = -ballSpeedY * 0.8f;
            GameData.setBallY(ballY);
            GameData.setBallSpeedY(ballSpeedY);
            adjustRotation();
        }
        if (ballX < 0) {
            ballX = 0;
            ballSpeedX = -ballSpeedX * 0.8f;
        } else if (ballX + GameData.BALL_WIDTH > worldWidth) {
            ballX = worldWidth - GameData.BALL_WIDTH;
            ballSpeedX = -ballSpeedX * 0.8f;
        }
        GameData.setBallX(ballX);
        GameData.setBallSpeedX(ballSpeedX);
    }
    private void handleFloorCollision() {
        ballY = 0;
        ballSpeedY = -ballSpeedY * 0.8f;
        GameData.setBallY(0);
        GameData.setBallSpeedY(ballSpeedY);
        GameData.setIsGameTheEnd(true);
        adjustRotation();
    }
}

