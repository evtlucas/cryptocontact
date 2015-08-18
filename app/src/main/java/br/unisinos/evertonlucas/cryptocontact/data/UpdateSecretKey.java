package br.unisinos.evertonlucas.cryptocontact.data;

import javax.crypto.SecretKey;

/**
 * Interface created for update some class about reading data.crypto file and update key
 * Created by everton on 17/08/15.
 */
public interface UpdateSecretKey {
    void update(byte[] key);
}
