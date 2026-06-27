package gui;

import models.Patient;
import managers.PatientManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.PriorityQueue;
import java.util.Comparator;


public class EmergencyPanel extends JPanel {

    
    static class EmergencyEntry {
        String patientId;
        String name;
        int    priority;    
        String priorityLabel;
        String notes;

        EmergencyEntry(String id, String name, int priority, String label, String notes) {
            this.patientId     = id;
            this.name          = name;
            this.priority      = priority;
            this.priorityLabel = label;
            this.notes         = notes;
        }
    }

    
    private PriorityQueue<EmergencyEntry> emergencyQueue = new PriorityQueue<>(
        Comparator.comparingInt(e -> e.priority)
    );

    private PatientManager   patientManager;
    private DefaultTableModel tableModel;
    private JTable            table;
    private JLabel            queueSizeLbl;

    private static final String[] COLUMNS = { "Priority", "Patient ID", "Name", "Notes" };

    public EmergencyPanel(PatientManager pm) {
        this.patientManager = pm;
        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT_BG);

        JButton addBtn     = UIHelper.primaryButton("+ Add to Queue");
        JButton processBtn = UIHelper.secondaryButton("Process Next");
        add(UIHelper.topBar("Emergency Management", addBtn, processBtn), BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIHelper.styledTable(COLUMNS, tableModel);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.PANEL_BG);

        
        JPanel infoPanel = buildInfoPanel();

        queueSizeLbl = UIHelper.label("Queue size: 0", Theme.FONT_SMALL, Theme.TEXT_SECONDARY);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(Theme.CONTENT_BG);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        center.add(infoPanel, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(queueSizeLbl, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        
        addBtn.addActionListener(e -> showAddEmergencyDialog());

        
        processBtn.addActionListener(e -> {
            if (emergencyQueue.isEmpty()) {
                UIHelper.showInfo(this, "Emergency queue is empty.");
                return;
            }
            
            EmergencyEntry next = emergencyQueue.poll();
            refreshTable();

            
            JOptionPane.showMessageDialog(this,
                "Processing Patient:\n\n" +
                "ID       : " + next.patientId + "\n" +
                "Name     : " + next.name + "\n" +
                "Priority : " + next.priorityLabel + "\n" +
                "Notes    : " + next.notes,
                "Processing Emergency Patient",
                JOptionPane.WARNING_MESSAGE
            );
        });
    }

    
    private JPanel buildInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        panel.add(makeInfoCard("🔴 Critical (P1)", "Life-threatening condition.\nProcessed first.",
                               Theme.RED_BG, Theme.RED_FG));
        panel.add(makeInfoCard("🟡 Serious (P2)", "Requires urgent attention.\nProcessed second.",
                               Theme.AMBER_BG, Theme.AMBER_FG));
        panel.add(makeInfoCard("🟢 Normal (P3)", "Non-emergency case.\nProcessed last.",
                               Theme.GREEN_BG, Theme.GREEN_FG));
        return panel;
    }

    private JPanel makeInfoCard(String title, String desc, Color bg, Color fg) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fg, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JLabel t = new JLabel(title);
        t.setFont(Theme.FONT_BOLD); t.setForeground(fg);
        JLabel d = new JLabel("<html>" + desc.replace("\n","<br>") + "</html>");
        d.setFont(Theme.FONT_SMALL); d.setForeground(fg);
        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        return card;
    }

    private void showAddEmergencyDialog() {
        
        java.util.ArrayList<Patient> patients = patientManager.getAll();
        if (patients.isEmpty()) {
            UIHelper.showError(this, "No patients found. Add patients first."); return;
        }

        String[] options = new String[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            options[i] = p.getPatientId() + " - " + p.getName();
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     "Add Emergency Patient", true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 4, 8, 4);

        JComboBox<String> patientCb  = UIHelper.comboBox(options);
        JComboBox<String> priorityCb = UIHelper.comboBox(
            new String[]{"Critical (P1)", "Serious (P2)", "Normal (P3)"}
        );
        JTextField notesFld = UIHelper.textField(25);

        String[] labels = { "Patient *", "Priority *", "Notes" };
        Component[] fields = { patientCb, priorityCb, notesFld };
        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.3;
            form.add(UIHelper.label(labels[i], Theme.FONT_REGULAR, Theme.TEXT_SECONDARY), gc);
            gc.gridx = 1; gc.weightx = 0.7;
            form.add(fields[i], gc);
        }

        JButton addBtn    = UIHelper.primaryButton("Add to Queue");
        JButton cancelBtn = UIHelper.secondaryButton("Cancel");
        JPanel  btnRow    = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(Theme.PANEL_BG);
        btnRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 0, 4, 4)));
        btnRow.add(cancelBtn); btnRow.add(addBtn);
        cancelBtn.addActionListener(e -> dialog.dispose());

        addBtn.addActionListener(e -> {
            int pi = patientCb.getSelectedIndex();
            Patient selPat = patients.get(pi);

            
            int    priority;
            String label;
            int    sel = priorityCb.getSelectedIndex();
            if (sel == 0)      { priority = 1; label = "Critical"; }
            else if (sel == 1) { priority = 2; label = "Serious"; }
            else               { priority = 3; label = "Normal"; }

            String notes = notesFld.getText().trim();
            if (notes.isEmpty()) notes = "No notes";

            
            emergencyQueue.offer(new EmergencyEntry(
                selPat.getPatientId(), selPat.getName(), priority, label, notes
            ));

            refreshTable();
            dialog.dispose();
            UIHelper.showInfo(this, selPat.getName() + " added to emergency queue as " + label + ".");
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setSize(400, 270);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    
    private void refreshTable() {
        tableModel.setRowCount(0);

        
        Object[] entries = emergencyQueue.toArray();
        
        java.util.Arrays.sort(entries, Comparator.comparingInt(o -> ((EmergencyEntry) o).priority));

        for (Object obj : entries) {
            EmergencyEntry e = (EmergencyEntry) obj;
            tableModel.addRow(new Object[]{
                e.priorityLabel, e.patientId, e.name, e.notes
            });
        }

        queueSizeLbl.setText("Queue size: " + emergencyQueue.size());
    }
}
