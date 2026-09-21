/*
 * SecPwdMan
 * Copyright (C) 2026  Philipp Seerainer
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

import static io.github.seerainer.secpwdman.config.Icons.APP_ICON;
import static io.github.seerainer.secpwdman.config.Icons.DEL;
import static io.github.seerainer.secpwdman.config.Icons.EDIT;
import static io.github.seerainer.secpwdman.config.Icons.EDIT24;
import static io.github.seerainer.secpwdman.config.Icons.EXIT;
import static io.github.seerainer.secpwdman.config.Icons.GEAR;
import static io.github.seerainer.secpwdman.config.Icons.INFO;
import static io.github.seerainer.secpwdman.config.Icons.KEY;
import static io.github.seerainer.secpwdman.config.Icons.KEY24;
import static io.github.seerainer.secpwdman.config.Icons.LINK;
import static io.github.seerainer.secpwdman.config.Icons.LINK24;
import static io.github.seerainer.secpwdman.config.Icons.LOCK;
import static io.github.seerainer.secpwdman.config.Icons.LOCK24;
import static io.github.seerainer.secpwdman.config.Icons.MASTER_KEY;
import static io.github.seerainer.secpwdman.config.Icons.NEW;
import static io.github.seerainer.secpwdman.config.Icons.NEW24;
import static io.github.seerainer.secpwdman.config.Icons.NOTE;
import static io.github.seerainer.secpwdman.config.Icons.NOTE24;
import static io.github.seerainer.secpwdman.config.Icons.OPEN;
import static io.github.seerainer.secpwdman.config.Icons.OPEN24;
import static io.github.seerainer.secpwdman.config.Icons.SAVE;
import static io.github.seerainer.secpwdman.config.Icons.SAVE24;
import static io.github.seerainer.secpwdman.config.Icons.SEARCH;
import static io.github.seerainer.secpwdman.config.Icons.SEARCH24;
import static io.github.seerainer.secpwdman.config.Icons.SELA;
import static io.github.seerainer.secpwdman.config.Icons.SYSTEM;
import static io.github.seerainer.secpwdman.config.Icons.USER;
import static io.github.seerainer.secpwdman.config.Icons.USER24;
import static io.github.seerainer.secpwdman.config.Icons.WEB;
import static io.github.seerainer.secpwdman.config.Icons.WEB24;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.DARK_FORE;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.LINK_COL1;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.LINK_COL2;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.LINK_COL3;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.MENU_BACK;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.PREF_POS_XY;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.PREF_SIZE_Y;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.SASH_FORM_WEIGHT_1;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.SASH_FORM_WEIGHT_2;
import static io.github.seerainer.secpwdman.config.PrimitiveConstants.TOOL_BACK;
import static io.github.seerainer.secpwdman.config.StringConstants.menuAbou;
import static io.github.seerainer.secpwdman.config.StringConstants.menuChaP;
import static io.github.seerainer.secpwdman.config.StringConstants.menuClCb;
import static io.github.seerainer.secpwdman.config.StringConstants.menuClea;
import static io.github.seerainer.secpwdman.config.StringConstants.menuClos;
import static io.github.seerainer.secpwdman.config.StringConstants.menuCnot;
import static io.github.seerainer.secpwdman.config.StringConstants.menuCpwd;
import static io.github.seerainer.secpwdman.config.StringConstants.menuCurl;
import static io.github.seerainer.secpwdman.config.StringConstants.menuCusr;
import static io.github.seerainer.secpwdman.config.StringConstants.menuDels;
import static io.github.seerainer.secpwdman.config.StringConstants.menuEdit;
import static io.github.seerainer.secpwdman.config.StringConstants.menuEent;
import static io.github.seerainer.secpwdman.config.StringConstants.menuExit;
import static io.github.seerainer.secpwdman.config.StringConstants.menuExpo;
import static io.github.seerainer.secpwdman.config.StringConstants.menuFile;
import static io.github.seerainer.secpwdman.config.StringConstants.menuFind;
import static io.github.seerainer.secpwdman.config.StringConstants.menuFoSh;
import static io.github.seerainer.secpwdman.config.StringConstants.menuFoTa;
import static io.github.seerainer.secpwdman.config.StringConstants.menuFont;
import static io.github.seerainer.secpwdman.config.StringConstants.menuGrou;
import static io.github.seerainer.secpwdman.config.StringConstants.menuHelp;
import static io.github.seerainer.secpwdman.config.StringConstants.menuHpwd;
import static io.github.seerainer.secpwdman.config.StringConstants.menuImpo;
import static io.github.seerainer.secpwdman.config.StringConstants.menuLock;
import static io.github.seerainer.secpwdman.config.StringConstants.menuNent;
import static io.github.seerainer.secpwdman.config.StringConstants.menuOpen;
import static io.github.seerainer.secpwdman.config.StringConstants.menuOurl;
import static io.github.seerainer.secpwdman.config.StringConstants.menuPGen;
import static io.github.seerainer.secpwdman.config.StringConstants.menuPcol;
import static io.github.seerainer.secpwdman.config.StringConstants.menuPref;
import static io.github.seerainer.secpwdman.config.StringConstants.menuReaO;
import static io.github.seerainer.secpwdman.config.StringConstants.menuSave;
import static io.github.seerainer.secpwdman.config.StringConstants.menuSear;
import static io.github.seerainer.secpwdman.config.StringConstants.menuSecD;
import static io.github.seerainer.secpwdman.config.StringConstants.menuSela;
import static io.github.seerainer.secpwdman.config.StringConstants.menuSpwd;
import static io.github.seerainer.secpwdman.config.StringConstants.menuSysI;
import static io.github.seerainer.secpwdman.config.StringConstants.menuText;
import static io.github.seerainer.secpwdman.config.StringConstants.menuTool;
import static io.github.seerainer.secpwdman.config.StringConstants.menuView;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_NOTES;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_PASS;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_URL;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_COPY_USER;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_DELETE;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_EDIT;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_NEW;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_OPEN_URL;
import static io.github.seerainer.secpwdman.ui.MenuIds.EDIT_SELECT_ALL;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_CHANGE_KEY;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_CLOSE;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_EXPORT;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_IMPORT;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_LOCK;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_OPEN;
import static io.github.seerainer.secpwdman.ui.MenuIds.FILE_SAVE;
import static io.github.seerainer.secpwdman.ui.MenuIds.FIND_SEARCH;
import static io.github.seerainer.secpwdman.ui.MenuIds.MENU_EDIT;
import static io.github.seerainer.secpwdman.ui.MenuIds.MENU_FILE;
import static io.github.seerainer.secpwdman.ui.MenuIds.MENU_FIND;
import static io.github.seerainer.secpwdman.ui.MenuIds.MENU_VIEW;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_GROUPS;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_HIDE_PASS;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_READ_ONLY;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_RESIZE;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_SHOW_PASS;
import static io.github.seerainer.secpwdman.ui.MenuIds.VIEW_TEXT;
import static io.github.seerainer.secpwdman.ui.Widgets.findMenuItem;
import static io.github.seerainer.secpwdman.ui.Widgets.menu;
import static io.github.seerainer.secpwdman.ui.Widgets.menuItem;
import static io.github.seerainer.secpwdman.ui.Widgets.menuItemSeparator;
import static io.github.seerainer.secpwdman.ui.Widgets.setTrayItem;
import static io.github.seerainer.secpwdman.ui.Widgets.table;
import static io.github.seerainer.secpwdman.ui.Widgets.tag;
import static io.github.seerainer.secpwdman.ui.Widgets.toolItem;
import static io.github.seerainer.secpwdman.ui.Widgets.toolItemSeparator;
import static io.github.seerainer.secpwdman.util.SWTUtil.DARK;
import static io.github.seerainer.secpwdman.util.SWTUtil.MACOS;
import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;
import static io.github.seerainer.secpwdman.util.SWTUtil.disposeOnExit;
import static io.github.seerainer.secpwdman.util.SWTUtil.getColor;
import static io.github.seerainer.secpwdman.util.SWTUtil.getFont;
import static io.github.seerainer.secpwdman.util.SWTUtil.getGridData;
import static io.github.seerainer.secpwdman.util.SWTUtil.getImage;
import static io.github.seerainer.secpwdman.util.SWTUtil.getLayout;
import static io.github.seerainer.secpwdman.util.SWTUtil.getPrefSize;
import static io.github.seerainer.secpwdman.util.SWTUtil.safeDispose;
import static io.github.seerainer.secpwdman.util.SWTUtil.setOwnedFont;

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
import io.github.seerainer.secpwdman.crypto.Crypto;
import io.github.seerainer.secpwdman.io.IOUtil;

/**
 * The class MainWindow.
 */
