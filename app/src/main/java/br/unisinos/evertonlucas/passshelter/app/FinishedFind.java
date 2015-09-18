package br.unisinos.evertonlucas.passshelter.app;

import java.util.List;

/**
 * Created by everton on 16/09/15.
 */
public interface FinishedFind {
    void notifyFinished(List<String> emailList);
    void notifyError();
}
