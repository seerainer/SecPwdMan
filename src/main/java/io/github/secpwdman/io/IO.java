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
import static io.github.secpwdman.widgets.Widgets.msg;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
	 * @param cData the cdata
	 * @param s     the string
	 * @return the string
	 */
	private static String escapeSpecialChar(final ConfData cData, final String s) {
		if (isEmpty(s))
			return s;

		var escapedData = s.replaceAll(cData.lineBrk, cData.space);
		final var newLine = s.contains(cData.newLine);
		final var doubleQ = s.contains(cData.doubleQ);
		final var space = s.contains(cData.space);
		final var apost = s.contains(cData.apost);
		final var comma = s.contains(cData.comma);
		final var grave = s.contains(cData.grave);

		if (newLine || doubleQ || space || apost || comma || grave) {
			final var str = s.replace(cData.doubleQ, cData.doubleQ + cData.doubleQ);
			escapedData = cData.doubleQ + str + cData.doubleQ;
		}

		return escapedData;
	}

	/**
	 * Extract data from table.
	 *
	 * @param cData the cdata
	 * @param table the table
	 * @return the StringBuilder
	 */
	public static StringBuilder extractData(final ConfData cData, final Table table) {
		final var sb = new StringBuilder();
		final var items = table.getItems();

		sb.append(cData.getHeader() + cData.newLine);

		for (final TableItem item : items) {
			final var itemText = new String[table.getColumnCount()];
			for (var i = 0; i < itemText.length; i++)
				itemText[i] = escapeSpecialChar(cData, item.getText(i));
			final var line = new StringBuilder();
			for (var j = 0; j < itemText.length - 1; j++)
				line.append(itemText[j]).append(cData.comma);
			line.append(itemText[itemText.length - 1]).append(cData.newLine);
			sb.append(line.toString());
		}

		return sb;
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
	 * @param cData the cdata
	 * @param str   the string
	 * @param table the table
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void fillTable(final ConfData cData, final String str, final Table table) throws IOException {
		final var csv = CsvReader.parse(str);

		if (!csv.readHeaders())
			throw new IOException();

		table.setRedraw(false);

		if (str.startsWith(cData.tableHeader))
			action.createColumns(cData.defaultHeader);
		else
			action.createColumns(csv.getHeaders());

		while (csv.readRecord())
			new TableItem(table, SWT.NONE).setText(csv.getValues());

		table.setRedraw(true);

		csv.close();

		action.colorURL();
		action.resizeColumns();
		table.redraw();
	}

	/**
	 * Open file.
	 *
	 * @param pwd the password
	 * @return true, if successful
	 */
	public boolean openFile(final byte[] pwd) {
		final var cData = action.getCData();
		var exMsg = cData.empty;

		try (final var fis = new FileInputStream(cData.getFile())) {
			final var fileBytes = new byte[fis.available()];
			fis.read(fileBytes);
			fis.close();

			if (fileBytes.length <= 0) {
				cData.setFile(null);
				throw new NullPointerException();
			}

			var s = cData.empty;

			if (pwd != null && pwd.length > 0)
				s = new String(new Crypto(cData).decrypt(fileBytes, pwd));
			else {
				s = new String(fileBytes).trim();
				cData.setFile(null);
			}

			fillTable(cData, s, action.getTable());
			return true;
		} catch (final BadPaddingException e) {
			exMsg = cData.errorPwd;
		} catch (final IllegalArgumentException | IOException | NullPointerException e) {
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
		final var table = action.getTable();

		try (final var fos = new FileOutputStream(file)) {
			final var data = extractData(cData, table).toString().getBytes();

			if (pwd != null && pwd.length > 0)
				fos.write(new Crypto(cData).encrypt(data, pwd));
			else
				fos.write(data);

			fos.close();
			return true;
		} catch (final BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException | InvalidKeySpecException
				| IOException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			msg(action.getShell(), SWT.ICON_ERROR | SWT.OK, cData.titleErr, e.fillInStackTrace().toString());
		}

		return false;
	}
}
