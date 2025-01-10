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
package io.github.seerainer.secpwdman.gui;

import static io.github.seerainer.secpwdman.crypto.CryptoUtil.selfTest;
import static io.github.seerainer.secpwdman.dialog.DialogFactory.createEntryDialog;
import static io.github.seerainer.secpwdman.dialog.DialogFactory.createPasswordDialog;
import static io.github.seerainer.secpwdman.util.SWTUtil.DARK;
import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.getPrefSize;
import static io.github.seerainer.secpwdman.util.Util.isFileReady;
import static io.github.seerainer.secpwdman.widgets.Widgets.menu;
import static io.github.seerainer.secpwdman.widgets.Widgets.menuItem;
import static io.github.seerainer.secpwdman.widgets.Widgets.menuItemSeparator;
import static io.github.seerainer.secpwdman.widgets.Widgets.table;
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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Control;
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
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.Icons;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.CryptoConstants;
import io.github.seerainer.secpwdman.dialog.DialogFactory;
import io.github.seerainer.secpwdman.io.IO;

/**
 * The class MainWindow.
 */
public final class MainWindow implements CryptoConstants, Icons, PrimitiveConstants, StringConstants {

	private final ConfigData cData = new ConfigData();

	private List list;
	private Shell shell;
	private Table table;
	private EditAction editAction;
	private FileAction fileAction;
	private ViewAction viewAction;

	private final MenuListener enableItems = menuShownAdapter(e -> editAction.enableItems());
	private final SelectionListener openFile = widgetSelectedAdapter(e -> fileAction.openDialog());
	private final SelectionListener saveFile = widgetSelectedAdapter(e -> {
		cData.setClearAfterSave(false);
		cData.setExitAfterSave(false);
		fileAction.saveDialog();
	});
	private final SelectionListener lockFile = widgetSelectedAdapter(e -> fileAction.lockSwitch());
	private final SelectionListener newEntry = widgetSelectedAdapter(e -> createEntryDialog(editAction, -1));
	private final SelectionListener editEntry = widgetSelectedAdapter(
			e -> createEntryDialog(editAction, table.getSelectionIndex()));
	private final SelectionListener openSearch = widgetSelectedAdapter(e -> DialogFactory.createSearchDialog(fileAction));
	private final SelectionListener selectAll = widgetSelectedAdapter(e -> {
		table.selectAll();
		table.setFocus();
		editAction.enableItems();
	});
	private final SelectionListener deleteLine = widgetSelectedAdapter(e -> editAction.deleteLine());
	private final SelectionListener copyURL = widgetSelectedAdapter(
			e -> editAction.copyToClipboard(cData.getColumnMap().get(csvHeader[3]).intValue()));
	private final SelectionListener copyName = widgetSelectedAdapter(
			e -> editAction.copyToClipboard(cData.getColumnMap().get(csvHeader[4]).intValue()));
	private final SelectionListener copyPass = widgetSelectedAdapter(
			e -> editAction.copyToClipboard(cData.getColumnMap().get(csvHeader[5]).intValue()));
	private final SelectionListener copyNotes = widgetSelectedAdapter(
			e -> editAction.copyToClipboard(cData.getColumnMap().get(csvHeader[6]).intValue()));
	private final SelectionListener openURL = widgetSelectedAdapter(e -> {
		final var index = cData.getColumnMap().get(csvHeader[3]).intValue();
		Program.launch(table.getSelection()[0].getText(index));
	});

	private final ShellListener activated = shellActivatedAdapter(e -> {
		shell.removeListener(SWT.Activate, shell.getListeners(SWT.Activate)[3]);
		if (!cData.isModified() && cData.isLocked() && fileAction.isPasswordFileReady(cData.getFile())) {
			createPasswordDialog(fileAction, false);
		}
	});

	private final ShellListener deiconified = shellDeiconifiedAdapter(e -> {
		shell.addShellListener(activated);
		final var tray = shell.getDisplay().getSystemTray();
		if (tray != null && WIN32) {
			tray.getItem(0).setVisible(false);
		}
	});