public class MainWindow {

    private final ConfigData cData;
    private final Event event;

    private FileAction fileAction;
    private List groupList;
    private Menu menuBar;
    private Shell shell;
    private Table table;
    private TrayItem trayItem;
    private DropTarget dropTarget;

    /**
     * Opens the main window. Initializes the shell, its listeners, layout, UI
     * components, actions and UI values. Also sets up the drop target and tray item
     * if applicable.
     *
     * @param display the display used to create the shell and other UI components
     * @param args    the arguments
     */
    public MainWindow(final Display display, final String[] args) {
	this.event = new Event();
	this.cData = event.getConfigData();

	if (args.length > 0) {
	    cData.setTempFile(args[0]);
	}

	final var image = getImage(display, APP_ICON);
	initializeShell(display, image);
	initializeShellActions();
	initializeShellValues(display);
	initializeDropTarget(table);
	initializeTrayItem(display, image);

	shell.open();
	shell.forceActive();

	Crypto.selfTest(cData.getCryptoConfig());

	fileAction.setAffinity(shell);
	fileAction.openFileArg();
	fileAction.resizeColumns();
	fileAction.updateUI();
    }

    private void createEditMenu() {
	final var edit = menu(shell, SWT.DROP_DOWN, event.enableItems);
	tag(menuItem(menuBar, SWT.CASCADE, edit, menuEdit), MENU_EDIT);
	tag(menuItem(edit, SWT.PUSH, event.newEntry, SWT.INSERT, menuNent, NEW), EDIT_NEW);
	tag(menuItem(edit, SWT.PUSH, event.editEntry, SWT.CR, menuEent, EDIT), EDIT_EDIT);
	menuItemSeparator(edit);
	tag(menuItem(edit, SWT.PUSH, event.selectAll, SWT.CTRL + 'A', menuSela, SELA), EDIT_SELECT_ALL);
	tag(menuItem(edit, SWT.PUSH, event.deleteLine, SWT.DEL, menuDels, DEL), EDIT_DELETE);
	menuItemSeparator(edit);
	tag(menuItem(edit, SWT.PUSH, event.copyURL, SWT.CTRL + 'R', menuCurl, LINK), EDIT_COPY_URL);
	tag(menuItem(edit, SWT.PUSH, event.copyName, SWT.CTRL + 'U', menuCusr, USER), EDIT_COPY_USER);
	tag(menuItem(edit, SWT.PUSH, event.copyPass, SWT.CTRL + 'P', menuCpwd, KEY), EDIT_COPY_PASS);
	tag(menuItem(edit, SWT.PUSH, event.copyNotes, SWT.CTRL + 'K', menuCnot, NOTE), EDIT_COPY_NOTES);
	menuItemSeparator(edit);
	tag(menuItem(edit, SWT.PUSH, event.openURL, SWT.CTRL + 'D', menuOurl, WEB), EDIT_OPEN_URL);
	menuItemSeparator(edit);
	menuItem(edit, SWT.PUSH, event.clearCB, SWT.CTRL + 'Z', menuClCb);
    }

