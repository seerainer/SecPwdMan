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
package io.github.secpwdman.widgets;

import static io.github.secpwdman.util.Util.isEmptyString;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import io.github.secpwdman.config.ConfData;
import io.github.secpwdman.images.IMG;

/**
 * The Class Widgets.
 */
public class Widgets {

	/**
	 * File dialog.
	 *
	 * @param parent the parent
	 * @param style  the style
	 * @return the dialog
	 */
	public static FileDialog fileDialog(final Shell parent, final int style) {
		final var dialog = new FileDialog(parent, style);
		dialog.setOverwrite(true);
		return dialog;
	}

	/**
	 * Horizontal separator.
	 *
	 * @param parent the parent
	 * @return the label
	 */
	public static Label horizontalSeparator(final Composite parent) {
		return newLabel(parent, SWT.HORIZONTAL | SWT.SEPARATOR, null);
	}

	/**
	 * A link.
	 *
	 * @param parent the parent
	 * @param url    the url
	 * @param color  the color
	 * @param text   the text
	 * @return the link
	 */
	public static Link link(final Composite parent, final String url, final Color color, final String text) {
		return link(parent, url, color, text, null);
	}

	/**
	 * A link.
	 *
	 * @param parent the parent
	 * @param url    the url
	 * @param color  the color
	 * @param text   the text
	 * @param font   the font
	 * @return the link
	 */
	public static Link link(final Composite parent, final String url, final Color color, final String text, final String font) {
		final var link = new Link(parent, SWT.NONE);
		link.addSelectionListener(widgetSelectedAdapter(e -> Program.launch(url)));
		link.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		link.setLinkForeground(color);
		link.setText(text);

		if (!isEmptyString(font))
			link.setFont(new Font(parent.getDisplay(), new FontData(font, 12, SWT.NORMAL)));

		return link;
	}

	/**
	 * Menu item.
	 *
	 * @param parent    the parent
	 * @param state     the state
	 * @param menu      the menu
	 * @param listener  the listener
	 * @param acc       the accelerator
	 * @param text      the text
	 * @param image     the image
	 * @param selection the selection
	 * @return the menu item
	 */
	private static MenuItem menuItem(final Menu parent, final int state, final Menu menu, final SelectionListener listener, final int acc, final String text, final String image,
			final boolean selection) {
		final var item = new MenuItem(parent, state);

		if (menu != null)
			item.setMenu(menu);

		if (listener != null)
			item.addSelectionListener(listener);

		if (acc > 0)
			item.setAccelerator(acc);

		if (image != null && ConfData.WIN32) {
			final var img = IMG.getImage(parent.getDisplay(), image);
			item.setImage(img);
			img.dispose();
		}

		if (selection)
			item.setSelection(selection);

		if (!isEmptyString(text))
			item.setText(text);

		return item;
	}

	/**
	 * Menu item.
	 *
	 * @param parent the parent
	 * @param state  the state
	 * @param menu   the menu
	 * @param text   the text
	 * @return the menu item
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final Menu menu, final String text) {
		return menuItem(parent, state, menu, null, 0, text, null, false);
	}

	/**
	 * Menu item.
	 *
	 * @param parent   the parent
	 * @param state    the state
	 * @param listener the listener
	 * @param acc      the accelerator
	 * @param text     the text
	 * @return the menu item
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final int acc, final String text) {
		return menuItem(parent, state, null, listener, acc, text, null, false);
	}

	/**
	 * Menu item.
	 *
	 * @param parent   the parent
	 * @param state    the state
	 * @param listener the listener
	 * @param acc      the accelerator
	 * @param text     the text
	 * @param image    the image
	 * @return the menu item
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final int acc, final String text, final String image) {
		return menuItem(parent, state, null, listener, acc, text, image, false);
	}

	/**
	 * Menu item.
	 *
	 * @param parent   the parent
	 * @param state    the state
	 * @param listener the listener
	 * @param text     the text
	 * @return the menu item
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final String text) {
		return menuItem(parent, state, null, listener, 0, text, null, false);
	}

	/**
	 * Menu item.
	 *
	 * @param parent    the parent
	 * @param state     the state
	 * @param listener  the listener
	 * @param text      the text
	 * @param selection the selection
	 * @return the menu item
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final String text, final boolean selection) {
		return menuItem(parent, state, null, listener, 0, text, null, selection);
	}

	/**
	 * Menu item.
	 *
	 * @param parent   the parent
	 * @param state    the state
	 * @param listener the listener
	 * @param text     the text
	 * @param image    the image
	 * @return the menu item
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final String text, final String image) {
		return menuItem(parent, state, null, listener, 0, text, image, false);
	}

	/**
	 * Menu item separator.
	 *
	 * @param parent the parent
	 * @return the menu item
	 */
	public static MenuItem menuItemSeparator(final Menu parent) {
		return new MenuItem(parent, SWT.SEPARATOR);
	}

