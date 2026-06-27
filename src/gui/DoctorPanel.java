package gui;

import managers.DoctorManager;
import models.Doctor;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;


public class DoctorPanel extends JPanel {

    private DoctorManager        manager;
    private DefaultTableModel    tableModel;
    private JTable               table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField           searchField;

    private static final String[] COLUMNS =
        { "ID", "Name", "Specialization", "Contact", "Fee (₹)", "Availability" };

    public DoctorPanel(DoctorManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT_BG);

        JButton addBtn    = UIHelper.primaryButton("+ Add Doctor");
        JButton editBtn   = UIHelper.secondaryButton("Edit");
        JButton deleteBtn = UIHelper.dangerButton("Delete");

        add(UIHelper.topBar("Doctor Management", addBtn, editBtn, deleteBtn), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table  = UIHelper.styledTable(COLUMNS, tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.PANEL_BG);

        JLabel countLbl = UIHelper.label("", Theme.FONT_SMALL, Theme.TEXT_SECONDARY);

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(Theme.CONTENT_BG);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        center.add(buildSearchPanel(countLbl), BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(countLbl, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
        loadTableData();
        updateCountLabel(countLbl);

        addBtn.addActionListener(e -> showAddEditDialog(null, countLbl));

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showInfo(this, "Please select a doctor to edit."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            Doctor d = manager.findById((String) tableModel.getValueAt(modelRow, 0));
            if (d != null) showAddEditDialog(d, countLbl);
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showInfo(this, "Please select a doctor to delete."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            String id   = (String) tableModel.getValueAt(modelRow, 0);
            String name = (String) tableModel.getValueAt(modelRow, 1);
            if (UIHelper.confirm(this, "Delete Dr. " + name + " (" + id + ")?")) {
                manager.deleteById(id);
                loadTableData();
                updateCountLabel(countLbl);
                UIHelper.showInfo(this, "Doctor deleted successfully.");
            }
        });
    }

    private JPanel buildSearchPanel(JLabel countLbl) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        searchField = UIHelper.textField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter(countLbl); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filter(countLbl); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(countLbl); }
        });
        JButton clearBtn = UIHelper.secondaryButton("Clear");
        clearBtn.setPreferredSize(new Dimension(70, 32));
        clearBtn.addActionListener(e -> { searchField.setText(""); filter(countLbl); });
        panel.add(UIHelper.label("Search: ", Theme.FONT_REGULAR, Theme.TEXT_SECONDARY), BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(clearBtn, BorderLayout.EAST);
        return panel;
    }

    private void filter(JLabel countLbl) {
        String text = searchField.getText().trim();
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
        updateCountLabel(countLbl);
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        for (Doctor d : manager.getAll()) {
            tableModel.addRow(new Object[]{
                d.getDoctorId(), d.getName(), d.getSpecialization(),
                d.getContactNumber(), String.format("%.0f", d.getConsultationFee()),
                d.getAvailability()
            });
        }
    }

    private void updateCountLabel(JLabel lbl) {
        lbl.setText("Showing " + table.getRowCount() + " of " + manager.count() + " doctors");
    }

    private void showAddEditDialog(Doctor doctor, JLabel countLbl) {
        boolean isEdit = (doctor != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     isEdit ? "Edit Doctor" : "Add New Doctor", true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 4, 6, 4);

        JTextField nameFld   = UIHelper.textField(20);
        JTextField specFld   = UIHelper.textField(20);
        JTextField contactFld= UIHelper.textField(15);
        JTextField feeFld    = UIHelper.textField(10);
        JComboBox<String> availCb = UIHelper.comboBox(new String[]{"On Duty","Off Duty"});

        if (isEdit) {
            nameFld.setText(doctor.getName());
            specFld.setText(doctor.getSpecialization());
            contactFld.setText(doctor.getContactNumber());
            feeFld.setText(String.valueOf((int) doctor.getConsultationFee()));
            availCb.setSelectedItem(doctor.getAvailability());
        }

        String[] labels = {"Name *","Specialization *","Contact *","Fee (₹) *","Availability"};
        Component[] fields = { nameFld, specFld, contactFld, feeFld, availCb };

        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.35;
            form.add(UIHelper.label(labels[i], Theme.FONT_REGULAR, Theme.TEXT_SECONDARY), gc);
            gc.gridx = 1; gc.weightx = 0.65;
            form.add(fields[i], gc);
        }

        JButton saveBtn   = UIHelper.primaryButton(isEdit ? "Update" : "Save");
        JButton cancelBtn = UIHelper.secondaryButton("Cancel");
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(Theme.PANEL_BG);
        btnRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 0, 4, 4)
        ));
        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String name    = nameFld.getText().trim();
            String spec    = specFld.getText().trim();
            String contact = contactFld.getText().trim();
            String feeText = feeFld.getText().trim();
            String avail   = (String) availCb.getSelectedItem();

            if (name.isEmpty() || spec.isEmpty() || contact.isEmpty() || feeText.isEmpty()) {
                UIHelper.showError(dialog, "Please fill all required fields (marked *)."); return;
            }
            double fee;
            try { fee = Double.parseDouble(feeText); if (fee < 0) throw new NumberFormatException(); }
            catch (NumberFormatException ex) { UIHelper.showError(dialog, "Enter a valid fee."); return; }

            if (isEdit) {
                doctor.setName(name); doctor.setSpecialization(spec);
                doctor.setContactNumber(contact); doctor.setConsultationFee(fee);
                doctor.setAvailability(avail); manager.update(doctor);
                UIHelper.showInfo(dialog, "Doctor updated successfully!");
            } else {
                manager.createDoctor(name, spec, contact, fee, avail);
                UIHelper.showInfo(dialog, "Doctor added successfully!");
            }
            loadTableData(); updateCountLabel(countLbl); dialog.dispose();
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setSize(420, 330);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
}
