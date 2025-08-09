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

import static io.github.seerainer.secpwdman.ui.Widgets.menu;
import static io.github.seerainer.secpwdman.ui.Widgets.menuItem;
import static io.github.seerainer.secpwdman.ui.Widgets.menuItemSeparator;
import static io.github.seerainer.secpwdman.ui.Widgets.table;
import static io.github.seerainer.secpwdman.ui.Widgets.toolItem;
import static io.github.seerainer.secpwdman.ui.Widgets.toolItemSeparator;
import static io.github.seerainer.secpwdman.util.SWTUtil.DARK;
import static io.github.seerainer.secpwdman.util.SWTUtil.MACOS;
import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;
import static io.github.seerainer.secpwdman.util.SWTUtil.getColor;
import static io.github.seerainer.secpwdman.util.SWTUtil.getFont;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.getPrefSize;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TrayItem;

import io.github.seerainer.secpwdman.action.FileAction;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.Icons;
import io.github.seerainer.secpwdman.config.PrimitiveConstants;
import io.github.seerainer.secpwdman.config.StringConstants;
import io.github.seerainer.secpwdman.crypto.Crypto;
import io.github.seerainer.secpwdman.io.IOUtil;
import io.github.seerainer.secpwdman.util.Win32Affinity;

/**
 * The class MainWindow.
 */
public class MainWindow implements Icons, PrimitiveConstants, StringConstants {

    private final ConfigData cData;
    private final Event event;

    private FileAction fileAction;
    private List groupList;
    private Menu menuBar;
    private Shell shell;
    private Table table;

    /**
     * Instantiates a new MainWindow.
     *
     * @param args the arguments
     */
    public MainWindow(final String[] args) {
	this.event = new Event();
	this.cData = event.getConfigData();
	if (args.length > 0) {
	    cData.setTempFile(args[0]);
	}
    }

    private void createEditMenu() {
	final var edit = menu(shell, SWT.DROP_DOWN, event.enableItems);
	menuItem(menuBar, SWT.CASCADE, edit, menuEdit);
	menuItem(edit, SWT.PUSH, event.newEntry, SWT.INSERT, menuNent, NEW);
	menuItem(edit, SWT.PUSH, event.editEntry, SWT.CR, menuEent, EDIT);
	menuItemSeparator(edit);
	menuItem(edit, SWT.PUSH, event.selectAll, SWT.CTRL + 'A', menuSela, SELA);
	menuItem(edit, SWT.PUSH, event.deleteLine, SWT.DEL, menuDels, DEL);
	menuItemSeparator(edit);
	menuItem(edit, SWT.PUSH, event.copyURL, SWT.CTRL + 'R', menuCurl, LINK);
	menuItem(edit, SWT.PUSH, event.copyName, SWT.CTRL + 'U', menuCusr, USER);
	menuItem(edit, SWT.PUSH, event.copyPass, SWT.CTRL + 'P', menuCpwd, KEY);
	menuItem(edit, SWT.PUSH, event.copyNotes, SWT.CTRL + 'K', menuCnot, NOTE);
	menuItemSeparator(edit);
	menuItem(edit, SWT.PUSH, event.openURL, SWT.CTRL + 'D', menuOurl, WEB);
	menuItemSeparator(edit);
	menuItem(edit, SWT.PUSH, event.clearCB, SWT.CTRL + 'Z', menuClCb);
    }

    private void createFileMenu() {
	final var file = menu(shell, SWT.DROP_DOWN, event.enableItems);
	menuItem(menuBar, SWT.CASCADE, file, menuFile);
	menuItem(file, SWT.PUSH, event.newFile, SWT.CTRL + 'N', menuClea);
	menuItem(file, SWT.PUSH, event.openFile, SWT.CTRL + 'O', menuOpen, OPEN);
	menuItem(file, SWT.PUSH, event.saveFile, SWT.CTRL + 'S', menuSave, SAVE);
	menuItem(file, SWT.PUSH, event.closeFile, SWT.CTRL + 'W', menuClos);
	menuItemSeparator(file);
	menuItem(file, SWT.PUSH, event.changeKey, menuChaP, MASTER_KEY);
	menuItemSeparator(file);
	menuItem(file, SWT.PUSH, event.lockFile, SWT.CTRL + 'L', menuLock, LOCK);
	menuItemSeparator(file);
	menuItem(file, SWT.PUSH, event.impFile, menuImpo);
	menuItem(file, SWT.PUSH, event.expFile, menuExpo);
	menuItemSeparator(file);
	menuItem(file, SWT.PUSH, event.quit, SWT.ESC, menuExit, EXIT);
    }

    private void createHelpMenu() {
	final var info = menu(shell, SWT.DROP_DOWN, event.enableItems);
	menuItem(menuBar, SWT.CASCADE, info, menuHelp);
	menuItem(info, SWT.PUSH, event.system, menuSysI, SYSTEM);
	menuItemSeparator(info);
	menuItem(info, SWT.PUSH, event.about, menuAbou, INFO);
    }

