package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class SidebarPanel extends JPanel {

    
    private static final String[][] NAV_ITEMS = {
        { "Dashboard",    "dashboard"    },
        { "Patients",     "patients"     },
        { "Doctors",      "doctors"      },
        { "Appointments", "appointments" },
        { "Emergency",    "emergency"    },
        { "Billing",      "billing"      },
    };

    
    private JPanel activeItem = null;

    public interface NavListener {
        void onNavigate(String panelName);
    }

    private NavListener navListener;

    public SidebarPanel(NavListener listener) {
        this.navListener = listener;

        setBackground(Theme.SIDEBAR_BG);
        setPreferredSize(new Dimension(Theme.SIDEBAR_WIDTH, 0));
        // BoxLayout stacks components top to bottom
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    
        add(buildLogoPanel());
        add(Box.createVerticalStrut(8)); // 8px gap

        JPanel navSection = new JPanel();
        navSection.setLayout(new BoxLayout(navSection, BoxLayout.Y_AXIS));
        navSection.setOpaque(false);

        for (String[] item : NAV_ITEMS) {
            JPanel navItem = buildNavItem(item[0], item[1]);
            navSection.add(navItem);
            if (item[1].equals("dashboard")) {
                setActive(navItem);
            }
        }
        add(navSection);

        add(Box.createVerticalGlue());
        add(buildExitButton());
    }

    private JPanel buildLogoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 14, 14, 14));

        JLabel icon = new JLabel("🏥");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("HMS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Hospital System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(Theme.SIDEBAR_MUTED);

        textPanel.add(title);
        textPanel.add(subtitle);

        panel.add(icon, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Builds a single clickable nav item panel.
     *
     * @param label     Display text (e.g., "Patients")
     * @param panelName The card name to switch to in MainFrame
     */
    private JPanel buildNavItem(String label, String panelName) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(true);
        item.setBackground(Theme.SIDEBAR_BG);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        item.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_NAV);
        lbl.setForeground(Theme.SIDEBAR_MUTED);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 0));
        item.add(lbl, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActive(item);
                navListener.onNavigate(panelName);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (item != activeItem) {
                    item.setBackground(new Color(0x212f42));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (item != activeItem) {
                    item.setBackground(Theme.SIDEBAR_BG);
                }
            }
        });

        return item;
    }

    
    private void setActive(JPanel item) {
        if (activeItem != null) {
            activeItem.setBackground(Theme.SIDEBAR_BG);
            activeItem.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            JLabel lbl = (JLabel) activeItem.getComponent(0);
            lbl.setForeground(Theme.SIDEBAR_MUTED);
        }
        activeItem = item;
        item.setBackground(Theme.SIDEBAR_ACTIVE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, Theme.ACCENT),
            BorderFactory.createEmptyBorder(0, 11, 0, 14)
        ));
        JLabel lbl = (JLabel) item.getComponent(0);
        lbl.setForeground(Color.WHITE);
    }

    private JPanel buildExitButton() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x2d3a4d)),
            BorderFactory.createEmptyBorder(10, 14, 14, 14)
        ));

        JLabel exitLbl = new JLabel("⏻  Exit");
        exitLbl.setFont(Theme.FONT_NAV);
        exitLbl.setForeground(new Color(0xfc8181));
        exitLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    null, "Are you sure you want to exit?",
                    "Exit HMS", JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) System.exit(0);
            }
        });
        panel.add(exitLbl);
        return panel;
    }
}
