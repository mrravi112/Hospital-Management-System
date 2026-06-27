package gui;

import managers.*;
import models.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;


public class AppointmentPanel extends JPanel {

    private AppointmentManager manager;
    private PatientManager     patientManager;
    private DoctorManager      doctorManager;
    private DefaultTableModel  tableModel;
    private JTable             table;
    private TableRowSorter<DefaultTableModel> sorter;

    private static final String[] COLUMNS =
        { "Appt ID", "Patient", "Doctor", "Date", "Time", "Status" };

    public AppointmentPanel(AppointmentManager am, PatientManager pm, DoctorManager dm) {
        this.manager        = am;
        this.patientManager = pm;
        this.doctorManager  = dm;

        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT_BG);

        JButton bookBtn   = UIHelper.primaryButton("+ Book Appointment");
        JButton cancelBtn = UIHelper.dangerButton("Cancel Appt");
        add(UIHelper.topBar("Appointment Management", bookBtn, cancelBtn), BorderLayout.NORTH);

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
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.CONTENT_BG);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        center.add(scroll, BorderLayout.CENTER);
        center.add(countLbl, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        loadTableData();
        updateCountLabel(countLbl);

        bookBtn.addActionListener(e -> showBookingDialog(countLbl));

        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showInfo(this, "Please select an appointment to cancel."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            String id     = (String) tableModel.getValueAt(modelRow, 0);
            String status = (String) tableModel.getValueAt(modelRow, 5);
            if ("Cancelled".equals(status)) { UIHelper.showInfo(this, "Already cancelled."); return; }
            if (UIHelper.confirm(this, "Cancel appointment " + id + "?")) {
                manager.cancelById(id);
                loadTableData(); updateCountLabel(countLbl);
                UIHelper.showInfo(this, "Appointment cancelled.");
            }
        });
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        for (Appointment a : manager.getAll()) {
            tableModel.addRow(new Object[]{
                a.getAppointmentId(),
                a.getPatientId() + " - " + a.getPatientName(),
                a.getDoctorId() + " - " + a.getDoctorName(),
                a.getDate(), a.getTimeSlot(), a.getStatus()
            });
        }
    }

    private void updateCountLabel(JLabel lbl) {
        lbl.setText("Total appointments: " + manager.count());
    }

    private void showBookingDialog(JLabel countLbl) {
        if (patientManager.count() == 0) {
            UIHelper.showError(this, "Please add at least one patient first."); return;
        }
        if (doctorManager.count() == 0) {
            UIHelper.showError(this, "Please add at least one doctor first."); return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     "Book Appointment", true);

        // Build patient dropdown from existing patients
        java.util.ArrayList<Patient> patients = patientManager.getAll();
        String[] patientOptions = new String[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            patientOptions[i] = p.getPatientId() + " - " + p.getName();
        }

        // Build doctor dropdown from existing doctors
        java.util.ArrayList<Doctor> doctors = doctorManager.getAll();
        String[] doctorOptions = new String[doctors.size()];
        for (int i = 0; i < doctors.size(); i++) {
            Doctor d = doctors.get(i);
            doctorOptions[i] = d.getDoctorId() + " - Dr. " + d.getName();
        }

        JComboBox<String> patientCb = UIHelper.comboBox(patientOptions);
        JComboBox<String> doctorCb  = UIHelper.comboBox(doctorOptions);
        JTextField dateFld  = UIHelper.textField(15);
        JTextField timeFld  = UIHelper.textField(10);
        dateFld.setText("DD-MM-YYYY");
        timeFld.setText("10:00 AM");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 4, 8, 4);

        String[] labels = { "Patient *", "Doctor *", "Date *", "Time Slot *" };
        Component[] fields = { patientCb, doctorCb, dateFld, timeFld };

        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.35;
            form.add(UIHelper.label(labels[i], Theme.FONT_REGULAR, Theme.TEXT_SECONDARY), gc);
            gc.gridx = 1; gc.weightx = 0.65;
            form.add(fields[i], gc);
        }

        JButton saveBtn   = UIHelper.primaryButton("Book");
        JButton cancelBtn = UIHelper.secondaryButton("Cancel");
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(Theme.PANEL_BG);
        btnRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 0, 4, 4)));
        btnRow.add(cancelBtn); btnRow.add(saveBtn);
        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            String date = dateFld.getText().trim();
            String time = timeFld.getText().trim();
            if (date.isEmpty() || time.isEmpty() || date.equals("DD-MM-YYYY")) {
                UIHelper.showError(dialog, "Please enter date and time."); return;
            }

            int pi = patientCb.getSelectedIndex();
            int di = doctorCb.getSelectedIndex();
            Patient selPat = patients.get(pi);
            Doctor  selDoc = doctors.get(di);

            manager.createAppointment(
                selPat.getPatientId(), selPat.getName(),
                selDoc.getDoctorId(), selDoc.getName(), date, time
            );

            loadTableData(); updateCountLabel(countLbl);
            UIHelper.showInfo(dialog, "Appointment booked successfully!");
            dialog.dispose();
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
}
