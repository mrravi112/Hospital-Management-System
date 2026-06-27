package gui;

import managers.*;
import models.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;


public class BillingPanel extends JPanel {

    private BillingManager   manager;
    private PatientManager   patientManager;
    private DefaultTableModel tableModel;
    private JTable            table;

    private static final String[] COLUMNS =
        { "Bill ID", "Patient", "Doctor Fee", "Medicine", "Room", "Total (₹)", "Date", "Status" };

    public BillingPanel(BillingManager bm, PatientManager pm) {
        this.manager        = bm;
        this.patientManager = pm;
        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT_BG);

        JButton genBtn  = UIHelper.primaryButton("+ Generate Bill");
        JButton viewBtn = UIHelper.secondaryButton("View Bill");
        JButton payBtn  = UIHelper.secondaryButton("Mark Paid");
        add(UIHelper.topBar("Billing Management", genBtn, viewBtn, payBtn), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIHelper.styledTable(COLUMNS, tableModel);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.PANEL_BG);

        JLabel countLbl = UIHelper.label("", Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        JPanel center   = new JPanel(new BorderLayout());
        center.setBackground(Theme.CONTENT_BG);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        center.add(scroll, BorderLayout.CENTER);
        center.add(countLbl, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        loadTableData();
        updateCountLabel(countLbl);

        genBtn.addActionListener(e -> showGenerateBillDialog(countLbl));

        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showInfo(this, "Please select a bill to view."); return; }
            String id = (String) tableModel.getValueAt(row, 0);
            Bill b    = manager.findById(id);
            if (b != null) showBillDetails(b);
        });

        payBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIHelper.showInfo(this, "Please select a bill to mark paid."); return; }
            String id     = (String) tableModel.getValueAt(row, 0);
            String status = (String) tableModel.getValueAt(row, 7);
            if ("Paid".equals(status)) { UIHelper.showInfo(this, "Bill is already paid."); return; }
            if (UIHelper.confirm(this, "Mark bill " + id + " as Paid?")) {
                manager.markPaid(id);
                loadTableData(); updateCountLabel(countLbl);
                UIHelper.showInfo(this, "Bill marked as Paid.");
            }
        });
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        for (Bill b : manager.getAll()) {
            tableModel.addRow(new Object[]{
                b.getBillId(),
                b.getPatientId() + " - " + b.getPatientName(),
                String.format("%.0f", b.getDoctorFee()),
                String.format("%.0f", b.getMedicineCost()),
                String.format("%.0f", b.getRoomCharges()),
                String.format("%.0f", b.getTotalAmount()),
                b.getDate(),
                b.getStatus()
            });
        }
    }

    private void updateCountLabel(JLabel lbl) {
        lbl.setText("Total bills: " + manager.count());
    }

    private void showGenerateBillDialog(JLabel countLbl) {
        if (patientManager.count() == 0) {
            UIHelper.showError(this, "No patients found. Add a patient first."); return;
        }

        java.util.ArrayList<Patient> patients = patientManager.getAll();
        String[] options = new String[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            options[i] = p.getPatientId() + " - " + p.getName();
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                     "Generate Bill", true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.PANEL_BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 4, 8, 4);

        JComboBox<String> patientCb = UIHelper.comboBox(options);
        JTextField docFeeFld  = UIHelper.textField(12);
        JTextField medFld     = UIHelper.textField(12);
        JTextField roomFld    = UIHelper.textField(12);
        JTextField dateFld    = UIHelper.textField(15);
        JLabel     totalLbl   = UIHelper.label("₹ 0", Theme.FONT_BOLD, Theme.ACCENT);

        dateFld.setText(java.time.LocalDate.now().toString());

        // Auto-calculate total on field change
        javax.swing.event.DocumentListener calcListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { calcTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { calcTotal(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcTotal(); }
            void calcTotal() {
                try {
                    double t = Double.parseDouble(docFeeFld.getText().trim())
                             + Double.parseDouble(medFld.getText().trim())
                             + Double.parseDouble(roomFld.getText().trim());
                    totalLbl.setText("₹ " + String.format("%.0f", t));
                } catch (Exception ex) { totalLbl.setText("₹ -"); }
            }
        };
        docFeeFld.getDocument().addDocumentListener(calcListener);
        medFld.getDocument().addDocumentListener(calcListener);
        roomFld.getDocument().addDocumentListener(calcListener);

        String[] labels = { "Patient *", "Doctor Fee (₹) *", "Medicine Cost (₹) *",
                            "Room Charges (₹) *", "Date", "Total Amount" };
        Component[] fields = { patientCb, docFeeFld, medFld, roomFld, dateFld, totalLbl };

        for (int i = 0; i < labels.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.4;
            form.add(UIHelper.label(labels[i], Theme.FONT_REGULAR, Theme.TEXT_SECONDARY), gc);
            gc.gridx = 1; gc.weightx = 0.6;
            form.add(fields[i], gc);
        }

        JButton genBtn    = UIHelper.primaryButton("Generate");
        JButton cancelBtn = UIHelper.secondaryButton("Cancel");
        JPanel  btnRow    = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(Theme.PANEL_BG);
        btnRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 0, 4, 4)));
        btnRow.add(cancelBtn); btnRow.add(genBtn);
        cancelBtn.addActionListener(e -> dialog.dispose());

        genBtn.addActionListener(e -> {
            try {
                int pi = patientCb.getSelectedIndex();
                Patient p = patients.get(pi);
                double df = Double.parseDouble(docFeeFld.getText().trim());
                double mc = Double.parseDouble(medFld.getText().trim());
                double rc = Double.parseDouble(roomFld.getText().trim());
                if (df < 0 || mc < 0 || rc < 0) throw new NumberFormatException();
                String date = dateFld.getText().trim();

                Bill b = manager.createBill(p.getPatientId(), p.getName(), df, mc, rc, date);
                loadTableData(); updateCountLabel(countLbl);
                dialog.dispose();
                showBillDetails(b); // Show the generated bill immediately
            } catch (NumberFormatException ex) {
                UIHelper.showError(dialog, "Please enter valid amounts (numbers only, no negatives).");
            }
        });

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setSize(420, 370);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /** Shows a formatted bill receipt in a dialog */
    private void showBillDetails(Bill b) {
        String receipt = String.format(
            "╔══════════════════════════════════════╗\n"
          + "         HOSPITAL BILL RECEIPT         \n"
          + "╚══════════════════════════════════════╝\n\n"
          + "  Bill ID       : %s\n"
          + "  Patient ID    : %s\n"
          + "  Patient Name  : %s\n"
          + "  Date          : %s\n"
          + "  Status        : %s\n\n"
          + "  ─────────────────────────────────────\n"
          + "  Doctor Fee    : ₹ %.0f\n"
          + "  Medicine Cost : ₹ %.0f\n"
          + "  Room Charges  : ₹ %.0f\n"
          + "  ─────────────────────────────────────\n"
          + "  TOTAL AMOUNT  : ₹ %.0f\n",
            b.getBillId(), b.getPatientId(), b.getPatientName(),
            b.getDate(), b.getStatus(),
            b.getDoctorFee(), b.getMedicineCost(), b.getRoomCharges(),
            b.getTotalAmount()
        );

        JTextArea area = new JTextArea(receipt);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(new Color(0xf8fafc));
        area.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JOptionPane.showMessageDialog(this, area, "Bill Receipt — " + b.getBillId(),
                                      JOptionPane.PLAIN_MESSAGE);
    }
}