    private void createFileMenu() {
	final var file = menu(shell, SWT.DROP_DOWN, event.enableItems);
	tag(menuItem(menuBar, SWT.CASCADE, file, menuFile), MENU_FILE);
	menuItem(file, SWT.PUSH, event.newFile, SWT.CTRL + 'N', menuClea);
	tag(menuItem(file, SWT.PUSH, event.openFile, SWT.CTRL + 'O', menuOpen, OPEN), FILE_OPEN);
	tag(menuItem(file, SWT.PUSH, event.saveFile, SWT.CTRL + 'S', menuSave, SAVE), FILE_SAVE);
	tag(menuItem(file, SWT.PUSH, event.closeFile, SWT.CTRL + 'W', menuClos), FILE_CLOSE);
	menuItemSeparator(file);
	tag(menuItem(file, SWT.PUSH, event.changeKey, menuChaP, MASTER_KEY), FILE_CHANGE_KEY);
	menuItemSeparator(file);
	tag(menuItem(file, SWT.PUSH, event.lockFile, SWT.CTRL + 'L', menuLock, LOCK), FILE_LOCK);
	menuItemSeparator(file);
	tag(menuItem(file, SWT.PUSH, event.impFile, menuImpo), FILE_IMPORT);
	tag(menuItem(file, SWT.PUSH, event.expFile, menuExpo), FILE_EXPORT);
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
	tag(menuItem(menuBar, SWT.CASCADE, search, menuSear), MENU_FIND);
	tag(menuItem(search, SWT.PUSH, event.openSearch, SWT.CTRL + 'F', menuFind, SEARCH), FIND_SEARCH);
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
	tag(toolItem(toolBar, OPEN24, event.openFile, menuOpen), FILE_OPEN);
	tag(toolItem(toolBar, SAVE24, event.saveFile, menuSave), FILE_SAVE);
	toolItemSeparator(toolBar);
	tag(toolItem(toolBar, LOCK24, event.lockFile, menuLock), FILE_LOCK);
	toolItemSeparator(toolBar);
	tag(toolItem(toolBar, NEW24, event.newEntry, menuNent), EDIT_NEW);
	tag(toolItem(toolBar, EDIT24, event.editEntry, menuEent), EDIT_EDIT);
	toolItemSeparator(toolBar);
	tag(toolItem(toolBar, SEARCH24, event.openSearch, menuSear), FIND_SEARCH);
	toolItemSeparator(toolBar);
	tag(toolItem(toolBar, LINK24, event.copyURL, menuCurl), EDIT_COPY_URL);
	tag(toolItem(toolBar, USER24, event.copyName, menuCusr), EDIT_COPY_USER);
	tag(toolItem(toolBar, KEY24, event.copyPass, menuCpwd), EDIT_COPY_PASS);
	tag(toolItem(toolBar, NOTE24, event.copyNotes, menuCnot), EDIT_COPY_NOTES);
	toolItemSeparator(toolBar);
	tag(toolItem(toolBar, WEB24, event.openURL, menuOurl), EDIT_OPEN_URL);
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
	tag(menuItem(menuBar, SWT.CASCADE, view, menuView), MENU_VIEW);
	tag(menuItem(view, SWT.CHECK, event.readOnly, menuReaO), VIEW_READ_ONLY);
	menuItemSeparator(view);
	tag(menuItem(view, SWT.CHECK, event.openList, menuGrou), VIEW_GROUPS);
	menuItemSeparator(view);
	tag(menuItem(view, SWT.CHECK, event.resizeCol, menuPcol), VIEW_RESIZE);
	menuItemSeparator(view);
	tag(menuItem(view, SWT.RADIO, event.showPass, menuSpwd), VIEW_SHOW_PASS);
	tag(menuItem(view, SWT.RADIO, event.showPass, menuHpwd, true), VIEW_HIDE_PASS);
	menuItemSeparator(view);
	final var fontMenu = menu(shell, SWT.DROP_DOWN, event.enableItems);
	menuItem(view, SWT.CASCADE, fontMenu, menuFont);
	menuItem(fontMenu, SWT.PUSH, event.shellFont, menuFoSh);
	menuItem(fontMenu, SWT.PUSH, event.tableFont, menuFoTa);
	menuItemSeparator(view);
	tag(menuItem(view, SWT.PUSH, event.textDialog, menuText), VIEW_TEXT);
    }

