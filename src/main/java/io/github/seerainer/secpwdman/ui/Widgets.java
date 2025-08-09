/*
 * SecPwdMan
 * Copyright (C) 2025  Philipp Seerainer
 * philipp@seerainer.com
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
package io.github.seerainer.secpwdman.ui;

import static io.github.seerainer.secpwdman.util.SWTUtil.DARK;
import static io.github.seerainer.secpwdman.util.SWTUtil.LINUX;
import static io.github.seerainer.secpwdman.util.SWTUtil.MACOS;
import static io.github.seerainer.secpwdman.util.SWTUtil.getColor;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.Util.isBlank;
import static java.util.Objects.nonNull;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

import io.github.seerainer.secpwdman.config.PrimitiveConstants;

/**
 * The class Widgets.
 */
public class Widgets implements PrimitiveConstants {

    private Widgets() {
    }

    static Button button(final Composite parent, final boolean select, final String text) {
	return button(parent, SWT.CHECK, select, null, text);
    }

    private static Button button(final Composite parent, final int style, final boolean select,
	    final SelectionListener listener, final String text) {
	final var button = new Button(parent, style);
	button.setText(text);
	setFont(button, parent);
	setForeground(button, parent);
	if (nonNull(listener)) {
	    button.addSelectionListener(listener);
	}
	final GridData data;
	if (style == SWT.CHECK) {
	    data = getGridData(SWT.LEAD, SWT.CENTER, 1, 0, 2, 1);
	} else if (style == SWT.RADIO) {
	    data = getGridData(SWT.CENTER, SWT.CENTER, 1, 0);
	} else {
	    data = getGridData(SWT.LEAD, SWT.CENTER, 0, 0);
	    data.widthHint = BUTTON_WIDTH;
	}
	setLayoutData(button, data);
	if (select) {
	    button.setSelection(true);
	}
	return button;
    }

    static Button button(final Composite parent, final int style, final String text, final SelectionListener listener) {
	return button(parent, style, false, listener, text);
    }

    static Combo combo(final Composite parent, final int style) {
	final var combo = new Combo(parent, style);
	setFont(combo, parent);
	if (DARK) {
	    setForeground(combo, parent);
	}
	return combo;
    }

    static CTabItem cTabItem(final CTabFolder parent, final int style, final String image, final String text) {
	final var item = new CTabItem(parent, style);
	item.setImage(getImage(parent.getDisplay(), image));
	item.setText(text);
	return item;
    }

    static Label emptyLabel(final Composite parent, final int hSpan) {
	final var label = new Label(parent, SWT.NONE);
	setLayoutData(label, getGridData(SWT.BEGINNING, SWT.CENTER, 0, 0, hSpan, 1));
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
    public static String fileDialog(final Shell parent, final int style, final String filterName,
	    final String filterExte) {
	final var dialog = new FileDialog(parent, style);
	dialog.setFilterNames(new String[] { filterName });
	dialog.setFilterExtensions(new String[] { filterExte });
	dialog.setOverwrite(true);
	return dialog.open();
    }

    static Group group(final Composite parent, final Layout layout, final String text) {
	final var group = new Group(parent, SWT.SHADOW_NONE);
	group.setText(text);
	setFont(group, parent);
	setLayout(group, layout);
	setLayoutData(group, getGridData(SWT.FILL, SWT.FILL, 0, 0, 2, 1));
	if (DARK) {
	    setForeground(group, parent);
	}
	return group;
    }

    static Label horizontalSeparator(final Composite parent) {
	final var label = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
	setLayoutData(label, getGridData(SWT.FILL, SWT.CENTER, 1, 0, 2, 1));
	return label;
    }

    static Label label(final Composite parent, final int style, final String text) {
	final var label = new Label(parent, style);
	label.setText(text);
	setFont(label, parent);
	setForeground(label, parent);
	setLayoutData(label, getGridData(SWT.BEGINNING, SWT.CENTER, 0, 0));
	return label;
    }

    static Link link(final Composite parent, final String url, final Color color, final String text) {
	final var link = new Link(parent, SWT.NONE);
	link.addSelectionListener(widgetSelectedAdapter(_ -> Program.launch(url)));
	link.setLinkForeground(color);
	link.setText(text);
	link.setToolTipText(url);
	setBackground(link, parent);
	setForeground(link, parent);
	setFont(link, parent);
	setLayoutData(link, getGridData(SWT.CENTER, SWT.CENTER, 1, 0));
	return link;
    }

    static Menu menu(final Shell parent, final int state, final MenuListener listener) {
	final var menu = new Menu(parent, state);
	menu.addMenuListener(listener);
	return menu;
    }

    private static MenuItem menuItem(final Menu parent, final int state, final Menu menu,
	    final SelectionListener listener, final int acc, final String text, final String image,
	    final boolean selection) {
	final var item = new MenuItem(parent, state);
	if (nonNull(menu)) {
	    item.setMenu(menu);
	}
	if (nonNull(listener)) {
	    item.addSelectionListener(listener);
	}
	if (acc > 0) {
	    item.setAccelerator(acc);
	}
	if (nonNull(image) && !LINUX) {
	    final var img = getImage(parent.getDisplay(), image);
	    item.setImage(img);
	    img.dispose();
	}
	if (selection) {
	    item.setSelection(true);
	}
	if (!isBlank(text)) {
	    item.setText(text);
	}
	return item;
    }

    static MenuItem menuItem(final Menu parent, final int state, final Menu menu, final String text) {
	return menuItem(parent, state, menu, null, 0, text, null, false);
    }

    static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final int acc,
	    final String text) {
	return menuItem(parent, state, null, listener, acc, text, null, false);
    }

