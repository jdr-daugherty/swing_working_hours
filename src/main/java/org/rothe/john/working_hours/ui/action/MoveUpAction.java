package org.rothe.john.working_hours.ui.action;

import org.rothe.john.working_hours.model.Member;
import org.rothe.john.working_hours.ui.table.MembersTable;
import org.rothe.john.working_hours.util.Images;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoveUpAction extends AbstractReorderAction {
    public MoveUpAction(MembersTable table) {
        super(table, "Move Up", Images.load("move-up.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveUp(table().getSelectedRows());
    }

    private void moveUp(int[] rows) {
        Arrays.sort(rows);
        if (rows.length == 0 || rows[0] == 0) {
            return;
        }

        moveUp(rows, new ArrayList<>(team().getMembers()));
    }

    private void moveUp(int[] rows, List<Member> members) {
        for (int row : rows) {
            Member m = members.remove(row);
            members.add(row - 1, m);
        }
        fireTeamChanged("Move Up", team().withMembers(members));
        selectRowsLater(adjust(rows, -1));
    }
}
