package mygame.ru;

import com.badlogic.gdx.graphics.Texture;

public class TextureManager {
    private static TextureManager instance;  // Единственный экземпляр класса
    private final Texture ball;
    private final Texture background;
    private final Texture boots;

    // Конструктор инициализирует текстуры
    private TextureManager() {
        this.ball = new Texture("ball.png");
        this.background = new Texture("stadium.png");
        this.boots = new Texture("boots.png");
    }

    // Метод для получения единственного экземпляра
    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();  // Создаём экземпляр, если ещё не существует
        }
        return instance;  // Возвращаем единственный экземпляр
    }

    // Геттеры для текстур
    public Texture getBall() {
        return ball;
    }

    public Texture getBackground() {
        return background;
    }

    public Texture getBoots() {
        return boots;
    }

    // Освобождаем ресурсы при завершении
    public void dispose() {
        ball.dispose();
        background.dispose();
        boots.dispose();
    }
}

