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
package io.github.seerainer.secpwdman;

import static io.github.seerainer.secpwdman.util.SWTUtil.DARK;
import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.Util.isEmpty;
import static io.github.seerainer.secpwdman.util.Util.isFileOpen;
import static io.github.seerainer.secpwdman.util.Util.isReadable;
import static io.github.seerainer.secpwdman.widgets.Widgets.menuItem;
import static io.github.seerainer.secpwdman.widgets.Widgets.menuItemSeparator;
import static io.github.seerainer.secpwdman.widgets.Widgets.msg;
import static io.github.seerainer.secpwdman.widgets.Widgets.newMenu;
import static io.github.seerainer.secpwdman.widgets.Widgets.newTable;
import static io.github.seerainer.secpwdman.widgets.Widgets.toolItem;
import static io.github.seerainer.secpwdman.widgets.Widgets.toolItemSeparator;
import static org.eclipse.swt.events.KeyListener.keyPressedAdapter;
import static org.eclipse.swt.events.MenuListener.menuShownAdapter;
import static org.eclipse.swt.events.MouseListener.mouseDoubleClickAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
import static org.eclipse.swt.events.ShellListener.shellActivatedAdapter;
import static org.eclipse.swt.events.ShellListener.shellClosedAdapter;
import static org.eclipse.swt.events.ShellListener.shellDeiconifiedAdapter;
import static org.eclipse.swt.events.ShellListener.shellIconifiedAdapter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TrayItem;

import io.github.seerainer.secpwdman.action.EditAction;
import io.github.seerainer.secpwdman.action.FileAction;
import io.github.seerainer.secpwdman.action.ViewAction;
import io.github.seerainer.secpwdman.config.ConfData;
import io.github.seerainer.secpwdman.dialog.ConfigDialog;
import io.github.seerainer.secpwdman.dialog.EntryDialog;
import io.github.seerainer.secpwdman.dialog.InfoDialog;
import io.github.seerainer.secpwdman.dialog.PasswordDialog;
import io.github.seerainer.secpwdman.dialog.SearchDialog;
import io.github.seerainer.secpwdman.dialog.SystemInfoDialog;
import io.github.seerainer.secpwdman.dialog.TextDialog;
import io.github.seerainer.secpwdman.images.IMG;
import io.github.seerainer.secpwdman.io.IO;

/**
 * Secure Password Manager
 *
 * @author <a href="mailto:philipp@seerainer.com">Philipp Seerainer</a>
 */
public final class SecPwdMan {