    private Menu createMenuBar() {
	menuBar = new Menu(shell, SWT.BAR);
	createFileMenu();
	createEditMenu();
	createSearchMenu();
	createViewMenu();
	createToolMenu();
	createHelpMenu();
	return menuBar;
    }

    private void createSearchMenu() {
	final var search = menu(shell, SWT.DROP_DOWN, event.enableItems);
	menuItem(menuBar, SWT.CASCADE, search, menuSear);
	menuItem(search, SWT.PUSH, event.openSearch, SWT.CTRL + 'F', menuFind, SEARCH);
    }

    private void createShellArea() {
	final var foreground = shell.getForeground();
	final var form = new SashForm(shell, SWT.HORIZONTAL);
	form.setForeground(foreground);
	form.setLayoutData(getGridData(SWT.FILL, SWT.FILL, 1, 1));
	form.setLayout(getLayout());

	groupList = new List(form, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
	groupList.addSelectionListener(event.listSelection);
	groupList.setForeground(foreground);
	groupList.setVisible(false);

	table = table(form);
	table.addKeyListener(event.keyListener);
	table.addMouseListener(event.mouseListener);
	table.addSelectionListener(event.tableListener);
	table.setHeaderVisible(true);
	table.setMenu(initializeTableMenu());

	form.setWeights(SASH_FORM_WEIGHT_1, SASH_FORM_WEIGHT_2);
    }

    private ToolBar createToolBar() {
	final var toolBar = new ToolBar(shell, SWT.FLAT | SWT.SHADOW_OUT);
	toolBar.setLayoutData(getGridData(SWT.FILL, SWT.FILL, 1, 0));
	toolItem(toolBar, OPEN, event.openFile, menuOpen);
	toolItem(toolBar, SAVE, event.saveFile, menuSave);
	toolItemSeparator(toolBar);
	toolItem(toolBar, LOCK, event.lockFile, menuLock);
	toolItemSeparator(toolBar);
	toolItem(toolBar, NEW, event.newEntry, menuNent);
	toolItem(toolBar, EDIT, event.editEntry, menuEent);
	toolItemSeparator(toolBar);
	toolItem(toolBar, SEARCH, event.openSearch, menuSear);
	toolItemSeparator(toolBar);
	toolItem(toolBar, LINK, event.copyURL, menuCurl);
	toolItem(toolBar, USER, event.copyName, menuCusr);
	toolItem(toolBar, KEY, event.copyPass, menuCpwd);
	toolItem(toolBar, NOTE, event.copyNotes, menuCnot);
	toolItemSeparator(toolBar);
	toolItem(toolBar, WEB, event.openURL, menuOurl);
	return toolBar;
    }

    private void createToolMenu() {
	final var tool = menu(shell, SWT.DROP_DOWN, event.enableItems);
	menuItem(menuBar, SWT.CASCADE, tool, menuTool);
	menuItem(tool, SWT.PUSH, event.passGen, menuPGen);
	menuItem(tool, SWT.PUSH, event.shredFile, menuSecD);
	menuItemSeparator(tool);
	menuItem(tool, SWT.PUSH, event.settings, menuPref, GEAR);
    }

    private void createViewMenu() {
	final var view = menu(shell, SWT.DROP_DOWN, event.enableItems);
	menuItem(menuBar, SWT.CASCADE, view, menuView);
	menuItem(view, SWT.CHECK, event.readOnly, menuReaO);
	menuItemSeparator(view);
	menuItem(view, SWT.CHECK, event.openList, menuGrou);
	menuItemSeparator(view);
	menuItem(view, SWT.CHECK, event.resizeCol, menuPcol);
	menuItemSeparator(view);
	menuItem(view, SWT.RADIO, event.showPass, menuSpwd);
	menuItem(view, SWT.RADIO, event.showPass, menuHpwd, true);
	menuItemSeparator(view);
	final var fontMenu = menu(shell, SWT.DROP_DOWN, event.enableItems);
	menuItem(view, SWT.CASCADE, fontMenu, menuFont);
	menuItem(fontMenu, SWT.PUSH, event.shellFont, menuFoSh);
	menuItem(fontMenu, SWT.PUSH, event.tableFont, menuFoTa);
	menuItemSeparator(view);
	menuItem(view, SWT.PUSH, event.textDialog, menuText);
    }

    private void initializeDropTarget(final Control control) {
	final var dropTarget = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT);
	dropTarget.setTransfer(FileTransfer.getInstance());
	dropTarget.addDropListener(event.dropTargetAdapter);
    }