    /**
     * Gets the shell.
     *
     * @return the shell
     */
    public Shell getShell() {
	return shell;
    }

    private void initializeDropTarget(final Control control) {
	dropTarget = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT);
	dropTarget.setTransfer(FileTransfer.getInstance());
	dropTarget.addDropListener(event.dropTargetAdapter);
	// DropTarget is not a child widget: dispose it with its control
	final var target = dropTarget;
	control.addDisposeListener(_ -> safeDispose(target));
    }

    private void initializeShell(final Display display, final Image image) {
	shell = new Shell(display, SWT.SHELL_TRIM);
	shell.addShellListener(event.close);
	shell.addShellListener(event.deiconified);
	shell.addShellListener(event.iconified);
	shell.setImage(image);
	disposeOnExit(shell, image);
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
	findMenuItem(findMenuItem(menuBar, MENU_VIEW).getMenu(), VIEW_RESIZE).setSelection(cData.isResizeCol());
	shell.setLocation(Objects.isNull(shellLoca) || maximized ? new Point(PREF_POS_XY, PREF_POS_XY) : shellLoca);
	shell.setSize(
		Objects.isNull(shellSize) || maximized ? new Point(getPrefSize(shell).x, PREF_SIZE_Y) : shellSize);

	if (Objects.nonNull(shellFont)) {
	    setOwnedFont(shell, getFont(display, shellFont));
	}
	if (Objects.nonNull(tableFont)) {
	    final var font = getFont(display, tableFont);
	    setOwnedFont(table, font);
	    // group list shares the table font instance; tracked on both so a
	    // later changeFont() disposes the old instance (guarded, exactly once)
	    setOwnedFont(groupList, font);
	}
	if (maximized) {
	    shell.setMaximized(true);
	}
    }

    private Menu initializeTableMenu() {
	final var menu = menu(shell, SWT.POP_UP, event.tableMenu);
	tag(menuItem(menu, SWT.PUSH, event.openURL, menuOurl, WEB), EDIT_OPEN_URL);
	menuItemSeparator(menu);
	tag(menuItem(menu, SWT.PUSH, event.copyURL, menuCurl, LINK), EDIT_COPY_URL);
	tag(menuItem(menu, SWT.PUSH, event.copyName, menuCusr, USER), EDIT_COPY_USER);
	tag(menuItem(menu, SWT.PUSH, event.copyPass, menuCpwd, KEY), EDIT_COPY_PASS);
	tag(menuItem(menu, SWT.PUSH, event.copyNotes, menuCnot, NOTE), EDIT_COPY_NOTES);
	menuItemSeparator(menu);
	tag(menuItem(menu, SWT.PUSH, event.newEntry, menuNent, NEW), EDIT_NEW);
	tag(menuItem(menu, SWT.PUSH, event.editEntry, menuEent, EDIT), EDIT_EDIT);
	menuItemSeparator(menu);
	tag(menuItem(menu, SWT.PUSH, event.selectAll, menuSela, SELA), EDIT_SELECT_ALL);
	tag(menuItem(menu, SWT.PUSH, event.deleteLine, menuDels, DEL), EDIT_DELETE);
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
	trayItem = new TrayItem(tray, SWT.NONE);
	setTrayItem(shell, trayItem);
	trayItem.addListener(SWT.Selection, _ -> {
	    shell.setVisible(true);
	    shell.setMinimized(false);
	});
	// shares the shell (app) image: the image itself is freed via the shell
	// dispose listener, only the TrayItem needs explicit disposal here
	trayItem.setImage(image);
	trayItem.setVisible(false);
	final var item = trayItem;
	shell.addDisposeListener(_ -> {
	    safeDispose(item);
	    setTrayItem(shell, null);
	});
    }
}
