package org.rothe.john.working_hours.ui;

import lombok.val;
import org.rothe.john.working_hours.event.undo.UndoListener;
import org.rothe.john.working_hours.ui.action.*;
import org.rothe.john.working_hours.ui.canvas.Canvas;
import org.rothe.john.working_hours.ui.table.MembersTable;
import org.rothe.john.working_hours.ui.table.paste.Paster;
import org.rothe.john.working_hours.util.SampleFactory;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;

public class MenuBar extends JMenuBar {
    private final Canvas canvas;
    private final MembersTable table;
    private final UndoListener listener;
    private final JMenuItem copy;
    private final JMenuItem paste;

    public MenuBar(Canvas canvas, MembersTable table, UndoListener listener) {
        this.canvas = canvas;
        this.table = table;
        this.listener = listener;
        this.copy = newItem(new CopyAction(table), 'C', VK_C);
        this.paste = newItem(new PasteAction(table), 'P', VK_V);

        addMenus();

        table.getSelectionModel().addListSelectionListener(new TableSelectionListener(this::updateCopyPaste));
    }

    private void addMenus() {
        addFileMenu();
        addEditMenu();
        addMembersMenu();
        add(createSampleMenu());
    }

    private void addMembersMenu() {
        val menu = newMenu("Members", 'M');

        menu.add(new MemberAddAction(table));
        menu.add(new MemberRemoveAction(table));

        menu.addSeparator();
        menu.add(new MoveUpAction(table));
        menu.add(new MoveDownAction(table));
    }

    private void addFileMenu() {
        val menu = newMenu("File", 'F');

        menu.add(newItem(new NewTeamAction(getRootPane()), 'N'));

        menu.addSeparator();
        menu.add(new OpenAction(table)).setEnabled(false);
        menu.add(new SaveAction(table)).setEnabled(false);

        menu.addSeparator();
        menu.add(new SaveAsAction(table)).setEnabled(false);

        menu.addSeparator();
        menu.add(new ImportCsvAction(table)).setMnemonic('I');
        menu.add(new ExportCsvAction(table)).setMnemonic('E');
        menu.addSeparator();
        menu.add(new ExportImageAction(canvas)).setMnemonic('m');

        menu.addSeparator();
        menu.add(newItem(new ExitAction(getRootPane()), 'X'));
    }

    private JMenu createSampleMenu() {
        val menu = newMenu("Sample Teams", 'T');

        menu.add(new SampleTeamAction("Centering Debug Team", SampleFactory::centeringDebugMembers));
        menu.add(new SampleTeamAction("Debug Team", SampleFactory::debugMembers));
        menu.add(new SampleTeamAction("Debug Shift Team", SampleFactory::debugShiftMembers));
        menu.add(new SampleTeamAction("Demo Team", SampleFactory::demoMembers));

        return menu;
    }

    private void addEditMenu() {
        val menu = newMenu("Edit", 'E');

        menu.add(copy);
        menu.add(paste);
        menu.addMenuListener(new RunnableMenuListener(this::updateCopyPaste));

        menu.addSeparator();

        menu.add(newItem(new UndoAction(listener), 'U', VK_Z));
        menu.add(newItem(new RedoAction(listener), 'R', VK_Y));
    }

    private JMenuItem newItem(Action a, char mnemonic) {
        val item = new JMenuItem(a);
        item.setMnemonic(mnemonic);
        return item;
    }

    private JMenuItem newItem(Action a, char mnemonic, int accelerator) {
        val item = newItem(a, mnemonic);
        item.setAccelerator(KeyStroke.getKeyStroke(accelerator, CTRL_DOWN_MASK));
        return item;
    }

    private JMenu newMenu(String title, char mnemonic) {
        val menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        return add(menu);
    }

    private void updateCopyPaste() {
        copy.setEnabled(table.isShowing() && table.getSelectedRowCount() > 0);
        paste.setEnabled(table.isShowing() && Paster.canPaste(table));
    }

    private record RunnableMenuListener(Runnable runnable) implements MenuListener {
        @Override
        public void menuSelected(MenuEvent e) {
            runnable.run();
        }

        @Override
        public void menuDeselected(MenuEvent e) {
        }

        @Override
        public void menuCanceled(MenuEvent e) {
        }
    }

    private record TableSelectionListener(Runnable runnable) implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            runnable.run();
        }
    }
}