	/**
	 * Create the display.
	 *
	 * @return the display
	 */
	private static Display getDisplay() {
		Display.setAppName(ConfData.APP_NAME);
		Display.setAppVersion(ConfData.APP_VERS);

		System.setProperty("org.eclipse.swt.display.useSystemTheme", "true"); //$NON-NLS-1$ //$NON-NLS-2$

		final var display = Display.getDefault();

		if (DARK && WIN32) {
			display.setData("org.eclipse.swt.internal.win32.useDarkModeExplorerTheme", Boolean.TRUE); //$NON-NLS-1$
			display.setData("org.eclipse.swt.internal.win32.useShellTitleColoring", Boolean.TRUE); //$NON-NLS-1$
			display.setData("org.eclipse.swt.internal.win32.menuBarBackgroundColor", new Color(0x32, 0x32, 0x32)); //$NON-NLS-1$
			display.setData("org.eclipse.swt.internal.win32.menuBarForegroundColor", new Color(0xF8, 0xF8, 0xF8)); //$NON-NLS-1$
			display.setData("org.eclipse.swt.internal.win32.all.use_WS_BORDER", Boolean.TRUE); //$NON-NLS-1$
		}

		return display;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {
		final var display = getDisplay();
		final var shell = new SecPwdMan(args).open(display);

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
	}

	private final ConfData cData = new ConfData();

	private Shell shell;
	private Table table;
	private EditAction editAction;
	private FileAction fileAction;
	private ViewAction viewAction;

	private final MenuListener enableItems = menuShownAdapter(e -> {
		editAction.enableItems();
	});

	private final SelectionListener openFile = widgetSelectedAdapter(e -> {
		fileAction.openDialog();
	});

	private final SelectionListener saveFile = widgetSelectedAdapter(e -> {
		cData.setClearAfterSave(false);
		cData.setExitAfterSave(false);
		fileAction.saveDialog();
	});

	private final SelectionListener lockFile = widgetSelectedAdapter(e -> {
		fileAction.lockSwitch();
	});

	private final SelectionListener newEntry = widgetSelectedAdapter(e -> {
		new EntryDialog(editAction).open(-1);
	});

	private final SelectionListener editEntry = widgetSelectedAdapter(e -> {
		new EntryDialog(editAction).open(table.getSelectionIndex());
	});

	private final SelectionListener openSearch = widgetSelectedAdapter(e -> {
		new SearchDialog(fileAction).open();
	});

	private final SelectionListener selectAll = widgetSelectedAdapter(e -> {
		table.selectAll();
		table.setFocus();
		editAction.enableItems();
	});

	private final SelectionListener deleteLine = widgetSelectedAdapter(e -> {
		editAction.deleteLine();
	});

	private final SelectionListener copyURL = widgetSelectedAdapter(e -> {
		editAction.copyToClipboard(cData.getColumnMap().get(cData.csvHeader[3]).intValue());
	});

	private final SelectionListener copyName = widgetSelectedAdapter(e -> {
		editAction.copyToClipboard(cData.getColumnMap().get(cData.csvHeader[4]).intValue());
	});

	private final SelectionListener copyPass = widgetSelectedAdapter(e -> {
		editAction.copyToClipboard(cData.getColumnMap().get(cData.csvHeader[5]).intValue());
	});

	private final SelectionListener copyNotes = widgetSelectedAdapter(e -> {
		editAction.copyToClipboard(cData.getColumnMap().get(cData.csvHeader[6]).intValue());
	});

	private final SelectionListener openURL = widgetSelectedAdapter(e -> {
		final var index = cData.getColumnMap().get(cData.csvHeader[3]).intValue();
		Program.launch(table.getSelection()[0].getText(index));
	});

	private final ShellListener activated = shellActivatedAdapter(e -> {
		if (isFileOpen(cData.getFile()) && !cData.isModified())
			new PasswordDialog(fileAction).open(false);
		shell.removeListener(SWT.Activate, shell.getListeners(SWT.Activate)[3]);
	});

	private final ShellListener deiconified = shellDeiconifiedAdapter(e -> {
		final var tray = shell.getDisplay().getSystemTray();
		if (tray != null && WIN32)
			tray.getItem(0).setVisible(false);
		shell.addShellListener(activated);
	});

	private SecPwdMan() {
	}

	/**
	 * Instantiates SecPwdMan.
	 *
	 * @param args the args
	 */
	private SecPwdMan(final String[] args) {
		if (args.length > 0)
			cData.setFile(args[0]);
	}

	/**
	 * Menu bar.
	 *
	 * @return the menu
	 */
	private Menu menuBar() {
		final var menuBar = new Menu(shell, SWT.BAR);
		final var file = newMenu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, file, cData.menuFile);
		menuItem(file, SWT.PUSH, widgetSelectedAdapter(e -> fileAction.newDatabase()), SWT.CTRL + 'N', cData.menuClea);
		menuItem(file, SWT.PUSH, openFile, SWT.CTRL + 'O', cData.menuOpen, IMG.OPEN);
		menuItem(file, SWT.PUSH, saveFile, SWT.CTRL + 'S', cData.menuSave, IMG.SAVE);
		menuItemSeparator(file);
		menuItem(file, SWT.PUSH, lockFile, SWT.CTRL + 'L', cData.menuLock, IMG.LOCK);
		menuItemSeparator(file);
		menuItem(file, SWT.PUSH, widgetSelectedAdapter(e -> fileAction.importDialog()), cData.menuImpo);
		menuItem(file, SWT.PUSH, widgetSelectedAdapter(e -> fileAction.exportDialog()), cData.menuExpo, IMG.WARN);
		menuItemSeparator(file);
		menuItem(file, SWT.PUSH, widgetSelectedAdapter(e -> shell.close()), SWT.ESC, cData.menuExit, IMG.EXIT);

		final var edit = newMenu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, edit, cData.menuEdit);
		menuItem(edit, SWT.PUSH, newEntry, SWT.INSERT, cData.menuNent, IMG.NEW);
		menuItem(edit, SWT.PUSH, editEntry, SWT.CR, cData.menuEent, IMG.EDIT);
		menuItemSeparator(edit);
		menuItem(edit, SWT.PUSH, selectAll, SWT.CTRL + 'A', cData.menuSela, IMG.SELA);
		menuItem(edit, SWT.PUSH, deleteLine, SWT.DEL, cData.menuDels, IMG.DEL);
		menuItemSeparator(edit);
		menuItem(edit, SWT.PUSH, copyURL, SWT.CTRL + 'R', cData.menuCurl, IMG.LINK);
		menuItem(edit, SWT.PUSH, copyName, SWT.CTRL + 'U', cData.menuCusr, IMG.USER);
		menuItem(edit, SWT.PUSH, copyPass, SWT.CTRL + 'P', cData.menuCpwd, IMG.KEY);
		menuItem(edit, SWT.PUSH, copyNotes, SWT.CTRL + 'K', cData.menuCnot, IMG.NOTE);
		menuItemSeparator(edit);
		menuItem(edit, SWT.PUSH, openURL, SWT.CTRL + 'D', cData.menuOurl, IMG.WEB);
		menuItemSeparator(edit);
		menuItem(edit, SWT.PUSH, widgetSelectedAdapter(e -> editAction.clearClipboard()), SWT.CTRL + 'Z', cData.menuClCb);

