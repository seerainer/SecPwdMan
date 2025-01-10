
package io.github.seerainer.secpwdman.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * The class CryptoContext.
 */
public final class CryptoContext {
	private final EncryptionStrategy strategy;

	CryptoContext(final EncryptionStrategy strategy) {
		this.strategy = strategy;
	}

	public byte[] decrypt(final byte[] data, final byte[] password)
			throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
			InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		return strategy.decrypt(data, password);
	}

	public byte[] encrypt(final byte[] data, final byte[] password)
			throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
			InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		return strategy.encrypt(data, password);
	}
}
