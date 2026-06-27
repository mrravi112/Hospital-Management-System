package gui;

import managers.PatientManager;
import models.Patient;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class PatientPanel extends JPanel {

    private PatientManager       manager;
    private DefaultTableModel    tableModel;
    private JTable               table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField           searchField;

    private static final String[] COLUMNS =
        { "ID", "Name", "Age", "Gender", "Disease", "Contact", "Status" };

    public PatientPanel(PatientManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT_BG);

        
        JButton addBtn    = UIHelper.primaryButton("+ Add Patient");
        JButton editBtn   = UIHelper.secondaryButton("Edit");
        JButton deleteBtn = UIHelper.dangerButton("Delete");

        add(UIHelper.topBar("Patient Management", addBtn, editBtn, deleteBtn),
            BorderLayout.NORTH);

        
        JPanel searchBar = buildSearchBar();
        add(searchBar, BorderLayout.SOUTH);

        
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table  = UIHelper.styledTable(COLUMNS, tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.PANEL_BG);

        
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(Theme.CONTENT_BG);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        
        JLabel countLbl = UIHelper.label("", Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        center.add(buildSearchPanel(countLbl), BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(countLbl, BorderLayout.SOUTH);

        remove(searchBar); 
        add(center, BorderLayout.CENTER);

        
        loadTableData();
        updateCountLabel(countLbl);

        

        addBtn.addActionListener(e -> showAddEditDialog(null, countLbl));

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showInfo(this, "Please select a patient to edit."); return; }
            
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) tableModel.getValueAt(modelRow, 0);
            Patient p = manager.findById(id);
            if (p != null) showAddEditDialog(p, countLbl);
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showInfo(this, "Please select a patient to delete."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            String id   = (String) tableModel.getValueAt(modelRow, 0);
            String name = (String) tableModel.getValueAt(modelRow, 1);
            if (UIHelper.confirm(this, "Delete patient " + name + " (" + id + ")?")) {
                manager.deleteById(id);
                loadTableData();
                updateCountLabel(countLbl);
                UIHelper.showInfo(this, "Patient deleted successfully.");
            }
        });
    }

    
    private JPanel buildSearchPanel(JLabel countLbl) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        searchField = UIHelper.textField(20);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name or ID...");

        
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filter(countLbl); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filter(countLbl); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(countLbl); }
        });

        JButton clearBtn = UIHelper.secondaryButton("Clear");
        clearBtn.setPreferredSize(new Dimension(70, 32));
        clearBtn.addActionListener(e -> { searchField.setText(""); filter(countLbl); });

        panel.add(UIHelper.label("Search: ", Theme.FONT_REGULAR, Theme.TEXT_SECONDARY),
                  BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(clearBtn, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildSearchBar() { return new JPanel(); } 

    
    private void filter(JLabel countLbl) {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null); 
        } else {
            
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
        updateCountLabel(countLbl);
    }

    
    public void loadTableData() {
        tableModel.setRowCount(0); 
        for (Patient p : manager.getAll()) {
            tableModel.addRow(new Object[]{
                p.getPatientId(),
                p.getName(),
                p.getAge(),
                p.getGender(),
                p.getDisease(),
                p.getContactNumber(),
                p.getStatus()
            });
        }
    }

    private void updateCountLabel(JLabel lbl) {
        int visible = table.getRowCount();
        int total   = manager.count();
        lbl.setText("Showing " + visible + " of " + total + " patients");
    }

    /**
     * 
     *
     * @param patient  
     * @param countLbl 
     */
    private void showAddEditDialog(Patient patient, JLabel countLbl) {
        boolean isEdit = (patient != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     isEdit ? "Edit Patient" : "Add New Patient", true);
        

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(6, 4, 6, 4);

        
        JTextField nameFld    = UIHelper.textField(20);
        JTextField ageFld     = UIHelper.textField(5);
        JComboBox<String> genderCb  = UIHelper.comboBox(new String[]{"Male","Female","Other"});
        JTextField diseaseFld = UIHelper.textField(20);
        JTextField contactFld = UIHelper.textField(15);
        JComboBox<String> statusCb  = UIHelper.comboBox(
            new String[]{"Stable","Monitoring","Critical","Admitted"});

        
        if (isEdit) {
            nameFld.setText(patient.getName());
            ageFld.setText(String.valueOf(patient.getAge()));
            genderCb.setSelectedItem(patient.getGender());
            diseaseFld.setText(patient.getDisease());
            contactFld.setText(patient.getContactNumber());
            statusCb.setSelectedItem(patient.getStatus());
        }

        
        String[][] rows = {
            {"Name *",    "name"},
            {"Age *",     "age"},
            {"Gender",    "gender"},
            {"Disease *", "disease"},
            {"Contact *", "contact"},
            {"Status",    "status"}
        };
        Component[] fields = { nameFld, ageFld, genderCb, diseaseFld, contactFld, statusCb };

        for (int i = 0; i < rows.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.3;
            form.add(UIHelper.label(rows[i][0], Theme.FONT_REGULAR, Theme.TEXT_SECONDARY), gc);
            gc.gridx = 1; gc.weightx = 0.7;
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
            String ageText = ageFld.getText().trim();
            String disease = diseaseFld.getText().trim();
            String contact = contactFld.getText().trim();
            String gender  = (String) genderCb.getSelectedItem();
            String status  = (String) statusCb.getSelectedItem();

            if (name.isEmpty() || ageText.isEmpty() || disease.isEmpty() || contact.isEmpty()) {
                UIHelper.showError(dialog, "Please fill all required fields (marked *).");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
                if (age < 0 || age > 130) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                UIHelper.showError(dialog, "Please enter a valid age (0–130).");
                return;
            }

            
            if (isEdit) {
                patient.setName(name);
                patient.setAge(age);
                patient.setGender(gender);
                patient.setDisease(disease);
                patient.setContactNumber(contact);
                patient.setStatus(status);
                manager.update(patient);
                UIHelper.showInfo(dialog, "Patient updated successfully!");
            } else {
                manager.createPatient(name, age, gender, disease, contact, status);
                UIHelper.showInfo(dialog, "Patient added successfully!");
            }

            loadTableData();
            updateCountLabel(countLbl);
            dialog.dispose();
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this); 
        dialog.setResizable(false);
        dialog.setVisible(true);            
    }
}
