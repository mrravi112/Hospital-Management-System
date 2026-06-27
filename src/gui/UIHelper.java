package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;


public class UIHelper {

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Theme.ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(Theme.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        // Hover effect: slightly darker on mouse-over
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0x3a85e8)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(Theme.ACCENT); }
        });
        return btn;
    }

    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Theme.TEXT_PRIMARY);
        btn.setFont(Theme.FONT_REGULAR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 36));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0xf0f4f8)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Theme.RED_FG);
        btn.setForeground(Color.WHITE);
        btn.setFont(Theme.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 36));
        return btn;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    public static JTextField textField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(Theme.FONT_REGULAR);
        tf.setPreferredSize(new Dimension(200, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    public static JComboBox<String> comboBox(String[] options) {
        JComboBox<String> cb = new JComboBox<>(options);
        cb.setFont(Theme.FONT_REGULAR);
        cb.setPreferredSize(new Dimension(200, 32));
        cb.setBackground(Color.WHITE);
        return cb;
    }

    
    public static JTable styledTable(String[] columns, DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Theme.PANEL_BG : Theme.TABLE_ALT_ROW);
                }
                return c;
            }
        };

        table.setFont(Theme.FONT_TABLE);
        table.setRowHeight(Theme.ROW_HEIGHT);
        table.setShowGrid(false);                          
        table.setIntercellSpacing(new Dimension(0, 0));    
        table.setSelectionBackground(Theme.BLUE_BG);
        table.setSelectionForeground(Theme.BLUE_FG);
        table.getTableHeader().setFont(Theme.FONT_TABLE_HEAD);
        table.getTableHeader().setBackground(Theme.TABLE_HEADER);
        table.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
        table.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER)
        );
        table.setDefaultEditor(Object.class, null);        

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return lbl;
            }
        };
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        return table;
    }

    
    public static JPanel statCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Theme.PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Theme.FONT_SMALL);
        titleLbl.setForeground(Theme.TEXT_SECONDARY);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLbl.setForeground(valueColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    
    public static JLabel badge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        return lbl;
    }

    
    public static JPanel topBar(String title, JButton... buttons) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.PANEL_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        bar.setPreferredSize(new Dimension(0, Theme.TOPBAR_HEIGHT));

        JLabel titleLbl = label(title, Theme.FONT_LARGE, Theme.TEXT_PRIMARY);
        bar.add(titleLbl, BorderLayout.WEST);

        if (buttons.length > 0) {
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btnPanel.setOpaque(false);
            for (JButton b : buttons) btnPanel.add(b);
            bar.add(btnPanel, BorderLayout.EAST);
        }

        return bar;
    }

    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Info",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
               JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
