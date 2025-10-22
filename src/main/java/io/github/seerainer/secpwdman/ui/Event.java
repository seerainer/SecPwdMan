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

import static io.github.seerainer.secpwdman.util.SWTUtil.WIN32;
import static org.eclipse.swt.events.KeyListener.keyPressedAdapter;
import static org.eclipse.swt.events.MenuListener.menuShownAdapter;
import static org.eclipse.swt.events.MouseListener.mouseDoubleClickAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;
import static org.eclipse.swt.events.ShellListener.shellActivatedAdapter;
import static org.eclipse.swt.events.ShellListener.shellClosedAdapter;
import static org.eclipse.swt.events.ShellListener.shellDeiconifiedAdapter;
import static org.eclipse.swt.events.ShellListener.shellIconifiedAdapter;

import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import io.github.seerainer.secpwdman.action.EditAction;
import io.github.seerainer.secpwdman.action.FileAction;
import io.github.seerainer.secpwdman.action.ViewAction;
import io.github.seerainer.secpwdman.config.ConfigData;
import io.github.seerainer.secpwdman.config.StringConstants;

/**
 * The class Event.
 */
class Event implements StringConstants {

    private final ConfigData cData = new ConfigData();

    private EditAction editAction;
    private FileAction fileAction;
    private ViewAction viewAction;

