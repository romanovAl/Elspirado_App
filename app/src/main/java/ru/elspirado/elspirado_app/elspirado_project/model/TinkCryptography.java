package ru.elspirado.elspirado_app.elspirado_project.model;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.firebase.auth.FirebaseAuth;

import java.security.GeneralSecurityException;

public class TinkCryptography {

    private KeysetHandle keysetHandle;

    public KeysetHandle getKeysetHandle() {
        return keysetHandle;
    }

    public void setKeysetHandle(KeysetHandle keysetHandle) {
        this.keysetHandle = keysetHandle;
    }

    public Aead generateKeysetHandle() throws GeneralSecurityException {
        AeadConfig.register();

        keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);

        Aead aead = AeadFactory.getPrimitive(keysetHandle);

        return aead;
    }

    public byte[] encryptString(String stringForEncrypt) throws GeneralSecurityException {

        AeadConfig.register();

        keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);

        Aead aead = AeadFactory.getPrimitive(keysetHandle);

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return aead.encrypt(stringForEncrypt.getBytes(), key.getBytes());
    }

    public String decryptString(byte[] stringForDecrypt) throws GeneralSecurityException {

        AeadConfig.register();
        Aead aead = AeadFactory.getPrimitive(keysetHandle);

        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

        byte[] decrypted = aead.decrypt(stringForDecrypt, key.getBytes());

        String s = new String(decrypted);
        System.err.println("ВОТ оно " + s);
        return s;
    }
}
