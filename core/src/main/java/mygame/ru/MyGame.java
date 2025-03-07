package mygame.ru;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MyGame extends Game {
    // Основной объект для отрисовки графики
    private SpriteBatch batch;

    @Override
    public void create() {
        // Инициализация объекта для отрисовки
        batch = new SpriteBatch();

        // Создаем сцену для размещения игровых объектов
        Stage stage = new Stage();

        // Создаем игровой экран и передаем в него ссылку на игру и сцену
        GameScreen gameScreen = new GameScreen(this, stage);

        // Устанавливаем текущий экран игры
        this.setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        // Освобождаем ресурсы, когда игра закрывается
        batch.dispose();
    }

    // Геттер для получения batch в других частях игры
    public SpriteBatch getBatch() {
        return batch;
    }
}

