package gui;

import managers.*;
import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    
    private PatientManager     patientManager;
    private DoctorManager      doctorManager;
    private AppointmentManager appointmentManager;
    private BillingManager     billingManager;

    private DashboardPanel    dashboardPanel;
    private PatientPanel      patientPanel;
    private DoctorPanel       doctorPanel;
    private AppointmentPanel  appointmentPanel;
    private EmergencyPanel    emergencyPanel;
    private BillingPanel      billingPanel;

    
    private CardLayout cardLayout;
    private JPanel     cards;         
    public MainFrame() {
        
        super("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);   

        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            
        }

        
        patientManager     = new PatientManager();
        doctorManager      = new DoctorManager();
        appointmentManager = new AppointmentManager();
        billingManager     = new BillingManager();

        
        dashboardPanel   = new DashboardPanel(patientManager, doctorManager, appointmentManager);
        patientPanel     = new PatientPanel(patientManager);
        doctorPanel      = new DoctorPanel(doctorManager);
        appointmentPanel = new AppointmentPanel(appointmentManager, patientManager, doctorManager);
        emergencyPanel   = new EmergencyPanel(patientManager);
        billingPanel     = new BillingPanel(billingManager, patientManager);

        
        cardLayout = new CardLayout();
        cards      = new JPanel(cardLayout);
        cards.setBackground(Theme.CONTENT_BG);

        
        cards.add(dashboardPanel,   "dashboard");
        cards.add(patientPanel,     "patients");
        cards.add(doctorPanel,      "doctors");
        cards.add(appointmentPanel, "appointments");
        cards.add(emergencyPanel,   "emergency");
        cards.add(billingPanel,     "billing");

        
        cardLayout.show(cards, "dashboard");

        
        SidebarPanel sidebar = new SidebarPanel(panelName -> {
           
            cardLayout.show(cards, panelName);

            
            if ("dashboard".equals(panelName)) {
                dashboardPanel.refresh();
            }
        });

        
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(cards,   BorderLayout.CENTER);

        setVisible(true);
    }
}