    private void initializeShell(final Display display, final Image image) {
	shell = new Shell(display, SWT.SHELL_TRIM);
	shell.addShellListener(event.close);
	shell.addShellListener(event.deiconified);
	shell.addShellListener(event.iconified);
	shell.addDisposeListener(event.dispose);
	shell.setImage(image);
	shell.setLayout(getLayout());
	shell.setMenuBar(createMenuBar());
	initializeShellColor(display, createToolBar());
	createShellArea();
    }

    private void initializeShellActions() {
	fileAction = event.setActions(shell, table);
	fileAction.defaultHeader();
    }

    private void initializeShellColor(final Display display, final ToolBar toolBar) {
	if (DARK && !MACOS) {
	    final var darkForeground = getColor(DARK_FORE, DARK_FORE, DARK_FORE);
	    toolBar.setBackground(getColor(TOOL_BACK, TOOL_BACK, TOOL_BACK));
	    toolBar.setForeground(darkForeground);
	    cData.setLinkColor(getColor(LINK_COL1, LINK_COL2, LINK_COL3));
	    cData.setTextColor(darkForeground);
	    shell.setBackground(getColor(MENU_BACK, MENU_BACK, MENU_BACK));
	    shell.setForeground(darkForeground);
	    shell.setBackgroundMode(SWT.INHERIT_FORCE);
	} else {
	    cData.setLinkColor(display.getSystemColor(SWT.COLOR_LINK_FOREGROUND));
	    cData.setTextColor(display.getSystemColor(SWT.COLOR_LIST_FOREGROUND));
	}
    }

    private void initializeShellValues(final Display display) {
	IOUtil.openConfig(fileAction);

	final var maximized = cData.isMaximized();
	final var shellFont = cData.getShellFont();
	final var shellLoca = cData.getShellLocation();
	final var shellSize = cData.getShellSize();
	final var tableFont = cData.getTableFont();
	menuBar.getItem(3).getMenu().getItem(4).setSelection(cData.isResizeCol());
	shell.setLocation(Objects.isNull(shellLoca) || maximized ? new Point(PREF_POS_XY, PREF_POS_XY) : shellLoca);
	shell.setSize(
		Objects.isNull(shellSize) || maximized ? new Point(getPrefSize(shell).x, PREF_SIZE_Y) : shellSize);

	if (Objects.nonNull(shellFont)) {
	    shell.setFont(getFont(display, shellFont));
	}
	if (Objects.nonNull(tableFont)) {
	    final var font = getFont(display, tableFont);
	    groupList.setFont(font);
	    table.setFont(font);
	}
	if (maximized) {
	    shell.setMaximized(true);
	}
    }

    private Menu initializeTableMenu() {
	final var menu = menu(shell, SWT.POP_UP, event.tableMenu);
	menuItem(menu, SWT.PUSH, event.openURL, menuOurl, WEB);
	menuItemSeparator(menu);
	menuItem(menu, SWT.PUSH, event.copyURL, menuCurl, LINK);
	menuItem(menu, SWT.PUSH, event.copyName, menuCusr, USER);
	menuItem(menu, SWT.PUSH, event.copyPass, menuCpwd, KEY);
	menuItem(menu, SWT.PUSH, event.copyNotes, menuCnot, NOTE);
	menuItemSeparator(menu);
	menuItem(menu, SWT.PUSH, event.newEntry, menuNent, NEW);
	menuItem(menu, SWT.PUSH, event.editEntry, menuEent, EDIT);
	menuItemSeparator(menu);
	menuItem(menu, SWT.PUSH, event.selectAll, menuSela, SELA);
	menuItem(menu, SWT.PUSH, event.deleteLine, menuDels, DEL);
	return menu;
    }

    private void initializeTrayItem(final Display display, final Image image) {
	if (!WIN32) {
	    return;
	}
	final var tray = display.getSystemTray();
	if (Objects.isNull(tray)) {
	    return;
	}
	final var trayItem = new TrayItem(tray, SWT.NONE);
	trayItem.addListener(SWT.Selection, _ -> {
	    shell.setVisible(true);
	    shell.setMinimized(false);
	});
	trayItem.setImage(image);
	trayItem.setVisible(false);
    }

    /**
     * Opens the main window. Initializes the shell, its listeners, layout, UI
     * components, actions and UI values. Also sets up the drop target and tray item
     * if applicable.
     *
     * @param display the display used to create the shell and other UI components
     * @return the shell instance representing the main window
     */
    public Shell open(final Display display) {
	final var image = getImage(display, APP_ICON);

	initializeShell(display, image);
	initializeShellActions();
	initializeShellValues(display);
	initializeDropTarget(table);
	initializeTrayItem(display, image);

	shell.open();
	shell.forceActive();
	display.asyncExec(() -> Win32Affinity.setWindowDisplayAffinity(shell));
	fileAction.openFileArg();
	fileAction.resizeColumns();
	fileAction.updateUI();
	Crypto.selfTest(cData.getCryptoConfig());

	return shell;
    }
}