    static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final int acc,
	    final String text, final String image) {
	return menuItem(parent, state, null, listener, acc, text, image, false);
    }

    static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final String text) {
	return menuItem(parent, state, null, listener, 0, text, null, false);
    }

    static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final String text,
	    final boolean selection) {
	return menuItem(parent, state, null, listener, 0, text, null, selection);
    }

    static MenuItem menuItem(final Menu parent, final int state, final SelectionListener listener, final String text,
	    final String image) {
	return menuItem(parent, state, null, listener, 0, text, image, false);
    }

    static MenuItem menuItemSeparator(final Menu parent) {
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

    private static void setBackground(final Control control, final Composite parent) {
	control.setBackground(parent.getBackground());
    }

    private static void setFont(final Control control, final Composite parent) {
	control.setFont(parent.getFont());
    }

    private static void setForeground(final Control control, final Composite parent) {
	control.setForeground(parent.getForeground());
    }

    private static void setLayout(final Composite composite, final Layout layout) {
	composite.setLayout(layout);
    }

    private static void setLayoutData(final Control control, final GridData data) {
	control.setLayoutData(data);
    }

    static Shell shell(final Shell parent, final int style, final Image image, final Layout layout, final String text) {
	final var shell = new Shell(parent, style);
	shell.addDisposeListener(_ -> shell.dispose());
	setFont(shell, parent);
	setLayout(shell, layout);
	if (DARK && !MACOS) {
	    setBackground(shell, parent);
	    setForeground(shell, parent);
	    shell.setBackgroundMode(SWT.INHERIT_FORCE);
	}
	if (nonNull(image)) {
	    shell.setImage(image);
	}
	if (!isBlank(text)) {
	    shell.setText(text);
	}
	return shell;
    }

    static Shell shell(final Shell parent, final int style, final Layout layout, final String text) {
	return shell(parent, style, null, layout, text);
    }

    static Spinner spinner(final Composite parent, final int sel, final int min, final int max, final int dig,
	    final int inc, final int pag) {
	final var spinner = new Spinner(parent, SWT.BORDER);
	setFont(spinner, parent);
	setForeground(spinner, parent);
	spinner.setValues(sel, min, max, dig, inc, pag);
	spinner.pack();
	return spinner;
    }

    static Table table(final Composite parent) {
	final var table = new Table(parent, SWT.FULL_SELECTION | SWT.MULTI);
	table.setFocus();
	table.setLinesVisible(true);
	setFont(table, parent);
	setLayoutData(table, getGridData(SWT.FILL, SWT.FILL, 1, 1));
	if (DARK && !MACOS) {
	    setBackground(table, parent);
	    setForeground(table, parent);
	    table.setHeaderBackground(getColor(HEAD_BACK, HEAD_BACK, HEAD_BACK));
	    table.setHeaderForeground(getColor(HEAD_FORE, HEAD_FORE, HEAD_FORE));
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
	setFont(text, parent);
	setForeground(text, parent);
	if (style == SWT.SINGLE) {
	    final var data = new GridData();
	    data.exclude = true;
	    setLayoutData(text, data);
	    text.setVisible(false);
	} else if (style == SWT.BORDER + SWT.MULTI + SWT.V_SCROLL + SWT.WRAP) {
	    setLayoutData(text, getGridData(SWT.FILL, SWT.FILL, 1, 1, 2, 1));
	} else {
	    setLayoutData(text, getGridData(SWT.FILL, SWT.CENTER, 1, 0, 2, 1));
	}
	return text;
    }

    static ToolItem toolItem(final ToolBar toolBar, final String image, final SelectionListener listener,
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

    static ToolItem toolItemSeparator(final ToolBar toolBar) {
	return new ToolItem(toolBar, SWT.SEPARATOR);
    }
}
