package org.rothe.john.working_hours.ui.action;

import lombok.val;
import org.rothe.john.working_hours.event.Teams;
import org.rothe.john.working_hours.model.*;
import org.rothe.john.working_hours.ui.table.MembersTable;
import org.rothe.john.working_hours.util.Images;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MemberAddAction extends ToolbarAction {
    private final MembersTable table;

    public MemberAddAction(MembersTable table) {
        super("Add Member", Images.load("plus-square.png"));
        this.table = table;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(nonNull(table.getCellEditor())) {
            table.getCellEditor().cancelCellEditing();
        }
        addMember(Teams.getTeam());
    }

    private void addMember(Team team) {
        fireTeamChanged(team.withMembers(newMembers(team.getMembers())));
        SwingUtilities.invokeLater(() -> editNewMember(team.getMembers().size()));
    }

    private List<Member> newMembers(List<Member> members) {
        return Stream.concat(members.stream(), Stream.of(newMember())).toList();
    }

    private Member newMember() {
        val selected = table.getSelectedMember();
        return newMember(
                selected.map(Member::role).orElse("Role"),
                selected.map(Member::location).orElse("Location"),
                selected.map(Member::zone).orElse(Zone.utc()));
    }

    private Member newMember(String role, String location, Zone zone) {
        return new Member("New Member", role, location, zone,
                TimePair.businessNormal(zone),
                TimePair.businessLunch(zone));
    }

    private void editNewMember(int index) {
        table.clearSelection();
        table.addRowSelectionInterval(index, index);
        table.addColumnSelectionInterval(0, 0);
        table.editCellAt(index, 0);

        editNewMemberName(table.getEditorComponent());
    }

    private void editNewMemberName(Component component) {
        if(isNull(component)) {
            return;
        }
        component.requestFocusInWindow();
        if(component instanceof JTextComponent t) {
            t.selectAll();
        }
    }

    private void fireTeamChanged(Team newTeam) {
        Teams.fireTeamChanged(this, String.valueOf(getValue(NAME)), newTeam);
    }
}
