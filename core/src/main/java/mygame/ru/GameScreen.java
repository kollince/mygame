package mygame.ru;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {
    private Stage stage;
    private final SpriteBatch batch;
    private FitViewport viewport;
    private Label countdownLabel;
    private TextButton startButton;
    private final ExecuteGame executeGame;

    // Конструктор класса GameScreen
    public GameScreen(Game game, Stage stage) {
        // Присваиваем переданную сцену переменной класса
        this.stage = stage;
        // Получаем объект SpriteBatch из главного класса игры
        this.batch = ((MyGame) game).getBatch();
        // Создаем объект игровой логики, передавая ему сцену
        this.executeGame = new ExecuteGame(stage);
    }

    @Override
    public void show() {
        // Настраиваем камеру и сцену
        setupCameraAndStage();
        // Устанавливаем фоновое изображение
        setupBackground();
        // Создаем метку обратного отсчета
        setupCountdownLabel();
        // Создаем кнопку старта
        setupStartButton();
        // Инициализируем обработку ввода
        initInput();
    }

    private void setupCameraAndStage() {
        // Создаем ортографическую камеру
        OrthographicCamera camera = new OrthographicCamera();
        // Создаем вьюпорт с заданными размерами
        viewport = new FitViewport(1280, 720, camera);
        // Применяем вьюпорт
        viewport.apply();
        // Создаем сцену с этим вьюпортом
        stage = new Stage(viewport);
        // Устанавливаем сцену как обработчик ввода
        Gdx.input.setInputProcessor(stage);
    }

    private void setupBackground() {
        // Получаем текстуру фона из менеджера текстур
        Texture backgroundTexture = TextureManager.getInstance().getBackground();
        // Создаем изображение с этой текстурой
        Image backgroundImage = new Image(backgroundTexture);
        // Настраиваем масштабирование изображения
        backgroundImage.setScaling(Scaling.fill);
        // Заполняем все пространство сцены фоном
        backgroundImage.setFillParent(true);
        // Добавляем фон на сцену
        stage.addActor(backgroundImage);
    }

    private void setupCountdownLabel() {
        // Создаем новый шрифт
        BitmapFont font = new BitmapFont();
        // Создаем стиль для метки
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        // Устанавливаем шрифт в стиль
        labelStyle.font = font;
        // Устанавливаем цвет шрифта белый
        labelStyle.fontColor = Color.WHITE;
        // Создаем объект метки с пустым текстом
        countdownLabel = new Label("", labelStyle);
        // Устанавливаем масштаб шрифта
        countdownLabel.setFontScale(3);
        // Размещаем метку в центре экрана
        countdownLabel.setPosition(stage.getViewport().getWorldWidth() / 2,
            stage.getViewport().getWorldHeight() / 2, Align.center);
        // Добавляем метку на сцену
        stage.addActor(countdownLabel);
    }

    private void setupStartButton() {
        // Создаем новый шрифт для кнопки
        BitmapFont font = new BitmapFont();
        // Создаем стиль для кнопки
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        // Устанавливаем шрифт кнопки
        buttonStyle.font = font;
        // Устанавливаем цвет шрифта кнопки
        buttonStyle.fontColor = Color.WHITE;
        // Устанавливаем фоновое изображение кнопки
        buttonStyle.up = createButtonBackground();
        // Создаем объект кнопки с текстом "Start"
        startButton = new TextButton("Start", buttonStyle);
        // Устанавливаем масштаб шрифта кнопки
        startButton.getLabel().setFontScale(2);
        // Устанавливаем цвет кнопки
        startButton.setColor(Color.CHARTREUSE);
        // Устанавливаем размеры кнопки
        startButton.setSize(200, 80);
        // Размещаем кнопку в нижней части экрана
        startButton.setPosition(stage.getViewport().getWorldWidth() / 2 - startButton.getWidth() / 2,
            stage.getViewport().getWorldHeight() / 4);
        // Добавляем обработчик нажатия на кнопку
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Сбрасываем данные игры
                resetData();
                // Устанавливаем флаг начала игры
                GameData.setIsGameStarted(true);
                // Удаляем кнопку со сцены
                startButton.remove();
            }
        });
        // Если игра на паузе, добавляем кнопку на сцену
        if (GameData.getIsPause()) {
            stage.addActor(startButton);
        }
    }

    // Создает фоновое изображение для кнопки
    private Drawable createButtonBackground() {
        // Создаем пустую картинку размером 1x1 пиксель с поддержкой RGBA
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        // Устанавливаем серый цвет
        pixmap.setColor(Color.GRAY);
        // Заполняем всю картинку этим цветом
        pixmap.fill();
        // Создаем текстуру на основе полученного изображения
        Texture texture = new Texture(pixmap);
        // Создаем объект Drawable из текстуры
        Drawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        // Очищаем память, удаляя Pixmap
        pixmap.dispose();
        // Возвращаем созданное изображение для кнопки
        return drawable;
    }

    // Инициализирует обработку пользовательского ввода
    private void initInput(){
        // Создаем мультиплексор для обработки нескольких типов ввода
        InputMultiplexer multiplexer = new InputMultiplexer();
        // Добавляем сцену как обработчик ввода
        multiplexer.addProcessor(stage);
        // Добавляем кастомный обработчик движения мыши
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                // Преобразуем координаты экрана в игровые координаты
                Vector3 coords = new Vector3(screenX, screenY, 0);
                stage.getViewport().unproject(coords);
                // Устанавливаем позицию бутсы в зависимости от перемещения мыши
                GameData.setBootsX(coords.x - GameData.getBoots().getWidth()/2f);
                GameData.setBootsY(coords.y - TextureManager.getInstance().getBoots().getHeight() / 2f);
                return true;
            }
        });
        // Устанавливаем мультиплексор как текущий обработчик ввода
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        // Очищаем экран перед отрисовкой нового кадра
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Выполняем логику игры
        executeGame.execute();
        // Обновляем состояние сцены
        stage.act(delta);
        // Применяем вьюпорт перед отрисовкой
        stage.getViewport().apply();
        // Отрисовываем сцену
        stage.draw();

        // Выполняем обратный отсчет, если требуется
        if (handleCountdown(delta)) return;

        // Проверяем, завершилась ли игра
        if (GameData.getIsGameTheEnd()) {
            handleGameEnd();
            return;
        }

        // Если игра не запущена, запускаем ее
        if (!GameData.getIsGameStarted()) {
            GameData.setIsGameStarted(true);
        }

        // Отрисовываем игровой процесс
        renderGame();
    }

    // Обрабатывает обратный отсчет перед стартом игры
    private boolean handleCountdown(float delta) {
        // Если игра на паузе, отображаем сообщение
        if (GameData.getIsPause()) {
            countdownLabel.setText("Pause");
            renderGame();
            return true;
        }
        // Если отсчет больше 0, уменьшаем его
        if (GameData.getCountdown() > 0) {
            GameData.setCountdown(GameData.getCountdown() - delta);
            countdownLabel.setText(String.valueOf((int) Math.ceil(GameData.getCountdown())));
            renderGame();
            return true;
        }
        // Очищаем текст обратного отсчета
        countdownLabel.setText("");
        return false;
    }

    // Обрабатывает окончание игры
    private void handleGameEnd() {
        // Если игра не завершена, выходим
        if (!GameData.getIsGameTheEnd()) return;
        // Если кнопка перезапуска не добавлена на сцену, добавляем ее
        if (startButton.getStage() == null) {
            startButton.setPosition(
                (stage.getViewport().getWorldWidth() - startButton.getWidth()) / 2,
                (stage.getViewport().getWorldHeight() - startButton.getHeight()) / 2
            );
            stage.addActor(startButton);
        }
    }

    // Отрисовывает игровые объекты
    private void renderGame(){
        // Устанавливаем проекционную матрицу камеры
        batch.setProjectionMatrix(stage.getCamera().combined);
        // Начинаем отрисовку
        batch.begin();
        // Рисуем мяч с учетом его текущих координат и поворота
        batch.draw(new TextureRegion(GameData.getBall()),
            GameData.getBallX(), GameData.getBallY(),
            GameData.BALL_WIDTH / 2, GameData.getBallHeight() / 2,
            GameData.BALL_WIDTH, GameData.getBallHeight(),
            1, 1, GameData.getBallRotation());
        // Рисуем бутсу
        batch.draw(GameData.getBoots(), GameData.getBootsX(), GameData.getBootsY());
        // Завершаем отрисовку
        batch.end();
    }

    // Сбрасывает игровые данные при перезапуске
    private void resetData(){
        GameData.setIsGameTheEnd(false);
        GameData.setIsGameStarted(false);
        GameData.setCountdown(GameData.getDefaultCountdown());
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем размеры вьюпорта при изменении окна
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // Очищаем сцену при скрытии экрана
        stage.clear();
        // Сбрасываем игровые данные
        resetData();
    }

    @Override
    public void dispose() {
        // Освобождаем ресурсы сцены
        stage.dispose();
        // Освобождаем текстуры
        TextureManager.getInstance().dispose();
    }
}