    DisposeListener dispose = _ -> {
	final var shell = fileAction.getShell();
	if (Objects.nonNull(shell) && !shell.isDisposed()) {
	    shell.dispose();
	}
    };
    KeyListener keyListener = keyPressedAdapter(_ -> fileAction.enableItems());
    MenuListener enableItems = menuShownAdapter(_ -> fileAction.enableItems());
    MenuListener tableMenu = menuShownAdapter(e -> {
	final var item = ((Menu) e.widget);
	final var editMenu = editAction.getMenu().getItem(1).getMenu();
	item.getItem(0).setEnabled(editMenu.getItem(11).getEnabled());
	item.getItem(2).setEnabled(editMenu.getItem(6).getEnabled());
	item.getItem(3).setEnabled(editMenu.getItem(7).getEnabled());
	item.getItem(4).setEnabled(editMenu.getItem(8).getEnabled());
	item.getItem(5).setEnabled(editMenu.getItem(9).getEnabled());
	item.getItem(7).setEnabled(editMenu.getItem(0).getEnabled());
	item.getItem(8).setEnabled(editMenu.getItem(1).getEnabled());
	item.getItem(10).setEnabled(editMenu.getItem(3).getEnabled());
	item.getItem(11).setEnabled(editMenu.getItem(4).getEnabled());
    });
    MouseListener mouseListener = mouseDoubleClickAdapter(
	    _ -> DialogFactory.createEntryDialog(editAction, editAction.getTable().getSelectionIndex()));
    SelectionListener newFile = widgetSelectedAdapter(_ -> fileAction.newDatabase());
    SelectionListener openFile = widgetSelectedAdapter(_ -> fileAction.openDialog());
    SelectionListener saveFile = widgetSelectedAdapter(_ -> {
	cData.setClearAfterSave(false);
	cData.setExitAfterSave(false);
	fileAction.saveDialog();
    });
    SelectionListener closeFile = widgetSelectedAdapter(_ -> fileAction.closeDatabase());
    SelectionListener changeKey = widgetSelectedAdapter(_ -> DialogFactory.createPasswordDialog(fileAction, true));
    SelectionListener lockFile = widgetSelectedAdapter(_ -> fileAction.lockSwitch());
    SelectionListener impFile = widgetSelectedAdapter(_ -> fileAction.importDialog());
    SelectionListener expFile = widgetSelectedAdapter(_ -> fileAction.exportDialog());
    SelectionListener quit = widgetSelectedAdapter(_ -> fileAction.getShell().close());
    SelectionListener newEntry = widgetSelectedAdapter(_ -> DialogFactory.createEntryDialog(editAction, -1));
    SelectionListener editEntry = widgetSelectedAdapter(
	    _ -> DialogFactory.createEntryDialog(editAction, editAction.getTable().getSelectionIndex()));
    SelectionListener selectAll = widgetSelectedAdapter(_ -> {
	final var table = editAction.getTable();
	table.selectAll();
	table.setFocus();
	editAction.enableItems();
    });
    SelectionListener deleteLine = widgetSelectedAdapter(_ -> editAction.deleteLine());
    SelectionListener copyURL = widgetSelectedAdapter(
	    _ -> editAction.copyToClipboard(cData.getColumnMap().get(csvHeader[3]).intValue()));
    SelectionListener copyName = widgetSelectedAdapter(
	    _ -> editAction.copyToClipboard(cData.getColumnMap().get(csvHeader[4]).intValue()));
    SelectionListener copyPass = widgetSelectedAdapter(
	    _ -> editAction.copyToClipboard(cData.getColumnMap().get(csvHeader[5]).intValue()));
    SelectionListener copyNotes = widgetSelectedAdapter(
	    _ -> editAction.copyToClipboard(cData.getColumnMap().get(csvHeader[6]).intValue()));
    SelectionListener openURL = widgetSelectedAdapter(_ -> Program.launch(
	    editAction.getTable().getSelection()[0].getText(cData.getColumnMap().get(csvHeader[3]).intValue())));
    SelectionListener clearCB = widgetSelectedAdapter(_ -> editAction.clearClipboard());
    SelectionListener openSearch = widgetSelectedAdapter(_ -> DialogFactory.createSearchDialog(fileAction));
    SelectionListener readOnly = widgetSelectedAdapter(_ -> viewAction.readOnlySwitch());
    SelectionListener openList = widgetSelectedAdapter(_ -> viewAction.openGroupList());
    SelectionListener resizeCol = widgetSelectedAdapter(_ -> viewAction.resizeColumns());
    SelectionListener showPass = widgetSelectedAdapter(e -> viewAction.showPasswordColumn(e));
    SelectionListener shellFont = widgetSelectedAdapter(_ -> viewAction.changeFont(true));
    SelectionListener tableFont = widgetSelectedAdapter(_ -> viewAction.changeFont(false));
    SelectionListener textDialog = widgetSelectedAdapter(_ -> DialogFactory.createTextDialog(viewAction));
    SelectionListener passGen = widgetSelectedAdapter(_ -> DialogFactory.createPasswordGeneratorDialog(editAction));
    SelectionListener shredFile = widgetSelectedAdapter(_ -> fileAction.shredFile());
    SelectionListener settings = widgetSelectedAdapter(_ -> DialogFactory.createConfigDialog(viewAction));
    SelectionListener system = widgetSelectedAdapter(_ -> DialogFactory.createSystemDialog(fileAction));
    SelectionListener about = widgetSelectedAdapter(_ -> DialogFactory.createInfoDialog(fileAction));
    SelectionListener listSelection = widgetSelectedAdapter(_ -> fileAction.setGroupSelection());
    SelectionListener tableListener = widgetSelectedAdapter(_ -> fileAction.enableItems());
    ShellListener activated = shellActivatedAdapter(_ -> {
	final var shell = fileAction.getShell();
	shell.removeListener(SWT.Activate, shell.getListeners(SWT.Activate)[3]);
	if (!cData.isModified() && cData.isLocked() && fileAction.isPasswordFileReady(cData.getFile())) {
	    DialogFactory.createPasswordDialog(fileAction, false);
	}
    });
    ShellListener close = shellClosedAdapter(e -> e.doit = fileAction.exit());
    ShellListener deiconified = shellDeiconifiedAdapter(_ -> {
	final var shell = fileAction.getShell();
	final var tray = shell.getDisplay().getSystemTray();
	if (Objects.nonNull(tray) && WIN32) {
	    tray.getItem(0).setVisible(false);
	}
	shell.addShellListener(activated);
    });
    ShellListener iconified = shellIconifiedAdapter(_ -> fileAction.setLocked());
    DropTargetAdapter dropTargetAdapter = new DropTargetAdapter() {
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
	    if (cData.isLocked() || cData.isReadOnly()) {
		event.detail = DND.DROP_NONE;
	    }
	}

	@Override
	public void drop(final DropTargetEvent event) {
	    if (Objects.isNull(event.data)) {
		return;
	    }
	    cData.setTempFile(((String[]) event.data)[0]);
	    fileAction.openFileArg();
	}
    };

    Event() {
    }

    ConfigData getConfigData() {
	return cData;
    }

    FileAction setActions(final Shell shell, final Table table) {
	this.editAction = new EditAction(cData, shell, table);
	this.fileAction = new FileAction(cData, shell, table);
	this.viewAction = new ViewAction(cData, shell, table);
	return fileAction;
    }
}
