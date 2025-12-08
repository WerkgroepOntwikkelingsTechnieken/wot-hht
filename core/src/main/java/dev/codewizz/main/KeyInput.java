package dev.codewizz.main;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class KeyInput implements InputProcessor {

    private String currentScan = "";
    private final Main main;

    public KeyInput(Main main) {
        this.main = main;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode != 66  && Input.Keys.toString(keycode).matches("[a-zA-Z0-9]")) {
            currentScan += Input.Keys.toString(keycode);
        } else if (!currentScan.isEmpty()) {
            main.scan(currentScan);
            currentScan = "";
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
