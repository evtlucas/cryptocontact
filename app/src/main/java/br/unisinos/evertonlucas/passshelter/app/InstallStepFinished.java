package br.unisinos.evertonlucas.passshelter.app;

import android.content.Context;

/**
 * Interface created for notificate finished installation step
 * Created by everton on 06/09/15.
 */
public interface InstallStepFinished {
    void finished(InstallState state);
    void persistState(InstallState state);
}
