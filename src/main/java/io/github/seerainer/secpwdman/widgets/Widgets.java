/*
 * Secure Password Manager
 * Copyright (C) 2025  Philipp Seerainer
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
package io.github.seerainer.secpwdman.widgets;

import static io.github.seerainer.secpwdman.util.SWTUtil.DARK;
import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * The class Widgets.
 */
public final class Widgets {

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#button(Composite, int,
	 *      boolean, SelectionListener, String)
	 */
	public static Button button(final Composite parent, final boolean select, final String text) {
		return button(parent, SWT.CHECK, select, null, text);
	}

	/**
	 * Button.
	 *
	 * @param parent   the parent
	 * @param style    the style
	 * @param select   the select
	 * @param listener the listener
	 * @param text     the text
	 * @return the button
	 */
	private static Button button(final Composite parent, final int style, final boolean select, final SelectionListener listener,
			final String text) {
		final var button = new Button(parent, style);
		button.setFont(parent.getFont());
		button.setForeground(parent.getForeground());
		button.setText(text);
		if (listener != null) {
			button.addSelectionListener(listener);
		}
		final GridData data;
		if (style == SWT.CHECK) {
			data = new GridData(SWT.LEAD, SWT.CENTER, true, false, 2, 1);
		} else if (style == SWT.RADIO) {
			data = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		} else {
			data = new GridData(SWT.LEAD, SWT.CENTER, false, false);
			data.widthHint = 80;
		}
		button.setLayoutData(data);
		if (select) {
			button.setSelection(select);
		}
		return button;
	}

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#button(Composite, int,
	 *      boolean, SelectionListener, String)
	 */
	public static Button button(final Composite parent, final int style, final String text, final SelectionListener listener) {
		return button(parent, style, false, listener, text);
	}

	/**
	 * Combo.
	 *
	 * @param parent the parent
	 * @param style  the style
	 * @return the combo
	 */
	public static Combo combo(final Composite parent, final int style) {
		final var combo = new Combo(parent, style);
		combo.setFont(parent.getFont());
		if (DARK) {
			combo.setForeground(parent.getForeground());
		}
		return combo;
	}

	/**
	 * Empty label.
	 *
	 * @param parent the parent
	 * @param hSpan  the horizontalSpan
	 * @return the label
	 */
	public static Label emptyLabel(final Composite parent, final int hSpan) {
		final var label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, hSpan, 1));
		return label;
	}

	/**
	 * File dialog.
	 *
	 * @param parent     the parent
	 * @param style      the style
	 * @param filterName the filter names
	 * @param filterExte the filter extensions
	 * @return path of the first selected file or null
	 */
	public static String fileDialog(final Shell parent, final int style, final String filterName, final String filterExte) {
		final var dialog = new FileDialog(parent, style);
		dialog.setFilterNames(new String[] { filterName });
		dialog.setFilterExtensions(new String[] { filterExte });
		dialog.setOverwrite(true);
		return dialog.open();
	}

	/**
	 * Group.
	 *
	 * @param parent the parent
	 * @param layout the layout
	 * @param text   the text
	 * @return the group
	 */
	public static Group group(final Composite parent, final Layout layout, final String text) {
		final var group = new Group(parent, SWT.SHADOW_NONE);
		group.setFont(parent.getFont());
		group.setLayout(layout);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		group.setText(text);
		if (DARK) {
			group.setForeground(parent.getForeground());
		}
		return group;
	}

	/**
	 * Horizontal separator.
	 *
	 * @param parent the parent
	 * @return the label
	 */
	public static Label horizontalSeparator(final Composite parent) {
		return label(parent, SWT.HORIZONTAL | SWT.SEPARATOR, null);
	}

	/**
	 * Label.
	 *
	 * @param parent the parent
	 * @param style  the style
	 * @param text   the text
	 * @return the label
	 */
	public static Label label(final Composite parent, final int style, final String text) {
		final var label = new Label(parent, style);
		label.setFont(parent.getFont());
		label.setForeground(parent.getForeground());
		label.setLayoutData(style == SWT.HORIZONTAL + SWT.SEPARATOR ? new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1)
				: new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		if (!isBlank(text)) {
			label.setText(text);
		}
		return label;
	}

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#link(Composite, String,
	 *      Color, String, String)
	 */
	public static Link link(final Composite parent, final String url, final Color color, final String text) {
		return link(parent, url, color, text, null);
	}

	/**
	 * Link.
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
		link.setForeground(parent.getForeground());
		link.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		link.setLinkForeground(color);
		link.setText(text);
		link.setToolTipText(url);
		link.setFont(isBlank(font) ? parent.getFont() : new Font(parent.getDisplay(), new FontData(font, 12, SWT.BOLD)));
		return link;
	}

	/**
	 * Menu.
	 *
	 * @param parent   the parent
	 * @param state    the state
	 * @param listener the listener
	 * @return the menu
	 */
	public static Menu menu(final Shell parent, final int state, final MenuListener listener) {
		final var menu = new Menu(parent, state);
		menu.addMenuListener(listener);
		return menu;
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
	private static MenuItem menuItem(final Menu parent, final int state, final Menu menu, final SelectionListener listener,
			final int acc, final String text, final String image, final boolean selection) {
		final var item = new MenuItem(parent, state);
		if (menu != null) {
			item.setMenu(menu);
		}
		if (listener != null) {
			item.addSelectionListener(listener);
		}
		if (acc > 0) {
			item.setAccelerator(acc);
		}
		if (image != null && WIN32) {
			final var img = getImage(parent.getDisplay(), image);
			item.setImage(img);
			img.dispose();
		}
		if (selection) {
			item.setSelection(selection);
		}
		if (!isBlank(text)) {
			item.setText(text);
		}
		return item;
	}

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#menuItem(Menu, int, Menu,
	 *      SelectionListener, int, String, String, boolean)
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final Menu menu, final String text) {
		return menuItem(parent, state, menu, null, 0, text, null, false);
	}

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#menuItem(Menu, int, Menu,
	 *      SelectionListener, int, String, String, boolean)
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final int acc,
			final String text) {
		return menuItem(parent, state, null, listener, acc, text, null, false);
	}

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#menuItem(Menu, int, Menu,
	 *      SelectionListener, int, String, String, boolean)
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final int acc,
			final String text, final String image) {
		return menuItem(parent, state, null, listener, acc, text, image, false);
	}

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#menuItem(Menu, int, Menu,
	 *      SelectionListener, int, String, String, boolean)
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final String text) {
		return menuItem(parent, state, null, listener, 0, text, null, false);
	}

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#menuItem(Menu, int, Menu,
	 *      SelectionListener, int, String, String, boolean)
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final String text,
			final boolean selection) {
		return menuItem(parent, state, null, listener, 0, text, null, selection);
	}

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#menuItem(Menu, int, Menu,
	 *      SelectionListener, int, String, String, boolean)
	 */
	public static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final String text,
			final String image) {
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
	 * MessageBox.
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
	 * Shell.
	 *
	 * @param parent the parent
	 * @param style  the style
	 * @param image  the image
	 * @param layout the layout
	 * @param text   the text
	 * @return the shell
	 */
	public static Shell shell(final Shell parent, final int style, final Image image, final Layout layout, final String text) {
		final var shell = new Shell(parent, style);
		shell.setFont(parent.getFont());
		shell.setLayout(layout);
		if (DARK) {
			shell.setBackground(parent.getBackground());
			shell.setForeground(parent.getForeground());
			shell.setBackgroundMode(SWT.INHERIT_FORCE);
		}
		if (image != null) {
			shell.setImage(image);
		}
		if (!isBlank(text)) {
			shell.setText(text);
		}
		return shell;
	}

	/**
	 * @see io.github.seerainer.secpwdman.widgets.Widgets#shell(Shell, int, Image,
	 *      Layout, String)
	 */
	public static Shell shell(final Shell parent, final int style, final Layout layout, final String text) {
		return shell(parent, style, null, layout, text);
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
	public static Spinner spinner(final Composite parent, final int sel, final int min, final int max, final int dig,
			final int inc, final int pag) {
		final var spinner = new Spinner(parent, SWT.BORDER);
		spinner.setFont(parent.getFont());
		spinner.setForeground(parent.getForeground());
		spinner.setValues(sel, min, max, dig, inc, pag);
		spinner.pack();
		return spinner;
	}

	/**
	 * Table.
	 *
	 * @param parent the parent
	 * @return the table
	 */
	public static Table table(final Composite parent) {
		final var table = new Table(parent, SWT.FULL_SELECTION | SWT.MULTI);
		table.setFocus();
		table.setFont(parent.getFont());
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible(true);
		if (DARK) {
			table.setBackground(parent.getBackground());
			table.setForeground(parent.getForeground());
			table.setHeaderBackground(new Color(0x48, 0x48, 0x48));
			table.setHeaderForeground(new Color(0xDD, 0xDD, 0xDD));
		}
		return table;
	}

	/**
	 * Text.
	 *
	 * @param parent the parent
	 * @param style  the style
	 * @return the text
	 */
	public static Text text(final Composite parent, final int style) {
		final var text = new Text(parent, style);
		text.setFont(parent.getFont());
		text.setForeground(parent.getForeground());
		if (style == SWT.SINGLE) {
			final var data = new GridData();
			data.exclude = true;
			text.setLayoutData(data);
			text.setVisible(false);
		} else if (style == SWT.BORDER + SWT.MULTI + SWT.V_SCROLL + SWT.WRAP) {
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		} else {
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}
		return text;
	}

	/**
	 * Tool item.
	 *
	 * @param toolBar  the toolbar
	 * @param image    the image
	 * @param listener the listener
	 * @param toolTip  the tooltip
	 * @return the tool item
	 */
	public static ToolItem toolItem(final ToolBar toolBar, final String image, final SelectionListener listener,
			final String toolTip) {
		final var display = toolBar.getDisplay();
		final var item = new ToolItem(toolBar, SWT.PUSH);
		final var img = getImage(display, image);
		item.addSelectionListener(listener);
		item.setImage(img);
		item.setDisabledImage(new Image(display, img, SWT.IMAGE_GRAY));
		item.setToolTipText(toolTip);
		return item;
	}

	/**
	 * Tool item separator.
	 *
	 * @param toolBar the toolbar
	 * @return the tool item
	 */
	public static ToolItem toolItemSeparator(final ToolBar toolBar) {
		return new ToolItem(toolBar, SWT.SEPARATOR);
	}

	private Widgets() {
	}
}
