package mygame.ru;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ExecuteGame {
    Stage stage;  // Объект Stage для отображения сцены
    private final Texture ball;  // Текстура мяча
    private final Texture boots;  // Текстура бутс

    private float ballX, ballY;  // Позиции мяча по осям X и Y

    private float ballSpeedY;  // Вертикальная скорость мяча
    private float ballSpeedX;  // Горизонтальная скорость мяча

    private static final float RANDOM_TRAJECTORY_CHANGE_PROBABILITY = 0.7f; // Вероятность изменения траектории

    private int sameTrajectoryCount = 0; // Количество одинаковых ударов мяча
    private float lastBallAngle = 0; // Последний угол мяча

    private float ballRotation = 0; // Текущий угол вращения мяча
    private float rotationSpeed = 0; // Скорость вращения мяча

    // Конструктор класса, инициализирует все необходимые данные для мяча и бутс
    public ExecuteGame(Stage stage) {
        this.ball = GameData.getBall();
        this.boots = GameData.getBoots();
        this.ballSpeedY = GameData.getBallSpeedY();
        this.ballSpeedX = GameData.getBallSpeedX();
        this.stage = stage;

        // Рассчитываем высоту мяча пропорционально его ширине
        float aspectRatio = (float) ball.getHeight() / ball.getWidth();
        float ballHeight = GameData.BALL_WIDTH * aspectRatio;
        GameData.setBallHeight(ballHeight);

        // Размещаем мяч по центру экрана
        ballX = (stage.getViewport().getWorldWidth() - GameData.BALL_WIDTH) / 2;
        GameData.setBallX(ballX);
        ballY = stage.getViewport().getWorldHeight() - GameData.getBallHeight() - 10;
        GameData.setBallY(ballY);

        // Размещаем бутсы по центру экрана
        float bootsX = (stage.getViewport().getWorldWidth() - boots.getWidth()) / 2;
        GameData.setBootsX(bootsX);
        float bootsY = (stage.getViewport().getWorldHeight() - boots.getHeight()) / 3;
        GameData.setBootsY(bootsY);
    }

    // Основной метод, выполняющий логику игры, обновляющий состояние мяча и выполняющий столкновения
    public void execute() {
        ballSpeedY += GameData.getGravity();  // Применяем гравитацию
        if (!GameData.getIsGameStarted()) {
            resetBallPosition();  // Если игра не началась, сбрасываем позицию мяча
            return;
        }
        if (GameData.getIsGameTheEnd()) {
            resetBallPosition();  // Если игра закончена, сбрасываем позицию мяча
            resetBallState();  // Сбрасываем все параметры мяча
        }
        if (GameData.getIsPause() || GameData.getCountdown() > 0) {
            return;  // Если игра на паузе или идет обратный отсчет, не обновляем состояние
        }
        handleBallCollision();  // Обрабатываем столкновения мяча
        applyRandomTrajectoryChange();  // Применяем случайное изменение траектории мяча

        updateBallPosition();  // Обновляем позицию мяча
        handleWallCollisions();  // Обрабатываем столкновения с границами экрана

        ballRotation += rotationSpeed;  // Обновляем угол вращения мяча
        GameData.setBallRotation(ballRotation);
    }

    // Сбрасываем позицию мяча в центр экрана
    private void resetBallPosition() {
        ballX = (stage.getViewport().getWorldWidth() - GameData.BALL_WIDTH) / 2;
        ballY = stage.getViewport().getWorldHeight() - GameData.getBallHeight() - 10;
        GameData.setBallX(ballX);
        GameData.setBallY(ballY);
    }

    // Сбрасываем все параметры состояния мяча
    private void resetBallState() {
        ballRotation = rotationSpeed = lastBallAngle = ballSpeedY = ballSpeedX = 0;
        sameTrajectoryCount = 0;
        GameData.setBallRotation(0);
        GameData.setBallSpeedY(0);
        GameData.setBallSpeedX(0);
    }

    // Обрабатываем столкновение мяча с бутсами
    private void handleBallCollision() {
        // Центр мяча для точности вычислений
        float ballCenterX = GameData.getBallX() + GameData.BALL_WIDTH / 2;
        float ballCenterY = GameData.getBallY() + GameData.getBallHeight() / 2;
        float ballRadius = GameData.BALL_WIDTH / 2;

        // Проверка столкновения мяча с бутсами
        boolean collisionWithBoots = ballCenterY - ballRadius <= GameData.getBootsY() + boots.getHeight() &&
            ballCenterY + ballRadius > GameData.getBootsY() &&
            ballCenterX + ballRadius > GameData.getBootsX() &&
            ballCenterX - ballRadius < GameData.getBootsX() + boots.getWidth();

        // Если нет столкновения, выходим
        if (!collisionWithBoots) return;

        // Вычисляем, с какой позиции мяча произошло столкновение
        float impactPosition = (ballCenterX - GameData.getBootsX()) / boots.getWidth();
        float randomOffset = MathUtils.random(-1f, 1f);  // Случайное смещение для изменения траектории
        ballSpeedY = GameData.getBounceStrength();  // Применяем силу отскока
        ballSpeedX = (impactPosition - 0.5f) * 30f + randomOffset;  // Применяем вычисленное смещение по оси X
        GameData.setBallSpeedY(ballSpeedY);  // Обновляем вертикальную скорость
        GameData.setBallSpeedX(ballSpeedX);  // Обновляем горизонтальную скорость

        adjustTrajectory();  // Корректируем траекторию мяча
        adjustRotation();  // Корректируем вращение мяча
    }
    // Метод для корректировки траектории мяча
    private void adjustTrajectory() {
        // Рассчитываем новый угол траектории мяча
        float newAngle = Math.abs(ballSpeedY / (ballSpeedX + 0.01f));

        // Если угол траектории сильно изменился, уменьшаем количество одинаковых траекторий
        if (Math.abs(newAngle - lastBallAngle) > 0.05f) {
            sameTrajectoryCount = Math.max(0, sameTrajectoryCount - 2);
        } else {
            sameTrajectoryCount++;  // Увеличиваем количество одинаковых траекторий
        }

        // Обновляем последний угол мяча
        lastBallAngle = newAngle;

        // Если количество одинаковых траекторий слишком велико, меняем траекторию мяча
        if (sameTrajectoryCount < 6) return;

        // Попытка изменить траекторию мяча трижды
        for (int i = 0; i < 3; i++) {
            float oldSpeedX = ballSpeedX;  // Сохраняем текущую горизонтальную скорость мяча
            ballSpeedY = -GameData.getBounceStrength() * 0.9f;  // Устанавливаем вертикальную скорость
            ballSpeedX += MathUtils.random(-10f, 10f);  // Добавляем случайное изменение горизонтальной скорости

            // Обновляем скорости мяча
            GameData.setBallSpeedY(ballSpeedY);
            GameData.setBallSpeedX(ballSpeedX);

            // Вычисляем новый угол траектории
            float adjustedAngle = Math.abs(ballSpeedY / (ballSpeedX + 0.01f));

            // Если угол сильно изменился, выходим из цикла
            if (Math.abs(adjustedAngle - lastBallAngle) > 0.05f) break;

            // Если угол не изменился, восстанавливаем старую горизонтальную скорость
            ballSpeedX = oldSpeedX;
            GameData.setBallSpeedX(ballSpeedX);
        }

        // Если мяч слишком близко к левому краю экрана, сдвигаем его вправо, иначе влево
        if (GameData.getBallX() < 10) {
            ballX += 150;
        } else {
            ballX -= 150;
        }

        // Обновляем позицию мяча
        GameData.setBallX(ballX);

        // Уменьшаем количество одинаковых траекторий
        sameTrajectoryCount = Math.max(3, sameTrajectoryCount - 3);
    }

    // Метод для корректировки вращения мяча
    private void adjustRotation() {
        // Вычисляем угол вращения мяча
        float angle = Math.abs((float) Math.atan2(ballSpeedY, ballSpeedX));

        // Задаем минимальную и максимальную скорость вращения
        float minRotationSpeed = 2f;
        float maxRotationSpeed = 15f;

        // Вычисляем скорость вращения мяча на основе его угла и горизонтальной скорости
        rotationSpeed = Math.signum(ballSpeedX) * (maxRotationSpeed - (angle / (float) Math.PI * maxRotationSpeed));

        // Если скорость вращения слишком мала, устанавливаем минимальную скорость
        if (Math.abs(rotationSpeed) < minRotationSpeed) {
            rotationSpeed = Math.signum(rotationSpeed) * minRotationSpeed;
        }
    }

    // Метод для применения случайных изменений траектории мяча
    private void applyRandomTrajectoryChange() {
        // С вероятностью RANDOM_TRAJECTORY_CHANGE_PROBABILITY изменяем траекторию мяча
        if (MathUtils.random() < RANDOM_TRAJECTORY_CHANGE_PROBABILITY) {
            ballSpeedX += MathUtils.random(-2f, 2f);  // Изменяем горизонтальную скорость
            ballSpeedY += MathUtils.random(-1f, 1f);  // Изменяем вертикальную скорость
            GameData.setBallSpeedX(ballSpeedX);
            GameData.setBallSpeedY(ballSpeedY);
        }
    }

    // Метод для обновления позиции мяча
    private void updateBallPosition() {
        // Обновляем позиции мяча по осям X и Y
        ballX += ballSpeedX;
        ballY += ballSpeedY;
        GameData.setBallX(ballX);
        GameData.setBallY(ballY);

        // Если мяч опустился ниже экрана, установим его на уровне пола
        if (GameData.getBallY() < 0) {
            ballY = 0;
            GameData.setBallY(0);
        }
    }

    // Метод для обработки столкновений мяча с границами экрана
    private void handleWallCollisions() {
        // Получаем размеры экрана
        float worldWidth = stage.getViewport().getWorldWidth();
        float worldHeight = stage.getViewport().getWorldHeight();

        // Если мяч упал на пол
        if (ballY <= 0) {
            handleFloorCollision();  // Обрабатываем столкновение с полом
        } else if (ballY + GameData.getBallHeight() > worldHeight) {
            // Если мяч выходит за верхнюю границу экрана
            ballY = worldHeight - GameData.getBallHeight();
            ballSpeedY = -ballSpeedY * 0.8f;  // Отражаем мяч с уменьшенной скоростью
            GameData.setBallY(ballY);
            GameData.setBallSpeedY(ballSpeedY);
            adjustRotation();  // Корректируем вращение мяча
        }

        // Если мяч выходит за левую или правую границу экрана
        if (ballX < 0) {
            ballX = 0;
            ballSpeedX = -ballSpeedX * 0.8f;  // Отражаем мяч с уменьшенной скоростью
        } else if (ballX + GameData.BALL_WIDTH > worldWidth) {
            ballX = worldWidth - GameData.BALL_WIDTH;
            ballSpeedX = -ballSpeedX * 0.8f;  // Отражаем мяч с уменьшенной скоростью
        }

        // Обновляем позицию мяча и его скорость
        GameData.setBallX(ballX);
        GameData.setBallSpeedX(ballSpeedX);
    }

    // Метод для обработки столкновения мяча с полом
    private void handleFloorCollision() {
        ballY = 0;  // Устанавливаем мяч на уровень пола
        ballSpeedY = -ballSpeedY * 0.8f;  // Отражаем мяч с уменьшенной вертикальной скоростью
        GameData.setBallY(0);
        GameData.setBallSpeedY(ballSpeedY);
        GameData.setIsGameTheEnd(true);  // Завершаем игру
        adjustRotation();  // Корректируем вращение мяча
    }

}

