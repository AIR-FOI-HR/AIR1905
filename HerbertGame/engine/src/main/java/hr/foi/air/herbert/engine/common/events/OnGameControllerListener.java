package hr.foi.air.herbert.engine.common.events;

/**
 * Created by Filka Milip on 30.10.17..
 */

public interface OnGameControllerListener {
    void OnLevelSolved(int levelScore);
    void OnCodeWithError(String error);
}