		final var search = newMenu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, search, cData.menuSear);
		menuItem(search, SWT.PUSH, openSearch, SWT.CTRL + 'F', cData.menuFind, IMG.SEARCH);

		final var view = newMenu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, view, cData.menuView);
		menuItem(view, SWT.CHECK, widgetSelectedAdapter(e -> viewAction.readOnlySwitch()), cData.menuReaO);
		menuItemSeparator(view);
		menuItem(view, SWT.CHECK, widgetSelectedAdapter(e -> viewAction.openGroupList()), cData.menuGrou);
		menuItemSeparator(view);
		menuItem(view, SWT.CHECK, widgetSelectedAdapter(e -> viewAction.resizeColumns()), cData.menuPcol);
		menuItemSeparator(view);
		menuItem(view, SWT.RADIO, widgetSelectedAdapter(e -> viewAction.showPasswordColumn(e)), cData.menuSpwd);
		menuItem(view, SWT.RADIO, widgetSelectedAdapter(e -> viewAction.showPasswordColumn(e)), cData.menuHpwd, true);
		menuItemSeparator(view);
		menuItem(view, SWT.PUSH, widgetSelectedAdapter(e -> viewAction.changeFont()), cData.menuFont);
		menuItemSeparator(view);
		menuItem(view, SWT.PUSH, widgetSelectedAdapter(e -> new TextDialog(viewAction)), cData.menuText);
		menuItemSeparator(view);
		menuItem(view, SWT.PUSH, widgetSelectedAdapter(e -> new ConfigDialog(viewAction)), cData.menuPref, IMG.GEAR);

		final var info = newMenu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, info, cData.menuInfo);
		menuItem(info, SWT.PUSH, widgetSelectedAdapter(e -> new SystemInfoDialog(fileAction)), cData.menuSysI, IMG.SYSTEM);
		menuItemSeparator(info);
		menuItem(info, SWT.PUSH, widgetSelectedAdapter(e -> new InfoDialog(fileAction)), cData.menuAbou, IMG.INFO);

		return menuBar;
	}

	/**
	 * Open.
	 *
	 * @param display the display
	 * @return the shell
	 */
	private Shell open(final Display display) {
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.addShellListener(shellClosedAdapter(e -> e.doit = fileAction.exit()));
		shell.addShellListener(deiconified);
		shell.addShellListener(shellIconifiedAdapter(e -> fileAction.setLocked()));

		var height = 700;
		final var image = getImage(display, IMG.APP_ICON);
		final var layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;

		if (WIN32) {
			layout.marginTop = -2;
			height = display.getBounds().height - 40;
			trayItem(display, image);
			shell.setLocation(-7, 0);
		}

		shell.setImage(image);
		shell.setLayout(layout);
		shell.setMenuBar(menuBar());

		shellColor(display, toolBar());
		shellArea();

		editAction = new EditAction(cData, shell, table);
		fileAction = new FileAction(cData, shell, table);
		viewAction = new ViewAction(cData, shell, table);
		viewAction.defaultHeader();

		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, height);
		shell.open();
		shell.forceActive();

		openFileArg();

		fileAction.enableItems();
		fileAction.setText();

		return shell;
	}

	/**
	 * Open file arg.
	 */
	private void openFileArg() {
		final var file = cData.getFile();

		if (!isEmpty(file)) {
			final var aes = cData.passExte.substring(1, 5);
			final var jsn = cData.passExte.substring(8);

			if (isReadable(file) && (file.endsWith(aes) || file.endsWith(jsn))) {
				new PasswordDialog(fileAction).open(false);
				cData.setLocked(true);
			} else {
				final var csv = cData.imexExte.substring(1, 5);
				final var txt = cData.imexExte.substring(8);

				if (isReadable(file) && (file.endsWith(csv) || file.endsWith(txt))) {
					if (new IO(fileAction).openFile(null, file))
						cData.setModified(true);
				} else
					msg(shell, SWT.ICON_ERROR | SWT.OK, cData.titleErr, cData.errorFil + file);

				cData.setFile(null);
			}
		}
	}

	/**
	 * SashForm with list & table.
	 */
	private void shellArea() {
		final var layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		final var foreground = shell.getForeground();
		final var form = new SashForm(shell, SWT.HORIZONTAL);
		form.setForeground(foreground);
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		form.setLayout(layout);

		final var list = new List(form, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		list.addSelectionListener(widgetSelectedAdapter(e -> fileAction.setGroupSelection()));
		list.setForeground(foreground);
		list.setVisible(false);

		table = newTable(form);
		table.addKeyListener(keyPressedAdapter(e -> fileAction.enableItems()));
		table.addMouseListener(mouseDoubleClickAdapter(e -> new EntryDialog(editAction).open(table.getSelectionIndex())));
		table.addSelectionListener(widgetSelectedAdapter(e -> fileAction.enableItems()));
		table.setHeaderVisible(true);
		table.setMenu(tableMenu());

		form.setWeights(16, 84);
	}

	/**
	 * Shell color.
	 *
	 * @param display the display
	 * @param toolbar the tool bar
	 */
	private void shellColor(final Display display, final ToolBar toolBar) {
		if (DARK) {
			final var darkForeground = new Color(0xEE, 0xEE, 0xEE);
			toolBar.setBackground(new Color(0x64, 0x64, 0x64));
			toolBar.setForeground(darkForeground);
			cData.setLinkColor(new Color(0x0, 0xBB, 0xFF));
			cData.setTextColor(darkForeground);
			shell.setBackground(new Color(0x32, 0x32, 0x32));
			shell.setForeground(darkForeground);
			shell.setBackgroundMode(SWT.INHERIT_FORCE);
		} else {
			cData.setLinkColor(display.getSystemColor(SWT.COLOR_LINK_FOREGROUND));
			cData.setTextColor(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
		}
	}

	/**
	 * Table menu.
	 *
	 * @return the menu
	 */
	private Menu tableMenu() {
		final var menu = newMenu(shell, SWT.POP_UP, menuShownAdapter(e -> {
			final var item = ((Menu) e.widget);
			final var editMenu = shell.getMenuBar().getItem(1).getMenu();
			item.getItem(0).setEnabled(editMenu.getItem(11).getEnabled());
			item.getItem(2).setEnabled(editMenu.getItem(6).getEnabled());
			item.getItem(3).setEnabled(editMenu.getItem(7).getEnabled());
			item.getItem(4).setEnabled(editMenu.getItem(8).getEnabled());
			item.getItem(5).setEnabled(editMenu.getItem(9).getEnabled());
			item.getItem(7).setEnabled(editMenu.getItem(0).getEnabled());
			item.getItem(8).setEnabled(editMenu.getItem(1).getEnabled());
			item.getItem(10).setEnabled(editMenu.getItem(3).getEnabled());
			item.getItem(11).setEnabled(editMenu.getItem(4).getEnabled());
		}));

		menuItem(menu, SWT.PUSH, openURL, cData.menuOurl, IMG.WEB);
		menuItemSeparator(menu);
		menuItem(menu, SWT.PUSH, copyURL, cData.menuCurl, IMG.LINK);
		menuItem(menu, SWT.PUSH, copyName, cData.menuCusr, IMG.USER);
		menuItem(menu, SWT.PUSH, copyPass, cData.menuCpwd, IMG.KEY);
		menuItem(menu, SWT.PUSH, copyNotes, cData.menuCnot, IMG.NOTE);
		menuItemSeparator(menu);
		menuItem(menu, SWT.PUSH, newEntry, cData.menuNent, IMG.NEW);
		menuItem(menu, SWT.PUSH, editEntry, cData.menuEent, IMG.EDIT);
		menuItemSeparator(menu);
		menuItem(menu, SWT.PUSH, selectAll, cData.menuSela, IMG.SELA);
		menuItem(menu, SWT.PUSH, deleteLine, cData.menuDels, IMG.DEL);

		return menu;
	}

	/**
	 * Tool bar.
	 *
	 * @return the toolbar
	 */
	private ToolBar toolBar() {
		final var toolBar = new ToolBar(shell, SWT.FLAT | SWT.SHADOW_OUT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		toolItem(toolBar, IMG.OPEN, openFile, cData.menuOpen);
		toolItem(toolBar, IMG.SAVE, saveFile, cData.menuSave);
		toolItemSeparator(toolBar);
		toolItem(toolBar, IMG.LOCK, lockFile, cData.menuLock);
		toolItemSeparator(toolBar);
		toolItem(toolBar, IMG.NEW, newEntry, cData.menuNent);
		toolItem(toolBar, IMG.EDIT, editEntry, cData.menuEent);
		toolItemSeparator(toolBar);
		toolItem(toolBar, IMG.SEARCH, openSearch, cData.menuSear);
		toolItemSeparator(toolBar);
		toolItem(toolBar, IMG.LINK, copyURL, cData.menuCurl);
		toolItem(toolBar, IMG.USER, copyName, cData.menuCusr);
		toolItem(toolBar, IMG.KEY, copyPass, cData.menuCpwd);
		toolItem(toolBar, IMG.NOTE, copyNotes, cData.menuCnot);
		toolItemSeparator(toolBar);
		toolItem(toolBar, IMG.WEB, openURL, cData.menuOurl);

		return toolBar;
	}

	/**
	 * Tray item.
	 *
	 * @param display the display
	 * @param image   the image
	 */
	private void trayItem(final Display display, final Image image) {
		final var tray = display.getSystemTray();

		if (tray != null) {
			final var trayItem = new TrayItem(tray, SWT.NONE);
			trayItem.addListener(SWT.DefaultSelection, e -> {
				shell.setVisible(true);
				shell.setMinimized(false);
			});
			trayItem.setImage(image);
			trayItem.setVisible(false);
		}
	}
}
