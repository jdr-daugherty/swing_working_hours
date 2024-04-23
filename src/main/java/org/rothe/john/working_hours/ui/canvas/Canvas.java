package org.rothe.john.working_hours.ui.canvas;

import lombok.val;
import org.rothe.john.working_hours.model.Member;
import org.rothe.john.working_hours.model.Team;
import org.rothe.john.working_hours.ui.canvas.rows.AbstractZoneRow;
import org.rothe.john.working_hours.ui.canvas.rows.CanvasRow;
import org.rothe.john.working_hours.ui.canvas.rows.MemberRow;
import org.rothe.john.working_hours.ui.canvas.rows.ZoneRow;
import org.rothe.john.working_hours.ui.canvas.rows.ZoneTransitionsRow;
import org.rothe.john.working_hours.util.Palette;
import org.rothe.john.working_hours.util.Zone;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static javax.swing.BorderFactory.createBevelBorder;

public class Canvas extends JPanel {
    private static final int INSET = 5;
    private final RowList rows = new RowList();
    private final CanvasInfoProxy canvasInfo = new CanvasInfoProxy(rows);
    private Palette palette = null;
    private Team team = null;

    public Canvas() {
        super();
        setBackground(Color.WHITE);
        setOpaque(true);
        setBorder(createBevelBorder(BevelBorder.RAISED));
        setLayout(new GridBagLayout());
        initialize();
    }

    @Override
    protected void paintChildren(Graphics g) {
        canvasInfo.update((Graphics2D) g);
        if (!rows.isEmpty()) {
            GridPainter.paintGrid((Graphics2D) g, this, rows.getFirst());
        }
        super.paintChildren(g);
    }


    public void setTeam(Team team) {
        this.team = team;
        initialize();
    }

    private void initialize() {
        rows.clear();
        if (isNull(team)) {
            initBlankCanvas();
        } else {
            initTeamCanvas();
        }
    }

    private void initBlankCanvas() {
        this.palette = new Palette(List.of());
        removeAll();
    }

    private void initTeamCanvas() {
        val zones = team.getZones();
        this.palette = new Palette(zones);
        removeAll();
        addZones(zones);
        addMembers(team.getMembers());
        addTransitionsRow(zones);
        addSpacerGlue();
    }

    private void addZones(List<Zone> zones) {
        final Function<Zone, ZoneRow> toRow = zoneId -> new ZoneRow(canvasInfo, zoneId, palette);

        zones.stream()
                .map(toRow)
                .sorted(zoneRowComparator())
                .forEach(this::addRow);
    }

    private void addMembers(List<Member> members) {
        final Function<Member, MemberRow> toRow = member -> new MemberRow(canvasInfo, member, palette);

        members.stream()
                .map(toRow)
                .sorted(zoneRowComparator())
                .forEach(this::addRow);
    }

    private Comparator<AbstractZoneRow> zoneRowComparator() {
        return comparing(AbstractZoneRow::getOffsetHours);
    }

    private void addRow(CanvasRow row) {
        add(row, rowConstraints());
        rows.add(row);
    }

    private void addTransitionsRow(List<Zone> zoneIds) {
        val row = new ZoneTransitionsRow(canvasInfo, zoneIds);
        add(row, transitionsConstraints());
        rows.add(row);
    }

    private void addSpacerGlue() {
        add(Box.createGlue(), new GridBagConstraints(0, -1, 27, 1,
                1.0, 1.0, CENTER, BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    private static GridBagConstraints transitionsConstraints() {
        return new GridBagConstraints(0, -1, 27, 1,
                0.0, 0.0, WEST, NONE,
                new Insets(30, INSET, 2, INSET), 20, 20);
    }

    private static GridBagConstraints rowConstraints() {
        return new GridBagConstraints(0, -1, 27, 1,
                1.0, 0.0, CENTER, BOTH,
                rowInsets(), 0, 30);
    }

    private static Insets rowInsets() {
        return new Insets(0, INSET, 2, INSET);
    }

    private static class CanvasInfoProxy implements CanvasInfo {
        private final RowList rows;
        private int headerWidth = 0;
        private int footerWidth = 0;

        public CanvasInfoProxy(RowList rows) {
            this.rows = rows;
        }

        @Override
        public int getRowHeaderWidth() {
            return headerWidth;
        }

        @Override
        public int getRowFooterWidth() {
            return footerWidth;
        }

        public void update(Graphics2D g2d) {
            headerWidth = rows.getColumnHeaderWidth(g2d);
            footerWidth = rows.getColumnFooterWidth(g2d);
        }
    }
}