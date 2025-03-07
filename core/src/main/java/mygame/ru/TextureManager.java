package mygame.ru;

import com.badlogic.gdx.graphics.Texture;

public class TextureManager {
    // Единственный экземпляр класса (синглтон)
    private static TextureManager instance;

    // Текстура мяча
    private final Texture ball;
    // Текстура фона
    private final Texture background;
    // Текстура бутсы
    private final Texture boots;

    // Приватный конструктор, загружает текстуры
    private TextureManager() {
        this.ball = new Texture("ball.png"); // Загружаем текстуру мяча
        this.background = new Texture("stadium.png"); // Загружаем текстуру фона
        this.boots = new Texture("boots.png"); // Загружаем текстуру бутсы
    }

    // Метод для получения единственного экземпляра класса
    public static TextureManager getInstance() {
        if (instance == null) { // Если объект еще не создан, создаем его
            instance = new TextureManager();
        }
        return instance; // Возвращаем существующий экземпляр
    }

    // Метод для получения текстуры мяча
    public Texture getBall() {
        return ball;
    }

    // Метод для получения текстуры фона
    public Texture getBackground() {
        return background;
    }

    // Метод для получения текстуры бутсы
    public Texture getBoots() {
        return boots;
    }

    // Освобождает память, удаляя загруженные текстуры
    public void dispose() {
        ball.dispose(); // Удаляем текстуру мяча
        background.dispose(); // Удаляем текстуру фона
        boots.dispose(); // Удаляем текстуру бутсы
    }
}