	/**
	 * Msg.
	 *
	 * @param parent the parent
	 * @param style  the style
	 * @param title  the title
	 * @param msg    the msg
	 * @return the int
	 */
	public static int msg(final Shell parent, final int style, final String title, final String msg) {
		final var mb = new MessageBox(parent, style);
		mb.setMessage(msg);
		mb.setText(title);
		return mb.open();
	}

	/**
	 * New button.
	 *
	 * @param parent the parent
	 * @param select the select
	 * @param text   the text
	 * @return the button
	 */
	public static Button newButton(final Composite parent, final boolean select, final String text) {
		return newButton(parent, SWT.CHECK, select, null, text);
	}

	/**
	 * New button.
	 *
	 * @param parent   the parent
	 * @param style    the style
	 * @param select   the select
	 * @param listener the listener
	 * @param text     the text
	 * @return the button
	 */
	private static Button newButton(final Composite parent, final int style, final boolean select, final SelectionListener listener, final String text) {
		final var button = new Button(parent, style);
		button.setText(text);

		if (listener != null)
			button.addSelectionListener(listener);

		GridData data;

		if (style == SWT.CHECK) {
			button.setForeground(parent.getForeground());
			data = new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1);
		} else {
			button.setBackground(parent.getBackground());
			data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);
			data.widthHint = 80;
		}

		button.setLayoutData(data);

		if (select)
			button.setSelection(select);

		return button;
	}

	/**
	 * New button.
	 *
	 * @param parent   the parent
	 * @param style    the style
	 * @param listener the listener
	 * @param text     the text
	 * @return the button
	 */
	public static Button newButton(final Composite parent, final int style, final SelectionListener listener, final String text) {
		return newButton(parent, style, false, listener, text);
	}

	/**
	 * New label.
	 *
	 * @param parent the parent
	 * @param style  the style
	 * @param text   the text
	 * @return the label
	 */
	public static Label newLabel(final Composite parent, final int style, final String text) {
		final var label = new Label(parent, style);
		label.setForeground(parent.getForeground());

		if (style == SWT.HORIZONTAL + SWT.SEPARATOR)
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		else
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));

		if (!isEmptyString(text))
			label.setText(text);

		return label;
	}

	/**
	 * New menu.
	 *
	 * @param shell    the shell
	 * @param state    the state
	 * @param listener the listener
	 * @return the menu
	 */
	public static Menu newMenu(final Shell shell, final int state, final MenuListener listener) {
		final var menu = new Menu(shell, state);
		menu.addMenuListener(listener);
		return menu;
	}

	/**
	 * New text.
	 *
	 * @param parent the parent
	 * @param style  the style
	 * @return the text
	 */
	public static Text newText(final Composite parent, final int style) {
		final var text = new Text(parent, style);
		text.setForeground(parent.getForeground());

		if (style == SWT.SINGLE) {
			final var data = new GridData();
			data.exclude = true;
			text.setLayoutData(data);
			text.setVisible(false);
		} else if (style == SWT.BORDER + SWT.MULTI + SWT.V_SCROLL + SWT.WRAP)
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		else
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		return text;
	}

	/**
	 * Spinner.
	 *
	 * @param parent the parent
	 * @param sel    the selection
	 * @param min    the minimum
	 * @param max    the maximum
	 * @param dig    the digits
	 * @param inc    the increment
	 * @param pag    the pageIncrement
	 * @return the spinner
	 */
	public static Spinner spinner(final Composite parent, final int sel, final int min, final int max, final int dig, final int inc, final int pag) {
		final var spinner = new Spinner(parent, SWT.BORDER);
		spinner.setValues(sel, min, max, dig, inc, pag);
		spinner.pack();
		return spinner;
	}

	/**
	 * Tool item.
	 *
	 * @param toolBar  the tool bar
	 * @param image    the image
	 * @param listener the listener
	 * @param toolTip  the tooltip
	 * @return the tool item
	 */
	public static ToolItem toolItem(final ToolBar toolBar, final String image, final SelectionListener listener, final String toolTip) {
		final var display = toolBar.getDisplay();
		final var item = new ToolItem(toolBar, SWT.PUSH);
		final var img = IMG.getImage(display, image);
		item.addSelectionListener(listener);
		item.setImage(img);
		item.setDisabledImage(new Image(display, img, SWT.IMAGE_GRAY));
		item.setToolTipText(toolTip);
		return item;
	}

	/**
	 * Tool item separator.
	 *
	 * @param toolBar the tool bar
	 * @return the tool item
	 */
	public static ToolItem toolItemSeparator(final ToolBar toolBar) {
		return new ToolItem(toolBar, SWT.SEPARATOR);
	}

	private Widgets() {
	}
}
