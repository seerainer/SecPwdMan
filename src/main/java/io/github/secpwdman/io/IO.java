/*
 * Secure Password Manager
 * Copyright (C) 2024  Philipp Seerainer
 * philipp@seerainer.com
 * https://www.seerainer.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
package io.github.secpwdman.io;

import static io.github.secpwdman.util.Util.isEmpty;
import static io.github.secpwdman.util.Util.isEqual;
import static io.github.secpwdman.widgets.Widgets.msg;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.csvreader.CsvReader;

import io.github.secpwdman.action.FileAction;
import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.crypto.Crypto;

/**
 * The Class IO.
 */
public class IO {
	/**
	 * Escape special character.
	 *
	 * @param s the string
	 * @return the string
	 */
	private static String escapeSpecialChar(final ConfData cData, final String s) {
		if (isEmpty(s))
			return s;

		final var newLine = s.contains(cData.newLine);
		final var doubleQ = s.contains(cData.doubleQ);
		final var space = s.contains(cData.space);
		final var apost = s.contains(cData.apost);
		final var comma = s.contains(cData.comma);
		final var grave = s.contains(cData.grave);
		var escapedData = s.replaceAll(cData.lineBrk, cData.space);

		if (newLine || doubleQ || space || apost || comma || grave) {
			final var str = s.replace(cData.doubleQ, cData.doubleQ + cData.doubleQ);
			escapedData = cData.doubleQ + str + cData.doubleQ;
		}

		return escapedData;
	}

	/**
	 * Extract data from table.
	 *
	 * @return the byte[]
	 */
	public static byte[] extractData(final ConfData cData, final Table table) {
		final var sb = new StringBuilder();
		final var items = table.getItems();

		sb.append(cData.getHeader() + cData.newLine);

		for (final var item : items) {
			final var itemText = new String[table.getColumnCount()];
			for (var i = 0; i < itemText.length; i++)
				itemText[i] = escapeSpecialChar(cData, item.getText(i));
			final var line = new StringBuilder();
			for (var j = 0; j < itemText.length - 1; j++)
				line.append(itemText[j]).append(cData.comma);
			line.append(itemText[itemText.length - 1]).append(cData.newLine);
			sb.append(line.toString());
		}

		return sb.toString().getBytes();
	}

	private final FileAction action;

	/**
	 * Instantiates a new io.
	 *
	 * @param action the action
	 */
	public IO(final FileAction action) {
		this.action = action;
	}

	/**
	 * Fill table.
	 *
	 * @param data the data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void fillTable(final byte[] data) throws IOException {
		final var csv = new CsvReader(new ByteArrayInputStream(data), Charset.defaultCharset());
		final var cData = action.getCData();

		if (!csv.readHeaders())
			throw new IOException(cData.errorHea);

		final var table = action.getTable();
		table.setRedraw(false);

		if (data.length < cData.csvHeader.length())
			action.createColumns(csv.getHeaders());
		else {
			final var head1 = cData.csvHeader.getBytes();
			final var head2 = new byte[head1.length];
			System.arraycopy(data, 0, head2, 0, head2.length);

			if (isEqual(head1, head2))
				action.createColumns(cData.tableHeader);
			else
				action.createColumns(csv.getHeaders());
		}

		while (csv.readRecord())
			new TableItem(table, SWT.NONE).setText(csv.getValues());

		table.setRedraw(true);

		csv.close();

		action.colorURL();
		action.resizeColumns();
		cData.setData(action.cryptData(data, true));
		table.redraw();
	}

	/**
	 * Open file.
	 *
	 * @param pwd the password
	 * @return true, if successful
	 */
	public boolean openFile(final byte[] pwd, final String file) {
		final var cData = action.getCData();
		var exMsg = cData.empty;

		try (final var fis = new FileInputStream(file)) {
			var fileBytes = new byte[fis.available()];
			fis.read(fileBytes);
			fis.close();

			if (fileBytes.length <= 0)
				throw new NullPointerException(cData.errorNul);

			if (pwd != null && pwd.length > 0)
				fillTable(new Crypto(cData).decrypt(fileBytes, pwd));
			else
				fillTable(fileBytes);

			fileBytes = null;
			return true;
		} catch (final BadPaddingException e) {
			exMsg = cData.errorPwd;
		} catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException | IOException | NullPointerException e) {
			exMsg = cData.errorImp + cData.newLine + cData.newLine + e.fillInStackTrace().toString();
		} catch (final IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			exMsg = e.fillInStackTrace().toString();
		}

		msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, exMsg);

		return false;
	}

	/**
	 * Save file.
	 *
	 * @param pwd  the password
	 * @param file the file
	 * @return true, if successful
	 */
	public boolean saveFile(final byte[] pwd, final String file) {
		final var cData = action.getCData();

		action.resetGroupState();

		try (final var fos = new FileOutputStream(file)) {
			final var table = action.getTable();
			if (pwd != null && pwd.length > 0)
				fos.write(new Crypto(cData).encrypt(extractData(cData, table), pwd));
			else
				fos.write(extractData(cData, table));

			fos.close();
			return true;
		} catch (final BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException | InvalidKeySpecException
				| IOException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, e.fillInStackTrace().toString());
		}

		return false;
	}
}
