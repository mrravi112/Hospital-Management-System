package gui;

import managers.*;
import javax.swing.*;
import java.awt.*;


public class DashboardPanel extends JPanel {

    private PatientManager     patientManager;
    private DoctorManager      doctorManager;
    private AppointmentManager appointmentManager;

   
    private JLabel patientCountLbl;
    private JLabel doctorCountLbl;
    private JLabel apptCountLbl;

    public DashboardPanel(PatientManager pm, DoctorManager dm, AppointmentManager am) {
        this.patientManager     = pm;
        this.doctorManager      = dm;
        this.appointmentManager = am;

        setLayout(new BorderLayout());
        setBackground(Theme.CONTENT_BG);

        
        add(UIHelper.topBar("Dashboard"), BorderLayout.NORTH);

        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.CONTENT_BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

       
        JPanel banner = buildBanner();
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(banner);
        content.add(Box.createVerticalStrut(20));

        
        JLabel sectionLbl = UIHelper.label("Overview", Theme.FONT_BOLD, Theme.TEXT_SECONDARY);
        sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(sectionLbl);
        content.add(Box.createVerticalStrut(10));

        
        JPanel statsRow = buildStatsRow();
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(statsRow);
        content.add(Box.createVerticalStrut(24));

        
        JPanel tips = buildTipsPanel();
        tips.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tips);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    
    private JPanel buildBanner() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(Theme.ACCENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x3a85e8), 1),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel title = new JLabel("Welcome to Hospital Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Manage patients, doctors, appointments, and billing — all in one place.");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(new Color(0xdbeafe));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 4));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

  
    private JPanel buildStatsRow() {
        
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

       
        JPanel pCard = UIHelper.statCard("Total Patients",
                String.valueOf(patientManager.count()), Theme.BLUE_FG);
        // Save reference to the value label so refresh() can update it
        patientCountLbl = (JLabel) pCard.getComponent(1);
        row.add(pCard);

        JPanel dCard = UIHelper.statCard("Doctors",
                String.valueOf(doctorManager.count()), Theme.GREEN_FG);
        doctorCountLbl = (JLabel) dCard.getComponent(1);
        row.add(dCard);

        
        JPanel aCard = UIHelper.statCard("Appointments",
                String.valueOf(appointmentManager.count()), Theme.AMBER_FG);
        apptCountLbl = (JLabel) aCard.getComponent(1);
        row.add(aCard);

       
        row.add(UIHelper.statCard("Emergency Queue", "0", Theme.RED_FG));

        return row;
    }

    
    private JPanel buildTipsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel title = UIHelper.label("Quick Guide", Theme.FONT_BOLD, Theme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        String[] tips = {
            "→  Use 'Patients' to add, search, update, or remove patient records.",
            "→  Use 'Doctors' to manage doctor profiles and availability.",
            "→  Use 'Appointments' to book or cancel patient-doctor sessions.",
            "→  Use 'Emergency' to handle critical patients via priority queue.",
            "→  Use 'Billing' to generate and manage patient bills.",
            "→  All data is automatically saved to the data/ folder as .txt files."
        };

        JPanel tipsList = new JPanel(new GridLayout(tips.length, 1, 0, 6));
        tipsList.setOpaque(false);
        tipsList.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        for (String t : tips) {
            JLabel lbl = UIHelper.label(t, Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
            tipsList.add(lbl);
        }
        panel.add(tipsList, BorderLayout.CENTER);
        return panel;
    }

    
    public void refresh() {
        patientCountLbl.setText(String.valueOf(patientManager.count()));
        doctorCountLbl.setText(String.valueOf(doctorManager.count()));
        apptCountLbl.setText(String.valueOf(appointmentManager.count()));
    }
}