	/**
	 * Instantiates a new MainWindow.
	 *
	 * @param args the arguments
	 */
	public MainWindow(final String[] args) {
		if (args.length > 0) {
			cData.setFile(args[0]);
		}
	}

	private void dropTarget(final Control control) {
		final var dropTarget = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT | DND.DROP_NONE);
		dropTarget.setTransfer(FileTransfer.getInstance());
		dropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragEnter(final DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					event.detail = DND.DROP_COPY;
				}
			}

			@Override
			public void dragOperationChanged(final DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					event.detail = DND.DROP_COPY;
				}
			}

			@Override
			public void dragOver(final DropTargetEvent event) {
				if (isFileReady(cData.getFile())) {
					event.detail = DND.DROP_NONE;
				}
			}

			@Override
			public void drop(final DropTargetEvent event) {
				if (event.data != null && !isFileReady(cData.getFile())) {
					fileAction.clearData();
					cData.setFile(((String[]) event.data)[0]);
					fileAction.openFileArg();
				}
			}
		});
	}

	private void initializeActions() {
		editAction = new EditAction(cData, shell, table);
		fileAction = new FileAction(cData, shell, table);
		viewAction = new ViewAction(cData, shell, table);
		viewAction.defaultHeader();
	}

	private Menu menuBar() {
		final var menuBar = new Menu(shell, SWT.BAR);
		final var file = menu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, file, menuFile);
		menuItem(file, SWT.PUSH, widgetSelectedAdapter(e -> fileAction.newDatabase()), SWT.CTRL + 'N', menuClea);
		menuItem(file, SWT.PUSH, openFile, SWT.CTRL + 'O', menuOpen, OPEN);
		menuItem(file, SWT.PUSH, saveFile, SWT.CTRL + 'S', menuSave, SAVE);
		menuItemSeparator(file);
		menuItem(file, SWT.PUSH, widgetSelectedAdapter(e -> createPasswordDialog(fileAction, true)), menuChaP, KEY);
		menuItemSeparator(file);
		menuItem(file, SWT.PUSH, lockFile, SWT.CTRL + 'L', menuLock, LOCK);
		menuItemSeparator(file);
		menuItem(file, SWT.PUSH, widgetSelectedAdapter(e -> fileAction.importDialog()), menuImpo);
		menuItem(file, SWT.PUSH, widgetSelectedAdapter(e -> fileAction.exportDialog()), menuExpo, WARN);
		menuItemSeparator(file);
		menuItem(file, SWT.PUSH, widgetSelectedAdapter(e -> shell.close()), SWT.ESC, menuExit, EXIT);

		final var edit = menu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, edit, menuEdit);
		menuItem(edit, SWT.PUSH, newEntry, SWT.INSERT, menuNent, NEW);
		menuItem(edit, SWT.PUSH, editEntry, SWT.CR, menuEent, EDIT);
		menuItemSeparator(edit);
		menuItem(edit, SWT.PUSH, selectAll, SWT.CTRL + 'A', menuSela, SELA);
		menuItem(edit, SWT.PUSH, deleteLine, SWT.DEL, menuDels, DEL);
		menuItemSeparator(edit);
		menuItem(edit, SWT.PUSH, copyURL, SWT.CTRL + 'R', menuCurl, LINK);
		menuItem(edit, SWT.PUSH, copyName, SWT.CTRL + 'U', menuCusr, USER);
		menuItem(edit, SWT.PUSH, copyPass, SWT.CTRL + 'P', menuCpwd, KEY);
		menuItem(edit, SWT.PUSH, copyNotes, SWT.CTRL + 'K', menuCnot, NOTE);
		menuItemSeparator(edit);
		menuItem(edit, SWT.PUSH, openURL, SWT.CTRL + 'D', menuOurl, WEB);
		menuItemSeparator(edit);
		menuItem(edit, SWT.PUSH, widgetSelectedAdapter(e -> editAction.clearClipboard()), SWT.CTRL + 'Z', menuClCb);

		final var search = menu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, search, menuSear);
		menuItem(search, SWT.PUSH, openSearch, SWT.CTRL + 'F', menuFind, SEARCH);

		final var view = menu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, view, menuView);
		menuItem(view, SWT.CHECK, widgetSelectedAdapter(e -> viewAction.readOnlySwitch()), menuReaO);
		menuItemSeparator(view);
		menuItem(view, SWT.CHECK, widgetSelectedAdapter(e -> viewAction.openGroupList()), menuGrou);
		menuItemSeparator(view);
		menuItem(view, SWT.CHECK, widgetSelectedAdapter(e -> viewAction.resizeColumns()), menuPcol);
		menuItemSeparator(view);
		menuItem(view, SWT.RADIO, widgetSelectedAdapter(e -> viewAction.showPasswordColumn(e)), menuSpwd);
		menuItem(view, SWT.RADIO, widgetSelectedAdapter(e -> viewAction.showPasswordColumn(e)), menuHpwd, true);
		menuItemSeparator(view);
		final var fontMenu = menu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(view, SWT.CASCADE, fontMenu, menuFont);
		menuItem(fontMenu, SWT.PUSH, widgetSelectedAdapter(e -> viewAction.changeFont(true)), menuFoSh);
		menuItem(fontMenu, SWT.PUSH, widgetSelectedAdapter(e -> viewAction.changeFont(false)), menuFoTa);
		menuItemSeparator(view);
		menuItem(view, SWT.PUSH, widgetSelectedAdapter(e -> DialogFactory.createTextDialog(viewAction)), menuText, WARN);
		menuItemSeparator(view);
		menuItem(view, SWT.PUSH, widgetSelectedAdapter(e -> DialogFactory.createConfigDialog(viewAction)), menuPref, GEAR);

		final var info = menu(shell, SWT.DROP_DOWN, enableItems);
		menuItem(menuBar, SWT.CASCADE, info, menuInfo);
		menuItem(info, SWT.PUSH, widgetSelectedAdapter(e -> DialogFactory.createSystemDialog(fileAction)), menuSysI, SYSTEM);
		menuItemSeparator(info);
		menuItem(info, SWT.PUSH, widgetSelectedAdapter(e -> DialogFactory.createInfoDialog(fileAction)), menuAbou, INFO);

		return menuBar;
	}

	/**
	 * Opens the main window.
	 *
	 * @param display the display
	 * @return the shell
	 */
	public Shell open(final Display display) {
		final var image = getImage(display, APP_ICON);
		final var layout = getLayout();
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.addShellListener(shellClosedAdapter(e -> e.doit = fileAction.exit()));
		shell.addShellListener(deiconified);
		shell.addShellListener(shellIconifiedAdapter(e -> fileAction.setLocked()));
		if (WIN32) {
			layout.marginTop = -2;
		}
		shell.setImage(image);
		shell.setLayout(layout);
		shell.setMenuBar(menuBar());

		shellColor(display, toolBar());
		shellArea();
		initializeActions(); // Initialize actions after table creation
		IO.openConfig(fileAction);
		shellValues();

		shell.open();
		shell.forceActive();

		dropTarget(table);
		if (WIN32) {
			trayItem(display, image);
		}
		fileAction.openFileArg();
		fileAction.resizeColumns();
		fileAction.updateUI();
		selfTest();

		return shell;
	}

	private void shellArea() {
		final var foreground = shell.getForeground();
		final var form = new SashForm(shell, SWT.HORIZONTAL);
		form.setForeground(foreground);
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		form.setLayout(getLayout());

		list = new List(form, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		list.addSelectionListener(widgetSelectedAdapter(e -> fileAction.setGroupSelection()));
		list.setForeground(foreground);
		list.setVisible(false);

		table = table(form);
		table.addKeyListener(keyPressedAdapter(e -> fileAction.enableItems()));
		table.addMouseListener(mouseDoubleClickAdapter(e -> createEntryDialog(editAction, table.getSelectionIndex())));
		table.addSelectionListener(widgetSelectedAdapter(e -> fileAction.enableItems()));
		table.setHeaderVisible(true);
		table.setMenu(tableMenu());

		form.setWeights(SASH_FORM_WEIGHT_1, SASH_FORM_WEIGHT_2);
	}

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

	private void shellValues() {
		final var display = shell.getDisplay();
		final var maximized = cData.isMaximized();
		final var shellFont = cData.getShellFont();
		final var shellLoca = cData.getShellLocation();
		final var shellSize = cData.getShellSize();
		final var tableFont = cData.getTableFont();
		shell.getMenuBar().getItem(3).getMenu().getItem(4).setSelection(cData.isResizeCol());
		shell.setLocation(shellLoca == null || maximized ? new Point(PREF_POS_XY, PREF_POS_XY) : shellLoca);
		shell.setSize(shellSize == null || maximized ? new Point(getPrefSize(shell).x, PREF_SIZE_Y) : shellSize);

		if (shellFont != null) {
			shell.setFont(new Font(display, new FontData(shellFont)));
		}
		if (tableFont != null) {
			list.setFont(new Font(display, new FontData(tableFont)));
			table.setFont(new Font(display, new FontData(tableFont)));
		}
		if (maximized) {
			shell.setMaximized(true);
		}
	}

	private Menu tableMenu() {
		final var menu = menu(shell, SWT.POP_UP, menuShownAdapter(e -> {
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

		menuItem(menu, SWT.PUSH, openURL, menuOurl, WEB);
		menuItemSeparator(menu);
		menuItem(menu, SWT.PUSH, copyURL, menuCurl, LINK);
		menuItem(menu, SWT.PUSH, copyName, menuCusr, USER);
		menuItem(menu, SWT.PUSH, copyPass, menuCpwd, KEY);
		menuItem(menu, SWT.PUSH, copyNotes, menuCnot, NOTE);
		menuItemSeparator(menu);
		menuItem(menu, SWT.PUSH, newEntry, menuNent, NEW);
		menuItem(menu, SWT.PUSH, editEntry, menuEent, EDIT);
		menuItemSeparator(menu);
		menuItem(menu, SWT.PUSH, selectAll, menuSela, SELA);
		menuItem(menu, SWT.PUSH, deleteLine, menuDels, DEL);

		return menu;
	}

	private ToolBar toolBar() {
		final var toolBar = new ToolBar(shell, SWT.FLAT | SWT.SHADOW_OUT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		toolItem(toolBar, OPEN, openFile, menuOpen);
		toolItem(toolBar, SAVE, saveFile, menuSave);
		toolItemSeparator(toolBar);
		toolItem(toolBar, LOCK, lockFile, menuLock);
		toolItemSeparator(toolBar);
		toolItem(toolBar, NEW, newEntry, menuNent);
		toolItem(toolBar, EDIT, editEntry, menuEent);
		toolItemSeparator(toolBar);
		toolItem(toolBar, SEARCH, openSearch, menuSear);
		toolItemSeparator(toolBar);
		toolItem(toolBar, LINK, copyURL, menuCurl);
		toolItem(toolBar, USER, copyName, menuCusr);
		toolItem(toolBar, KEY, copyPass, menuCpwd);
		toolItem(toolBar, NOTE, copyNotes, menuCnot);
		toolItemSeparator(toolBar);
		toolItem(toolBar, WEB, openURL, menuOurl);

		return toolBar;
	}

	private void trayItem(final Display display, final Image image) {
		final var tray = display.getSystemTray();
		if (tray == null) {
			return;
		}
		final var trayItem = new TrayItem(tray, SWT.NONE);
		trayItem.addListener(SWT.Selection, e -> {
			shell.setVisible(true);
			shell.setMinimized(false);
		});
		trayItem.setImage(image);
		trayItem.setVisible(false);
	}
}
